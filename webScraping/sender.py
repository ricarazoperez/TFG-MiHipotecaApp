import os 
import socket


client=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client.connect(("localhost",9999))

file