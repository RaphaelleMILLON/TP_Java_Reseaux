import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class UDPTest {

    public static void main(String[] args) {

        int[] sizes = {10, 10000};

        try {
            InetAddress address = InetAddress.getByName("localhost");

            DatagramSocket socket = new DatagramSocket();

            for (int size : sizes) {

                byte[] data = new byte[size];

                // Remplir le message avec des caractères A
                for (int i = 0; i < size; i++) {
                    data[i] = 'A';
                }

                DatagramPacket packet = new DatagramPacket(
                        data,
                        data.length,
                        address,
                        8080
                );

                socket.send(packet);

                System.out.println(
                        "Sent datagram of size: " + size + " bytes"
                );
            }

            socket.close();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}