package org.p2p;

import com.google.gson.Gson;

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
        SystemLogger.info("Start listning");
        String request;
        String length;
        String command = null;
        String query = null;
        DatagramPacket incoming = null;
        try {
            while (true) {
                // listen for incoming request
                incoming = CommunicationModule.receiveIncomingCommand();

                if (incoming != null) {
                    request = CommunicationModule.getDataFromIncomingPacket(incoming);
                    StringTokenizer st = new StringTokenizer(request, "&%");
                    SystemLogger.info(request);

                    if (st.countTokens() == 3) {
                        length = st.nextToken();
                        command = st.nextToken();
                        query = st.nextToken();
                    } else if (st.countTokens() == 2) {
                        length = st.nextToken();
                        command = st.nextToken();
                    }
                    /*
                     * Find specific file in node
                     * 12&%FIND&%x-men
                     */
                    if (Objects.equals(command, "FIND")) {

                        SearchQuery searchQuery = Helper.fromJson(query, SearchQuery.class);

                        // increment current search depth by one
                        searchQuery.setCurrentSearchDepth(searchQuery.getCurrentSearchDepth() + 1);

                        // search in local storage
                        SearchResult searchResult = systemNode.searchInStorage(searchQuery);

                        // if not found file in this node and search depth is below threshold we will request file form Neighbour nodes
                        Helper.searchFile(searchQuery);

                        String response = searchResult.toJson();

                        SystemLogger.info(response);
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
            SystemLogger.info(e.getMessage());
        }
    }
}
