import re

single_keys = ["title", "document_date", "event", "event_date", "event_duration", "people_ill", "people_hospitalized",
               "people_dead", "document_date"]
multiple_keys = ["street", "city", "region", "country", "cause", "source", "detection", "contaminants", "symptoms",
                 "mitigation", "monitoring", "restoration", "prevention", "investigation", "infrastructure"]

separators = [",", ".", " and ", " or "]


def reduce(values):
    maxlen = max([len(v) for v in values])
    for v in values:
        if len(v) == maxlen:
            return v


def separate_value(value):
    # Eliminar parentesis i tot lo de dins
    value = re.sub("[(][a-zA-Z0-9]*[)]", "", value)

    # Eliminar dobles espais que es puguin haver causat
    value = value.replace("  ", " ")

    # Separar per comes, ands, i ors
    results = [value]

    for separator in separators:
        new_results = []
        for result in results:
            new_results.extend(result.split(separator))
        results = new_results

    return [r.strip() for r in results]


def separate(values):
    retval = []
    for value in values:
        retval.extend(separate_value(value))
    return retval


def unify_answers(initial_answers):
    answers = {}
    for key in initial_answers.keys():
        if key in initial_answers:
            if key in single_keys:
                if len(initial_answers[key]) == 1:
                    answers[key] = initial_answers[key]
                else:
                    answers[key] = reduce(initial_answers[key])
            else:
                answers[key] = separate(initial_answers[key])
        for elem in answers[key]:
            if elem == '':
                answers[key].remove(elem)
    return answers
