import sys

from ComponentsService.duplicated_info_finder import similarity, similarity_numeric
from neo4j import GraphDatabase

uri = "neo4j://localhost:7687"
db_driver = GraphDatabase.driver(uri, auth=("neo4j", "1234"))


class Location():
    def __init__(self, country, street=None, city=None, region=None):
        self.street = street
        self.city = city
        self.region = region
        self.country = country

    def level(self):
        if self.street is not None:
            return 0
        elif self.city is not None:
            return 1
        elif self.region is not None:
            return 2
        else:
            return 3


class Contaminant():
    def __init__(self, name, symptoms=[]):
        self.name = name
        self.symptoms = symptoms

    def __eq__(self, o: object):
        if type(object) != Contaminant:
            return False
        else:
            if self.name != o.name:
                return False
            seen_symptoms = []
            for symp in self.symptoms:
                for symp2 in o.symptoms:
                    if symp2 in seen_symptoms:
                        continue
                    if symp == symp2:
                        seen_symptoms.append(symp2)
                        break
                else:
                    return False
            return True


class Emergency():
    def __init__(self, location, contaminants, date, sources, causes):
        self.location = location
        self.contaminants = contaminants
        self.date = date
        self.sources = sources
        self.causes = causes


def compare_multiple_data(original, recent):
    used_original_elems = []
    distance_for_original_elems = []
    result = 0
    for elem in recent:
        min_distance = 5
        for elem2 in original:
            distance = similarity_numeric(elem, elem2)
            if distance < min_distance:
                min_distance = distance
                used_original_elems.append(elem2)
                distance_for_original_elems.append(distance)
        result += ((100 - min_distance * 20) / max(len(original), len(recent)))
    return min(100, result)


def compare_event_dates(original, recent):
    original_d = original.split("-")
    recent_d = recent.split("-")
    if original_d[0] == recent_d[0] and original_d[1] == recent_d[1]:
        return 100
    elif original_d[0] == recent_d[0]:
        return 80
    else:
        return max(0, 80 - 10 * (int(recent_d[0]) - int(original_d[0])))


def compare_locations(original, recent):
    result = 0
    if original.level() == recent.level():
        result += 50
    else:
        result = [30, 10, 0][max(original.level(), recent.level()) - min(original.level(), recent.level()) - 1]
    common_elements = 4 - max(original.level(), recent.level())
    for i in range(common_elements):
        if i == 0:
            if original.country == recent.country:
                result += (50 / common_elements)
            else:
                break
        elif i == 1:
            if original.region == recent.region:
                result += (50 / common_elements)
            else:
                break
        elif i == 2:
            if original.city == recent.city:
                result += (50 / common_elements)
            else:
                break
        elif i == 3:
            if original.street == recent.street:
                result += (50 / common_elements)
            else:
                break
    return min(result, 100)


def compare_contaminants(original, recent):
    result = compare_multiple_data(
        [og.name for og in original], [re.name for re in recent]
    ) * 0.5
    symptoms_result = 0
    used_contaminants = []
    for contaminant in recent:
        for contaminant2 in original:
            if contaminant2 not in used_contaminants and similarity(contaminant.name, contaminant2.name):
                symptoms_result += compare_multiple_data(contaminant.symptoms, contaminant2.symptoms)
                used_contaminants.append(contaminant2)
    result += ((symptoms_result / max(len(recent), len(original))) * 0.5)
    return result


def compare(emergency1, emergency2):
    total_value = 0
    total_value += compare_locations(emergency1.location, emergency2.location)
    total_value += compare_contaminants(emergency1.contaminants, emergency2.contaminants)
    total_value += compare_multiple_data(emergency1.sources, emergency2.sources)
    total_value += compare_multiple_data(emergency1.causes, emergency2.causes)
    # total_value += compare_event_dates(emergency1.date, emergency2.date)
    return min(100, total_value / 4)


def from_db_to_emergency(document_title, driver=db_driver):
    session = driver.session()
    location_node = session.run(
        "MATCH (n:Document {attrs})-[:HAS_LOCATION]->(i) return i".format(attrs="{title: \"" + document_title + "\"}"))
    location_data = location_node.data()
    location = Location(None)
    for key in location_data[0]["i"]:
        if key == "country":
            location.country = location_data[0]["i"][key]
        if key == "region":
            location.region = location_data[0]["i"][key]
        if key == "city":
            location.city = location_data[0]["i"][key]
        if key == "street":
            location.street = location_data[0]["i"][key]
    contaminants_nodes = session.run(
        "MATCH (n:Document {attrs})-[:HAS_CONTAMINANT]->(i)-[:HAS_SYMPTOMS]->(s) return i,s".format(
            attrs="{title: \"" + document_title + "\"}"))
    contaminants_data = contaminants_nodes.data()
    contaminants_names = list(set([elem["i"]["name"] for elem in contaminants_data]))
    contaminants = []
    for contaminant in contaminants_names:
        contaminants.append(Contaminant(contaminant, [elem["s"]["name"] for elem in contaminants_data if
                                                      elem["i"]["name"] == contaminant]))
    date_node = session.run("MATCH (n:Document {attrs}) return n".format(attrs="{title: \"" + document_title + "\"}"))
    date_data = date_node.data()
    date = date_data[0]["n"]["event_date"]
    sources_node = session.run(
        "MATCH (n:Document {attrs})-[:HAS_SOURCE]->(i) return i".format(attrs="{title: \"" + document_title + "\"}"))
    sources_data = sources_node.data()
    sources = [elem["i"]["description"] for elem in sources_data]
    causes_node = session.run(
        "MATCH (n:Document {attrs})-[:HAS_CAUSE]->(i) return i".format(attrs="{title: \"" + document_title + "\"}"))
    causes_data = causes_node.data()
    causes = [elem["i"]["description"] for elem in causes_data]
    emergency = Emergency(location, contaminants, date, sources, causes)
    session.close()
    return emergency


# Earthquake causes covid outbreak
# Large waterborne Campylobacter outbreak: use of multiple approaches to investigate contamination of the drinking water supply system, Norway, June 2019
if __name__ in "__main__":
    em1 = from_db_to_emergency(sys.argv[1])
    em2 = from_db_to_emergency(sys.argv[2])
    print(compare(em1, em2))
