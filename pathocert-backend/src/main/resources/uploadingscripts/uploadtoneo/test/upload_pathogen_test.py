import unittest
from unittest.case import expectedFailure
from upload_pathogen_json import *

class NodeTest(unittest.TestCase):
    def testEquals(self):
        node1 = Node("Event", "earthquake")
        self.assertTrue(node1.equals(node1))
        node2 = Node("Event", "earthquake")
        self.assertTrue(node1.equals(node2))
        node3 = Node("Contaminant", "earthquake")
        self.assertFalse(node1.equals(node3))

class RelationTest(unittest.TestCase):
    def testEquals(self):
        rel1 = Relation(Node("Event", "earthquake"), Node("CascadingWNEvent", "pipe break"), "CAN_PRODUCE")
        self.assertTrue(rel1.equals(rel1))
        rel2 = Relation(Node("Event", "earthquake"), Node("CascadingWNEvent", "pipe break"), "CAN_PRODUCE")
        self.assertTrue(rel1.equals(rel2))
        rel3 = Relation(Node("Event", "earthquake"), Node("CascadingWNEvent", "pipe break"), "CAN_CAUSE")
        self.assertFalse(rel1.equals(rel3))

class ResultStub():
    def __init__(self,data):
        self.data_list = data

    def data(self):
        return self.data_list

class SessionDummy():
    def __init__(self,response=True):
        self.response = response
        self.created = 0

    # Retornarà un element sempre que es demani un node
    def run(self, query):
        if query.startswith("MATCH (n:") or query.endswith("return n1,n2,r"):
            if self.response:
                return ResultStub(["node"])
            else:
                return ResultStub([])
        else:
            self.created += 1

class DriverStub():
    def __init__(self, response=None):
        self.response_saved = response

    def session(self, response=True):
        if self.response_saved is not None:
            response = self.response_saved
        self.saved_session = SessionDummy(response)
        return self.saved_session

    def close(self):
        pass

