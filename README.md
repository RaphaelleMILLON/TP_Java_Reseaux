# TP_Java_Reseaux
Report on the Network Java lab

## UDP SERVER
**Step 1**
The objective of this step is to create a UDP server in Java that receives datagrams from clients.

The server listens on a specified port and displays each received message along with the client's IP address and port number.


The project contains one Java classes: UDPServer.java


The UDPServer.java class implements the UDP server. It contains 2 constructors etc 


The default port is 8080. 

PowerShell is the "client" 


![Server running](commande_step_1.png)


![Server running](Powershell_1.png)

We can notice we can connect

**Step 2**
We are using the ncat programm in an other powershell 


![Server running](step2.png)

We can see that the 2 servers are trying to use the same port (8080) at the same time but it is already being used by the first one 

Therefore we close the first powershell and run again and we get :



![Server running](bonstep2I.png)


![Server running](bonstep2ps.png)



## UDP Client

**Step 3**

So for this step we have one powershell that is the server and an other powershell that is the client

![Server running](step3server.png)


![Client running](step3client.png)

The client takes 2 command arguments : the server address (localhost) and the server port (8080)



**Step 4**

We tested the UDP communication between two computers connected to the same network.

One computer acted as the UDP server and listened on port 8080. The second computer acted as the UDP client and sent messages to the server using its IPv4 address.

![Server running](step4moi.png)


![Client running](step4pasmoi.jpeg)



**Step 5**

We verify that the 3 lengh buffer can work

![Verification](step5_3b.png)

We have 2 powershell
![Server](step5longmessage.png)
We write a very long text and we choose a buffer of 64 octets (the smaller one)
![Client](step5messagecoupe.png)

we can notice that the text is shorten because the length of the buffer is way lower than the length of the text. 

As we can see, the client is not informe of the cut and the rest of the text disappeared 



**Step 6**
Compare to the step 5, we take the largest buffer now
We created a test program called UDPTest to send UDP datagrams of increasing sizes to the server, which was started with a receive buffer of 65,507 bytes.


![Server](step6server.png)

The tested datagram sizes were 1,000, 10,000, 65,507 and 65,508 bytes
The first 3 datagrams were successfully sent but for the 65,508 bytes datagram, the operation failed with the error saying the message was larger than the maximum supported size. 

![Client](step6.png)

**Step 7**

Here is the wireshark capture for localhost between 2 powershell (server and client) with protocol : UDP and with the port = 8080
![Wireshark](wiresharkstep7.png)

In the Java programm, we change the int sizes int[] sizes = {10, 10000};

![Wireshark](wiresharkstep7cap2.png)

We sent two datagrams from the client to the server : a small datagram containing 10 bytes and a large datagram containing 10,000 bytes.

On the localhost interface, we have one IP packet for each datagram including the 10,000-byte datagram. This is because the loopback interface does not behave like a standard Ethernet network with an MTU of 1,500 bytes.
![10,000-byte datagram ](step7_checksumlengthetc.png)


**Step 8**
a) The receive buffer is chosen by the receiver because it is the server that decides how much memory it wants to allocate to store incoming datagrams.
The sender does not know the receiver's available memory or buffer size. If a datagram is larger than the receiver's buffer, the received data may be damage.



b)Sending datagrams larger than about 1,400 bytes is not recommended because they may need to be fragmented into several IP packets to travel across the network because of MTU


## Connectionless communication

**Step 9**
a)
So here we send a message from the client to the server before starting the server
![Client](step9_a_client.png)
The server doesn't reveice any message even when we stop sending messages to the server and starting it. 

![Server](step9_a_server.png)



b) we send messages to several different ports, on which no server is listening. In Wireshark, we can see ICMP Destination Unreachable messages, specifically Port Unreachable messages. It means that the UDP datagram reaches a computer but there is no application listening on the destinbation port, so the computer can send an ICMP error message to inform the sender that the port is unreachable 
![Wireshark](step9_a_server.png)


![Ports](step9_a_server.png)



