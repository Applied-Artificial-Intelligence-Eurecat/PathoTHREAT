FROM python:3.10.13-bookworm

COPY requirements.txt ./requirements.txt
RUN python -m pip install -r requirements.txt

COPY . .
CMD ["python3", "opentest.py"]
