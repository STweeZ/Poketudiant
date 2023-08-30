from map import *
from fights import *

import _thread, select

class Game:
    def __init__(self, name):
        self.name = name
        self.map = ""
        self.players = []
    
    def getTypeCase(self, player):
        return self.map.map.split("\n")[player.y][player.x]
    
    def playersPos(self, player):
        for p in self.players:
            if (p != player) and (player.x == p.x) and (player.y == p.y):
                return p
        return False

    def checkPosition(self, player): # check position of the player, in which case he's in
        pos = self.getTypeCase(player)
        p=self.playersPos(player)
        if p:
            return CaseType.RIVAL
        elif pos == "+":
            player.healPoketudiants()
            return CaseType.HEALTH
        elif pos == "*":
            if random.randint(0,9) <= 3:
                return CaseType.POKETUDIANT
            return CaseType.GRASS
        return CaseType.NEUTRAL
    
    def sendMap(self, player): # send the map to all players of the game
        map = self.map
        player.client.send(("map " + str(map.width) + " " + str(map.height) + "\n").encode('utf-8'))
        mapSplit = map.map.split("\n")
        for p in self.players:
            if p.client == player.client:
                mapSplit[p.y] = charReplacer(mapSplit[p.y], str(0), p.x)
            else:
                mapSplit[p.y] = charReplacer(mapSplit[p.y], str(p.nbRival), p.x)
        for line in mapSplit:
            player.client.send((line + "\n").encode('utf-8'))

    def __str__(self):
        return "%d %s" % (len(self.players), self.name)

games = []
maxPlayer = 4

def gameNameExists(name):
    for g in games:
        if str(g.name) == str(name):
            return True
    return False

def printGames(client): # print all games to clients
    client.send(("number of games " + str(len(games)) + "\n").encode('utf-8'))
    for g in games:
        client.send((str(g) + "\n").encode('utf-8'))

def getNextPoketudiant(player): # get next poketudiant in a fight
    for i in range(0,len(player.poketudiants)):
        if player.poketudiants[i].currentHP > 0 :
            return i

def actionEncounter(game,message,client): # action of a player in a fight
    fight = False
    player = getPlayer(game,client)
    for f in fights: # the fight structure of the other player if there is one
        if f.player == player:
            fight = f
    poketudiant = player.poketudiants[fight.poketudiant]
    if message == "leave" and (not fight.rival): # the player wants to leave the fight
        if probaFuite(player.poketudiants[fight.poketudiant],fight.poketudiantSauvage): # probability of escape
            player.client.send(("encounter escape ok\n").encode('utf-8'))
            player.endFight()
            fights.remove(fight) # end of the fight
            return True
        else:
            player.client.send(("encounter escape fail\n").encode('utf-8'))
    elif message == "catch" and (not fight.rival): # the player wants to catch the poketudiant
        if (probaCapture(fight.poketudiantSauvage.currentHP, fight.poketudiantSauvage.maxHP) >= 0.5): # probability of catching it
            player.capturePoketudiant(fight.poketudiantSauvage) # add it to his team
            player.client.send(("encounter catch ok\n").encode('utf-8'))
            player.sendPoketudiants() # send the new team
            player.endFight() # end of the fight
            fights.remove(fight)
            return True
        else:
            player.client.send(("encounter catch fail\n").encode('utf-8'))
    elif message == "switch": # he wants to switch the actual poketudiant, send him the instruction
        fight.action = message
        player.client.send(("encounter enter poketudiant index\n").encode('utf-8'))
        return True
    elif message == "attack1" or message == "attack2": # he wants to attack
        fight.action = message
        if fight.rival: # manage the turns in the case of a fight against a rival
            manageTour(fight)
            return True
        else:
            if fightAttack(message,poketudiant,fight): # attack in the case of a wild poketudiant
                return True
    else:
        player.client.send(("encounter forbidden action\n").encode('utf-8'))
        if fight.rival:
            return True
    if poketudiantRandomAttack(player,poketudiant,fight):
        return True
    player.client.send(("encounter enter action\n").encode('utf-8'))
    player.client.send(("encounter poketudiant player " + poketudiant.variety + " " + str(poketudiant.level) + " " + str(int((poketudiant.currentHP / poketudiant.maxHP) * 100)) + " " + poketudiant.attacks[0].name + " " + poketudiant.attacks[0].type + " " + poketudiant.attacks[1].name + " " + poketudiant.attacks[1].type + "\n").encode('utf-8'))
    player.client.send(("encounter poketudiant opponent " + fight.poketudiantSauvage.variety + " " + str(fight.poketudiantSauvage.level) + " " + str(int((fight.poketudiantSauvage.currentHP / fight.poketudiantSauvage.maxHP) * 100)) + "\n").encode('utf-8'))

