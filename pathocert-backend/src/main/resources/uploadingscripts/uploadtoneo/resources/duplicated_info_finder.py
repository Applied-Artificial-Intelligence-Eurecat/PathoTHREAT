import resources.w2n as w2n
import Levenshtein as lv


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
                value = w2n.word_to_num(' '.join(list_slice))
                for elem in list_slice:
                    elem_value = w2n.word_to_num(elem)
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
    for elem in "-_/":
        word = word.replace(elem, " ")
    return word


alt_words = load_csv()


def similarity(word1, word2, max_distance=3):
    word1_c = word1[:]
    word2_c = word2[:]
    word1_c = transform(word1_c)
    word2_c = transform(word2_c)
    if word1_c in word2_c or word2_c in word1_c or lv.distance(word1_c, word2_c) < max_distance:
        return True
    return False


def similarity_numeric(word1, word2):
    word1_c = word1[:]
    word2_c = word2[:]
    word1_c = transform(word1_c)
    word2_c = transform(word2_c)
    if word1_c in word2_c or word2_c in word1_c:
        return 0
    return lv.distance(word1_c, word2_c)


def transform(word):
    if type(word) != str:
        return word
    word_c = word[:]
    word_c = word_c.lower()
    word_c = all_to_spaces(word_c)
    word_c = remove_double_spaces(word_c)
    word_c = transform_to_numbers(word_c)
    word_c = transform_similar_words(word_c)
    word_c = word_c[0].upper() + word_c[1:]
    return word_c
