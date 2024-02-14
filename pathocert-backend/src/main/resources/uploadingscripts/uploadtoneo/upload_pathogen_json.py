import json
from neo4j import GraphDatabase
import resources.duplicated_info_finder as d
import glob
import sys
from time import sleep
import dataclasses as dto

node_information = {"type_of_event": "Event",
                    "cascading_event": "CascadingWNEvent",
                    "produced_event": "ProducedEvent",
                    "effect_water_taste": "EffectWaterTaste",
                    "effect_water_odor": "EffectWaterOdor",
                    "effect_water_color": "EffectWater",
                    }
contaminant_information = {"name": "Contaminant",
                           "family": "ContaminantFamily",
                           "treatment": "ContaminantTreatment",
                           "health_effect": "EffectHealth",
                           "symptoms": "Symptom",
                           "type": "ContaminantType",
                           "mitigation": "ContaminantMitigation"
                           }
relation_information = {"Event": {"CascadingWNEvent": "CAN_PRODUCE",
                                    "Contaminant": "CAN_PRODUCE"},
                        "CascadingWNEvent": {"ProducedEvent": "CAN_CAUSE", "CascadingWNEvent": "CAN_CASCADE",
                                             "EffectWater": "HAS_EFFECT"},
                        "ProducedEvent": {"Contaminant": "CAN_PRODUCE", "ProducedEvent": "CAN_PRODUCE",
                                          "EffectWater": "HAS_EFFECT"},
                        "EffectHealth": {"Symptom": "HAS_SYMPTOM"},
                        "Contaminant": {"EffectHealth": "CAN_CAUSE",
                                         "ContaminantFamily": "FAMILY",
                                         "ContaminantTreatment": "IS_TREATED",
                                         "Symptom": "HAS_SYMPTOM",
                                         "EffectWaterOdor": "CAN_SMELL",
                                         "EffectWaterTaste": "CAN_TASTE",
                                         "ContaminantMitigation": "CAN_BE_MITIGATED"},
                        "ContaminantFamily": {"EffectWaterTaste": "CAN_TASTE",
                                              "ContaminantType": "TYPE"},
                        "EffectWaterTaste": {"EffectWaterOdor": "CAN_SMELL"},
                        "EffectWaterOdor": {"ContaminantFamily": "FAMILY"}
                        }

uri = "neo4j://pathocert-neo:7687"
# uri = "neo4j://localhost:7687"
db_driver = GraphDatabase.driver(uri, auth=("neo4j", "1234"))


# Representa un node com de la BD
@dto.dataclass
class Node:
    type: str
    name: str
    order: int = 0
    max_order: int = 0

    def __eq__(self, other):
        return type(other) == Node and self.type == other.type and self.name == other.name

    def __str__(self):
        return self.name + "(" + self.type + ")"

    def __repr__(self):
        return "NAME: " + self.name + \
               "\tTYPE: " + self.type + \
               "\tORDER: " + str(self.order) + \
               "\tMAX_ORDER: " + str(self.max_order)


# Representa la relació (node1)-[:type]->(node2) com a la BD
@dto.dataclass
class Relation:
    node1: Node
    node2: Node
    type: str

    def __str__(self):
        return self.node1.name + "(" + self.node1.type + ") " + self.type + " " + self.node2.name + "(" + self.node2.type + ")"

    def __repr__(self):
        return str(self)

    def __eq__(self, other):
        return type(other) == Relation and str(self) == str(other)


def open_json_file(filename):
    with open(filename, "r") as json_f:
        json_list = json.loads(json_f.read())
    return json_list


def get_list_of_nodes(node_type, list_of_values):
    if type(list_of_values) == list:
        return [Node(node_type, d.transform(elem), i, len(list_of_values) - 1) for i, elem in enumerate(list_of_values)]
    else:
        return [Node(node_type, d.transform(list_of_values))]


def identify_nodes(json_file):
    nodes = {"simple": [], "contaminants": []}
    for key in json_file.keys():
        if key in node_information.keys():
            # Els que són 2 es creen per separat
            nodes["simple"].extend(get_list_of_nodes(node_information[key], json_file[key]))
        elif key == "contaminants":
            if type(json_file[key]) == list:
                for contaminant in json_file[key]:
                    contaminant_nodes = []
                    for c_key in contaminant.keys():
                        if c_key in contaminant_information.keys():
                            contaminant_nodes.extend(
                                get_list_of_nodes(contaminant_information[c_key], contaminant[c_key]))
                    nodes["contaminants"].append(contaminant_nodes)
            else:
                contaminant = json_file[key]
                contaminant_nodes = []
                for c_key in contaminant.keys():
                    if c_key in contaminant_information.keys():
                        contaminant_nodes.extend(get_list_of_nodes(contaminant_information[c_key], contaminant[c_key]))
                nodes["contaminants"].append(contaminant_nodes)
    return nodes