def fightAttack(message,poketudiant,fight):
    player = fight.player
    poketudiantAdverse = None
    if fight.rival: # there is a rival in this game
        poketudiantAdverse = fight.rival.poketudiants[fight.poketudiantSauvage]
    else:
        poketudiantAdverse = fight.poketudiantSauvage
    if message == "attack1": # calcul the power of the first attack
            power = calculPuissance(poketudiant.attacks[0].type, poketudiantAdverse.type, poketudiant.attacks[0].power)
    elif message == "attack2":
        power = calculPuissance(poketudiant.attacks[1].type, poketudiantAdverse.type, poketudiant.attacks[1].power)
    poketudiantAdverse.currentHP = int(poketudiantAdverse.currentHP - calculDamagePoketudiants(poketudiant.attack, poketudiant.defence, power))
    poketudiantAdverse.currentHP = 0 if poketudiantAdverse.currentHP < 0 else poketudiantAdverse.currentHP
    fight.hasAttacked = True
    if poketudiantAdverse.currentHP <= 0: # rival poketudiant or wild poketudiant is dead
        if not fight.rival: # there is no rival, there is a wild poketudiant against the player
            player.client.send(("encounter poketudiant player " + poketudiant.variety + " " + str(poketudiant.level) + " " + str(int((poketudiant.currentHP / poketudiant.maxHP) * 100)) + " " + poketudiant.attacks[0].name + " " + poketudiant.attacks[0].type + " " + poketudiant.attacks[1].name + " " + poketudiant.attacks[1].type + "\n").encode('utf-8'))
            player.client.send(("encounter poketudiant opponent " + poketudiantAdverse.variety + " " + str(poketudiantAdverse.level) + " " + str(int((poketudiantAdverse.currentHP / poketudiantAdverse.maxHP) * 100)) + "\n").encode('utf-8'))
            player.client.send(("encounter KO opponent\n").encode('utf-8'))
            player.client.send(("encounter win\n").encode('utf-8'))
            exp = int((0.1 * (poketudiantAdverse.calculExpTotal() + poketudiantAdverse.exp)) / len(fight.participation)) # calcul the xp to earn for each poketudiant in the player team
            for i in fight.participation: # each poketudiant which participated earn xp
                player.poketudiants[i].gainExp(exp, player.client, i)
            player.endFight() # end fight
            fights.remove(fight)
            return True
        else:
            if not fight.rival.checkPoketudiantsStatus(): # the rival doesn't have poketudiant in his team
                sendResults(fight) # send results of the fight
                return True
            else:
                fight2 = None
                for f in fights:
                    if f.rival == fight.player:
                        fight2 = f
                        break
                if fight2:
                    fight2.forceChange = True
                sendMajPoketudiantsFight(fight.player,fight.rival,fight.poketudiant,fight2.poketudiant)
                fight.rival.client.send(("encounter enter poketudiant index\n").encode('utf-8'))
    return False

