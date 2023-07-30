package org.p2p;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.StringTokenizer;

public class CommunicationModule {
    private static DatagramSocket socket;
    private static DatagramSocket socketOutgoing;

    public static void createSocket(Node systemNode) {
        try {
            socket = new DatagramSocket(systemNode.getPort());
        } catch (Exception e) {
            SystemLogger.info(e.getMessage());
        }
    }

    public static void createSocketOutgoing(Node systemNode) {
        try {
            socketOutgoing = new DatagramSocket(systemNode.getOutgoingPort());
        } catch (Exception e) {
            SystemLogger.info(e.getMessage());
        }
    }


    /**
     * Watch incoming file search traffic
     * @return DatagramPacket
     */
    public static DatagramPacket receiveIncomingCommand() {
        try {
            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            socket.receive(incoming);
            return incoming;
        } catch (Exception e) {
            SystemLogger.info(e.getMessage());
        }
        return null;
    }

    /**
     * Wait for get reply for send command
     * @return DatagramPacket
     */
    public static DatagramPacket waitForReply() {
        try {
            byte[] buffer = new byte[65536];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            socketOutgoing.receive(reply);
            return reply;
        } catch (Exception e) {
            SystemLogger.info(e.getMessage());
        }
        return null;
    }

    /**
     * Send command to other nodes
     * @param command command that need to send
     * @param address ip address of the node
     * @param port pot of the node
     */
    public static void sendCommand(String command, InetAddress address, int port) {
        try {
            SystemLogger.info(command);
            // send response to client
            DatagramPacket dpReply = new DatagramPacket(command.getBytes() , command.getBytes().length , address, port);
            socketOutgoing.send(dpReply);
        } catch (Exception e) {
            SystemLogger.info(e.getMessage());
        }
    }

    public static String getDataFromIncomingPacket(DatagramPacket incomingPacket) {
        if(incomingPacket != null) {
            byte[] data = incomingPacket.getData();
            return new String(data, 0, incomingPacket.getLength());
        }
        return null;
    }

    public static void setSocketTimeout(int timeout) {
        try {
            socket.setSoTimeout(timeout);
        } catch (Exception e) {
            SystemLogger.info(e.getMessage());
        }
    }

    public static void setOutgoingSocketTimeout(int timeout) {
        try {
            socketOutgoing.setSoTimeout(timeout);
        } catch (Exception e) {
            SystemLogger.info(e.getMessage());
        }
    }
}
