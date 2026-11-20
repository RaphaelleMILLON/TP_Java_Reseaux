
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class MulticastReceiver {

    public static void main(String[] args) {
        try {
            // Display available network interfaces
            Enumeration<NetworkInterface> interfaces =
                    NetworkInterface.getNetworkInterfaces();

            System.out.println("Available network interfaces:");

            while (interfaces.hasMoreElements()) {
                NetworkInterface netIf = interfaces.nextElement();
                System.out.println(
                        netIf.getName() + " - " + netIf.getDisplayName()
                );
            }

            if (args.length < 1) {
                System.out.println(
                        "Usage: java MulticastReceiver <interface_name>"
                );
                return;
            }

            // Multicast group address
            InetAddress group = InetAddress.getByName("239.0.0.1");

            // Select the network interface
            NetworkInterface netIf =
                    NetworkInterface.getByName(args[0]);

            if (netIf == null) {
                System.out.println("Network interface not found.");
                return;
            }

            // Create multicast socket
            MulticastSocket socket = new MulticastSocket(8888);

            // Join the multicast group
            socket.joinGroup(
                    new InetSocketAddress(group, 8888),
                    netIf
            );

            System.out.println("Joined multicast group: " + group);
            System.out.println("Waiting for messages...");


            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    socket.leaveGroup(
                            new InetSocketAddress(group, 8888),
                            netIf
                    );
                    System.out.println("Left multicast group.");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }));


            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet =
                        new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                String message = new String(
                        packet.getData(),
                        0,
                        packet.getLength(),
                        java.nio.charset.StandardCharsets.UTF_8
                );

                System.out.println("Received: " + message);
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

