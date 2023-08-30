import random

class Fight:
    def __init__(self, player, poketudiant, poketudiantSauvage, rival=None):
        self.player = player
        self.poketudiant = poketudiant
        self.tempo = False
        self.poketudiantSauvage = poketudiantSauvage
        self.participation = [poketudiant]
        self.rival = rival
        self.action = ""
        self.hasAttacked = False
        self.forceChange = False # force to change the actual poketudiant, because the precedent is dead

def calculDamagePoketudiants(attack, defense, power): # calcul the damage of the attack via statistics and probability
    return random.uniform(0.9,1.1) * (attack / defense) * power

def calculPuissance(type1, type2, power): # calcul the attack power comparing the poketudiants type
    if type1 == "Noisy" and type2 == "Lazy":
        return power*2
    elif type1 == "Lazy" and type2 == "Motivated":
        return power*2
    elif type1 == "Motivated" and type2 == "Noisy":
        return power*2
    elif type1 == "Teacher" and (type2 == "Noisy" or type2 == "Lazy" or type2 == "Motivated"):
        return power*2
    return power

def probaCapture(pvEff, pvMax):
    return 2*max(0.5-(pvEff/pvMax),0)

def probaFuite(poketudiant1, poketudiant2): # probability of escape a fight with a wild poketudiant
    if (poketudiant1.level - poketudiant2.level) >= 3:
        return 1
    elif (poketudiant1.level - poketudiant2.level) <= -3:
        return 0
    elif (poketudiant1.level - poketudiant2.level) == 2:
        if random.uniform(0.0,100.0) <= 90.0:
            return 1
        else :
            return 0
    elif (poketudiant1.level - poketudiant2.level) == -2:
        if random.uniform(0.0,100.0) <= 25.0:
            return 1
        else :
            return 0
    elif (poketudiant1.level - poketudiant2.level) == 1:
        if random.uniform(0.0,100.0) <= 75.0:
            return 1
        else :
            return 0
    elif (poketudiant1.level - poketudiant2.level) == -1:
        if random.uniform(0.0,100.0) <= 40.0:
            return 1
        else :
            return 0
    elif (poketudiant1.level - poketudiant2.level) == 0:
        if random.uniform(0.0,100.0) <= 50.0:
            return 1
        else :
            return 0

fights = []