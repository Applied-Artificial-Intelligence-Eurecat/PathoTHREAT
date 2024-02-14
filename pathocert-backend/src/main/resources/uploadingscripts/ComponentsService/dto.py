class Location:
    def __init__(self, country=None, street=None, city=None, region=None):
        self.street = street
        self.city = city
        self.region = region
        self.country = country

    def level(self):
        if self.street is not None:
            return 0
        elif self.city is not None:
            return 1
        elif self.region is not None:
            return 2
        elif self.country is not None:
            return 3
        else:
            return None


class Contaminant:
    def __init__(self, name, symptoms=None):
        if symptoms is None:
            symptoms = []
        self.name = name
        self.symptoms = symptoms

    def __eq__(self, o: 'Contaminant'):
        if type(o) != Contaminant:
            return False
        else:
            if self.name != o.name:
                return False
            symptoms2 = set(o.symptoms)
            for symp in self.symptoms:
                if symp not in symptoms2:
                    return False
            return True


class Emergency:
    def __init__(self, location, contaminants, date, sources, causes, event, infrastructure, detection):
        self.location = location
        self.contaminants = contaminants
        self.date = date
        self.sources = sources
        self.causes = causes
        self.event = event
        self.infrastructure = infrastructure
        self.detection = detection