def check_for_existing_node(session, node):
    result = session.run(f"MATCH (n:{node.type}) WHERE n.name = '{node.name}' return n")
    result_data = result.data()
    if len(result_data) >= 1:
        return True
    result = session.run(f"MATCH (n:{node.type}) return n")
    for n in result.data():
        if d.similarity(n["n"]["name"], node.name, max_distance=0):
            return n["n"]["name"]
    return len(result.data()) >= 1


def add_node(session, node):
    session.run(f"CREATE (:{node.type} " + "{" + f"name: '" + node.name + "'" + "})")


def create_non_repeated_nodes(identified_nodes, driver=db_driver):
    session = driver.session()
    for node in identified_nodes["simple"]:
        value = check_for_existing_node(session, node)
        if type(value) == str:
            node.name = value
        else:
            if not value:
                add_node(session, node)
    for contaminant in identified_nodes["contaminants"]:
        for node in contaminant:
            value = check_for_existing_node(session, node)
            if type(value) == str:
                node.name = value
            else:
                if not value:
                    add_node(session, node)
    driver.close()
    return identified_nodes


def search_for_relations(nodes, relation_information=relation_information):
    relations = []
    for node1 in nodes:
        for node2 in nodes:
            if node1.type in relation_information:
                if node2.type in relation_information[node1.type]:
                    if node1.type == node2.type:
                        if node1.order < node2.order:
                            relations.append(Relation(node1, node2, relation_information[node1.type][node2.type]))
                    else:
                        if node1.type == "Event":
                            if node2.order == 0:
                                relations.append(Relation(node1, node2, relation_information[node1.type][node2.type]))
                        elif node1.type == "CascadingWNEvent" or node1.type == "ProducedEvent":
                            if node1.order == node1.max_order:
                                relations.append(Relation(node1, node2, relation_information[node1.type][node2.type]))
                        else:
                            relations.append(Relation(node1, node2, relation_information[node1.type][node2.type]))
    return relations


def identify_relations(identified_nodes):
    identified_relations = []
    identified_relations.extend(search_for_relations(identified_nodes["simple"]))
    for contaminant_nodes in identified_nodes["contaminants"]:
        identified_relations.extend(search_for_relations(contaminant_nodes + identified_nodes["simple"]))
    identified_relations_unique = []
    for relation in identified_relations:
        for relation2 in identified_relations_unique:
            if relation == relation2:
                break
        else:
            identified_relations_unique.append(relation)
    return identified_relations_unique


def match_relation(relation):
    return f"MATCH (n1:{relation.node1.type}) -[r:{relation.type}]-> (n2:{relation.node2.type}) " + \
           f"WHERE n1.name = \"{relation.node1.name}\" AND n2.name = \"{relation.node2.name}\" return n1,n2,r"


def create_relation(relation):
    return f"MATCH (n1:{relation.node1.type}) WITH n1 MATCH (n2:{relation.node2.type}) WHERE n1.name='{relation.node1.name}' " + \
           f"and n2.name='{relation.node2.name}' CREATE (n1)-[rl:{relation.type}]->(n2)"


def check_for_existing_relation(session, relation):
    result = session.run(match_relation(relation))
    data = result.data()
    return not len(data) >= 1


def add_relation(session, relation):
    session.run(create_relation(relation))


def create_non_repeated_relations(identified_relations, driver=db_driver):
    session = driver.session()
    for relation in identified_relations:
        if check_for_existing_relation(session, relation):
            add_relation(session, relation)
    driver.close()


if __name__ in "__main__":
    print("Starting pathogen script")
    tries = 0
    while tries < 30:
        with db_driver.session() as session:
            try:
                result = session.run("MATCH (n) return n limit 1")
                print("Query ran correctly")
                break
            except Exception:
                print("Retrying...")
                tries += 1
                sleep(10)
    if tries == 30:
        print("Health check failed")
        raise Exception()
    print("Health check succesful")
    for file in glob.glob(sys.argv[1]):
        print(file)
        list_of_jsons = open_json_file(file)
        for json_file in list_of_jsons:
            identified_nodes = identify_nodes(json_file)
            identified_nodes = create_non_repeated_nodes(identified_nodes)
            identified_relations = identify_relations(identified_nodes)
            create_non_repeated_relations(identified_relations)
