package org.p2p;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class Main {

    static Node systemNode;
    public static void main(String[] args) {

        try {
            // create system node
            List<String> storage = getStorage();
            systemNode = new Node(storage);

            // Create socket
            CommunicationModule.createSocket(systemNode);
            CommunicationModule.createSocketOutgoing(systemNode);

            // lets register our node in boostrap server
            String response = registerNodeInBS(systemNode);

            // If BS server response not found throw error
            if (response == null) {
                throw new Exception("BS Server error");
            }

            // validate BS server response and create routing table
            System.out.println(response);
            RoutingTable.createRoutingTable(response);

            // refresh routing table
            List<Node> nodes = RoutingTable.refreshRoutingTable(systemNode);

            System.out.println(nodes);

            //create new thread and if someone ask file from the server lest send the reply
            Thread th1 = new Thread(new ClientListener(systemNode), "Client Listener");
            th1.start();

            System.out.println(systemNode.getNodeJsonObject());

            while (true) {
                // wait for user input and if user request file start search
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter File Name To Search: ");
                String  fileName = scanner.nextLine();

                SearchResult searchResult = search(fileName);
                System.out.println(searchResult.toJson());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Make storage with
     * random file list at system start up
     * @return
     */
    private static List<String> getStorage(){
        String fileName = "files.txt";
        List<String> files = new ArrayList<String>();

        // Use the ClassLoader to get the resource as an InputStream.
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(fileName);

        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    files.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Random random = new Random();
        int numberOfFilesToPick = random.nextInt(3, files.size());

        return Helper.pickRandomFiles(files,numberOfFilesToPick);
    }

    public static String registerNodeInBS(Node node) {
        try {
            // BS ip address
            String ipAddress = "127.0.0.1";
            //BS port
            int port = 55555;

            InetAddress destinationAddress = InetAddress.getByName(ipAddress);

            // Send BS server to incoming port details
            String regCommand = "REG " + node.getIp() + " " + node.getPort() + " "+ node.getNodeId();
            regCommand = regCommand.length() + " " + regCommand;

            System.out.println(regCommand);

            // Send registration command to server
            CommunicationModule.sendCommand(regCommand, destinationAddress, port);

            // get BS server response
            DatagramPacket incoming = CommunicationModule.waitForReply();
            return CommunicationModule.getDataFromIncomingPacket(incoming);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SearchResult search(String file) {
        SearchQuery searchQuery = new SearchQuery(file, 0);
        return Helper.searchFile(searchQuery);
    }
}