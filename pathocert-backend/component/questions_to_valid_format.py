import glob
import json

with open("questions.json", "r", errors="ignore") as f:
    tags = json.loads(f.read())

for file in glob.glob("PathoTHREAT_Data/JSONs/train/*"):
    file_title = file.replace("PathoTHREAT_Data/JSONs/train\\", "").replace(".json", "").title()
    print(file_title)
    with open(file, "r", errors="ignore") as f:
        file_dict = json.loads(f.read())
    # file_dict[pregunta] = resposta
    # values_stored[tag] = resposta
    # tags[tag] = pregunta
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
    for val in values_stored:
        if len(values_stored[val]) == 1:
            values_stored[val] = values_stored[val][0]
    values_stored['title'] = file_title
    with open(f'realdocs/{file_title}.json', "w") as fw:
        fw.write(json.dumps(values_stored))
