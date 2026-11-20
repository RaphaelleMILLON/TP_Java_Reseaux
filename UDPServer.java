import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class UDPServer {

    private int port;
    private int bufferSize;
    private double dropRate;    
    private DatagramSocket socket;
    private boolean running;
    private int expectedSeq = 0; 

    public UDPServer(int port, int bufferSize, double dropRate) {
        this.port = port;
        this.bufferSize = bufferSize;
        this.dropRate = dropRate;
        this.running = false;
    }

    public UDPServer(int port, int bufferSize) {
        this(port, bufferSize, 0.0);
    }

    public UDPServer(int port) {
        this(port, 1024);
    }

    public UDPServer() {
        this(8080, 1024);
    }

    public void launch() {
        try {
            socket = new DatagramSocket(port);
            running = true;

            System.out.println("UDP Server started on port " + port);
            System.out.println("Receive buffer size: " + bufferSize);
            System.out.println("Drop rate: " + dropRate);

            byte[] buffer = new byte[bufferSize];

            while (running) {
                DatagramPacket packet =
                        new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                String msg = new String(
                        packet.getData(),
                        0,
                        packet.getLength(),
                        StandardCharsets.UTF_8
                );

                String clientAddress =
                        packet.getAddress().getHostAddress();

                int clientPort = packet.getPort();

                if (Math.random() < dropRate) {
                    System.out.println("[DROPPED MESSAGE] " + msg);
                    continue; // on fait comme si rien n'était arrivé (pas d'ACK)
                }

                int sep = msg.indexOf(':');
                if (sep == -1) {
                    System.out.println("Message without number: " + msg);
                    continue;
                }
                int seq = Integer.parseInt(msg.substring(0, sep));
                String text = msg.substring(sep + 1);

                if (seq == expectedSeq) {
                    System.out.println(clientAddress + ":" + clientPort + " seq=" + seq + " -> " + text);
                    expectedSeq = seq + 1;

                } else if (seq > expectedSeq) {
                    System.out.println("[MISSING] seq " + expectedSeq + " to " + (seq - 1));
                    System.out.println(clientAddress + ":" + clientPort + " seq=" + seq + " -> " + text);
                    expectedSeq = seq + 1;

                } else {
                    System.out.println("[DUPLICATE] seq=" + seq + " -> " + text + " (ignored)");
                }

                if (Math.random() < dropRate) {
                    System.out.println("  [DROPPED ACK:" + seq + "]");
                    continue; // l'ACK n'est pas envoyé -> le client renverra
                }

                byte[] ack = ("ACK:" + seq).getBytes(StandardCharsets.UTF_8);
                DatagramPacket ackPacket = new DatagramPacket(
                        ack,
                        ack.length,
                        packet.getAddress(),
                        packet.getPort()
                );
                socket.send(ackPacket);
                System.out.println("  -> sent ACK:" + seq);
            }

        } catch (SocketException e) {
            System.err.println("Socket error: " + e.getMessage());

        } catch (IOException e) {
            System.err.println("Communication error: " + e.getMessage());

        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            running = false;
        }
    }

    @Override
    public String toString() {
        return "UDPServer [port=" + port +
                ", bufferSize=" + bufferSize +
                ", dropRate=" + dropRate +
                ", running=" + running + "]";
    }

    public static void main(String[] args) {
        int port = 8080;
        int bufferSize = 1024;
        double dropRate = 0.0;

        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }

        if (args.length >= 2) {
            bufferSize = Integer.parseInt(args[1]);
        }

        if (args.length >= 3) {
            dropRate = Double.parseDouble(args[2]);
        }

        UDPServer server = new UDPServer(port, bufferSize, dropRate);

        System.out.println(server);
        server.launch();
    }
}
