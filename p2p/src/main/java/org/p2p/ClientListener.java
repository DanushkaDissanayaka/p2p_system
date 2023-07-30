package org.p2p;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Objects;
import java.util.StringTokenizer;

public class ClientListener implements Runnable {

    private final Node systemNode;


    public ClientListener(Node systemNode) {
        this.systemNode = systemNode;
    }

    @Override
    public void run() {
        System.out.println("Start listning");
        String request;
        String length;
        String command = null;
        String file = null;
        DatagramPacket incoming = null;
        try {
            while (true) {
                // listen for incoming request
                incoming = CommunicationModule.receiveCommand();

                if (incoming != null) {
                    request = CommunicationModule.getDataFromIncomingPacket(incoming);
                    StringTokenizer st = new StringTokenizer(request, "&%");
                    System.out.println(request);

                    if (st.countTokens() == 3) {
                        length = st.nextToken();
                        command = st.nextToken();
                        file = st.nextToken();
                    } else if (st.countTokens() == 2) {
                        length = st.nextToken();
                        command = st.nextToken();
                    }
                    /*
                     * Find specific file in node
                     * 12&%FIND&%x-men
                     */
                    if (Objects.equals(command, "FIND")) {

                        boolean found = systemNode.searchInStorage(file);
                        // if not found from this node we will request file form Neighbour nodes
                        String response = found ? "FOUND" : "NTFOUND";

                        System.out.println(response);
                        // send response to client
                        CommunicationModule.sendCommand(response, incoming.getAddress() , incoming.getPort());
                    } else if (Objects.equals(command, "PING")) {
                    /*
                      If we receive ping command
                      we will send pong response
                      indicating the node is up and running
                     */
                        String response = "PONG";
                        CommunicationModule.sendCommand(response, incoming.getAddress() , incoming.getPort());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
