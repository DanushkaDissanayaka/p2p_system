package org.p2p;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.StringTokenizer;

public class CommunicationModule {
    private static DatagramSocket socket;

    public static void createSocket(Node systemNode) {
        try {
            socket = new DatagramSocket(systemNode.getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatagramPacket receiveCommand() {
        try {
            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            socket.receive(incoming);
            return incoming;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendCommand(String command, InetAddress address, int port) {
        try {
            System.out.println(command);
            // send response to client
            DatagramPacket dpReply = new DatagramPacket(command.getBytes() , command.getBytes().length , address, port);
            socket.send(dpReply);
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}
