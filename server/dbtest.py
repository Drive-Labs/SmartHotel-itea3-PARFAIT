#!/usr/bin/python           # This is server.py file
import mysql.connector
from mysql.connector import Error

from datetime import datetime
from datetime import timedelta
import time 
import hashlib




#Ce fichier sert à titre de Tester la base de donnée insert,select

def storeNewUser(user, password, email, pin):
	try:
		cnx = mysql.connector.connect(
			user='root', 
			password='',
			host='127.0.0.1',
			database='milestones')
		cursor = cnx.cursor()
		print("MySQL Connexion Done\r\n")
		hash = hashlib.md5(password.encode()).hexdigest()
		try:
			
			sql = ("CREATE TABLE IF NOT EXISTS client (id_user int NOT NULL AUTO_INCREMENT PRIMARY KEY, user VARCHAR(20) NOT NULL,  password VARCHAR(40) NOT NULL, email VARCHAR(50) NOT NULL, pin VARCHAR(4) NOT NULL, dateReg datetime NOT NULL)")
			cursor = cnx.cursor()
			cursor.execute(sql)
			print("Table client Added")
			cdate = time.strftime("%Y-%m-%d %H:%M:%S")
			sql = (
				" INSERT INTO client (user, password, email, pin, dateReg) VALUES ('{}','{}','{}','{}','{}')"
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

def pin_fromDB(id_user):
	try:
		cnx = mysql.connector.connect(
			user='root', 
			password='',
			host='127.0.0.1',
			database='milestones')
		cursor = cnx.cursor()
		print("MySQL Connexion Done\r\n")
		try:
			
			sql = ("SELECT pin FROM client WHERE id_user = {}".format(id_user))
			cursor = cnx.cursor()
			cursor.execute(sql)
			record = cursor.fetchone()
			value = int(record[0])
			if (record ):
				#print ("user {} : {}".format(id_user,value) )
				return value
			else:
				print ("Wrong User")
			cnx.commit()
			print("Adding user Done")
		except mysql.connector.Error as error :
			cnx.rollback() #rollback if any exception occured
			print("Failed inserting user into table {}".format(error))
	except Error as e :
		print ("Error while connecting to MySQL", e) 


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

def main():
	#storeNewUser("esubalew","1234op","esubalew@fo.rt","1234")
	print(pin_fromDB(4))
	v = pin_fromDB(1)
	print (v)

	storeNewReservation("00001", "1", "2019-08-01 12:00:00", "2019-09-01 12:00:00 ", " 2019-09-03 12:00:00", "fsdfgsdfgfdgfdsfgdsgfd")


main()