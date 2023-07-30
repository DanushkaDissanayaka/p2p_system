package org.p2p;

import com.google.gson.Gson;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

// Sample Node class for representing nodes in the network
class Node {
    private final String ip;
    private final List<String> storage;
    private final int port;

    private final int outgoingPort;
    private final String nodeId;

    private final boolean isSystemNode;

    public Node(String ip, int port, int outgoingPort, List<String> storage) {
        this.ip = ip;
        this.storage = storage;
        this.port = port;
        this.outgoingPort = outgoingPort;
        this.nodeId = UUID.randomUUID().toString();
        this.isSystemNode = true;
    }

    public Node(List<String> storage) {
        this.ip = "127.0.0.1"; // Change this to the destination IP address

        // init random port
        Random random = new Random();
        this.port = random.nextInt(55556, 65535);

        // init outgoing port
        this.outgoingPort = random.nextInt(55556, 65535);

        // generate node id
        this.nodeId = UUID.randomUUID().toString();

        // setup storage
        this.storage = storage;

        this.isSystemNode = true;

    }

    public Node(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.outgoingPort = port;

        this.storage = new ArrayList<>();
        this.nodeId = UUID.randomUUID().toString();
        this.isSystemNode = false;
    }
    public String getIp() {
        return ip;
    }

    public InetAddress getAddress () throws UnknownHostException {
        return InetAddress.getByName(this.ip);
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

    public int getOutgoingPort() throws Exception {
        if (this.isSystemNode) {
            return this.outgoingPort;
        }
        throw new Exception("Only system node can retrieve outgoing port");
    }
    public SearchResult searchInStorage (SearchQuery query) {
        SearchResult searchResult = new SearchResult(this.ip, this.port, query.getCurrentSearchDepth(), false);
        if(this.storage.contains(query.getFilename())) {
            searchResult.setResultFound(true);
            return searchResult;
        }
        return searchResult;
    }
}