def sendResults(fight): # send result of the game to the two real player in a fight
    fight2 = None
    for f in fights:
        if f.rival == fight.player:
            fight2 = f
            break
    sendMajPoketudiantsFight(fight.player,fight.rival,fight.poketudiant,fight2.poketudiant)
    fight.player.client.send(("encounter KO opponent\n").encode('utf-8'))
    fight.player.client.send(("encounter win\n").encode('utf-8'))
    fight.player.endFight()
    fight.rival.client.send(("encounter KO player\n").encode('utf-8'))
    fight.rival.client.send(("encounter lose\n").encode('utf-8'))
    fight.rival.healPoketudiants()
    fight.rival.loseFight()
    fight.rival.sendPoketudiants()
    fight.rival.endFight()
    fights.remove(fight2)
    fights.remove(fight)

def manageTour(fight1): # manage turn of players in the fight
    fight2 = None
    for f in fights: # the fight of the second player
        if f.rival == fight1.player:
            fight2 = f
            break
    if not fight2:
        return False
    if fight1.action != "" and (fight2.action == "" or fight2.action == "switch"): # the player can switch first
        if fight1.action == "switch":
            fight1.action = "switched"
        return False
    if fight1.action == "switch" and fight2.action.startswith("attack"): # the first player has already switched so the second can attack
        if (not fight2.hasAttacked) and fightAttack(fight2.action, fight2.player.poketudiants[fight2.poketudiant], fight2):
            return True
    if fight1.action.startswith("attack") and fight2.action == "switched": # you can attack because the second player has already switch
        if fightAttack(fight1.action, fight1.player.poketudiants[fight1.poketudiant], fight1):
            return True
    if fight1.action.startswith("attack") and fight2.action.startswith("attack"): # random player attack first
        if (not fight1.hasAttacked) and fightAttack(fight1.action, fight1.player.poketudiants[fight1.poketudiant], fight1):
            return True
        if (not fight2.hasAttacked) and fightAttack(fight2.action, fight2.player.poketudiants[fight2.poketudiant], fight2):
            return True
    if (not fight1.forceChange) and (not fight2.forceChange):
        sendMajPoketudiantsFight(fight1.player,fight1.rival,fight1.poketudiant,fight2.poketudiant)
        sendActions(fight1.player,fight2.player)
    fight1.action = ""
    fight2.action = ""
    fight1.hasAttacked = False
    fight2.hasAttacked = False

def poketudiantRandomAttack(player,poketudiant,fight):
    nbAttack = random.randint(0,1)
    power = calculPuissance(fight.poketudiantSauvage.attacks[nbAttack].type, poketudiant.type, fight.poketudiantSauvage.attacks[nbAttack].power)
    fight.player.poketudiants[fight.poketudiant].currentHP = int(fight.player.poketudiants[fight.poketudiant].currentHP - calculDamagePoketudiants(poketudiant.attack, poketudiant.defence, power))
    fight.player.poketudiants[fight.poketudiant].currentHP = 0 if fight.player.poketudiants[fight.poketudiant].currentHP < 0 else fight.player.poketudiants[fight.poketudiant].currentHP
    player.client.send(("encounter poketudiant player " + poketudiant.variety + " " + str(poketudiant.level) + " " + str(int((poketudiant.currentHP / poketudiant.maxHP) * 100)) + " " + poketudiant.attacks[0].name + " " + poketudiant.attacks[0].type + " " + poketudiant.attacks[1].name + " " + poketudiant.attacks[1].type + "\n").encode('utf-8'))
    player.client.send(("encounter poketudiant opponent " + fight.poketudiantSauvage.variety + " " + str(fight.poketudiantSauvage.level) + " " + str(int((fight.poketudiantSauvage.currentHP / fight.poketudiantSauvage.maxHP) * 100)) + "\n").encode('utf-8'))
    if fight.player.poketudiants[fight.poketudiant].currentHP <= 0:
        fight.tempo = True
        if player.checkPoketudiantsStatus():
            player.client.send(("encounter enter poketudiant index\n").encode('utf-8'))
            return True
        else:
            player.client.send(("encounter KO player\n").encode('utf-8'))
            player.client.send(("encounter lose\n").encode('utf-8'))
            player.healPoketudiants()
            player.loseFight()
            player.sendPoketudiants()
            player.endFight()
            fights.remove(fight)
            return True
    return False

