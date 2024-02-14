from python:3.7.9 as python
copy . /csvtosql
RUN apt-get update
RUN apt-get install software-properties-common python3-pip -y
RUN pip3 install neo4j
RUN pip3 install Levenshtein
RUN pip3 install pandas
RUN pip3 install requests
RUN pip3 install pyyaml
WORKDIR /csvtosql
# Web: "http://pathocert-frontend:80/api"
# Local: "http://localhost:4567"
CMD ["python3", "csvtojson.py", "Doc-merge-info.csv", "http://pathocert-api:4567"]