c)

We started and stopped the UDP client twice
the IP address remains the same but the source port changed, therefore, we can confirm that the UDP is connectionless


![Server](step9_c_server.png)


![Client](step9_c_client.png)



**Step 11**

<img width="706" height="153" alt="image" src="https://github.com/user-attachments/assets/c2f334d0-4c1f-4ba4-908c-8fa3a5ae23b7" />

The client prefixes the typed line with its sequence number and sends 0:hello. The server extracts the number and the text, and displays seq=0 -> hello, with the client’s address and port

<img width="749" height="145" alt="image" src="https://github.com/user-attachments/assets/61e15c3f-fe2a-44ed-856b-e2db106130be" />


The server receives them in order (seq=0, seq=1, seq=2), so no [MISSING] or [OUT OF ORDER] message appears. This is expected on localhost, since the datagrams do not cross a real network.

**Step 12**

<img width="703" height="191" alt="image" src="https://github.com/user-attachments/assets/7074fae6-dac7-4fd3-8b7c-bbfd639685ea" />

Each message is sent with its sequence number, and the client waits for the matching acknowledgment (ACK:0, ACK:1, ACK:2). All messages are acknowledged at the first attempt, so no retransmission is needed.

<img width="644" height="196" alt="image" src="https://github.com/user-attachments/assets/f7184848-7c68-4f5c-8156-83bf31239a80" />

After receiving message n, the server sends ACK:n back to the address and port of the sender (127.0.0.1:53416).

<img width="388" height="186" alt="image" src="https://github.com/user-attachments/assets/bcde9628-0bad-49cf-9d7d-ab6ef87dc60b" />

No acknowledgment is received for message 3, so after each 500 ms timeout the client sends the message again. After 5 unsuccessful attempts, it gives up and displays an error. With UDP, the client is not informed that the server is down. It only notices that no acknowledgment comes back.

**Step 13**
a) On localhost

<img width="462" height="36" alt="image" src="https://github.com/user-attachments/assets/ca68e62e-d37a-477c-a394-355e31a7aeb1" />

<img width="644" height="69" alt="image" src="https://github.com/user-attachments/assets/19edc019-1226-4023-9a69-c545b0227bdc" />


The server received all of them (loss rate = 0 %). Both programs run on the same machine, so the datagrams never cross a real network, and the server reads them fast enough to keep its receive buffer from filling up.

b)  between two machines of the room:

Machine A:

<img width="638" height="45" alt="image" src="https://github.com/user-attachments/assets/67be2a13-d5b9-4102-8277-0a553941c625" />

Machine B:


![Machine_B](step13.png)

We tried to run the server on one laptop (IP address 10.10.27.30, ENSEA Wi-Fi network) and the burst client on the other laptop, using the server’s IP address instead of localhost. We checked that the correct IP address was used and that no firewall was blocking the port, yet no datagram was received by the server, so the loss rate could not be measured

c) On localhost again, with a server that waits a few milliseconds (Thread.sleep) after each datagram
received:

<img width="521" height="55" alt="image" src="https://github.com/user-attachments/assets/73ac52e0-71ec-404f-a499-4d3bdaab3f5d" />

<img width="704" height="91" alt="image" src="https://github.com/user-attachments/assets/939abe9c-41c4-4242-b3a3-ec12ea575598" />

We restarted the server with a 1 ms pause (Thread.sleep(1)) after each received datagram, then sent the same burst of 10 000 datagrams. This time the server received only 6 690 of them, a loss rate of 33.10 %

In case a), the server reads fast enough, so the buffer never fills up. In case c), the server reads more slowly than the client sends, so the buffer fills up and the extra datagrams are dropped.

**Step 14**


**Step 16**
We created a Java class called MulticastReceiver to receive UDP multicast messages.

We got into the group : "Joined multicast group"

**Step 17**
a) the three receptors receive the message 
![1](step17_1.png)
![2](step17_2.png)
![3](step17_3.png)
![4](step17_4.png)
























