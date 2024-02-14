import dataclasses as dto
import glob
import json
import sys
from time import sleep

from neo4j import GraphDatabase

import resources.duplicated_info_finder as d
from upload_pathogen_json import search_for_relations


def process_title(title: str) -> str:
    return title.replace('.pdf', '').replace("-", " ").title()


document_information = {"Document": {"sql_id": "sql_id",
                                     "title": "title",
                                     "document_date": "document_date",
                                     "event_date": "event_date",
                                     "event_duration": "event_duration"},
                        "Location": {"street": "street",
                                     "city": "city",
                                     "region": "region",
                                     "country": "country"},
                        "Cause": {"cause": "description"},
                        "Source": {"source": "description"},
                        "Detection": {"detection": "description"},
                        "Impact": {"people_ill": "people_ill",
                                   "people_hospitalized": "people_hospitalized",
                                   "people_dead": "people_dead"},
                        "ContaminantD": {"contaminants": "name"},
                        "SymptomD": {"symptoms": "name"},
                        "Mitigation": {"mitigation": "description"},
                        "Monitoring": {"monitoring": "description"},
                        "Restoration": {"restoration": "description"},
                        "Prevention": {"prevention": "description"},
                        "Investigation": {"investigation": "description"},
                        "Infrastructure": {"infrastructure": "description"},
                        "TypeOfEvent": {"event": "description"}
                        }
relation_information_2 = {"Document": {"Location": "HAS_LOCATION",
                                       "Cause": "HAS_CAUSE",
                                       "Source": "HAS_SOURCE",
                                       "Detection": "HAS_DETECTION",
                                       "Impact": "HAS_IMPACT",
                                       "ContaminantD": "HAS_CONTAMINANT",
                                       "Mitigation": "HAS_MITIGATION",
                                       "Monitoring": "HAS_MONITORING",
                                       "Restoration": "HAS_RESTORATION",
                                       "Prevention": "HAS_PREVENTION",
                                       "Investigation": "HAS_INVESTIGATION",
                                       "Infrastructure": "HAS_INFRASTRUCTURE",
                                       "Type Of Event": "HAS_TYPE_EVENT"},
                          "ContaminantD": {"SymptomD": "HAS_SYMPTOMS"}}

uri = "neo4j://pathocert-neo:7687"
# uri = "neo4j://localhost:7687"
db_driver = GraphDatabase.driver(uri, auth=("neo4j", "1234"))


@dto.dataclass
class Noded():
    def __init__(self, type, attributes={}, name=None):
        self.type = type
        self.attributes = attributes
        if name is not None:
            self.name = name
        elif "name" in attributes.keys():
            self.name = attributes["name"]
        elif "description" in attributes.keys():
            self.name = attributes["description"]
        else:
            self.name = self.type

    def __str__(self):
        return self.type + " " + json.dumps(self.attributes, indent=4, sort_keys=False)

    def __eq__(self, other):
        if self.type != other.type:
            return False
        else:
            for key in self.attributes.keys():
                if key in other.attributes.keys():
                    if self.attributes[key] != other.attributes[key]:
                        return False
                else:
                    return False
            return True


def open_json_file(filename):
    with open(filename, "r", encoding="utf-8") as json_f:
        json_list = json.loads(json_f.read())
    if type(json_list) == dict:
        return [json_list]
    return json_list


