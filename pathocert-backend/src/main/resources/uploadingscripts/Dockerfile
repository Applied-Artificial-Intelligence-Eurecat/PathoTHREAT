from python:3.7.9 as python
copy . /scripts
RUN apt-get update
RUN apt-get install software-properties-common python3-pip -y
WORKDIR /scripts
RUN pip3 install neo4j
RUN pip3 install Levenshtein
RUN pip3 install pandas
RUN pip3 install requests
RUN pip3 install pyyaml
RUN pip3 install nltk
CMD ["python3", "uploadtoneo/upload_both.py", "expert_graph_jsons/*", "uploadtoneo/realdocs/*"]