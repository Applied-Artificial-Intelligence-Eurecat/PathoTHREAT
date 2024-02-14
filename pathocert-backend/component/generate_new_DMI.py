import glob
import json

from unify_answers import unify_answers

keys = ["cause", "detection", "source", "mitigation", "city", "region", "country", "people_dead",
        "people_ill", "people_hospitalized", "monitoring", "contaminants", "symptoms", "prevention", "restoration"]

with open("questions.json", "r", errors="ignore") as f:
    tags = json.loads(f.read())

with open("empty2.json", "r", errors="ignore") as f:
    questions = json.loads(f.read())

with open("Doc-merge-info.csv", "w", encoding='utf-8') as csv_f:
    csv_f.write(
        "Title;Cause;Detection;Source;Mitigation;Location-City;Location-Region;Location-Country;Impact-Dead;Impact-Ill;Impact-Hospitalized;Monitoring;Contaminant;Symptoms;Prevention;Restoration")

    routes = ["C:\\Users\\joel.aumedes\\Projectes\\PathoCERT\\Document Data Extraction\\JSONs\\train\\",
              "C:\\Users\\joel.aumedes\\Projectes\\PathoCERT\\Document Data Extraction\\JSONs\\test\\"]
    for route in routes:
        for doc in glob.glob(f"{route}*"):
            doc_title = doc.replace(route, "").replace(".json", "")
            csv_f.write(f"\n{doc_title};")
            print(doc)
            with open(doc, "r") as f:
                file_dict = json.loads(f.read())

            # Write dictionary values to a file
            values_stored = {}
            for tag in tags:
                for question in tags[tag]:
                    try:
                        value = values_stored.get(tag)
                        if value is None:
                            value = []
                        if file_dict[question] not in value and file_dict[question] != "":
                            if type(file_dict[question]) == list:
                                value.extend(file_dict[question])
                            else:
                                value.append(file_dict[question])
                        if value:
                            values_stored[tag] = value
                    except KeyError:
                        continue
            values_stored = unify_answers(values_stored)
            for val in values_stored:
                if len(values_stored[val]) == 1:
                    values_stored[val] = values_stored[val][0]

            line = ""
            for key in keys:
                value = values_stored.get(key, "")
                if type(value) == list:
                    val = ','.join(value)
                else:
                    val = value
                line += val + ";"
            line = line[:-1]
            csv_f.write(line)
