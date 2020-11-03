######################################
# killbirth.py
# Utilité: ce script chercher le pid d'un processus donnée, le tuer et le relancer par la suite
# Usage: python server.py
# Auteur: Mehdi Nacer KERKAR <mehdi.kerkar@tuta.io>
# Mise à jour le: 27/05/19
######################################import serial
import time
import sys, os
import string
import datetime

import argparse, re

#Ce script sert a tuer un pid

def findPid():
	f=open("memo.txt", "r")
	if f.mode == 'r':
		contents =f.read()
		c = contents.strip()
		s = c.split()
	return s[1]

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

def killRun(file, b):
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
	if b == True:
		print(cmd)	
	cmd = 'start cmd.exe @cmd /k "python {}\\{}"'.format(os.getcwd(),findRightFile(file))
	#cmd = 'runas /profile /user:ack-pc\\Administrateur cmd.exe @cmd /k "python {}\\{}"'.format(os.getcwd(),findRightFile(file))
	#cmd = 'runas /profile /user:ISAT-PORT-AA\\Administrateur "cmd.exe @cmd /k python {}\\{}"'.format(os.getcwd(),findRightFile(file))
	os.system(cmd)


def main():
	parser = argparse.ArgumentParser()
	parser.add_argument("file", type=str, help="Fichier to kill and to run again")

	parser.add_argument("-v", "--verbose", action="store_true", help="increase output verbosity")
	args = parser.parse_args()
	
	if args.verbose:
		print("verberose details <{}>\n".format(args.file))
		killRun(args.file, True)
	else:
		killRun(args.file,False)

if __name__ == "__main__":
	main()