def identify_nodes(json_file):
    nodes = []
    for possible_node in document_information.keys():
        node_attributes = {}
        multiple_nodes = None
        for possible_attribute in document_information[possible_node].keys():
            if possible_attribute in json_file.keys():
                if type(json_file[possible_attribute]) == list:
                    multiple_nodes = [
                        Noded(possible_node, {document_information[possible_node][possible_attribute]: d.transform(dt)})
                        for dt in json_file[possible_attribute] if dt != ""]
                else:
                    if json_file[possible_attribute] != "":
                        if possible_node == "Location" or possible_attribute == "document_date" or possible_attribute == "title":
                            node_attributes[document_information[possible_node][possible_attribute]] = json_file[
                                possible_attribute]
                        else:
                            node_attributes[document_information[possible_node][possible_attribute]] = d.transform(
                                json_file[possible_attribute])
        if multiple_nodes is not None and len(multiple_nodes) > 0:
            nodes.extend(multiple_nodes)
        elif len(node_attributes) > 0:
            nodes.append(Noded(possible_node, attributes=node_attributes))
    for i, node in enumerate(nodes):
        for j, attr in enumerate(node.attributes):
            try:
                if type(nodes[i].attributes[attr]) != str:
                    continue
                nodes[i].attributes[attr] = node.attributes[attr][0].upper() + node.attributes[attr][1:]
            except KeyError:
                pass
        try:
            nodes[i].attributes["title"] = process_title(node.attributes["title"])
        except KeyError:
            pass
    nodes2 = []
    for node in nodes:
        if str(node) not in [str(n) for n in nodes2]:
            nodes2.append(node)
    return nodes2


def attributes_to_query(attrs):
    stringed = "{"
    for attr in sorted(attrs.keys()):
        stringed += attr
        stringed += ": \""
        stringed += str(attrs[attr])
        stringed += "\","
    stringed = stringed[:-1]
    return stringed + "}"


def node_in_db(node, session):
    result = session.run(f"MATCH (n:{node.type} " + attributes_to_query(node.attributes) + ") return n")
    result_data = result.data()
    if len(result_data) >= 1:
        return True, node
    result = session.run(f"MATCH (n:{node.type}) return n")
    if node.type in ["Location", "Impact"]:
        return False, node
    important_attr = "description"
    if node.type in ["ContaminantD", "SymptomD"]:
        important_attr = "name"
    if node.type == "Document":
        important_attr = "title"
        node.attributes[important_attr] = node.attributes[important_attr].replace('.pdf', '').replace("-", " ").title()
    result_data2 = result.data()
    for n in result_data2:
        if d.similarity(n["n"][important_attr], node.attributes[important_attr]):
            return [important_attr, n["n"][important_attr]], node
    return len(result_data) >= 1, node


def add_node_to_db(node, session):
    session.run(f"CREATE (n:{node.type} " + attributes_to_query(node.attributes) + ")")


def create_non_repeated_nodes(identified_nodes, driver=db_driver):
    session = driver.session()
    for node in identified_nodes:
        value, node = node_in_db(node, session)
        if type(value) == list:
            node.attributes[value[0]] = value[1]
        else:
            if not value:
                add_node_to_db(node, session)
    driver.close()
    return identified_nodes


def relation_in_db(session, relation):
    result = session.run(f"MATCH (n1: {relation.node1.type} " + attributes_to_query(
        relation.node1.attributes) + f") -[r:{relation.type}]-> (n2: {relation.node2.type} " + \
                         attributes_to_query(relation.node2.attributes) + ") return n1,n2,r")
    return len(result.data()) >= 1


def add_rel_to_db(session, relation):
    query = f"MATCH (n1:{relation.node1.type} " + attributes_to_query(relation.node1.attributes) + f") WITH n1 " + \
            f"MATCH (n2:{relation.node2.type} " + attributes_to_query(
        relation.node2.attributes) + f") CREATE (n1)-[rl:{relation.type}]->(n2)"
    session.run(query)


def create_non_repeated_relations(identified_relations, driver=db_driver):
    session = driver.session()
    for relation in identified_relations:
        if not relation_in_db(session, relation):
            add_rel_to_db(session, relation)
    driver.close()


if __name__ in "__main__":
    print("Starting document script")
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
    print("Health check successful")
    for file in glob.glob(sys.argv[1]):
        print(file, flush=True)
        list_of_jsons = open_json_file(file)
        for json_file in list_of_jsons:
            identified_nodes = identify_nodes(json_file)
            identified_nodes = create_non_repeated_nodes(identified_nodes)
            identified_relations = search_for_relations(identified_nodes, relation_information=relation_information_2)
            create_non_repeated_relations(identified_relations)
