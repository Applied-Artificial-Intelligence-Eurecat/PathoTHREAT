import json

from flask import Flask, request
from sentence_transformers import SentenceTransformer, util

app = Flask(__name__)

model = SentenceTransformer('model')


def get_scores(main_w, sentences):
    if len(sentences) == 0:
        return []
    main_embed = model.encode(main_w)
    embeddings = model.encode(sentences)
    # Compute cosine-similarities
    cosine_scores = util.cos_sim(main_embed, embeddings)
    return cosine_scores[0][0].item()


@app.route('/comparation', methods=['POST'])
def compare():
    request_data = str(request.data.decode("ascii", errors="ignore"))
    r_data = json.loads(request_data)

    return str(get_scores(r_data["main"], r_data["sentences"].split("|")))


if __name__ in "__main__":
    app.logger.disabled = True
    app.run(host='0.0.0.0')
