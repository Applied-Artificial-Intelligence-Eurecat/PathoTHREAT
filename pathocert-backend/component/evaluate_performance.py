import evaluate
from colorama import Fore, Style
from colorama import init as colorama_init
from transformers import AutoModelForQuestionAnswering, AutoTokenizer
from transformers import pipeline

from load_datasets import load_datasets

batch_size = 10

metric = evaluate.load("squad_v2")

# test_dataset = load_datasets()["test"]


def compute_scores(predicted_answers, real_answers):
    theoretical_answers = [{"id": ex["id"], "answers": ex["answers"]} for ex in real_answers]
    return metric.compute(predictions=predicted_answers, references=theoretical_answers)


def create_model_pipeline(model_name, is_local=False):
    if is_local:
        model = AutoModelForQuestionAnswering.from_pretrained(model_name)
        tokenizer = AutoTokenizer.from_pretrained(model_name)
        return pipeline("question-answering", model=model, tokenizer=tokenizer)
    else:
        return pipeline("question-answering", model=model_name)


def print_score(score):
    for key in score:
        print(f"{Fore.LIGHTMAGENTA_EX}{key}:\t{Style.RESET_ALL}{score[key]}")


def calculate_num_batches(dataset):
    num_batches = len(dataset) // batch_size
    if len(dataset) % batch_size != 0:
        num_batches += 1
    return num_batches


def evaluate_model(model_name, batches, is_local=False):
    print(f"{Fore.BLUE}Evaluating model {Fore.LIGHTMAGENTA_EX}{model_name}{Fore.BLUE}...{Style.RESET_ALL}")

    pipe = create_model_pipeline(model_name, is_local)
    thresholds = [0.005, 0.001, 0.0005, 0.0001, 0.00005]
    pipeline_answers = [[], [], [], [], []]

    print(f"{Fore.BLUE}Pipeline created...{Style.RESET_ALL}")
    for batch in range(batches):
        print("|", end="")
        eval_batch = test_dataset[
                     batch * batch_size:batch * batch_size + batch_size]
        # The dataset already handles index out of bounds
        questions = eval_batch["question"]
        contexts = eval_batch["context"]
        answers = eval_batch["answers"]
        ids = eval_batch["id"]

        for i, given_answer in enumerate(pipe(question=questions, context=contexts)):
            for t, threshold in enumerate(thresholds):
                if given_answer['score'] < threshold:
                    prob = 100.0
                else:
                    prob = 0.0
                pipeline_answers[t].append(
                    {"id": ids[i], "prediction_text": given_answer["answer"], "no_answer_probability": prob})
            # S'haura de fer servir per crear els exemples
            # answer_text = answers[i]['text'][0] if len(answers[i]['text']) > 0 else "-"
    print()

    return [compute_scores(ans, test_dataset) for ans in pipeline_answers]


def main():
    n_batches = calculate_num_batches(test_dataset)

    model_names = ["deepset/roberta-base-squad2",
                   "distilbert-base-cased-distilled-squad",
                   "Galahad3x/QAModelForPatho",
                   "microsoft/BiomedNLP-PubMedBERT-base-uncased-abstract-fulltext"]

    model_names = ["microsoft/BiomedNLP-PubMedBERT-base-uncased-abstract-fulltext",
                   "Galahad3x/QAModelForPathoPubMed"]

    for model_name in model_names:
        model_score = evaluate_model(model_name, n_batches, is_local=False)
        print(f"{Fore.LIGHTBLUE_EX}Model name: {Style.RESET_ALL}{model_name}")
        for skr in model_score:
            print_score(skr)


if __name__ in "__main__":
    colorama_init()
    main()
