import socket
import sys
import ipaddress


s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Check if IP address is valid
dest_ip = sys.argv[1]
try:
  ipaddress.IPv4Address(dest_ip)
except ipaddress.AddressValueError:
  sys.exit(1)

# Check if port is valid
dest_port = sys.argv[2]

# Check if port is a number
if not dest_port.isdigit():
  sys.exit(1)

# Check if port is in range
dest_port_int = int(dest_port)
if dest_port_int < 1023 or dest_port_int > 65535:
  sys.exit(1)

s.connect((dest_ip, dest_port_int))

msg = input("")
while True:
    #print(f"Sending: '{msg}'")
    to_send = "\n" if msg == "" else msg  # Send newline for empty input
    s.send(bytes(to_send, 'utf-8'))
    data = s.recv(4096)
    #print(f"Data: '{data}'")
    print(data.decode('utf-8'))
    msg = input("")

s.close()

