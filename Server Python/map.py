import enum, os

from player import *

class Map:
    def __init__(self, map):
        self.map = map
        self.width = 0
        self.height = 0
        self.spawns = []

class CaseType(enum.Enum):
    NEUTRAL = 1
    HEALTH = 2
    GRASS = 3
    POKETUDIANT = 4
    RIVAL = 5

def sendMapForAll(game, clients):
    for c in clients:
        game.sendMap(getPlayer(game, c))

def charReplacer(s, newstring, index, nofail=False):
    if not nofail and index not in range(len(s)): # raise an error if index is outside of the string
        raise ValueError("index outside given string")
    if index < 0:  # add it to the beginning
        return newstring + s
    if index > len(s):  # add it to the end
        return s + newstring
    return s[:index] + newstring + s[index + 1:]

def initializeMap(game):
    script_dir = os.path.dirname(__file__) # absolute directory the file is in
    rel_path = "../map.txt"
    abs_file_path = os.path.join(script_dir, rel_path)
    fd = open(abs_file_path, 'r') # initialize the game map
    game.map = Map(fd.read())
    mapSplit = game.map.map.split("\n")
    width = len(mapSplit)-1
    height = len(mapSplit[0])
    game.map.width = width
    game.map.height = height
    game.map.spawns = [(0,0), (width-1,0), (0,height-1), (width-1,height-1)]
    fd.close()