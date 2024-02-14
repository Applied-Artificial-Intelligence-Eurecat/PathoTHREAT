import glob
import sys
from time import sleep

import upload_document_json as d
import upload_pathogen_json as p
from hierarchical import run_hierarchical

if __name__ in "__main__":
    print("Starting pathogen script")
    tries = 0
    while tries < 30:
        with p.db_driver.session() as session:
            try:
                #
                result = session.run("MATCH (n) DETACH DELETE n")
                print("Query ran correctly")
                break
            except Exception:
                print("Retrying...")
                tries += 1
                sleep(10)
    if tries == 30:
        print("Health check failed")
        raise Exception()
    with p.db_driver.session() as session:
        session.run("MATCH (n:Event)-[*]->(n2) DETACH DELETE n, n2")
    sleep(5)
    print("Health check succesful")
    for file in glob.glob(sys.argv[1]):
        print(file)
        list_of_jsons = p.open_json_file(file)
        for json_file in list_of_jsons:
            identified_nodes = p.identify_nodes(json_file)
            for node in identified_nodes["simple"]:
                node.name = node.name[0].upper() + node.name[1:]
            for node in identified_nodes["contaminants"]:
                for cont_node in node:
                    if type(cont_node) == p.Node:
                        cont_node.name = cont_node.name[0].upper() + cont_node.name[1:]
                    else:
                        print("ET: ", type(cont_node))
            identified_nodes = p.create_non_repeated_nodes(identified_nodes)
            identified_relations = p.identify_relations(identified_nodes)
            p.create_non_repeated_relations(identified_relations)

    print("Starting document script")
    tries = 0
    while tries < 30:
        with d.db_driver.session() as session:
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
    docs_jsons = []
    for file in glob.glob(sys.argv[2]):
        print(file, flush=True)
        list_of_jsons = d.open_json_file(file)
        for json_file in list_of_jsons:
            docs_jsons.append(json_file)
            identified_nodes = d.identify_nodes(json_file)
            identified_nodes = d.create_non_repeated_nodes(identified_nodes)
            identified_relations = p.search_for_relations(identified_nodes,
                                                          relation_information=d.relation_information_2)
            d.create_non_repeated_relations(identified_relations)

    print("Running clustering...")
    clusters = run_hierarchical(sys.argv[2])
    print("Clustering successful")

    session = d.db_driver.session()
    for members, title in clusters:
        session.run("CREATE (n:TypeOfEventClass {description: \"" + title + "\"})")

        for doc in docs_jsons:
            if type(doc) == list:
                for dk in doc:
                    t = d.process_title(dk["title"])
                    if "event" in dk.keys():
                        if type(dk["event"]) == list:
                            for ev in dk["event"]:
                                if ev in members:
                                    query = "MATCH (n:TypeOfEventClass {description: \"" + title + "\"}) WITH n1 " + \
                                            "MATCH (n2:Document {title: \"" + t + "\" CREATE (n2)-[rl:HAS_TOE_CLASS]->(n1)"
                                    session.run(query)
                        else:
                            if dk["event"] in members:
                                query = "MATCH (n:TypeOfEventClass {description: \"" + title + "\"}) WITH n1 " + \
                                        "MATCH (n2:Document {title: \"" + t + "\" CREATE (n2)-[rl:HAS_TOE_CLASS]->(n1)"
                                session.run(query)
            else:
                dk = doc
                t = d.process_title(dk["title"])
                if "event" in dk.keys():
                    if type(dk["event"]) == list:
                        for ev in dk["event"]:
                            if ev in members:
                                print(f"Creating")
                                query = "MATCH (n1:TypeOfEventClass {description: \"" + title + "\"}) WITH n1 " + \
                                        "MATCH (n2:Document {title: \"" + t + "\"}) WITH n1, n2 CREATE (n2)-[rl:HAS_TOE_CLASS]->(n1)"
                                session.run(query)
                    else:
                        if dk["event"] in members:
                            print(f"Creating")
                            query = "MATCH (n1:TypeOfEventClass {description: \"" + title + "\"}) WITH n1 " + \
                                    "MATCH (n2:Document {title: \"" + t + "\"}) WITH n1, n2 CREATE (n2)-[rl:HAS_TOE_CLASS]->(n1)"
                            session.run(query)

    session.close()
