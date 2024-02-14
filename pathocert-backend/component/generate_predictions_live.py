import json

from colorama import Fore, Style
from colorama import init as colorama_init
from transformers import pipeline


def main():
    print(f"{Fore.LIGHTGREEN_EX}Paste the PATH to the empty questions file (empty for default):{Style.RESET_ALL}")
    empty_path = input()
    if empty_path == "":
        empty_path = "empty2.json"
    print(f"{Fore.LIGHTGREEN_EX}Paste the PATH to the selected TXT article:{Style.RESET_ALL}")
    txt_path = input()

    try:
        text = open(txt_path, "r", errors="ignore").read()
    except FileNotFoundError:
        print(f"{Fore.RED}FILE {txt_path} NOT FOUND!{Style.RESET_ALL}")
        raise FileNotFoundError

    print(f"{Fore.LIGHTMAGENTA_EX}Generating answers...{Style.RESET_ALL}")

    qa = pipeline("question-answering", model="Galahad3x/QAModelForPatho")
    qa2 = pipeline("question-answering", model="deepset/roberta-base-squad2")

    with open(empty_path, "r", errors="ignore") as f:
        questions = json.loads(f.read())

    for question in questions:
        print(question)
        answer = qa(question=question, context=text)
        print("PRETRAINED\t", answer['answer'], "\t", answer['score'])
        answer = qa2(question=question, context=text)
        print("ROBERTABASE\t", answer['answer'], "\t", answer['score'])


if __name__ in "__main__":
    colorama_init()
    main()
