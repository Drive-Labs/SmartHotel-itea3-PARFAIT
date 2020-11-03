######################################
# server.py
# Utilité: ce script sert à intercepter les requêtes des utilisateurs et les analyser
# Usage: python server.py
# Auteur: Mehdi Nacer KERKAR <mehdi.kerkar@tuta.io>
# Mise à jour le: 27/05/19
######################################
import mysql.connector
from mysql.connector import Error
import socket      # Import socket module
import sys, os
import threading 
import base64, string
import hashlib, random

#Generateur de Cle
def key_generator( size=32, chars=string.digits+string.ascii_uppercase+string.ascii_lowercase): #string.printable
    return ''.join(random.choice(chars) for _ in range(size))

#Fonction de Hashage MD58
def md5(key, pin):
	return hashlib.md5((key+str(pin)).encode()).hexdigest()

#Generateur de pin TEST
def pin_generator(size=4, chars=string.digits):
    return ''.join(random.choice(chars) for _ in range(size))

#Retrouver le pin de l'utilisateur qui demande une reservation
def pin_fromDB(id_user):
	try:
		cnx = mysql.connector.connect(
			user='root', 
			password='',
			host='127.0.0.1',
			database='milestones')
		cursor = cnx.cursor()
		print("MySQL Connexion Done\r\n")
		#hash = hashlib.md5(password.encode()).hexdigest()
		try:		
			sql = ("SELECT pin FROM client WHERE id_user = {}".format(id_user))
			cursor = cnx.cursor()
			cursor.execute(sql, id_user)
			record = cursor.fetchone()
			value = int(record[0])
			if (record):
				print ("column value: ", value)
				return value
		except mysql.connector.Error as error :
			print("Failed retreiving data {}".format(error))
	except Error as e :
		print ("Error while connecting to MySQL", e) 

#Validation de l'adres IP
def is_valid_ip(ip):
    m = re.match(r"^(\d{1,3}\.){3}\d{1,3}$", ip)
    return bool(m) and all(map(lambda n: 0 <= int(n) <= 255, m.groups()))

#Convertion d'un string à du Base64
def stringToBase64(s):
    return base64.b64encode(s.encode('utf-8'))

#Convertion d'un Base64 à un string
def base64ToString(b):
    return base64.b64decode(b).decode('utf-8')

#0 partire d'un string à une list
def paramToList(str):
	d = base64ToString(str)
	return d.split()

#Socket indiquant la supression de tout utilisateurs sur l'arduino
def clear_device():
	b3 = stringToBase64("clr"+" 0").decode() #fct + key
	try:
		cmd = "python {}\\serial_sender_15-05-19.py -v {}".format(os.getcwd(), b3)
		os.system(cmd) # send to the IoT device a clear eeprom !
	except:
		print("Error Sending CLR to smart lock\n")

#Socket indiquant vouloir voir la list de client actuel sur Arduino,
def itr_device():
	b3 = stringToBase64("itr"+" 0").decode() #fct + key
	try:
		cmd = "python {}\\serial_sender_15-05-19.py -v {}".format(os.getcwd(), b3)
		os.system(cmd) # send to the IoT device a clear eeprom !
	except:
		print("Error Sending ITR to smart lock\n")

#Socket indiquant une inscription en cours pas encore terminé
def registration(data, ip):
	pin = pin_generator()
	print("registration of a new user")
	if data[0] == 'reg':
		b1 = stringToBase64(data[0] +" "+ip).decode() # fct+ip
		b2 = stringToBase64(data[1]+" "+data[2]+" "+data[3]+" "+pin).decode() # user+password+email+pin
		print(data[0]+" "+data[1]+" "+data[2]+" "+data[3]+" "+ip)
		try:
			cmd = "python {}\\client_27-05-19.py -v {} {}".format(os.getcwd(), b1, b2)
			os.system(cmd) # send a answer to the user
			storeNewUser(data[1], data[2], data[3]) # storing the new user in database			
		except:
			print("Error Sending to mobile client\n")	

	else:
		print("Paramameters false\n")