def manageEncounter(game,data,client): # manage the action of players in a fight
    if data[1].startswith('action') :
        actionEncounter(game,data[1].split(" ",1)[1],client) # an attack
    elif data[1].startswith('poketudiant index') : # change the current poketudiant
        index = int(data[1].split(" ")[2])
        fight = False
        player = getPlayer(game,client)
        for f in fights:
            if f.player == player:
                fight = f
        if index >= len(player.poketudiants): # invalid index
            player.client.send(("encounter invalid poketudiant index\n").encode('utf-8'))
            return False
        if player.poketudiants[index].currentHP > 0: # the poketudiant choosen is not dead
            if fight.action != "switch":
                fight.action = ""
            fight.forceChange = False
            fight.poketudiant = index
            fight2 = None
            for f in fights:
                if f.rival == fight.player:
                    fight2 = f
                    break
            if fight2:
                fight2.poketudiantSauvage = index
            if index not in fight.participation:
                fight.participation.append(index)
        else: # he can't change with a dead poketudiant
            player.client.send(("encounter enter poketudiant index\n").encode('utf-8'))
            return True
        if fight.rival: # the rival turn
            manageTour(fight)
            return True
        poketudiant = player.poketudiants[fight.poketudiant]
        if (not fight.tempo) and poketudiantRandomAttack(player,poketudiant,fight): # the wild poketudiant attack
            return True
        fight.tempo = False
        player.client.send(("encounter enter action\n").encode('utf-8')) # send basic fight message to the player
        player.client.send(("encounter poketudiant player " + poketudiant.variety + " " + str(poketudiant.level) + " " + str(int((poketudiant.currentHP / poketudiant.maxHP) * 100)) + " " + poketudiant.attacks[0].name + " " + poketudiant.attacks[0].type + " " + poketudiant.attacks[1].name + " " + poketudiant.attacks[1].type + "\n").encode('utf-8'))
        player.client.send(("encounter poketudiant opponent " + fight.poketudiantSauvage.variety + " " + str(fight.poketudiantSauvage.level) + " " + str(int((fight.poketudiantSauvage.currentHP / fight.poketudiantSauvage.maxHP) * 100)) + "\n").encode('utf-8'))

def startPoketudiantFight(game,client): # start a fight with a wild poketudiant
    player = getPlayer(game,client)
    player.startFight() # the player is in fight
    player.client.send(("encounter new wild 1\n").encode('utf-8'))
    poketudiantPlayerIndice = getNextPoketudiant(player)
    poketudiantPlayer = player.poketudiants[poketudiantPlayerIndice]
    poketudiantSauvage = poketudiantRandom(poketudiantPlayer.level) # create a random poketudiant
    player.client.send(("encounter poketudiant player " + poketudiantPlayer.variety + " " + str(poketudiantPlayer.level) + " " + str(int((poketudiantPlayer.currentHP / poketudiantPlayer.maxHP) * 100)) + " " + poketudiantPlayer.attacks[0].name + " " + poketudiantPlayer.attacks[0].type + " " + poketudiantPlayer.attacks[1].name + " " + poketudiantPlayer.attacks[1].type + "\n").encode('utf-8'))
    player.client.send(("encounter poketudiant opponent " + poketudiantSauvage.variety + " " + str(poketudiantSauvage.level) + " " + str(int((poketudiantSauvage.currentHP / poketudiantSauvage.maxHP) * 100)) + "\n").encode('utf-8'))
    fights.append(Fight(player,poketudiantPlayerIndice,poketudiantSauvage)) # append the fight in the fight array
    player.client.send(("encounter enter action\n").encode('utf-8'))

