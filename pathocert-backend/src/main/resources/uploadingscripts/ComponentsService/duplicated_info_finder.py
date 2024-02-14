import Levenshtein as lv
from string import punctuation

american_number_system = {
    'zero': 0,
    'one': 1,
    'two': 2,
    'three': 3,
    'four': 4,
    'five': 5,
    'six': 6,
    'seven': 7,
    'eight': 8,
    'nine': 9,
    'ten': 10,
    'eleven': 11,
    'twelve': 12,
    'thirteen': 13,
    'fourteen': 14,
    'fifteen': 15,
    'sixteen': 16,
    'seventeen': 17,
    'eighteen': 18,
    'nineteen': 19,
    'twenty': 20,
    'thirty': 30,
    'forty': 40,
    'fifty': 50,
    'sixty': 60,
    'seventy': 70,
    'eighty': 80,
    'ninety': 90,
    'hundred': 100,
    'thousand': 1000,
    'million': 1000000,
    'billion': 1000000000,
    'point': '.'
}

decimal_words = ['zero', 'one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine']


def number_formation(number_words):
    """
    function to form numeric multipliers for million, billion, thousand etc.

    input: list of strings
    return value: integer
    """
    numbers = []
    for number_word in number_words:
        numbers.append(american_number_system[number_word])
    if len(numbers) == 4:
        return (numbers[0] * numbers[1]) + numbers[2] + numbers[3]
    elif len(numbers) == 3:
        return numbers[0] * numbers[1] + numbers[2]
    elif len(numbers) == 2:
        if 100 in numbers:
            return numbers[0] * numbers[1]
        else:
            return numbers[0] + numbers[1]
    else:
        return numbers[0]


def get_decimal_sum(decimal_digit_words):
    """
    function to convert post decimal digit words to numerial digits
    input: list of strings
    output: double
    """
    decimal_number_str = []
    for dec_word in decimal_digit_words:
        if (dec_word not in decimal_words):
            return 0
        else:
            decimal_number_str.append(american_number_system[dec_word])
    final_decimal_string = '0.' + ''.join(map(str, decimal_number_str))
    return float(final_decimal_string)


def word_to_num(number_sentence):
    """
    function to return integer for an input `number_sentence` string
    input: string
    output: int or double or None
    """
    if type(number_sentence) is not str:
        raise ValueError(
            "Type of input is not string! Please enter a valid number word (eg. \'two million twenty three thousand and forty nine\')")

    number_sentence = number_sentence.replace('-', ' ')
    number_sentence = number_sentence.lower()  # converting input to lowercase

    if (number_sentence.isdigit()):  # return the number if user enters a number string
        return int(number_sentence)

    split_words = number_sentence.strip().split()  # strip extra spaces and split sentence into words

    clean_numbers = []
    clean_decimal_numbers = []

    # removing and, & etc.
    for word in split_words:
        if word in american_number_system:
            clean_numbers.append(word)

    # Error message if the user enters invalid input!
    if len(clean_numbers) == 0:
        raise ValueError(
            "No valid number words found! Please enter a valid number word (eg. two million twenty three thousand and forty nine)")

        # Error if user enters million,billion, thousand or decimal point twice
    if clean_numbers.count('thousand') > 1 or clean_numbers.count('million') > 1 or clean_numbers.count(
            'billion') > 1 or clean_numbers.count('point') > 1:
        raise ValueError(
            "Redundant number word! Please enter a valid number word (eg. two million twenty three thousand and forty nine)")

    # separate decimal part of number (if exists)
    if clean_numbers.count('point') == 1:
        clean_decimal_numbers = clean_numbers[clean_numbers.index('point') + 1:]
        clean_numbers = clean_numbers[:clean_numbers.index('point')]

    billion_index = clean_numbers.index('billion') if 'billion' in clean_numbers else -1
    million_index = clean_numbers.index('million') if 'million' in clean_numbers else -1
    thousand_index = clean_numbers.index('thousand') if 'thousand' in clean_numbers else -1

    if (thousand_index > -1 and (thousand_index < million_index or thousand_index < billion_index)) or (
            million_index > -1 and million_index < billion_index):
        raise ValueError(
            "Malformed number! Please enter a valid number word (eg. two million twenty three thousand and forty nine)")

    total_sum = 0  # storing the number to be returned

    if len(clean_numbers) > 0:
        if len(clean_numbers) == 1:
            total_sum += american_number_system[clean_numbers[0]]

        else:
            if billion_index > -1:
                billion_multiplier = number_formation(clean_numbers[0:billion_index])
                total_sum += billion_multiplier * 1000000000

            if million_index > -1:
                if billion_index > -1:
                    million_multiplier = number_formation(clean_numbers[billion_index + 1:million_index])
                else:
                    million_multiplier = number_formation(clean_numbers[0:million_index])
                total_sum += million_multiplier * 1000000

            if thousand_index > -1:
                if million_index > -1:
                    thousand_multiplier = number_formation(clean_numbers[million_index + 1:thousand_index])
                elif billion_index > -1 and million_index == -1:
                    thousand_multiplier = number_formation(clean_numbers[billion_index + 1:thousand_index])
                else:
                    thousand_multiplier = number_formation(clean_numbers[0:thousand_index])
                total_sum += thousand_multiplier * 1000

            if thousand_index > -1 and thousand_index != len(clean_numbers) - 1:
                hundreds = number_formation(clean_numbers[thousand_index + 1:])
            elif million_index > -1 and million_index != len(clean_numbers) - 1:
                hundreds = number_formation(clean_numbers[million_index + 1:])
            elif billion_index > -1 and billion_index != len(clean_numbers) - 1:
                hundreds = number_formation(clean_numbers[billion_index + 1:])
            elif thousand_index == -1 and million_index == -1 and billion_index == -1:
                hundreds = number_formation(clean_numbers)
            else:
                hundreds = 0
            total_sum += hundreds

    # adding decimal part to total_sum (if exists)
    if len(clean_decimal_numbers) > 0:
        decimal_sum = get_decimal_sum(clean_decimal_numbers)
        total_sum += decimal_sum

    return total_sum


