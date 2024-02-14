import requests as r
import json

if __name__ in "__main__":
    res = r.post("http://127.0.0.1:5000", data=json.dumps({
        "query": "Who was the first president of the United States?"
    }), headers={"Content-Type": "application/json"})
    print(res)
    res = r.post("http://127.0.0.1:5000", data=json.dumps({
        "query": "Write some code in Python that sends a POST request to a server with a JSON in the body"
    }), headers={"Content-Type": "application/json"})
    print(res)
    res = r.post("http://127.0.0.1:5000", data=json.dumps({
        "query": "Give me some ideas for a sci-fi movie"
    }), headers={"Content-Type": "application/json"})
    print(res)

    res = r.post("http://127.0.0.1:5000/langchain", data=json.dumps({
        "query": "What is the most common cause of drinking water contamination?"
    }), headers={"Content-Type": "application/json"})
    print(res)
    res = r.post("http://127.0.0.1:5000/langchain", data=json.dumps({
        "query": "What contaminants can be found in contaminated drinking water?"
    }), headers={"Content-Type": "application/json"})
    print(res)
