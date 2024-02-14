import emergency_comparator
import unittest

class MainTest(unittest.TestCase):
    def test_compare_multiple_data_equal(self):
        self.assertEqual(100, emergency_comparator.compare_multiple_data([
            "source contamination", "pipe_break"
        ],[
            "source contamination", "pipe break"
        ]))

    def test_compare_multiple_data_0(self):
        self.assertEqual(0, emergency_comparator.compare_multiple_data([
            "explosion", "pipe_break"
        ],[
            "source contamination"
        ]))

    def test_compare_multiple_data_similar(self):
        # 3 fallos -> 100 - 20*3
        self.assertEqual(40, emergency_comparator.compare_multiple_data([
            "pipe_break"
        ],[
            "pipe broke"
        ]))

    def test_compare_dates(self):
        # 1 any menys
        self.assertEqual(70, emergency_comparator.compare_event_dates("2019-10-18","2020-10-9"))
        # Fa més de 8 anys
        self.assertEqual(0, emergency_comparator.compare_event_dates("2010-10-18","2020-10-9"))
        # Mateix any i mes
        self.assertEqual(100, emergency_comparator.compare_event_dates("2020-10-18","2020-10-9"))
        # Mateix any diferent mes
        self.assertEqual(80, emergency_comparator.compare_event_dates("2020-5-18","2020-10-9"))

    def test_compare_same_location(self):
        location1 = emergency_comparator.Location("Spain", street="Jaume II", city="Lleida", region="Catalunya")
        self.assertEqual(100, emergency_comparator.compare_locations(location1, location1))

    def test_compare_remote_locations(self):
        location1 = emergency_comparator.Location("Spain", street="Jaume II", city="Lleida", region="Catalunya")
        location2 = emergency_comparator.Location("UK")
        self.assertEqual(0, emergency_comparator.compare_locations(location1, location2))

    def test_compare_location_same_level(self):
        location1 = emergency_comparator.Location("Spain", region="Catalunya")
        location2 = emergency_comparator.Location("France", region="Normandy")
        self.assertEqual(50, emergency_comparator.compare_locations(location1, location2))

    def test_compare_location_different_level(self):
        location1 = emergency_comparator.Location("Spain", street="Jaume II", city="Lleida", region="Catalunya")
        location2 = emergency_comparator.Location("Spain", region="Galicia")
        self.assertEqual(35, emergency_comparator.compare_locations(location1, location2))

    def test_same_contaminant(self):
        cont1 = emergency_comparator.Contaminant("ecoli", ["cough", "fever"])
        self.assertEqual(100, emergency_comparator.compare_contaminants([cont1],[cont1]))

    def test_different_contaminant(self):
        cont1 = emergency_comparator.Contaminant("ecoli", ["cough", "fever"])
        cont2 = emergency_comparator.Contaminant("norovirus", ["gastroenteritis"])
        self.assertEqual(0, emergency_comparator.compare_contaminants([cont1],[cont2]))

    def test_similar_contaminant(self):
        cont1 = emergency_comparator.Contaminant("ecoli", ["cough", "fever"])
        cont2 = emergency_comparator.Contaminant("ebola", ["gastroenteritis"])
        # 60 * 0,5 del nom + 0 dels symptoms
        self.assertEqual(30, emergency_comparator.compare_contaminants([cont1],[cont2]))

    def test_similar_contaminant_2(self):
        cont1 = emergency_comparator.Contaminant("ecoli", ["cough", "fever"])
        cont2 = emergency_comparator.Contaminant("ebola", ["cough", "salmonellosis"])
        # 60 * 0,5 del nom + 25 dels symptoms
        self.assertEqual(55, emergency_comparator.compare_contaminants([cont1],[cont2]))

    def test_emergency_compare_same(self):
        lc1 = emergency_comparator.Location("USA", region="South Dakota", city="Sioux Falls")
        ct1 = emergency_comparator.Contaminant("ecoli", ["cough", "fever"])
        em1 = emergency_comparator.Emergency(lc1, [ct1], "1979-1-22", ["pipe break"], ["explosion"])
        self.assertEqual(100, emergency_comparator.compare(em1,em1))

    def test_emergency_compare_different(self):
        lc1 = emergency_comparator.Location("USA")
        ct1 = emergency_comparator.Contaminant("ecoli", ["cough", "fever"])
        em1 = emergency_comparator.Emergency(lc1, [ct1], "1979-1-22", ["pipe break"], ["explosion"])
        lc2 = emergency_comparator.Location("Spain", region="Catalunya", city="Lleida", street="Jaume II")
        ct2 = emergency_comparator.Contaminant("norovirus", ["gastroenteritis"])
        em2 = emergency_comparator.Emergency(lc2, [ct2], "2020-1-22", ["water supply"], ["experiment"])
        self.assertEqual(0, emergency_comparator.compare(em1,em2))

    def test_emergency_compare_similar(self):
        lc1 = emergency_comparator.Location("Thailand", region="Bali")
        ct1 = emergency_comparator.Contaminant("ecoli", ["cough", "fever"])
        em1 = emergency_comparator.Emergency(lc1, [ct1], "2019-1-22", ["water supply"], ["source contamination"])
        lc2 = emergency_comparator.Location("Spain", region="Catalunya", city="Lleida", street="Jaume II")
        ct2 = emergency_comparator.Contaminant("ecoli", ["diarrhea"])
        em2 = emergency_comparator.Emergency(lc2, [ct2], "2020-1-22", ["water supply"], ["experiment", "source contamination"])
        # location -> 10
        # contaminant -> 50 del nom + 0 symptoms
        # date -> 70 diferencia 1 any
        # source -> 100
        # cause -> 50
        # total (10 + 50 + 70 + 100 + 50) / 5 = 
        self.assertEqual(56, emergency_comparator.compare(em1,em2))

if __name__ in "__main__":
    unittest.main()