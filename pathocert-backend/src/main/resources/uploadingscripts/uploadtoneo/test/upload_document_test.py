import unittest
from upload_document_json import *
from upload_pathogen_test import ResultStub, DriverStub, SessionDummy
from upload_pathogen_json import Relation, search_for_relations

class NodeTest(unittest.TestCase):
    def testEquals(self):
        node=Node("Document", {'sql_id': 10,'title': 'Earthquake causes covid outbreak','document_date': '2021-10-27'})
        self.assertTrue(node.equals(node))

class MainTest2(unittest.TestCase):
    def test_1_node(self):
        simple_node = Node("Document", {'sql_id': 10, 'title': 'Earthquake causes covid outbreak'})
        resultat = identify_nodes(open_json_file(r"src\main\resources\uploading-scripts\test\test_jsons\1_node_document.json")[0])
        self.assertTrue(len(resultat) == 1)
        self.assertEqual(simple_node.to_string(), resultat[0].to_string())

    def test_multiple_nodes(self):
        expected = [Node("Document", {'sql_id': 10,
                                      'title': 'Earthquake causes covid outbreak',
                                      'document_date': '2021-10-27'}),
                    Node("Location", {'city': 'Luverne', 'region': 'Minnesota', 'country': 'USA'}),
                    Node("Cause", {'description': "people reuniting on safehouse without masks"}),
                    Node("Source", {'description': "unknown patient 0"}),
                    Node("Detection", {'description': "multiple residents presenting symptoms"}),
                    Node("Impact", {"people_ill": "1000", "people_dead": "35"}),
                    Node("Contaminant", {"name": "covid"}),
                    Node("Symptom", {"name": "dry cough"}),
                    Node("Symptom", {"name": "fever"}),
                    Node("Symptom", {"name": "headache"}),
                    Node("Mitigation", {"description": "social distancing"}),
                    Node("Monitoring", {"description": "phone app"}),
                    Node("Monitoring", {"description": "blood tests"})]
        resultat = identify_nodes(open_json_file(r"src\main\resources\uploading-scripts\test\test_jsons\multiple_nodes_document.json")[0])
        self.assertTrue(len(resultat) == 13)
        for node in expected:
            for node2 in resultat:
                if node.equals(node2):
                    break
            else:
                print(node.to_string())
                self.fail()

    def test_attributes_to_query(self):
        simple_node = Node("Document", {'sql_id': 10, 'title': 'Earthquake causes covid outbreak'})
        expected = "{sql_id: \"10\",title: \"Earthquake causes covid outbreak\"}"
        self.assertEqual(attributes_to_query(simple_node.attributes), expected)

    def test_create_non_repeated_nodes_adding(self):
        nodes = identify_nodes(open_json_file(r"src\main\resources\uploading-scripts\test\test_jsons\multiple_nodes_document.json")[0])
        driver = DriverStub(False)
        create_non_repeated_nodes(nodes, driver=driver)
        self.assertEqual(driver.saved_session.created, 13)

    def test_create_non_repeated_nodes_not_adding(self):
        nodes = identify_nodes(open_json_file(r"src\main\resources\uploading-scripts\test\test_jsons\multiple_nodes_document.json")[0])
        driver = DriverStub()
        create_non_repeated_nodes(nodes, driver=driver)
        self.assertEqual(driver.saved_session.created, 0)

    def test_create_non_repeated_relations_adding(self):
        nodes = identify_nodes(open_json_file(r"src\main\resources\uploading-scripts\test\test_jsons\multiple_nodes_document.json")[0])
        driver = DriverStub(False)
        relations = search_for_relations(nodes, relation_information=relation_information_2)
        create_non_repeated_relations(relations, driver=driver)
        for relation in relations:
            print(relation.to_string())
        self.assertEqual(driver.saved_session.created, 12)

    def test_create_non_repeated_relations_not_adding(self):
        nodes = identify_nodes(open_json_file(r"src\main\resources\uploading-scripts\test\test_jsons\multiple_nodes_document.json")[0])
        driver = DriverStub()
        relations = search_for_relations(nodes, relation_information=relation_information_2)
        create_non_repeated_relations(relations, driver=driver)
        self.assertEqual(driver.saved_session.created, 0)

if __name__ in "__main__":
    unittest.main()