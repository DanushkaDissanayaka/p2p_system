import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
public class Main {
    public static void main(String[] args) {
        DatagramSocket sock = null;
        String s;
        List<Neighbour> nodes = new ArrayList<Neighbour>();

        try
        {
            sock = new DatagramSocket(55555);

            echo("Bootstrap Server created at 55555. Waiting for incoming data...");

            while(true)
            {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);

                byte[] data = incoming.getData();
                s = new String(data, 0, incoming.getLength());

                //echo the details of incoming data - client ip : client port - client message
                echo(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);

                StringTokenizer st = new StringTokenizer(s, " ");

                String length = st.nextToken();
                String command = st.nextToken();

                if (command.equals("REG")) {
                    String reply = "REGOK ";

                    String ip = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String username = st.nextToken();
                    if (nodes.size() == 0) {
                        reply += "0";
                        nodes.add(new Neighbour(ip, port, username));
                    } else {
                        boolean isOkay = true;
                        for (int i=0; i<nodes.size(); i++) {
                            if (nodes.get(i).getPort() == port) {
                                if (nodes.get(i).getUsername().equals(username)) {
                                    reply += "9998";
                                } else {
                                    reply += "9997";
                                }
                                isOkay = false;
                            }
                        }
                        if (isOkay) {
                            if (nodes.size() == 1) {
                                reply += "1 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort();
                            } else if (nodes.size() == 2) {
                                reply += "2 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort() + " " + nodes.get(1).getIp() + " " + nodes.get(1).getPort();
                            } else {
                                Random r = new Random();
                                int Low = 0;
                                int High = nodes.size();
                                int random_1 = r.nextInt(High-Low) + Low;
                                int random_2 = r.nextInt(High-Low) + Low;
                                while (random_1 == random_2) {
                                    random_2 = r.nextInt(High-Low) + Low;
                                }
                                echo (random_1 + " " + random_2);
                                reply += "2 " + nodes.get(random_1).getIp() + " " + nodes.get(random_1).getPort() + " " + nodes.get(random_2).getIp() + " " + nodes.get(random_2).getPort();
                            }
                            nodes.add(new Neighbour(ip, port, username));
                        }
                    }

                    reply = String.format("%04d", reply.length() + 5) + " " + reply;
                    System.out.println(reply);

                    DatagramPacket dpReply = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
                    sock.send(dpReply);
                    System.out.println(nodes.size());
                } else if (command.equals("UNREG")) {
                    String ip = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String username = st.nextToken();
                    for (int i=0; i<nodes.size(); i++) {
                        if (nodes.get(i).getPort() == port) {
                            nodes.remove(i);
                            String reply = "0012 UNROK 0";
                            DatagramPacket dpReply = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
                            sock.send(dpReply);
                        }
                    }
                } else if (command.equals("ECHO")) {
                    for (int i=0; i<nodes.size(); i++) {
                        echo(nodes.get(i).getIp() + " " + nodes.get(i).getPort() + " " + nodes.get(i).getUsername());
                    }
                    String reply = "0012 ECHOK 0";
                    DatagramPacket dpReply = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
                    sock.send(dpReply);
                } else if (command.equals("SYNC")) {
                    // send 3 random nodes except request node
                    List<Neighbour> neighboursCopy = new ArrayList<>(nodes);

                    String ip = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String username = st.nextToken();

                    // remove sync requested node
                    boolean isExist = false;
                    for (int i=0; i<neighboursCopy.size(); i++) {
                        if (nodes.get(i).getPort() == port) {
                            isExist = true;
                            neighboursCopy.remove(i);
                        }
                    }
                    if (!isExist) {
                        // this node not exist in bootstrap server add it to the list
                        nodes.add(new Neighbour(ip, port, username));
                    }

                    // prepare response data
                    String response = getRandomNodes(neighboursCopy);
                    DatagramPacket dpReply = new DatagramPacket(response.getBytes() , response.getBytes().length , incoming.getAddress() , incoming.getPort());
                    sock.send(dpReply);
                }

            }
        } catch(IOException e) {
            System.err.println("IOException " + e);
        }
    }

    public static void echo(String msg)
    {
        System.out.println(msg);
    }

    public static String getRandomNodes(List<Neighbour> nodes) {
        String reply = "0 ";
        if (nodes.size() == 1) {
            reply = "1 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort();
        } else if (nodes.size() == 2) {
            reply = "2 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort() + " " + nodes.get(1).getIp() + " " + nodes.get(1).getPort();
        } else if (nodes.size() > 2){
            Random r = new Random();
            int Low = 0;
            int High = nodes.size();
            int random_1 = r.nextInt(High-Low) + Low;
            int random_2 = r.nextInt(High-Low) + Low;
            while (random_1 == random_2) {
                random_2 = r.nextInt(High-Low) + Low;
            }
            echo (random_1 + " " + random_2);
            reply = "2 " + nodes.get(random_1).getIp() + " " + nodes.get(random_1).getPort() + " " + nodes.get(random_2).getIp() + " " + nodes.get(random_2).getPort();
        }

        return reply;
    }
}