def startRivalFight(game,player1): # send beginning message to players in a fight
    player2 = game.playersPos(player1)
    if player2.isInFight():
        return False
    player1.startFight() # they starting to fight
    player2.startFight()
    player1.client.send(("encounter new rival " + str(len(player2.poketudiants)) + "\n").encode('utf-8'))
    player2.client.send(("encounter new rival " + str(len(player1.poketudiants)) + "\n").encode('utf-8'))
    poketudiantPlayer1Indice = getNextPoketudiant(player1) # get the first poketudiant to fight
    poketudiantPlayer2Indice = getNextPoketudiant(player2)
    sendMajPoketudiantsFight(player1,player2,poketudiantPlayer1Indice,poketudiantPlayer2Indice)
    sendActions(player1,player2) # send actions to both players
    fights.append(Fight(player1, poketudiantPlayer1Indice, poketudiantPlayer2Indice, player2)) # append the fights to the fight array
    fights.append(Fight(player2, poketudiantPlayer2Indice, poketudiantPlayer1Indice, player1))
    

def sendMajPoketudiantsFight(player1,player2,poketudiantPlayer1Indice,poketudiantPlayer2Indice): # send the composition of both poketudiant squad of players
    poketudiantPlayer1 = player1.poketudiants[poketudiantPlayer1Indice]
    poketudiantPlayer2 = player2.poketudiants[poketudiantPlayer2Indice]
    player1.client.send(("encounter poketudiant player " + poketudiantPlayer1.variety + " " + str(poketudiantPlayer1.level) + " " + str(int((poketudiantPlayer1.currentHP / poketudiantPlayer1.maxHP) * 100)) + " " + poketudiantPlayer1.attacks[0].name + " " + poketudiantPlayer1.attacks[0].type + " " + poketudiantPlayer1.attacks[1].name + " " + poketudiantPlayer1.attacks[1].type + "\n").encode('utf-8'))
    player1.client.send(("encounter poketudiant opponent " + poketudiantPlayer2.variety + " " + str(poketudiantPlayer2.level) + " " + str(int((poketudiantPlayer2.currentHP / poketudiantPlayer2.maxHP) * 100)) + "\n").encode('utf-8'))
    player2.client.send(("encounter poketudiant player " + poketudiantPlayer2.variety + " " + str(poketudiantPlayer2.level) + " " + str(int((poketudiantPlayer2.currentHP / poketudiantPlayer2.maxHP) * 100)) + " " + poketudiantPlayer2.attacks[0].name + " " + poketudiantPlayer2.attacks[0].type + " " + poketudiantPlayer2.attacks[1].name + " " + poketudiantPlayer2.attacks[1].type + "\n").encode('utf-8'))
    player2.client.send(("encounter poketudiant opponent " + poketudiantPlayer1.variety + " " + str(poketudiantPlayer1.level) + " " + str(int((poketudiantPlayer1.currentHP / poketudiantPlayer1.maxHP) * 100)) + "\n").encode('utf-8'))

def sendActions(player1,player2): # send actions to both players
    player1.client.send(("encounter enter action\n").encode('utf-8'))
    player2.client.send(("encounter enter action\n").encode('utf-8'))
    
def createGame(client, data):
    datas = data.split(" ",2)
    if len(datas) != 3: # [create, game, game data]
        return False
    gameName = datas[2]
    if (not gameNameExists(gameName)) and len(games)<4: # The game does not exists and the parameters are correct
        game = Game(gameName)
        initializeMap(game)
        games.append(game)
        joinGame(client,gameName, 1)
        _thread.start_new_thread(startGame, (len(games) - 1,))
        return True
    else:
        client.send(("cannot create game\n").encode('utf-8'))
        return False

def joinGame(client, gameName, justCreated = 0):
    for g in games:
        if g.name == str(gameName) and maxPlayer > len(g.players): # the name specified is the same and the game is not full
            player = createPlayer(client,g)
            g.players.append(player)
            if not justCreated: # join a game not just created
                client.send(("game joined\n").encode('utf-8'))
            else: # join a game that he just created
                client.send(("game created\n").encode('utf-8'))
            g.sendMap(player) # send the map to the player
            player.sendPoketudiants()
            return True
    client.send(("cannot join game\n").encode('utf-8'))
    return False

