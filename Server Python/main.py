#!/usr/bin/python3

import _thread

from tcpManagement import *
from udpManagement import *

def Main():
    _thread.start_new_thread(udpConnexion, ()) # start an udp connection
    tcpConnexion()

if __name__ == '__main__':
    Main()