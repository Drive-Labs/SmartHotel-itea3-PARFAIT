######################################
# clientII_27-05-19.py
# Utilité: ce script vas recevoire des donnée à partire de server.py et répondre à l'utilisateur
# Usage: Ce script est lancé automatiqument par le serveur
# Auteur: Mehdi Nacer KERKAR <mehdi.kerkar@tuta.io>
# Mise à jour le: 27/05/19
######################################
import mysql.connector
from mysql.connector import Error

from datetime import datetime
from datetime import timedelta
import time as t

import socket, ssl
import string, random
import os, sys
import argparse, re

from Cryptodome.Cipher import AES
import base64, hashlib, string, random


#Conversion de Base64 a String
def base64ToString(b):
    return base64.b64decode(b).decode('utf-8', 'ignore') # , 'ignore'
    #  to ignore padding b+ '=' * (-len(b) % 4)

#Fonction qui rend un string on list
def paramToList(b):
	d = base64ToString(b)
	s = d.split()
	return s

#Generation d'un numero de chambre pour example
def cbr_generator(size=5, chars=string.digits):
    return ''.join(random.choice(chars) for _ in range(size))

#Mise en forme de la date
def reformate_date(date): # from "date/hour" to "date hour"
	d = date.split('/')
	return d[0]+" "+d[1]

#Cryptage avec AES
def AESencrypt(message, passphrase, IV, MODE):
	aes = AES.new(passphrase.encode('utf-8'), MODE, IV.encode()) #AES.MODE_CFB
	return base64.b64encode(aes.encrypt(message.encode()))

#Decryptage avec AES
def AESdecrypt(encrypted, passphrase, IV, MODE):
	aes = AES.new(passphrase.encode('utf-8'), MODE, IV.encode())
	return aes.decrypt(base64.b64decode(encrypted))

#Repondre au client avec le Token Encypté
def send_client_Ekey(f, key, a):
	print("send_client_EKey")
	AES_KEY = 'qpFccUqsN3McuSu9eK9jAv0Tcv4YvFLx'
	IV = 'WrWReisa4AumamDY'
	MODE = AES.MODE_CBC
	addr = (a, 1024)		# Reserve a port for your service.
	try:
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)		#open socket 
		s.connect(addr)
		try:
			encKey = AESencrypt(key, AES_KEY, IV, MODE)
			print("ENC KEY:", encKey.decode())
			dec = AESdecrypt(dec, AES_KEY, IV, MODE)
		except:
			print("Encryption Error")
		print("Key: ", key)
		
		data = "<"+f+"> <"+encKey.decode()+">\n"#"<"+f+"> <"+key+"> <"+AESkey+>"
		s.send(data.encode())
		print("Socket Commit\n")
		s.close()
		return True
	except:
		print ("Error Socket Connexion\n")
		return False
	finally:
			s.close()
			print("Socket connection is closed\n")

#Repondre au client avec un message d'erreur
def send_error_client(a, cbr, nc):
	addr = (a, 1024)			# Reserve a port for your service.
	try:
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)		#open socket 
		s.connect(addr)
		data = "<"+nc+"><"+cbr+">\n"
		s.send(data.encode())
		print("Error Socket Commit\n")
		s.close()
		return True
	except:
		print ("Error Socket Connexion\n")
		s.close()
		return False
	finally:
			s.close()
			print("Error Socket connection is closed\n")

#Stockage d'une rerservation
def storeNewReservation(id_cbr, id_user, adate, dd, df, key):
	try:
		cnx = mysql.connector.connect(
			user='root', 
			password='',
			host='127.0.0.1',
			database='milestones')
		print("MySQL Connexion Done\r\n")
		try:
			sql = "CREATE TABLE IF NOT EXISTS reservation  \
				(id_res int NOT NULL AUTO_INCREMENT PRIMARY KEY, id_cbr int NOT NULL, \
				id_user int NOT NULL, date_res datetime NOT NULL, date_debut datetime NOT NULL, \
				date_fin datetime NOT NULL, key_res VARCHAR(40) NOT NULL, stat boolean NOT NULL, \
				FOREIGN KEY (id_user) REFERENCES client(id_user))"
			cursor = cnx.cursor()
			cursor.execute(sql)
			cursor.execute(" INSERT INTO reservation (id_cbr, id_user, date_res, date_debut, date_fin, key_res, stat) VALUES ('{}','{}','{}','{}','{}','{}','{}')"
				.format(id_cbr, id_user, adate, dd, df, key, False) )
			cursor.execute(" COMMIT \n")
			print("DB Commit\n")
			cursor.close()
		except mysql.connector.Error as error :
			cnx.rollback() #rollback if any exception occured
			print("Failed inserting user into table {}".format(error))
	except Error as e :
		print ("Error while connecting to MySQL\n", e)
	finally:
		#closing database connection.
		if(cnx.is_connected()):
			cnx.close()
			print("MySQL connection is closed\n")

