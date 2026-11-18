# TP_Java_Reseaux
Report on the Network Java lab


**Step 1**
The objective of this step is to create a UDP server in Java that receives datagrams from clients.

The server listens on a specified port and displays each received message along with the client's IP address and port number.


The project contains two Java classes: UDPServer.java and Main.java


The UDPServer.java class implements the UDP server. It contains 2 constructors etc 


The Main.java class contains the `main()` method which starts the server.
If a port number is provided as an argument, the server uses that port. Otherwise, the default port 8080 is used.

PowerShell is the "client" and we are using the ncat programm : 


![Server running](IntelliJ_1.png)

We can see thaat the port is 8080 and the running=false is the state before i laughed the main
![Client running](Powershell_1.png)
56090 is the port used by the client to send the message 
