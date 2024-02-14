from python:3.7.9 as python
copy . /tfidf
RUN apt-get update
RUN apt-get install software-properties-common python3-pip -y
RUN python3 -m pip install --upgrade pip
RUN pip3 install flask
WORKDIR /tfidf
RUN pip3 install -r requirements.txt
CMD ["python3", "tfidf.py"]