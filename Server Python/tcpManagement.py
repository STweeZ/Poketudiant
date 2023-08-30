import _thread, socket

from game import *

def tcpConnexion():
    ip = ""
    port = 9001

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((ip, port)) # affect the socket to the port 9001
        s.listen() # listen on the socket
        clientid = 1

        try:
            while True:
                print(f'Le serveur écoute en TCP la connection du client n° {clientid}')
                client, address = s.accept() # accept connection with a client
                _thread.start_new_thread(listenToClient, (client,)) # start a tcp connection with him
                clientid += 1
        except KeyboardInterrupt:
            print('...Ok, c\'est terminé pour cette fois-ci...')

