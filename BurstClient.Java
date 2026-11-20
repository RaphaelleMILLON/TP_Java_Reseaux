import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

// Step 13 : envoie N datagrammes numérotés le plus vite possible
// Usage : java BurstClient <server> <port> [N]
public class BurstClient {

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.out.println("Usage: java BurstClient <server> <port> [N]");
            return;
        }

        InetAddress serverAddress = InetAddress.getByName(args[0]);
        int serverPort = Integer.parseInt(args[1]);
        int n = (args.length >= 3) ? Integer.parseInt(args[2]) : 10000;

        DatagramSocket socket = new DatagramSocket();

        for (int i = 0; i < n; i++) {
            // Message "i:N" : numéro + total, pour que le serveur connaisse N
            byte[] data = (i + ":" + n).getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(data, data.length, serverAddress, serverPort));
        }

        System.out.println("Sent " + n + " datagrams");
        socket.close();
    }
}