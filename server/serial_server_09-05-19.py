######################################
# serial_server_09-05-19.py
# Utilité: ce script va intercepter les données communiquées par l'arduino grace au module xbee.
# Usage: python serial_server.py
# Auteur: Mehdi Nacer KERKAR <mehdi.kerkar@tuta.io>
# Mise à jour le: 09/05/19
######################################
import serial
import time
import sys
import string
import datetime

import mysql.connector
from mysql.connector import Error

#sotckage de la requete traité
def store(cmd, key):
	try:
		cnx = mysql.connector.connect(
			user='root', 
			password='',
			host='127.0.0.1',
			database='milestones')
		cursor = cnx.cursor()
		try:
			cdate = time.strftime("%Y-%m-%d %H:%M:%S")
			sql = (
				" INSERT INTO log (cmd, cmd_time, key_log) VALUES ('{}','{}','{}')"
				.format(cmd, cdate, key)  
				)
			cursor.execute(sql)
			cnx.commit()
			print("DB Commit\n")
			cursor.close()
		except mysql.connector.Error as error :
			cnx.rollback() #rollback if any exception occured
			print("Failed inserting record into table {}".format(error))
	except Error as e :
		print ("Error while connecting to MySQL", e)  
	finally:
		#closing database connection.
		if(cnx.is_connected()):
			cnx.close()
			print("MySQL connection is closed\n")


#Interception des tentatives d'accee emise par l'arduino 
def main():
	try:
		serialPort = serial.Serial(
		port = "COM7",
		baudrate=9600,
		bytesize=8, 
		timeout=2, 
		stopbits=serial.STOPBITS_ONE)
		print("Serial Port Opened\r\n")
	except Error as e :
		print ("Error while openning the serial port", e)  
	# Wait until there is data waiting in the serial buffer
	try:
		while serialPort.isOpen():
			serialString = ""
			# Used to hold data coming over UART
			while serialPort.inWaiting() > 0:
				packet = serialPort.readline().decode('utf-8')
				#packet = serialPort.read(1).decode('utf-8')
				sig = packet[0:2]
				if packet != "":
					# Print the contents of the serial data
					if(sig == "NO"):
						print(">> " + sig + " + Received\n")
					elif(sig == "OK"):
						cmd = packet[3:6]
						if(cmd == "RUN"):
							print(">> " + sig+ " Xbee waitting data - "+datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')+"\n")
							#store(cmd,"vvvvvvvvvvvvvvvvv",cursor)
						elif(cmd == "ALW"):
							key = packet[7:27]
							print(">> " +sig+ " Acces Allowed - "+key+" - "+str(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))+"\n")
							store(cmd, key)
						elif(cmd == "DNY"):
							key = packet[7:27]
							print(">> " +sig+ " Acces Denied - "+key+" - "+str(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))+"\n")
							store(cmd, key)
						elif(cmd == "ERE"):
							key = packet[7:27]
							print(">> " +sig+ " Acces Tried - False Key - "+str(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))+"\n")
							#store(cmd, key)
	except:
		print("Serial Port Close\n")

if __name__ == "__main__":
	main()