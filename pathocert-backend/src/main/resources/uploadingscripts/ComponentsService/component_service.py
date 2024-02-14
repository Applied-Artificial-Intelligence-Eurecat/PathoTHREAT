import json
from time import sleep

from flask import Flask, request

from compare import compare_multiple_data, compare_multiple_data_model, from_db_to_emergency
from db_driver import db_driver
from dto import Contaminant
from merge_functions import merge_impact, merge_location, merge_natural_language, merge_symptom

similarity_threshold = 0.82

app = Flask(__name__)

elements_for_combine = ["Location", "Cause", "Source", "Detection", "Impact", "ContaminantD", "SymptomD", "Mitigation",
                        "Monitoring", "Restoration", "Prevention"]


def healthcheck():
    tries = 0
    while tries < 30:
        with db_driver.session() as session:
            try:
                result = session.run("MATCH (n) return n limit 1")
                print("Health check successful")
                break
            except Exception:
                print("Retrying health check...")
                tries += 1
                sleep(10)
    if tries == 30:
        print("Health check failed")
        raise Exception()


@app.route('/merge', methods=['POST'])
def merge_documents():
    healthcheck()
    titles = str(request.data.decode("ascii", errors="ignore")).split("|")
    combination = {}
    number_of_activations = 0
    for element in elements_for_combine:
        if element == "Impact":
            merge_impact(combination, titles)
        elif element == "Location":
            merge_location(combination, element, titles)
        elif element == "SymptomD" or element == "ContaminantD":
            number_of_activations = merge_symptom(combination, element, number_of_activations, titles)
        else:
            number_of_activations = merge_natural_language(combination, element, number_of_activations, titles)
    return combination


@app.route('/compare', methods=['POST'])
def compare_emergency():
    healthcheck()
    # Most errors break the application. I want to know why
    # UTF-8 potser???
    req_data = str(request.data.decode("utf-8", errors="ignore"))
    try:
        ddata = json.loads(req_data)
    except json.decoder.JSONDecodeError:
        print(req_data)

    document = from_db_to_emergency(ddata["document_title"])

    contaminants = [Contaminant(n) for n in ddata["contaminants"]]

    try:
        # String
        toe = ddata["type_of_event"]
    except KeyError:
        toe = ""

    try:
        # List of strings
        infrastructure = ddata["infrastructure"]
    except KeyError:
        infrastructure = ""

    try:
        # List of strings
        detection = [n for n in ddata["detection"]]
    except KeyError:
        detection = ""

    total_value = 0
    # Compare locations using symbolic rules
    # Compare contaminants using Levenshtein algorithm
    total_value += compare_multiple_data(document.contaminants, contaminants) * 0.20
    # Compare Type Of Event
    total_value += compare_multiple_data(document.event, [toe]) * 0.28
    # Compare Infrastructure
    total_value += compare_multiple_data_model(document.infrastructure, [infrastructure]) * 0.22
    # Compare Detection
    total_value += compare_multiple_data_model(document.detection, detection) * 0.2
    return str(min(100, total_value))


if __name__ in "__main__":
    app.run(host='0.0.0.0')
