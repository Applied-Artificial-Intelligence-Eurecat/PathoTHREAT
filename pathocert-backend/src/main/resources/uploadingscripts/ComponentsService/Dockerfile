FROM python:3.9.3 as python
RUN apt-get update
RUN apt-get install software-properties-common python3-pip -y
RUN python3 -m pip install --upgrade pip
COPY requirements.txt /merge/requirements.txt
RUN pip3 install -r /merge/requirements.txt
COPY . /merge
WORKDIR /merge
RUN pip3 install requests
CMD ["python3", "component_service.py"]