class MainTest(unittest.TestCase):
    # Testing get_list_of_nodes
    def test_single_data_single_node(self):
        simple_node = Node("Event", "earthquake")
        result = get_list_of_nodes("Event", "earthquake")
        self.assertTrue(len(result) == 1)
        self.assertTrue(simple_node.equals(result[0]))

    def test_multiple_data_multiple_nodes(self):
        simple_node1 = Node("Symptom", "insomnia")
        simple_node2 = Node("Symptom", "fever")
        simple_node3 = Node("Symptom", "coughing")
        nodes = [simple_node1, simple_node2, simple_node3]
        result = get_list_of_nodes("Symptom", ["insomnia", "fever", "coughing"])
        self.assertTrue(len(result) == 3)
        for i in range(len(nodes)):
            self.assertTrue(nodes[i].equals(result[i]))

    # Testing identify_nodes
    def open_a_json_simple_file(self, filename):
        json_f = open_json_file(filename)
        self.assertTrue(len(json_f) == 1)
        nodes = identify_nodes(json_f[0])
        self.assertIn("simple",nodes)
        self.assertIn("contaminants",nodes)
        return nodes

    def check_all_nodes(self, expected, actual):
        for node in expected:
            for node2 in actual:
                if node.equals(node2):
                    break
            else:
                # print(node.to_string())
                self.fail()

    def test_isolated_node(self):
        nodes = self.open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\just_1_node.json")
        self.assertTrue(len(nodes["simple"]) == 1)
        self.assertTrue(len(nodes["contaminants"]) == 0)
        self.assertTrue(nodes["simple"][0].equals(Node("Event", "terrorist attack")))

    def test_isolated_nodes(self):
        nodes = self.open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\isolated_nodes.json")
        expected_nodes = [Node("Event", "flood"),
                          Node("ProducedEvent", "water loss"),
                          Node("EffectWaterTaste", "earthy musty")]
        self.assertTrue(len(nodes["simple"]) == 3)
        self.assertTrue(len(nodes["contaminants"]) == 0)
        self.check_all_nodes(expected_nodes, nodes["simple"])
    
    def test_isolated_without_multiples(self):
        nodes = self.open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\isolated_without_multiples.json")
        expected_nodes_simple = [Node("Event", "flood"),
                                 Node("EffectWaterTaste", "earthy musty")]
        expected_nodes_cont = [Node("Contaminant", "ecoli"),
                               Node("Symptom", "death")]
        self.assertTrue(len(nodes["simple"]) == 2)
        self.assertTrue(len(nodes["contaminants"]) == 1)
        self.assertTrue(len(nodes["contaminants"][0]) == 2)
        self.check_all_nodes(expected_nodes_simple, nodes["simple"])
        self.check_all_nodes(expected_nodes_cont,nodes["contaminants"][0])

    def test_isolated_with_multiples(self):
        nodes = self.open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\isolated_with_multiples.json")
        expected_nodes_simple = [Node("Event", "flood"),
                          Node("EffectWaterTaste", "earthy musty")]
        expected_nodes_cont1 = [Node("Contaminant", "ecoli"),
                                Node("Symptom", "death"),
                                Node("Symptom", "chestpain")]
        expected_nodes_cont2 = [Node("Contaminant", "norovirus"),
                                Node("Symptom", "fever")]
        self.assertTrue(len(nodes["simple"]) == 2)
        self.assertTrue(len(nodes["contaminants"]) == 2)
        self.assertTrue(len(nodes["contaminants"][0]) == 3)
        self.assertTrue(len(nodes["contaminants"][1]) == 2)
        self.check_all_nodes(expected_nodes_simple, nodes["simple"])
        self.check_all_nodes(expected_nodes_cont1,nodes["contaminants"][0])
        self.check_all_nodes(expected_nodes_cont2,nodes["contaminants"][1])

    def test_isolated_multiples_repeated(self):
        nodes = self.open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\isolated_multiples_repeated.json")
        expected_nodes_simple = [Node("Event", "flood"),
                                 Node("EffectWaterTaste", "earthy musty")]
        expected_nodes_cont1 = [Node("Contaminant", "ecoli"),
                                Node("Symptom", "death")]
        expected_nodes_cont2 = [Node("Contaminant", "norovirus"),
                                Node("Symptom", "death")]
        self.assertTrue(len(nodes["simple"]) == 2)
        self.assertTrue(len(nodes["contaminants"]) == 2)
        self.assertTrue(len(nodes["contaminants"][0]) == 2)
        self.assertTrue(len(nodes["contaminants"][1]) == 2)
        self.check_all_nodes(expected_nodes_simple, nodes["simple"])
        self.check_all_nodes(expected_nodes_cont1,nodes["contaminants"][0])
        self.check_all_nodes(expected_nodes_cont2,nodes["contaminants"][1])

    # Testing identify_relations
    def test_just_1_relation(self):
        nodes = self.open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\just_1_relation.json")
        relations = identify_relations(nodes)
        self.assertEqual(len(relations),1)
        expected = Relation(Node("Event", "flood"), Node("CascadingWNEvent", "source contamination"), "CAN_PRODUCE")
        self.assertTrue(expected.equals(relations[0]))

    def test_multiple_relations(self):
        nodes = self.open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\multiple_relations.json")
        relations = identify_relations(nodes)
        # self.assertEqual(len(relations),2)
        expected1 = Relation(Node("Event", "flood"), Node("CascadingWNEvent", "source contamination"), "CAN_PRODUCE")
        expected2 = Relation(Node("CascadingWNEvent", "source contamination"), Node("ProducedEvent", "water loss"), "CAN_CAUSE")
        self.check_all_nodes([expected1, expected2], relations)

    def test_normal_case_simple(self):
        nodes = self.open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\normal_case_simple.json")
        relations = identify_relations(nodes)
        self.assertTrue(len(relations) == 6)
        water_loss = Node("ProducedEvent", "water loss")
        ecoli = Node("Contaminant", "ecoli")
        bacteria = Node("ContaminantFamily", "bacteria")
        norovirus = Node("Contaminant", "norovirus")
        expected = [Relation(water_loss, ecoli, "CAN_PRODUCE"),
                    Relation(water_loss, norovirus, "CAN_PRODUCE"),
                    Relation(ecoli,bacteria,"FAMILY"),
                    Relation(bacteria, Node("EffectWaterTaste", "chlorine bleach"), "CAN_TASTE"),
                    Relation(bacteria, Node("EffectWaterTaste", "earthy musty"), "CAN_TASTE"),
                    Relation(norovirus, Node("ContaminantTreatment", "coagulants"), "IS_TREATED")]
        self.check_all_nodes(expected, relations)

    def test_type_case(self):
        nodes = self.open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\type_case.json")
        relations = identify_relations(nodes)
        self.assertTrue(len(relations) == 1)
        expected = Relation(Node("ContaminantFamily", "bacteria"), Node("ContaminantType", "toxin"), "TYPE")
        self.assertTrue(expected.equals(relations[0]))

    def test_normal_case_complex(self):
        nodes = self.open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\normal_case_complex.json")
        relations = identify_relations(nodes)
        self.assertTrue(len(relations) == 18)
        cascading = Node("CascadingWNEvent", "poisonous cloud",1,1)
        cascading0 = Node("CascadingWNEvent", "gas explosion", 0,1)
        produced_event = Node("ProducedEvent", "source contamination")
        ecoli = Node("Contaminant", "ecoli")
        bacteria = Node("ContaminantFamily", "bacteria")
        virus = Node("ContaminantFamily", "virus")
        gastroenteritis = Node("EffectHealth", "gastroenteritis")
        norovirus = Node("Contaminant", "norovirus")
        expected = [Relation(produced_event, ecoli, "CAN_PRODUCE"),
                    Relation(produced_event, norovirus, "CAN_PRODUCE"),
                    Relation(ecoli,bacteria,"FAMILY"),
                    Relation(norovirus, virus, "FAMILY"),
                    Relation(virus, Node("ContaminantType", "chemical"), "TYPE"),
                    Relation(ecoli, Node("ContaminantTreatment", "antibiotics"), "IS_TREATED"),
                    Relation(ecoli, gastroenteritis, "CAN_CAUSE"),
                    Relation(gastroenteritis, Node("Symptom", "vomit"), "HAS_SYMPTOMS"),
                    Relation(gastroenteritis, Node("Symptom", "fever"), "HAS_SYMPTOMS"),
                    Relation(bacteria, Node("ContaminantType", "toxin"), "TYPE"),
                    Relation(bacteria, Node("EffectWaterTaste", "chlorine bleach"), "CAN_TASTE"),
                    Relation(bacteria, Node("EffectWaterTaste", "earthy musty"), "CAN_TASTE"),
                    Relation(norovirus, Node("ContaminantTreatment", "coagulants"), "IS_TREATED"),
                    Relation(Node("Event", "industrial accident"), cascading0, "CAN_PRODUCE"),
                    Relation(cascading, produced_event, "CAN_CAUSE"),
                    Relation(cascading0, cascading, "CAN_CASCADE")]
        self.check_all_nodes(expected, relations)

