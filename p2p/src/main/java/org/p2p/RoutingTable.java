package org.p2p;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;

public class RoutingTable {

    private static List<Node> neighbours;

    /**
     * IF registration was okay then check
     * if we got the Neighbour nodes
     * if we are the only node in server we will not geth any
     * Neighbour nodes response from the boostrap server
     */
   public static void createRoutingTable(String BSResponse) {
       // create list of Neighbour nodes
       neighbours = new ArrayList<>();

       StringTokenizer st = new StringTokenizer(BSResponse, " ");
       // first token is length
       String command = st.nextToken();
       // next token is the command
       command = st.nextToken();

       if (Objects.equals(command, "REGOK")) {
           // net token is count of nodes
           command = st.nextToken();
           int nodes = parseInt(command);

           if (nodes != 9998 && nodes != 9997) {
              for (int i = 0; i < nodes; i++) {
                  String ip = st.nextToken();
                  String port = st.nextToken();
                  neighbours.add(new Node(ip, parseInt(port)));
              }
           }
       }
   }

    /**
     * Update routing table
     * send PING command to each node and
     * if we got PONG response the server is available
     * else the node is not available
     */
   public static List<Node> refreshRoutingTable (Node systemNode) {

       List<Node> listUpdatedNeighbours = new ArrayList<>();
       neighbours.forEach (node -> {
           try {
               CommunicationModule.setOutgoingSocketTimeout(1000);

               // send ping command to node
               String command = "7&%PING";
               InetAddress destinationAddress = InetAddress.getByName(node.getIp());
               CommunicationModule.sendCommand(command, destinationAddress, node.getPort());

               // wait for pong command
               DatagramPacket incoming = CommunicationModule.waitForReply();

               // remove node form neighbours if we dos not get the pong response
               if(incoming != null) {
                   String Response = CommunicationModule.getDataFromIncomingPacket(incoming);
                   if (Response.equals("PONG")) {
                       listUpdatedNeighbours.add(node);
                   }
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
       });
       CommunicationModule.setOutgoingSocketTimeout(0);
       neighbours = listUpdatedNeighbours;
       return listUpdatedNeighbours;
   }
    public static List<Node> getNeighbours() {
        return neighbours;
    }
}
