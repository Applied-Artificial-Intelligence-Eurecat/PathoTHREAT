from colorama import Fore, Style
from colorama import init as colorama_init
import json

from transformers import pipeline

if __name__ in "__main__":
    colorama_init()

    print(f"{Fore.LIGHTGREEN_EX}Paste the PATH to the empty questions file:{Style.RESET_ALL}")
    empty_path = input()
    print(f"{Fore.LIGHTGREEN_EX}Paste the PATH to the selected TXT article:{Style.RESET_ALL}")
    txt_path = input()

    print(f"{Fore.LIGHTMAGENTA_EX}Generating answers...{Style.RESET_ALL}")

    text = open(txt_path, "r", errors="ignore").read()

    qa = pipeline("question-answering", model="Galahad3x/QAModelForPatho")

    with open(empty_path, "r", errors="ignore") as f:
        questions = json.loads(f.read())

    for question in questions:
        print(question)
        answer = qa(question=question, context=text)
        print("\t", answer['answer'], "\t", answer['score'])



