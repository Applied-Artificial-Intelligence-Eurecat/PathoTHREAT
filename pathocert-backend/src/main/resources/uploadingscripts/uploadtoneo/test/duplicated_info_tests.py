import unittest
import uploadtoneo.resources.ComponentsService.duplicated_info_finder as d

class MainTest(unittest.TestCase):
    def test_double_spaces(self):
        word1 = "water loss"
        word2 = "water  loss"
        self.assertEqual(word1, d.remove_double_spaces(word2))

    def test_all_to_spaces(self):
        word1 = "pipe_break/explosion"
        word2 = "pipe break explosion"
        self.assertEqual(d.all_to_spaces(word1), word2)

    def test_word_2_number_simple(self):
        word = "four deaths"
        self.assertEqual("4 deaths", d.transform_to_numbers(word))

    def test_word_2_number_multiple(self):
        word = "four deaths and twenty dead"
        self.assertEqual("4 deaths and 20 dead", d.transform_to_numbers(word))

    def test_word_2_number_with_and(self):
        word = "two hundred and forty five"
        self.assertEqual("245", d.transform_to_numbers(word))

    def test_word_2_number_between(self):
        word = "between three and ten"
        self.assertEqual("between 3 and 10", d.transform_to_numbers(word))

    def test_alt_words(self):
        word = "different cases of escherichia coli"
        self.assertEqual("different cases of ecoli", d.transform_similar_words(word))

    def test_contains(self):
        word = "243 cases of diarrhea"
        self.assertTrue(d.similarity(word, "two hundred forty three cases"))

if __name__ in "__main__":
    unittest.main()