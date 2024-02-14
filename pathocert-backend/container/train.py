# Fine-tune a model using the problem-specific data

import sys

import tensorflow as tf
from colorama import Fore, Style
from colorama import init as colorama_init
from huggingface_hub import login, Repository, get_full_repo_name
from transformers import AutoTokenizer, AutoModelForQuestionAnswering, Trainer, TrainingArguments

import load_datasets


def configure_gpu(skip=False):
    if skip:
        print(f"{Fore.LIGHTYELLOW_EX}Skipping GPU test and configuration...{Style.RESET_ALL}")
    else:
        physical_devices = tf.config.list_physical_devices('GPU')
        if len(physical_devices) == 0:
            print(f"{Fore.RED}NO GPUs DETECTED!\nQuitting the script...{Style.RESET_ALL}")
            sys.exit(-1)
        print(f"{Fore.GREEN}Detected {len(physical_devices)} GPUs...{Style.RESET_ALL}")
        for device in physical_devices:
            tf.config.experimental.set_memory_growth(device, True)


if __name__ in "__main__":
    colorama_init()

    configure_gpu(True)

    login(token="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", add_to_git_credential=False)

    raw_data = load_datasets.load_datasets()

    pretrained_model_name = "deepset/roberta-base-squad2"

    tokenizer = AutoTokenizer.from_pretrained(pretrained_model_name)
    pretrained_model = AutoModelForQuestionAnswering.from_pretrained(pretrained_model_name)
    load_datasets.set_tokenizer(tokenizer)

    train_dataset = raw_data['train'].map(
        load_datasets.preprocess_dataset_train,
        batched=True,
        remove_columns=raw_data['train'].column_names
    )
    print(f"{Fore.LIGHTMAGENTA_EX}Train:\t{Style.RESET_ALL}{len(raw_data['train'])}->{len(train_dataset)}")

    test_dataset = raw_data['test'].map(
        load_datasets.preprocess_dataset_test,
        batched=True,
        remove_columns=raw_data['test'].column_names,
    )
    print(f"{Fore.LIGHTMAGENTA_EX}Test:\t{Style.RESET_ALL}{len(raw_data['test'])}->{len(test_dataset)}")

    # metric = evaluate.load("squad_v2")
    # Definir alguna manera de comprovar performances (potser en un altre script millor)

    model_name = "QAModelForPatho"

    args = TrainingArguments(
        "QAModelForPatho",
        evaluation_strategy="no",
        save_strategy="epoch",
        learning_rate=2e-5,
        num_train_epochs=3,
        weight_decay=0.01,
    )

    trainer = Trainer(
        model=pretrained_model,
        args=args,
        train_dataset=train_dataset,
        eval_dataset=test_dataset,
        tokenizer=tokenizer,
    )
    print(f"{Fore.BLUE}Starting the training process...{Style.RESET_ALL}")
    trainer.train()
    print(f"{Fore.BLUE}Training Complete!{Style.RESET_ALL}")

    repo_name = get_full_repo_name(model_name)
    repo = Repository("QAModel", clone_from=repo_name)

    repo.git_pull()

    trainer.save_model("QAModel")

    repo.push_to_hub(commit_message="Trained model GPUs")
