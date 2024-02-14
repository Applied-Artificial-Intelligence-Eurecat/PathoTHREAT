FROM pytorch/pytorch:1.13.1-cuda11.6-cudnn8-runtime

LABEL org.opencontainers.image.authors="lluis.echeverria@eurecat.org, joel.aumedes@eurecat.org"

RUN apt-get update -y && apt-get install -y libopenmpi-dev libgl1-mesa-glx

ENV PT=/PT
ENV PYTHONPATH=$PT:$PYTHONPATH

WORKDIR $PT

RUN pip install --upgrade pip
RUN apt-get -y update
RUN apt-get -y install git
RUN apt-get -y install git-lfs
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD ["python", "component.py", "train"]
