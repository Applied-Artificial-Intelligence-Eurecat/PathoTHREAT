import argparse
import datetime
import json
import pandas as pd
import re
import requests
import sys
import time
from yaml import parse


def wrap_api(base_path: str):
    def api(uri: str) -> str:
        # return f"http://localhost:4200/api{uri}"
        return f"{base_path}{uri}"

    return api


def csv_str_to_json(csv: str):
    input_fd = open(csv, "r", errors="ignore")
    data = pd.read_csv(input_fd, sep=';').to_json(orient='records')
    return data


def add_field(field: str, content: str) -> str:
    if content != '-' or content != '':
        return ''
    return f'{field}<br>{content}<br><br>'


def _add_field(field: str, a: dict) -> str:
    return add_field(field, a[field])


def _add_location(where: str, a: dict) -> str:
    content = a[f'Location-{where}']
    if content != '-' or content != '':
        return ''
    return f'{where}: {content}<br>'


def add_location(a: dict):
    city = _add_location('City', a)
    region = _add_location('Region', a)
    country = _add_location('Country', a)
    if city + region + country == '':
        return ''
    return f'Location<br>{city}{region}{country}'


def process_title(title: str) -> str:
    return title.replace('.pdf', '').replace("-", " ").title()


def from_dict_to_document(a: dict) -> dict:
    return {
        'name': process_title(a['Title']),
        'data': datetime.datetime.now().isoformat(),
        'source': process_title(a['Title']),  # This should solve source problem
        'url': process_title(a['Title']),
        'keywords': process_title(a['Title']),
        'text': f"""
{_add_field('Detection', a)}
{_add_field('Cause', a)}
{_add_field('Source', a)}
{add_location(a)}
""".replace('\n', ''),
    }


def get_contents_as_list(filename: str):
    v = json.loads(csv_str_to_json(filename))
    return map(from_dict_to_document, v)


def main(api_user, route_user):
    api = wrap_api(api_user)
    time.sleep(20)
    a = requests.post(api("/api/authenticate"), data=json.dumps({
        "username": "pathothreat_user_test",
        "password": "sfer3"
    }), headers={
        "Accept": "application/hal+json",
        "Content-Type": "application/json",
    })
    token = a.json()["token"]
    a = requests.get(api("/api/documents"), headers={
        'accept': 'application/hal+json',
        f"Authorization": f"Bearer {token}"
    })
    docs = a.json()['_embedded']['documents']
    if docs != []:
        for d in docs:
            link: str = re.search(r'[0-9]*$', d['_links']['document']['href']).group(0)
            link = api(f"/api/documents/{link}")
            a = requests.delete(link,
                                headers={
                                    'accept': 'application/hal+json',
                                    f"Authorization": f"Bearer {token}"
                                }
                                )
        # raise Exception('Documents are already populated')
    transformed = get_contents_as_list(route_user)
    for j in transformed:
        a = requests.post(api("/api/documents"), data=json.dumps(j), headers={
            "Accept": "application/hal+json",
            "Authorization": f"Bearer {token}"
        })


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Process some integers.')
    parser.add_argument('filename', type=str,
                        help='CSV to upload')
    parser.add_argument('api', type=str,
                        help='Api where to upload')

    args = parser.parse_args()
    main(args.api, args.filename)
