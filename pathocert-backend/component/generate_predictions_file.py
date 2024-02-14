import glob
import json

from colorama import Fore, Style
from colorama import init as colorama_init
from transformers import pipeline
from tqdm import tqdm
from unify_answers import unify_answers


def main():
    print(f"{Fore.LIGHTGREEN_EX}Paste the PATH to the empty questions file (empty for default):{Style.RESET_ALL}")
    empty_path = input()
    if empty_path == "":
        empty_path = "empty2.json"
    print(f"{Fore.LIGHTGREEN_EX}Paste the PATH to the tags file (empty for default):{Style.RESET_ALL}")
    tags_path = input()
    if tags_path == "":
        tags_path = "questions.json"
    print(f"{Fore.LIGHTGREEN_EX}Paste the PATH to the selected folder of articles:{Style.RESET_ALL}")
    folder_path = input()

    print(f"{Fore.LIGHTMAGENTA_EX}Generating answers...{Style.RESET_ALL}")

    qa = pipeline("question-answering", model="Galahad3x/QAModelForPatho")

    with open(tags_path, "r", errors="ignore") as f:
        tags = json.loads(f.read())

    with open(empty_path, "r", errors="ignore") as f:
        questions = json.loads(f.read())

    for file in glob.glob(f"{folder_path}/*"):
        last_bar_index = file.rfind("\\")
        file_title = file[last_bar_index+1:]
        try:
            with open(file, "r", errors="ignore") as f:
                file_text = f.read()
        except FileNotFoundError:
            print(f"{Fore.RED}FILE {file} NOT FOUND!{Style.RESET_ALL}")
            raise FileNotFoundError
        file_dict = {}
        for question in tqdm(questions):
            answer = qa(question=question, context=file_text)
            if answer['score'] < 0.001:
                file_dict[question] = ""
            else:
                file_dict[question] = answer['answer']

        # Write dictionary values to a file
        values_stored = {}
        for tag in tags:
            for question in tags[tag]:
                try:
                    value = values_stored.get(tag)
                    if value is None:
                        value = []
                    if file_dict[question] not in value and file_dict[question] != "":
                        if type(file_dict[question]) == list:
                            value.extend(file_dict[question])
                        else:
                            value.append(file_dict[question])
                    if value:
                        values_stored[tag] = value
                except KeyError:
                    continue
        values_stored = unify_answers(values_stored)
        for val in values_stored:
            if len(values_stored[val]) == 1:
                values_stored[val] = values_stored[val][0]

        values_stored['title'] = file_title
        with open(f'realdocs/{file_title}.json', "w") as fw:
            fw.write(json.dumps(values_stored))


if __name__ in "__main__":
    colorama_init()
    main()