#Stockage d'un nouvel utilisateur à la base de donnée "inscription à la plateform"
def storeNewUser(user, password, email, pin):
	try:
		cnx = mysql.connector.connect(
			user='root', 
			password='',
			host='127.0.0.1',
			database='milestones')
		cursor = cnx.cursor()
		print("MySQL Connexion Done\r\n")
		hash = hashlib.sh1(password.encode()).hexdigest()
		try:
			
			sql = ("CREATE TABLE IF NOT EXISTS client (id_user  int NOT NULL AUTO_INCREMENT PRIMARY KEY, user VARCHAR(20) NOT NULL, password VARCHAR(40) NOT NULL, email VARCHAR(50) NOT NULL, pin VARCHAR(4) NOT NULL, dateReg datetime NOT NULL)")
			cursor = cnx.cursor()
			cursor.execute(sql)
			cdate = time.strftime("%Y-%m-%d %H:%M:%S")
			sql = (
				" INSERT INTO client (id_user, user, password, email, pin, dateReg) VALUES ('{}','{}','{}','{}')"
				.format(user, hash, email, pin, cdate)  
				)
			cursor.execute(sql)
			cnx.commit()
			print("Adding user Done")
		except mysql.connector.Error as error :
			cnx.rollback() #rollback if any exception occured
			print("Failed inserting user into table {}".format(error))
	except Error as e :
		print ("Error while connecting to MySQL", e) 

#Ajout d'une reservation
def insertReservation(f, dd, df, id_cbr, addr, id_user, key):
	#dd = pdd[0:10]+" "+pdd[11:19]
	#df = pdf[0:10]+" "+pdf[11:19]
	dd = reformate_date(dd)
	df = reformate_date(df)
	rex = re.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:\d{2}$")
	if f=="add":
		print ("--- function add begin ---\n")
		if rex.match(dd) and rex.match(df) :
			print("<",dd,"> <",df,"> Correct format\n")
			dated = datetime.strptime(dd,"%Y-%m-%d %H:%M:%S")
			datef = datetime.strptime(df,"%Y-%m-%d %H:%M:%S")
			adate = datetime.strptime(datetime.now().strftime("%Y-%m-%d %H:%M:%S"),"%Y-%m-%d %H:%M:%S")#print(adate, type(adate))
			if adate < dated < datef :
				bool = send_client_Ekey(f, key, addr) #'192.168.232.2', f, cbr) #'10.130.12.142',f, cbr)
				if bool == True:
					storeNewReservation(id_cbr, id_user, adate, dated, datef, key)
			else:
				send_error_client(addr, id_cbr, id_user)
				print("Dates are not in order\n")
		else:
			print("<",dd,"> <",df,"> Incorrect format\n")
	elif f == 'clr':
		send_client_Ekey(d1[4], d1[0], "0", "0")
	else:
		print(" No function named :\n",f)

#Premiere analyse du packet pour definire dans quel class nous somme, new user ou new reservation
def pre_reservation(b1, b2, v):
	d1 = paramToList(b1)
	d2 = paramToList(b2)
	if len(d1)==5:
		if v:
			print("<",d1[0],"><",d1[1],"><",d1[2],"><",d1[3],"><",d1[4],">")
			print("<",d2[0],"><",d2[1],">")
		insertReservation(d1[0], d1[1], d1[2],d1[3], d1[4], d2[0], d2[1])
		
	elif len(d1) == 2:
		if v:
			print("<",d1[0],"><",d1[1],">")
			print("<",d2[0],"><",d2[1],"><",d2[2],">")
		bool = send_client(d1[0], d1[1])
		if bool == True:
			storeNewUser(d2[0],d2[1],d2[2],d2[3],d[3])


#Reception des parametres passée par le serveur
def main():
	parser = argparse.ArgumentParser()
	parser.add_argument("data_param", type=str, help="base64 of fct+dd+df+nch+ip or fct+ip")
	parser.add_argument("client_param", type=str, help="base64 f nc+key or user+pass+email+pin")

	parser.add_argument("-v", "--verbose", action="store_true", help="increase output verbosity")
	args = parser.parse_args()
	
	if args.verbose:
	    print("verberose details <{}> <{}>\n".format(args.data_param, args.client_param))
	    pre_reservation(args.data_param, args.client_param, True)
	else:
	    pre_reservation(args.data_param, args.client_param, False)

if __name__ == "__main__":
	main()





'''
L'utilisation de socket SSL

YWRkIDIwMTktMDktMDEvMTI6MDA6MDAgMjAxOS0wOS0wMy8xMjowMDowMCAxIDE5Mi4xNjguNDMuNzc=
MSBpMjBlc3pMN3RKUGoyYlhoWUF2WDdNbnhTV2NWMlRXUw==
# SET VARIABLES
packet, reply = "<packet>SOME_DATA</packet>", ""
HOST, PORT = 'XX.XX.XX.XX', 4434

# CREATE SOCKET
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.settimeout(10)

# WRAP SOCKET
wrappedSocket = ssl.wrap_socket(sock, ssl_version=ssl.PROTOCOL_TLSv1, ciphers="ADH-AES256-SHA")

# CONNECT AND PRINT REPLY
wrappedSocket.connect((HOST, PORT))
wrappedSocket.send(packet)
print wrappedSocket.recv(1280)

# CLOSE SOCKET CONNECTION
wrappedSocket.close()
'''