import csv, os

poketudiants = []
statistics = []
attacks = []

def initializeRecords(): # import basic files
    templateRecords("poketudiants.csv",poketudiants)
    templateRecords("statistics.csv",statistics)
    templateRecords("attacks.csv",attacks)

def templateRecords(file_path,arr):
    script_dir = os.path.dirname(__file__) # absolute directory the file is in
    rel_path = file_path
    abs_file_path = os.path.join(script_dir, rel_path)
    with open(abs_file_path, 'r') as file:
        csv_file = csv.DictReader(file)
        for row in csv_file:
            arr.append(dict(row))

initializeRecords()
