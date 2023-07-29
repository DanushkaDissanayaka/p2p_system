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

            // lets register our node in boostrap server
            String response = registerNodeInBS(systemNode);

            // validate BS server response
            System.out.println(response);

            // create new thread and if someone ask file from as send reply

            // wait for user input and if user request file start search

            System.out.println(systemNode.getNodeJsonObject());
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

        return pickRandomFiles(files,numberOfFilesToPick);
    }

    public static List<String> pickRandomFiles(List<String> array, int numberOfElements) {
        List<String> pickedElements = new ArrayList<>();
        Random random = new Random();

        int maxIndex = Math.min(numberOfElements, array.size());

        while (pickedElements.size() < maxIndex) {
            int randomIndex = random.nextInt(array.size());
            String randomElement = array.get(randomIndex);

            if (!pickedElements.contains(randomElement)) {
                pickedElements.add(randomElement);
            }
        }

        return pickedElements;
    }

    public static String registerNodeInBS(Node node) {
        try {
            DatagramSocket socket = new DatagramSocket(55554);

            // BS ip address
            String ipAddress = "127.0.0.1";
            //BS port
            int port = 55555;

            InetAddress destinationAddress = InetAddress.getByName(ipAddress);

            List<String> commands = List.of("REG", "UNREG", "ECHO", "EXIT");

            String s;

            String regCommand = "REG " + node.getIp() + " " + node.getPort() + " "+ node.getNodeId();
            regCommand = regCommand.length() + " " + regCommand;

            System.out.println(regCommand);

            // Send registration command to server
            byte[] sendData = regCommand.getBytes();
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, destinationAddress, port);
            socket.send(packet);

            // get BS server response
            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            socket.receive(incoming);
            byte[] data = incoming.getData();
            socket.close();

            return new String(data, 0, incoming.getLength());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}