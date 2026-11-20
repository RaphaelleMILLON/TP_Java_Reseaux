import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UDPClient {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: java UDPClient <server> <port>");
            return;
        }

        try {
            InetAddress serverAddress = InetAddress.getByName(args[0]);
            int serverPort = Integer.parseInt(args[1]);

            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            
            socket.setSoTimeout(500);
            int maxAttempts = 5;

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(System.in, StandardCharsets.UTF_8)
            );

            String line;
            int seq = 0; 

            while ((line = in.readLine()) != null) {

                
                String msg = seq + ":" + line;
                byte[] data = msg.getBytes(StandardCharsets.UTF_8);

                DatagramPacket packet = new DatagramPacket(
                        data,
                        data.length,
                        serverAddress,
                        serverPort
                );

      
                boolean acked = false;

                for (int attempt = 1; attempt <= maxAttempts && !acked; attempt++) {

                    socket.send(packet);
                    System.out.println("Sent: " + msg + " (attempt " + attempt + ")");

                    try {
                        byte[] buf = new byte[100];
                        DatagramPacket ackPacket = new DatagramPacket(buf, buf.length);
                        socket.receive(ackPacket); 

                        String ack = new String(ackPacket.getData(), 0,
                                ackPacket.getLength(), StandardCharsets.UTF_8);

                        if (ack.equals("ACK:" + seq)) {
                            System.out.println("Received " + ack);
                            acked = true;
                        }

                    } catch (SocketTimeoutException e) {
                        
                        System.out.println("Timeout for seq=" + seq);
                    }
                }

                if (!acked) {
                    System.out.println("Error: seq=" + seq + " not acknowledged after "
                            + maxAttempts + " attempts");
                }

                seq++; // Step 11 : numéro suivant
            }

            socket.close();

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
