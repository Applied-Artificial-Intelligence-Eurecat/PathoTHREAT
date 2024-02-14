import json

from db_driver import db_driver
from dto import Location, Contaminant, Emergency
from duplicated_info_finder import similarity_numeric, similarity

import requests


def get_scores(main_w, sentences):
    r = requests.post("http://pathocert-model:5000/comparation", json={"main": main_w, "sentences": "|".join(sentences)})

    if r.status_code != 200:
        return 0
    return float(r.text)


def compare_multiple_data(original, recent):
    """
        Compare two lists of words using transformations and the Levenshtein algorithm
    """
    used_original_elems = []
    distance_for_original_elems = []
    result = 0
    if len(recent) == 1 and len(original) == 0:
        try:
            st = recent[0].name
        except AttributeError:
            st = recent[0]
        if st == "Other":
            return 100
    for elem in recent:
        min_distance = 5
        for elem2 in original:
            try:
                distance = similarity_numeric(elem.name, elem2.name)
            except AttributeError:
                try:
                    distance = similarity_numeric(elem.name, elem2)
                except AttributeError:
                    try:
                        distance = similarity_numeric(elem, elem2)
                    except AttributeError:
                        distance = 10000
            if distance < min_distance:
                min_distance = distance
                used_original_elems.append(elem2)
                distance_for_original_elems.append(distance)
        result += ((100 - min_distance * 20) / max(len(original), len(recent)))
    return min(100, result)


def compare_locations(original, recent):
    """
        Compare two Location objects
    """
    if recent.level() is None:
        return 0
    if original.level() == recent.level():
        result = 50
        if original.level() == 0:
            if original.street == recent.street:
                result += 50
        elif original.level() == 1:
            if original.city == recent.city:
                result += 50
        elif original.level() == 2:
            if original.region == recent.region:
                result += 50
        elif original.level() == 3:
            if original.country == recent.country:
                result += 50
    else:
        result = [30, 10, 0][max(original.level(), recent.level()) - min(original.level(), recent.level()) - 1]
    return min(result, 100)


def compare_contaminants(original, recent):
    """
        Used to compare Contaminants with their symptoms, now obsolete
    """
    result = compare_multiple_data(
        [og.name for og in original], [re.name for re in recent]
    ) * 0.5
    symptoms_result = 0
    used_contaminants = []
    for contaminant in recent:
        for contaminant2 in original:
            if contaminant2 not in used_contaminants and similarity(contaminant.name, contaminant2.name):
                # symptoms_result += compare_multiple_data(contaminant.symptoms, contaminant2.symptoms)
                used_contaminants.append(contaminant2)
    result += ((symptoms_result / max(len(recent), len(original))) * 0.5)
    return result


def compare_causes(user_desc, doc_causes):
    if user_desc == "" or user_desc == "null" or user_desc is None:
        return 0
    sum_score = 0
    for phrase in doc_causes:
        sum_score += get_scores(user_desc, [phrase]) * 100
    if len(doc_causes) == 0:
        return 0
    return sum_score / len(doc_causes)


def compare_multiple_data_model(original, recent):
    sim = 0

    if len(recent) == 1 and len(original) == 0:
        try:
            st = recent[0].name
        except AttributeError:
            st = recent[0]
        if st == "Other":
            return 100
    for elem in recent:
        sum_score = 0
        for elem2 in original:
            sum_score += get_scores(elem, [elem2]) * 100
        if len(original) != 0:
            sim += sum_score / len(original)
    if len(recent) == 0:
        return 0
    return sim / len(recent)


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
    try:
        date = date_data[0]["n"]["event_date"]
    except KeyError:
        date = ""
    sources_node = session.run(
        "MATCH (n:Document {attrs})-[:HAS_SOURCE]->(i) return i".format(attrs="{title: \"" + document_title + "\"}"))
    sources_data = sources_node.data()
    sources = [elem["i"]["description"] for elem in sources_data]
    causes_node = session.run(
        "MATCH (n:Document {attrs})-[:HAS_CAUSE]->(i) return i".format(attrs="{title: \"" + document_title + "\"}"))
    causes_data = causes_node.data()
    causes = [elem["i"]["description"] for elem in causes_data]
    event_node = session.run(
        "MATCH (n:Document {attrs})-[:HAS_TOE_CLASS]->(i) return i".format(
            attrs="{title: \"" + document_title + "\"}"))
    event_data = event_node.data()
    event = [elem["i"]["description"] for elem in event_data]
    infrastructure_node = session.run(
        "MATCH (n:Document {attrs})-[:HAS_INFRASTRUCTURE]->(i) return i".format(
            attrs="{title: \"" + document_title + "\"}"))
    infrastructure_data = infrastructure_node.data()
    infrastructure = [elem["i"]["description"] for elem in infrastructure_data]
    detection_node = session.run(
        "MATCH (n:Document {attrs})-[:HAS_DETECTION]->(i) return i".format(
            attrs="{title: \"" + document_title + "\"}"))
    detection_data = detection_node.data()
    detection = [elem["i"]["description"] for elem in detection_data]
    symptom_node = session.run(
        "MATCH (n:Document {attrs})-[:HAS_SYMPTOMS]->(i) return i".format(
            attrs="{title: \"" + document_title + "\"}"))
    symptom_data = symptom_node.data()
    symptom = [elem["i"]["description"] for elem in symptom_data]
    emergency = Emergency(location, contaminants, date, sources, causes, event, infrastructure, detection)
    session.close()
    return emergency
