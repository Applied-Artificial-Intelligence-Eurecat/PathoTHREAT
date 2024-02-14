from neo4j import GraphDatabase

uri = "neo4j://pathocert-neo:7687"
db_driver = GraphDatabase.driver(uri, auth=("neo4j", "1234"))
