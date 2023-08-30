import socket

def udpConnexion():
    ip = ""
    port = 9000

    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as s:
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        s.bind((ip, port)) # listen on the port 9000
        print('Le serveur écoute en UDP')

        while True:
            data, client = s.recvfrom(4096)
            print(f'Données : {data.decode("utf-8")} reçues de {client}')
            s.sendto("i'm a poketudiant server".encode('utf-8'),client)