#Socket indiquant une reservation puis la reponse ce feras via le script client
def reservation(data, ip):
	print("data[4]:",data[4])
	pin = pin_fromDB(data[4])
	key = key_generator()
	HAkey = md5(key, pin)
	print("booking for the user n:", data[4])
	if len(data[1])== 19 and len(data[2])== 19:
		b1 = stringToBase64(data[0]+" "+data[1]+" "+data[2]+" "+data[3]+" "+ip).decode() # fct + dd + df + RoomNbr + ip
		b2 = stringToBase64(data[4]+" "+key).decode() # clientNbr + Key
		b3 = stringToBase64(data[0]+" "+HAkey).decode() # fct + HAkey
		print(data[0]+" "+data[1]+" "+data[2]+" "+data[3]+" "+ip)
		try:
			#send the PIN to the user by email ???
			cmd = "python {}\\clientII_27-05-19.py -v {} {}".format(os.getcwd(), b1, b2)
			os.system(cmd)
		except:
			print("Error Sending to mobile client\n")
		try:

			cmd = "python {}\\serial_sender_15-05-19.py -v {}".format(os.getcwd(), b3)
			os.system(cmd)
		except:
			print("Error Sending to IoT device\n")
	else:
		print("Paramameters false\n")
		#add 2019-05-16/12:00:00 2019-05-20/12:00:00

#Lors d'une erreur de donnée ou de requet
def autre(d):
	print ('No Data or Insufissant\n')
	if d == '<q':
		sock.close()  
		conn.close()
		sys.exit()
	elif d == '<r':
		sock.close()  
		conn.close() 
		cmd = "start python {}\\serverII_27-05-19.py".format(os.getcwd())
		os.system(cmd)
		sys.exit()

#defragmentation de la socket pour savoir la demande de l'utilisateur
def defragSocket(dataB64, ip):
	data = paramToList(dataB64)
	print (len(data),"> {} from {}\n".format(data, ip) )
	if data: 
		if len(data) == 5:
			if data[0] == 'add':
				reservation(data, ip)
				return True
			elif data[0] == 'clr': # A suprimer plus tard
				clear_device()
				return True
			elif data[0] == 'itr': 
				itr_device()
				return True
			else:
				print("Paramameters false\n")
				return False	
		elif len(data) == 4:
			registration(data, ip)
			return True
		else:
			autre(data[1])
			return True
	else:
		print("Error No Data\n")
		return False

#Main interception de la demande du client
def main():
	# Create a TCP/IP socket
	sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
	addr = (socket.gethostbyname(socket.gethostname()), 1009)
	print ('Starting up on %s port %s' % addr, '\n')
	try:
		sock.bind(addr)        # Bind to the port
	except socket.error as error :
		print("Bind Failed, Error Code:", error)
		sys.exit()
	print ("Socket Bind Sucess - Server Listning now !\n")
	sock.listen(10)
	try:
		while True:
			print ("# while true...")
			try:
				print ("# try...\n")
				conn, addr= sock.accept()    # Wait for a connection
				print ("# sock.accept()...!\n")
				ip = addr[0]
				print ("# ip = addr[0]...\n")
			except KeyboardInterrupt:
				print ('Connexon failed Maissa!!!!\n')
			try:
				print ('Connection with', ip,':',addr[1], '\n')         
				while True:    # Receive the data in small chunks and retransmit it
					dataB64, addr_rec= conn.recvfrom(1024)
					if (defragSocket(dataB64, ip) == False):
						break
			finally:
				print ('Connexion Closed\n')   # Clean up the connection
	except KeyboardInterrupt:
		print ('Server Crashed\n')
		sock.close()  
		conn.close() 
		sys.exit()

if __name__ == "__main__":
	main()


