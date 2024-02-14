import sys

from colorama import Fore, Style
from colorama import init as colorama_init

import evaluate_performance
import generate_predictions_file
import generate_predictions_live
import train
import transform_to_csv

if __name__ in "__main__":
    colorama_init()

    while True:
        print(f"{Fore.CYAN}Available functions: {Style.RESET_ALL}")
        print(f"{Fore.LIGHTBLUE_EX}")
        print("1. Train a model")
        print("2. Generate live predictions")
        print("3. Generate file predictions")
        print("4. Evaluate model performances")
        print("5. Preprocess files into tsv dataset")
        print(f"{Style.RESET_ALL}")

        if len(sys.argv) >= 2:
            if sys.argv[1] == 'train':
                train.main()
                print(f"{Fore.GREEN}Successful! \n{Style.RESET_ALL}")
                print(f"{Fore.LIGHTWHITE_EX}Exiting...{Style.RESET_ALL}")
                break

        try:
            comm = int(input("Select an option: "))
        except ValueError:
            sys.exit(-1)

        if comm == 1:
            print(f"{Fore.YELLOW}Training is better on the R2D2 server, but proceeding...{Style.RESET_ALL}")
            train.main()
            print(f"{Fore.GREEN}Successful! \n{Style.RESET_ALL}")
        elif comm == 2:
            try:
                generate_predictions_live.main()
            except FileNotFoundError:
                pass
        elif comm == 3:
            generate_predictions_file.main()
        elif comm == 4:
            print(f"{Fore.YELLOW}Evaluating is better on the R2D2 server, but proceeding...{Style.RESET_ALL}")
            evaluate_performance.main()
            print(f"{Fore.GREEN}Successful! \n{Style.RESET_ALL}")
        elif comm == 5:
            baseurl = input("Write the url with the TXTs and JSONs folders: ")
            print(f"{Fore.GREEN}Preprocessing data into TSV datasets...{Style.RESET_ALL}")
            transform_to_csv.main(baseurl)
            print(f"{Fore.GREEN}Successful! \n{Style.RESET_ALL}")
        else:
            print(f"{Fore.LIGHTWHITE_EX}Exiting...{Style.RESET_ALL}")
            break
