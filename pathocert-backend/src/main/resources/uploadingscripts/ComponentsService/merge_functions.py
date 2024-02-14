import re
import requests
import json
import duplicated_info_finder as d
from db_driver import db_driver

similarity_threshold = 0.82


def get_scores(main_w, sentences):
    r = requests.post("http://pathocert-model:5000/comparation", json={"main": main_w, "sentences": "|".join(sentences)})

    if r.status_code != 200:
        return 0
    if type(json.loads(r.text)) == float:
        return [json.loads(r.text)]
    return json.loads(r.text)


def is_unique(data, good_vals):
    for val in good_vals:
        if d.similarity(data, val):
            return False
    return True


def merge_natural_language(combination, element, number_of_activations, titles):
    # Comparar frases amb model
    good_values = []
    with db_driver.session() as session:
        for title in titles:
            result = session.run("MATCH (n:" + element + ")<-[*]-(n2 {title:\"" + title + "\"}) return n")
            for record in result:
                data = record.data()['n']['description']
                if len(good_values) == 0:
                    good_values.append(data)
                elif max(get_scores(data, good_values)) < similarity_threshold:
                    good_values.append(data)
                else:
                    number_of_activations += 1
    good_values = list(set(good_values))
    good_values.sort()
    combination[element] = ",".join(good_values)
    return number_of_activations


def merge_symptom(combination, element, number_of_activations, titles):
    # Comparar noms amb levenshtein o amb levenshtein i model
    good_values = []
    with db_driver.session() as session:
        for title in titles:
            result = session.run("MATCH (n:" + element + ")<-[*]-(n2 {title:\"" + title + "\"}) return n")
            for record in result:
                data = record.data()['n']['name']
                if len(good_values) == 0:
                    good_values.append(data)
                elif is_unique(data, good_values):
                    good_values.append(data)
                else:
                    number_of_activations += 1
    good_values = list(set(good_values))
    good_values.sort()
    combination[element] = ",".join(good_values)
    return number_of_activations


def merge_location(combination, element, titles):
    combination['Location'] = {}
    good_cities = []
    good_regions = []
    good_countries = []
    # Comparar noms amb levenshtein enlloc de amb el model (o amb els 2)
    with db_driver.session() as session:
        for title in titles:
            result = session.run("MATCH (n:" + element + ")<-[*]-(n2 {title:\"" + title + "\"}) return n")
            for record in result:
                try:
                    data = record.data()['n']['city']
                    if len(good_cities) == 0:
                        good_cities.append(data)
                    elif is_unique(data, good_cities):
                        good_cities.append(data)
                except KeyError:
                    pass
                try:
                    data = record.data()['n']['region']
                    if len(good_regions) == 0:
                        good_regions.append(data)
                    elif is_unique(data, good_regions):
                        good_regions.append(data)
                except KeyError:
                    pass
                try:
                    data = record.data()['n']['country']
                    if len(good_countries) == 0:
                        good_countries.append(data)
                    elif is_unique(data, good_countries):
                        good_countries.append(data)
                except KeyError:
                    pass
    combination[element]['city'] = ",".join(good_cities)
    combination[element]['region'] = ",".join(good_regions)
    combination[element]['country'] = ",".join(good_countries)


def join_impact(impacted):
    if len(impacted) == 0:
        return ""
    elif len(impacted) == 1:
        return str(impacted[0])
    else:
        return str(min(impacted)) + "-" + str(max(impacted))


def merge_impact(combination, titles):
    people_ill_l = []
    people_hospitalized_l = []
    people_dead_l = []
    with db_driver.session() as session:
        results = []
        for title in titles:
            result = session.run("MATCH (n:Impact)<-[:HAS_IMPACT]-(n2 {title:\"" + title + "\"}) return n")
            for record in result:
                results.append(record)
        for record in results:
            try:
                people_ill_l.append(int(record.data()['n']['people_ill'].replace(",", "").replace(".", "")))
            except KeyError or ValueError:
                pass
            try:
                people_hospitalized_l.append(int(record.data()['n']['people_hospitalized'].replace(",", "").replace(".", "")))
            except KeyError or ValueError:
                pass
            try:
                people_dead_l.append(int(record.data()['n']['people_dead'].replace(",", "").replace(".", "")))
            except KeyError or ValueError:
                pass
    combination["Impact"] = {}
    combination["Impact"]['people_ill'] = join_impact(people_ill_l)
    combination["Impact"]['people_hospitalized'] = join_impact(people_hospitalized_l)
    combination["Impact"]['people_dead'] = join_impact(people_dead_l)