def startGame(indice):
    game = games[indice]
    clients = list(map(lambda player: player.client, game.players))
    hasLeft = 0
    while(len(clients)):
        readable,_,_= select.select(clients, [], [], 10)
        if readable:
            data = readable[0].recv(4096)
            if len(data) == 0: # manage the fact that one player can exit the game
                readable[0].close()
                for f in fights: # manage the fact that he wants to exit a game but he's in a fight
                    if f.player.client.fileno() == -1:
                        if f.rival:
                            f.rival.client.send(("encounter win\n").encode('utf-8'))
                            f.rival.endFight()
                            for f2 in fights:
                                if f2.player == f.rival:
                                    fights.remove(f2)
                        fights.remove(f)
                for p in game.players:
                    if p.client.fileno() == -1:
                        game.players.remove(p)
                        hasLeft = 1
                        break
            else:
                dataDecoded = data.decode('utf-8')
                dataDecoded = dataDecoded[:-1] if dataDecoded.endswith("\n") else dataDecoded
                player = getPlayer(game,readable[0])
                print(dataDecoded)
                if (dataDecoded.startswith('map move')): # he wants to move
                    if player.isInFight():
                        readable[0].send(("encounter forbidden action\n").encode('utf-8'))
                    elif movement(dataDecoded,player,game):
                        sendMapForAll(game,clients) # send the new map to all players
                        pos = game.checkPosition(player)
                        if pos == CaseType.POKETUDIANT:
                            startPoketudiantFight(game,readable[0])
                        elif pos == CaseType.RIVAL:
                            startRivalFight(game,getPlayer(game,readable[0]))
                elif (dataDecoded.startswith('encounter')): # he wants to do an action in a fight
                    manageEncounter(game,dataDecoded.split(" ",1), readable[0])
                elif (dataDecoded.startswith('send message')): # he wants to send a message in a game
                    message = dataDecoded.split(" ",2)
                    player.sendMsgChat(clients, message[2])
                elif (dataDecoded.startswith('poketudiant')): # he wants to move a poketudiant in his team
                    if player.isInFight(): # not possible in a fight
                        readable[0].send(("encounter forbidden action\n").encode('utf-8'))
                    else:
                        datas = dataDecoded.split(" ", 2)
                        if poketudiantManage(player, int(datas[1]), datas[2]):
                            player.sendPoketudiants()
                elif (dataDecoded.startswith('exit game')): # he wants to exit a game
                    game.players.remove(getPlayer(readable[0]))
                    listenToClient(readable[0])
        clients = list(map(lambda player: player.client, game.players))
        if hasLeft:
            hasLeft = 0
            sendMapForAll(game,clients)
    for c in clients: # no player in the game = close the game and connections
        c.close()
    games.remove(game)

def listenToClient(client):
    inGame = False
    while(not inGame):
        try:
            readable,_,_= select.select([client], [], [], 60)
            data = client.recv(4096)
            if len(data) == 0:
                print("Client %s déconnecté\n" % client)
                return False
            dataDecoded = data.decode('utf-8')
            dataDecoded = dataDecoded[:-1] if dataDecoded.endswith("\n") else dataDecoded # remove the \n at the end of the line
            print('Données reçues : ', dataDecoded)
            if (dataDecoded.startswith('require game list')): # print games to client
                printGames(client)
            elif (dataDecoded.startswith('create game')): # the client wants to create a game
                if createGame(client, dataDecoded):
                    inGame = True
            elif (dataDecoded.startswith('join game')): # the client wants to join a game
                gameDatas = dataDecoded.split(" ",2)
                if len(gameDatas) != 3:
                    client.send(("cannot join game\n").encode('utf-8'))
                else:
                    if joinGame(client, gameDatas[2]):
                        inGame = True
        except:
            client.close()
            return False
