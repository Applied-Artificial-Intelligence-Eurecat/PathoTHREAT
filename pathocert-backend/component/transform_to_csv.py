import glob
import json

from colorama import Fore
from colorama import Style
from colorama import init as colorama_init


def main(base_url=""):
    splits = ['train', 'test']

    id_counter = 0

    for split in splits:
        with open(split + ".tsv", "w") as f:
            f.write("id\ttitle\tcontext\tquestion\tanswer\tanswer_start\n")

            for file_route in glob.glob(f"{base_url}\\TXTs\\{split}\\*"):
                file = file_route.replace(f"{base_url}\\TXTs\\{split}\\", "").replace(".txt", "")
                txt_file = open(file_route, "r", errors="ignore")
                text = txt_file.read()
                text_replaced = text.replace("\n", "[[NEWLINE]]")
                if text.find("\t") != -1:
                    print(f"{Fore.CYAN}Text contains tabs!{Style.RESET_ALL}\n{file}")
                json_file = open(f"{base_url}\\JSONs\\{split}\\{file}.json")
                questions_json = json.load(json_file)
                for question in questions_json:
                    if type(questions_json[question]) == str:
                        answer = questions_json[question]
                        answer_replaced = answer.replace("\n", "[[NEWLINE]]")
                        f.write(f"{id_counter}\t{file}\t{text_replaced}\t{question}\t{answer_replaced}\t")
                        if questions_json[question] == "":
                            f.write("0\n")
                        else:
                            answer_start = text.find(answer)
                            if answer_start == -1:
                                print(
                                    f"{Fore.RED}ANSWER NOT FOUND IN TEXT{Style.RESET_ALL}\nText: {file}\nAnswer:{answer}")
                            else:
                                f.write(f"{answer_start}\n")
                        id_counter += 1
                    elif type(questions_json[question]) == list:
                        for answer in questions_json[question]:
                            answer_replaced = answer.replace("\n", "[[NEWLINE]]")
                            f.write(f"{id_counter}\t{file}\t{text_replaced}\t{question}\t{answer_replaced}\t")
                            if questions_json[question] == "":
                                f.write("0\n")
                            else:
                                answer_start = text.find(answer)
                                if answer_start == -1:
                                    print(
                                        f"{Fore.RED}ANSWER NOT FOUND IN TEXT{Style.RESET_ALL}\nText: {file}\nAnswer:{answer}")
                                else:
                                    f.write(f"{answer_start}\n")
                            id_counter += 1


if __name__ in "__main__":
    colorama_init()
    main()
