import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class BurstServer {

    public static void main(String[] args) throws Exception {

        int port = (args.length >= 1) ? Integer.parseInt(args[0]) : 8080;
        int sleepMs = (args.length >= 2) ? Integer.parseInt(args[1]) : 0;

        DatagramSocket socket = new DatagramSocket(port);
        byte[] buffer = new byte[1024];

        System.out.println("BurstServer on port " + port + ", sleep = " + sleepMs + " ms");

        while (true) {
            int count = 0;
            int total = 0;

            socket.setSoTimeout(0); 
            try {
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String msg = new String(packet.getData(), 0,
                            packet.getLength(), StandardCharsets.UTF_8);
                    total = Integer.parseInt(msg.split(":")[1]); 
                    count++;

                    if (sleepMs > 0) {
                        Thread.sleep(sleepMs); // cas c) : serveur lent
                    }

                    socket.setSoTimeout(2000);
                }
            } catch (SocketTimeoutException e) {
                double loss = 100.0 * (total - count) / total;
                System.out.println("Received " + count + " / " + total
                        + "  -> loss rate = " + String.format("%.2f", loss) + " %");
            }
        }
    }
}