def load_csv(filename=r"/scripts/uploadtoneo/resources/alt_words.csv"):
    alt_dict = {}
    try:
        with open(filename, "r") as f:
            for line in f.readlines():
                split_line = line.split(",")
                keyword = split_line[0].lower()
                others = [s.lower() for s in split_line[1:]]
                others = [all_to_spaces(w) for w in others]
                others = [remove_double_spaces(w) for w in others]
                others = [transform_to_numbers(w) for w in others]
                alt_dict[keyword] = others
    except FileNotFoundError:
        pass
    return alt_dict


def remove_double_spaces(word):
    return ' '.join(word.split()).lower()


def transform_to_numbers(word):
    word_list = word.split(" ")
    transformations = []
    for i in range(len(word_list)):
        for j in range(len(word_list) - i):
            list_slice = word_list[j:j + i + 1]
            try:
                value = word_to_num(' '.join(list_slice))
                for elem in list_slice:
                    elem_value = word_to_num(elem)
                transformations.append((' '.join(list_slice), value))
            except ValueError:
                pass
    for transf in transformations[::-1]:
        word = word.replace(transf[0], str(transf[1]))
    split_word = word.split()
    for i, elem in enumerate(split_word):
        try:
            split_word[i] = int(elem)
        except ValueError:
            continue
    split_result = []
    if "between" in split_word:
        return ' '.join([str(e) for e in split_word])
    for i, elem in enumerate(split_word):
        if type(elem) == int:
            try:
                if split_word[i - 1] == "and" or split_word[i + 1] == "and":
                    continue
                split_result.append(str(elem))
            except IndexError:
                split_result.append(str(elem))
        elif elem == "and" and type(split_word[i - 1]) == int and type(split_word[i + 1]) == int:
            split_result.append(str(split_word[i - 1] + split_word[i + 1]))
        elif elem == "and":
            if type(split_word[i - 1]) == int:
                split_result.append(str(split_word[i - 1]))
            split_result.append(elem)
            if type(split_word[i + 1]) == int:
                split_result.append(str(split_word[i + 1]))
        else:
            split_result.append(elem)
    return ' '.join(split_result)


def transform_similar_words(word):
    for key in alt_words.keys():
        for alt in alt_words[key]:
            word = word.replace(alt, key)
    return word


def all_to_spaces(word):
    for elem in punctuation:
        word = word.replace(elem, " ")
    return word.lower()


alt_words = load_csv()


def transform(word):
    if type(word) != str:
        return word
    word_c = word[:]
    word_c = word_c.lower()
    word_c = all_to_spaces(word_c)
    word_c = remove_double_spaces(word_c)
    word_c = transform_to_numbers(word_c)
    word_c = transform_similar_words(word_c)
    try:
        word_c = word_c[0].upper() + word_c[1:]
    except IndexError:
        return ""
    return word_c


def similarity(word1, word2, max_distance=5):
    #
    word1_c = word1[:]
    word2_c = word2[:]
    word1_c = transform(word1_c)
    word2_c = transform(word2_c)
    return word1_c in word2_c or word2_c in word1_c or lv.distance(word1_c, word2_c) < max_distance


def similarity_numeric(word1, word2):
    word1_c = word1[:]
    word2_c = word2[:]
    word1_c = transform(word1_c)
    word2_c = transform(word2_c)
    if word1_c in word2_c or word2_c in word1_c:
        return 0
    return lv.distance(word1_c, word2_c)
