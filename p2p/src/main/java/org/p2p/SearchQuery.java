package org.p2p;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SearchQuery {
    private String filename;
    private int currentSearchDepth;

    List<Node> searchedNodes = new ArrayList<>();

    public List<Node> getSearchedNodes() {
        return searchedNodes;
    }

    public void setSearchedNode(Node node) {
        this.searchedNodes.add(node);
    }

    public boolean isQuerySearchInNode(Node node) {
        for(Node n : searchedNodes) {
            if(n.getIp().equals(node.getIp()) && n.getPort() == node.getPort()) {
                return true;
            }
        }
        return false;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getCurrentSearchDepth() {
        return currentSearchDepth;
    }

    public void setCurrentSearchDepth(int currentSearchDepth) {
        this.currentSearchDepth = currentSearchDepth;
    }

    private static final String SEARCH_COMMAND = "FIND";
    private static final String COMMAND_SEPARATOR = "&%";

    public SearchQuery(String filename, int currentSearchDepth) {
        this.filename = filename;
        this.currentSearchDepth = currentSearchDepth;
    }

    public String getSearchQuery () {
        Gson gson = new Gson();
        String query = SEARCH_COMMAND +COMMAND_SEPARATOR+gson.toJson(this);

        return String.format("%04d", query.length() + 5) + COMMAND_SEPARATOR + query;
    }
}
