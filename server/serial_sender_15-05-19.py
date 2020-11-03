######################################
# serial_sender_15-05-19.py.py
# Utilité: ce script vas recevoire des données à partire de server.py et répondre à l'arduino
# Usage: est lancé automatiquement par server.py
# Auteur: Mehdi Nacer KERKAR <mehdi.kerkar@tuta.io>
# Mise à jour le: 15/05/19
######################################
import serial
import time
import sys, os
import string
import datetime

import argparse, re
import base64

import mysql.connector
from mysql.connector import Error

#Conversion de base64 a string
def base64ToString(b):
	return base64.b64decode(b).decode('utf-8', 'ignore') # , 'ignore'
	#  to ignore padding b+ '=' * (-len(b) % 4)

#De string a list
def paramToList(b):
	d = base64ToString(b)
	s = d.split()
	return s

#Retrouver le pid du bon processus
def findPid():
	f=open("memo.txt", "r")
	if f.mode == 'r':
		contents =f.read()
		c = contents.strip()
		s = c.split()
	return s[1]

#Trouver le bon script à relancer
def findRightFile(file):
	cmd = "ls {} > dir.txt".format(os.getcwd())
	os.system(cmd)
	f=open("dir.txt", "r")
	if f.mode == 'r':
		contents =f.read()
		c = contents.strip()
		s = c.split()
	for i in range(len(s)):
		if file in s[i]:
			return s[i]

#Tuez le prcessus qui utilise serial_server
def killProcess(file, b):
	cmd = 'tasklist /nh /v /fi "windowtitle eq Admin*" | find "{}" > memo.txt'.format(file)
	if os.system(cmd):
		cmd = 'tasklist /nh /v /fi "windowtitle eq {}" | find "{}" > memo.txt'.format('C:\\Windows\\system32\\cmd.exe*',file)
		os.system(cmd)
	if b == True:
		print(cmd)
	pid = findPid()
	if b == True:
		print(pid)
	cmd = "taskkill /pid {}".format(pid)
	os.system(cmd)

#Relancer serial_server mais pour ça il faut verifier le nom exact du fichier
def runProcess(file,b):
	cmd = 'start cmd.exe @cmd /k "python {}\\{}"'.format(os.getcwd(),findRightFile(file))
	#cmd = 'runas /profile /user:ISAT-PORT-AA\\Administrateur "cmd.exe @cmd /k python {}\\{}"'.format(os.getcwd(),findRightFile(file))
	if b == True:
		print(cmd)
	os.system(cmd)
		
def store(cmd, key):
	try:
		cnx = mysql.connector.connect(
			user='root', 
			password='',
			host='127.0.0.1',
			database='milestones')
		cursor = cnx.cursor()
		print("MySQL Connexion Done\r\n")
		try:
			cdate = time.strftime("%Y-%m-%d %H:%M:%S")
			sql = (
				" INSERT INTO log (cmd, cmd_time, key_log) VALUES ('{}','{}','{}')"
				.format(cmd, cdate, key)  
				)
			cursor.execute(sql)
			cnx.commit()
			print("Adding record Done")
		except mysql.connector.Error as error :
			cnx.rollback() #rollback if any exception occured
			print("Failed inserting record into table {}".format(error))
	except Error as e :
		print ("Error while connecting to MySQL", e) 


#Reponse à l'arduino, mais comme manque de materiels un suel port COM7, il faut
#fermer le port qui est utilisé par serial_server RX, l'utiliser en tant que TX trtansmetre 
#et puis relancer serial_server
def sendToDevice(fct, HAKey, file, b):
	killProcess(file, b)
	time.sleep( 1 )
	try:
		serialPort = serial.Serial(
		port = "COM7",
		baudrate=9600,
		bytesize=8, 
		timeout=2, 
		stopbits=serial.STOPBITS_ONE)
		print("Serial Port Opened\r\n")
		try:
			if (serialPort.isOpen()):
				# get packet from the server side
				if fct == 'add':
					print("Add Function in procees\n")	
					store(fct, HAKey)
					packet='>ADD:'+HAKey+'<'
					serialPort.write(packet.encode()+ b'\n')
				else:
					if fct == "itr":
							print("Iteration Function in procees")
							packet='>ITR<'
							serialPort.write(packet.encode()+ b'\n')
					elif fct == "clr":
							print("Clear Function in procees")
							packet='>CLR<'
							serialPort.write(packet.encode()+ b'\n')
							store(fct, "0")
					elif fct == "rbt":
							print("Reboot Function in procees")
							serialPort.write('>RBT:0<'+ b'\n')
		except Error as e :
			print ("Error while sending from the serial port", e)  
	except Error as e :
		print ("Error while openning the serial port", e)
	serialPort.close()
	runProcess(file, b)

#Avant reponse
def pre_sending(b1, v):
	d1 = paramToList(b1)
	if v:
		print("<",d1[0],"><",d1[1],">")
	sendToDevice(d1[0], d1[1], "serial_server_09", v)

#reception des donnée envoyé par le server.py
def main():
	parser = argparse.ArgumentParser()
	parser.add_argument("data_param", type=str, help="base64 of fct+HAkey")

	parser.add_argument("-v", "--verbose", action="store_true", help="increase output verbosity")
	args = parser.parse_args()
	
	if args.verbose:
		print("verberose details <{}>\n".format(args.data_param))
		pre_sending(args.data_param, True)
	else:
		pre_sending(args.data_param, False)

if __name__ == "__main__":
	main()