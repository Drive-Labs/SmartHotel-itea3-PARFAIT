from Cryptodome import Random
from Cryptodome.Cipher import AES
import base64, string, random

MODE = AES.MODE_CBC
BLOCK_SIZE = 16
#SEGMENT_SIZE = 128

def key_generator( size=32, chars=string.digits+string.ascii_uppercase+string.ascii_lowercase): #string.printable
    return ''.join(random.choice(chars) for _ in range(size))

def encrypt(message, passphrase, IV):
    # passphrase MUST be 16, 24 or 32 bytes long, how can I do that ?

    #IV = Random.new().read(BLOCK_SIZE)
    aes = AES.new(passphrase.encode('utf-8'), MODE, IV.encode()) #AES.MODE_CFB
    return base64.b64encode(aes.encrypt(message.encode()))

def decrypt(encrypted, passphrase, IV):
    aes = AES.new(passphrase.encode('utf-8'), MODE, IV.encode())
    return aes.decrypt(base64.b64decode(encrypted))



def main():
	KEY='qpFccUqsN3McuSu9eK9jAv0Tcv4YvFLx'
	#KEY = 'xbjdBt9h0IdZrsf0'
	IV = 'WrWReisa4AumamDY'
	msg = "VeT8Otol8GTNp58gLB780jLlThbHxJ0j"#key_generator()+key_generator()

	print("\n\t\tKEY: ", KEY,"\t\tIV: ", IV,"\t\tMODE: ", MODE,"\n")
	print("B64e(IV): ", base64.b64encode(IV.encode()),'\n')
	print("msg: ", msg,'\n')

	#tvwkGYko5so1g6Xm/8Uf9Q==
	enc = encrypt(msg, KEY, IV)
	print("enc: ", enc,'\n')
	print("B64d(enc): ", base64.b64decode(enc),'\n')
	print("HEX[B64d(enc)]: ", base64.b64decode(enc).hex(),'\n')

	dec = decrypt(enc, KEY, IV)
	print("dec: ", dec.decode())

main()