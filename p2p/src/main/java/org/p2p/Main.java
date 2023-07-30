package org.p2p;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Main {

    static Node systemNode;

    // BS ip address
    public static final String BS_IPADDRESS = "127.0.0.1";

    //BS port
    public static final int BS_PORT = 55555;

    public static void main(String[] args) {

        // create list of nodes to start process
        List<Node> startNodes = new ArrayList<Node>();

        startNodes.add(
                new Node(BS_IPADDRESS,
                        55556,
                        55557,
                        Arrays.asList(
                                "Adventures of Tintin",
                                "Jack and Jill",
                                "Glee",
                                "The Vampire Diarie",
                                "King Arthur",
                                "Windows XP")
                )
        );

        startNodes.add(
                new Node(BS_IPADDRESS,
                        55558,
                        55559,
                        Arrays.asList(
                                "Harry Potter",
                                "Kung Fu Panda",
                                "Lady Gaga",
                                "Twilight",
                                "Windows 8",
                                "Mission Impossible"
                        )
                )
        );

        startNodes.add(
                new Node(BS_IPADDRESS,
                        55560,
                        55561,
                        Arrays.asList(
                                "Turn Up The Music",
                                "Super Mario",
                                "American Pickers",
                                "Microsoft Office 2010",
                                "Happy Feet",
                                "Modern Family",
                                "American Idol",
                                "Hacking for Dummies")
                )
        );


        try {
            Scanner scanner = new Scanner(System.in);
//            System.out.print("Enter Node id: ");
//            int  index = parseInt(scanner.nextLine());
//
//            if (index > 3 || index < 0) {
//                throw new Exception("Only 3 nodes Allowed if you want more nodes add to the program");
//            }

            // create system node
            List<String> storage = getStorage();
//            systemNode = startNodes.get(index);
            systemNode = new Node(storage);

            // Init system logger
            SystemLogger.createLogger(systemNode);

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
            SystemLogger.info(response);

            RoutingTable.createRoutingTable(response);

            // refresh routing table
            List<Node> nodes = RoutingTable.refreshRoutingTable(systemNode);

            // Update route table periodically
            Thread th2 = new Thread( ()-> {
                while (true) {
                    try {
                        // Sync routing table with bootstrap server
                        RoutingTable.syncRouteTable();

                        // refresh routing table
                        //RoutingTable.refreshRoutingTable(systemNode);

                        // update every 60S
                        Thread.sleep(60000);
                    } catch (Exception e) {
                        SystemLogger.info(e.getMessage());
                    }
                }
            });
            th2.start();

            //create new thread and if someone ask file from the server lest send the reply
            Thread th1 = new Thread(new ClientListener(systemNode), "Client Listener");
            th1.start();

            SystemLogger.info(systemNode.getNodeJsonObject());

            while (true) {
                // wait for user input and if user request file start search
                System.out.print("Enter File Name To Search: ");
                String  fileName = scanner.nextLine();

                SearchResult searchResult = search(fileName);
                if (searchResult.isResultFound()) {
                    System.out.println("\u001B[32m Search result found for file : " + fileName);
                    System.out.println("Node Ip : " + searchResult.getNodeIp()
                            + "\n Node Port: " + searchResult.getNodePort()
                            + "\n Search Depth: " + searchResult.getCurrentSearchDepth()
                            + " \u001B[0m"
                    );
                } else {
                    System.out.println("\u001B[31m No Search result found for file : " + fileName
                            + "Search depth : " + searchResult.getCurrentSearchDepth()
                            + " \u001B[0m");
                }
            }

        } catch (Exception e) {
            SystemLogger.info(e.getMessage());
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
                SystemLogger.info(e.getMessage());
            }
        }
        Random random = new Random();
        int numberOfFilesToPick = random.nextInt(0,  (int)(files.size()/2));

        return Helper.pickRandomFiles(files,numberOfFilesToPick);
    }

    public static String registerNodeInBS(Node node) {
        try {

            InetAddress destinationAddress = InetAddress.getByName(BS_IPADDRESS);

            // Send BS server to incoming port details
            String regCommand = "REG " + node.getIp() + " " + node.getPort() + " "+ node.getNodeId();
            regCommand = regCommand.length() + " " + regCommand;

            SystemLogger.info(regCommand);

            // Send registration command to server
            CommunicationModule.sendCommand(regCommand, destinationAddress, BS_PORT);

            // get BS server response
            DatagramPacket incoming = CommunicationModule.waitForReply();
            return CommunicationModule.getDataFromIncomingPacket(incoming);
        } catch (Exception e) {
            SystemLogger.info(e.getMessage());
            return null;
        }
    }

    public static SearchResult search(String file) {
        SearchQuery searchQuery = new SearchQuery(file, 0);
        return Helper.searchFile(searchQuery);
    }
}