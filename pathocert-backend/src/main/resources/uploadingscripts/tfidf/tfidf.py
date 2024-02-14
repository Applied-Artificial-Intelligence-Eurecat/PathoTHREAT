import nltk

nltk.download('stopwords')
from nltk.corpus import stopwords
from nltk.stem import LancasterStemmer
from math import log
from tqdm import tqdm
import numpy as np

lst = LancasterStemmer()

from flask import Flask, request

app = Flask(__name__)

stopword = stopwords.words('english')


def transform(list_of_words):
    review = [lst.stem(w) for w in list_of_words if w not in stopword]
    return review


def remove_stopwords(review_text):
    if type(review_text) == list:
        return [w for w in review_text if w not in stopword]
    else:
        return [w for w in review_text.split(' ') if w not in stopword]


# Dictionary with [document_number] = Number of times the max word appears in the document
# Does not need to change
max_word_each_document = {}

# Dictionary with [word] = Number of documents with this word
# Has to be updated every new document
num_docs_containing_word = {}

# List with all the documents
documents = []


def add_document(doctext):
    index = len(documents)
    documents.append(doctext)
    max_w = doctext[0]
    max_c = doctext.count(max_w)
    c_words = [max_w]
    for w in doctext:
        if w not in c_words:
            c = doctext.count(w)
            if c > max_c:
                max_w = w
                max_c = c
            word_c = num_docs_containing_word.get(w, 0)
            word_c += 1
            num_docs_containing_word[w] = word_c
            c_words.append(w)
    max_word_each_document[len(max_word_each_document)] = max_c
    return index


def tf_idf(word, document_index):
    tf = documents[document_index].count(word) / max_word_each_document[document_index]
    if word not in num_docs_containing_word:
        return 0
    idf = log(len(documents) / num_docs_containing_word[word])
    return tf * idf


@app.route("/", methods=["GET"])
def get_some_statistics():
    result = "<h1> Statistics: </h1>\n"
    result += "Total number of documents: " + str(len(documents)) + "\n"
    result += "Most repeated words: \n"
    top_w = {k: v for k, v in sorted(num_docs_containing_word.items(), key=lambda x: x[1], reverse=True)}
    ks = list(top_w.keys())
    for i in range(10):
        try:
            result += str(i + 1) + ": " + ks[i] + " repeated " + str(top_w[ks[i]]) + " times\n"
        except IndexError:
            break
    return result


@app.route("/", methods=["POST"])
def add_new_document():
    doctext = remove_stopwords(str(request.data.decode("ascii", errors="ignore")))
    ind = add_document(doctext)
    scores = {}
    for w in doctext:
        if w not in scores.keys():
            scores[w] = tf_idf(w, ind)
    sorted_scores = {k: v for k, v in sorted(scores.items(), key=lambda x: x[1], reverse=True)}
    result_str = "<h1>Scores: </h1>"
    result_str += "\n"
    for w in sorted_scores:
        result_str += w + ": " + str(sorted_scores[w])
        result_str += "\n"
    return result_str


if __name__ in "__main__":
    app.run(host='0.0.0.0', port=5001)
