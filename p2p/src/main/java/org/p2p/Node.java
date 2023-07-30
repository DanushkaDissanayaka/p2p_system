package org.p2p;

import com.google.gson.Gson;

import javax.xml.crypto.Data;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

// Sample Node class for representing nodes in the network
class Node {
    private final String ip;
    private final List<String> storage;
    private final int port;
    private final String nodeId;

    public Node(String ip, int port, List<String> storage) {
        this.ip = ip;
        this.storage = storage;
        this.port = port;
        this.nodeId = UUID.randomUUID().toString();
    }

    public Node(List<String> storage) {
        this.ip = "127.0.0.1"; // Change this to the destination IP address

        // init random port
        Random random = new Random();
        this.port = random.nextInt(55556, 65535);

        // generate node id
        this.nodeId = UUID.randomUUID().toString();

        // setup storage
        this.storage = storage;

    }

    public Node(String ip, int port) {
        this.ip = ip;
        this.port = port;

        this.storage = new ArrayList<>();
        this.nodeId = UUID.randomUUID().toString();
    }
    public String getIp() {
        return ip;
    }
    public List<String> getStorage() {
        return this.storage;
    }

    public int getPort() {
        return port;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getNodeJsonObject() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public boolean searchInStorage (String file) {
        return this.storage.contains(file);
    }
}