class DatabaseTest(unittest.TestCase):
    # Comprova que si la BD retorna algun resultat, check_for_existing_node sap que ha de retornar false
    def test_check_nodes(self):
        driver = DriverStub()
        session = driver.session()
        self.assertFalse(check_for_existing_node(session,Node("Event", "terrorist attack")))
        session = driver.session(False)
        self.assertTrue(check_for_existing_node(session,Node("Event", "terrorist attack")))

    def test_all_nodes_adding(self):
        nodes = MainTest().open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\normal_case_simple.json")
        driver = DriverStub(False)
        create_non_repeated_nodes(nodes, driver=driver)
        self.assertEqual(driver.saved_session.created, 7)

    def test_all_nodes_not_adding(self):
        nodes = MainTest().open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\normal_case_simple.json")
        driver = DriverStub()
        create_non_repeated_nodes(nodes, driver=driver)
        self.assertEqual(driver.saved_session.created, 0)

    # Comprova que si la BD retorna algun resultat, check_for_existing_relation sap que ha de retornar false
    def test_check_relation(self):
        driver = DriverStub()
        session = driver.session()
        self.assertFalse(check_for_existing_relation(session,Relation(Node("Event", "industrial accident"), Node("CascadingWNEvent", "pipe break"), "CAN_PRODUCE")))
        session = driver.session(False)
        self.assertTrue(check_for_existing_relation(session,Relation(Node("Event", "industrial accident"), Node("CascadingWNEvent", "pipe break"), "CAN_PRODUCE")))

    def test_all_relations_adding(self):
        nodes = MainTest().open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\normal_case_simple.json")
        rels = identify_relations(nodes)
        driver = DriverStub(False)
        create_non_repeated_relations(rels, driver=driver)
        self.assertEqual(driver.saved_session.created, 6)

    def test_all_relations_not_adding(self):
        nodes = MainTest().open_a_json_simple_file(r"src\main\resources\uploading-scripts\test\test_jsons\normal_case_simple.json")
        rels = identify_relations(nodes)
        driver = DriverStub()
        create_non_repeated_relations(rels, driver=driver)
        self.assertEqual(driver.saved_session.created, 0)



if __name__ in "__main__":
    unittest.main()
