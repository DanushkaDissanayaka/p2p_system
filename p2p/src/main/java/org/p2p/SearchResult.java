package org.p2p;

import com.google.gson.Gson;

public class SearchResult {
    private String nodeIp;
    private int nodePort;
    private int CurrentSearchDepth;

    private boolean isResultFound;

    public SearchResult(String nodeIp, int nodePort, int currentSearchDepth, boolean isResultFound) {
        this.nodeIp = nodeIp;
        this.nodePort = nodePort;
        CurrentSearchDepth = currentSearchDepth;
        this.isResultFound = isResultFound;
    }

    public SearchResult() {

    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public int getNodePort() {
        return nodePort;
    }

    public void setNodePort(int nodePort) {
        this.nodePort = nodePort;
    }

    public int getCurrentSearchDepth() {
        return CurrentSearchDepth;
    }

    public void setCurrentSearchDepth(int currentSearchDepth) {
        CurrentSearchDepth = currentSearchDepth;
    }

    public boolean isResultFound() {
        return isResultFound;
    }

    public void setResultFound(boolean resultFound) {
        isResultFound = resultFound;
    }

    public String toJson () {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
