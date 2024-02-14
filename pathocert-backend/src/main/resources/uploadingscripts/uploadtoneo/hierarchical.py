import glob
import json
import re

import nltk
import numpy as np
from nltk.corpus import stopwords
from tqdm import tqdm
import requests

nltk.download('stopwords')


def put_all_members(mems):
    members_ordered = []
    for member in mems:
        if type(member) == str:
            members_ordered.append(member)
        elif type(member) == float:
            continue
        else:
            members_ordered.extend(put_all_members(member))
    return members_ordered


def get_scores(main_w, sentences):
    r = requests.post("http://pathocert-model:5000/comparation", json={"main": main_w, "sentences": "|".join(sentences)})

    if r.status_code != 200:
        return 0
    return json.loads(r.text)


def get_distances(main_w, sentences):
    return [(1 / score) ** 2 for score in get_scores(main_w, sentences)]


def compare_string_to_cluster(phrase, other_members):
    score = 0
    has_float_out = 0
    for member in other_members:
        if type(member) == str:
            score += get_scores(phrase, [member])
        elif type(member) == float:
            has_float_out = 1
            continue
        else:
            score_2 = 0
            has_float = 0
            for other_elem in member:
                if type(other_elem) == float:
                    has_float = 1
                    continue
                score_2 += compare_string_to_cluster(phrase, other_elem)
            score += score_2 / (len(member) - has_float)
    return score / (len(other_members) - has_float_out)


def compare_list_to_cluster(elems, other):
    score = 0
    has_float = 0
    for member in elems:
        if type(member) == str:
            score += compare_string_to_cluster(member, other.members)
        elif type(member) == float:
            has_float = 1
            continue
        else:
            score += compare_list_to_cluster(member, other)
    return score / (len(elems) - has_float)


class Tier:
    def __init__(self, title, members=None):
        self.title = title
        if members is None:
            self.members = []
        else:
            self.members = members

    def __str__(self):
        return f"{self.title}"

    def __eq__(self, other):
        if isinstance(other, Tier):
            return self.title == other.title and self.members == other.members
        return False

    def compare(self, other):
        self_members = put_all_members(self.members)
        other_members = put_all_members(other.members)
        self_members.sort(key=len)
        other_members.sort(key=len)
        self_short = self_members[0]
        other_short = other_members[0]
        return get_scores(self_short, [other_short])

    def compare_complete(self, other):
        score = 0
        has_score = 0
        for member in self.members:
            if type(member) == str:
                score += compare_string_to_cluster(member, other.members)
            elif type(member) == float:
                has_score = 1
                continue
            else:
                score += compare_list_to_cluster(member, other)
        return score / (len(self.members) - has_score)


def run_hierarchical(documents_path):
    threshold = 0.8

    data = []
    for file_name in glob.glob(documents_path):
        with open(file_name, "r") as f:
            file_dict = json.loads(f.read())
        if type(file_dict) == list:
            for file_d in file_dict:
                file_title = file_d['title']
                try:
                    if type(file_d['event']) == list:
                        for event in file_d['event']:
                            try:
                                data.append([event, file_title])
                            except KeyError:
                                pass
                    else:
                        try:
                            data.append([file_d['event'], file_title])
                        except KeyError:
                            pass
                except KeyError:
                    pass
        else:
            file_d = file_dict
            file_title = file_d['title']
            try:
                if type(file_d['event']) == list:
                    for event in file_d['event']:
                        try:
                            data.append([event, file_title])
                        except KeyError:
                            pass
                else:
                    try:
                        data.append([file_d['event'], file_title])
                    except KeyError:
                        pass
            except KeyError:
                pass

    data_arr = np.array(data)
    events = data_arr[:, 0]

    clean_phrases = []
    for phrase in events:
        phr = phrase
        for word in stopwords.words('english'):
            phr = phr.replace(f" {word} ", " ")
        clean_phrases.append(re.sub(" +", " ", phr))

    clusters = [Tier(e, members=[e]) for e in clean_phrases]
    while len(clusters) != 1:
        best_score = 0  # Scores entre 0 i 1
        best_combination = []
        new_clusters = []
        for i, cluster in tqdm(enumerate(clusters), colour='blue'):
            for cluster2 in clusters[i:]:
                if cluster != cluster2:
                    cluster_score = cluster.compare(cluster2)
                    if cluster_score > best_score:
                        best_combination = [cluster, cluster2]
                        best_score = cluster_score
        if best_score <= threshold:
            break
        for cluster in clusters:
            if cluster == best_combination[0]:
                cluster.members = [cluster.members, best_combination[1].members, best_score]
                titles = [cluster.title, best_combination[1].title]
                titles.sort(key=len)
                new_title = titles[0]
                cluster.title = new_title
                new_clusters.append(cluster)
            elif cluster == best_combination[1]:
                continue
            else:
                new_clusters.append(cluster)
        clusters = new_clusters

    retval = []
    for c in clusters:
        mems = put_all_members(c.members)
        all_m = []
        for i, c_phrase in enumerate(clean_phrases):
            if c_phrase in mems:
                all_m.append(events[i])
        ttl = events[clean_phrases.index(c.title)]
        retval.append((all_m, ttl))
    return retval
