package org.p2p;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class Helper {
    private static final int SEARCH_DEPTH = 3;

    public static  <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        Gson gson = new Gson();
        return gson.fromJson(json, classOfT);
    }

    public static SearchResult searchFile(SearchQuery searchQuery) {
        // search local storage
        SearchResult searchResult = Main.systemNode.searchInStorage(searchQuery);

        if (searchResult.isResultFound()) {
            return searchResult;
        }

        // search in neighbours if local storage not found and search depth not reached
        if (searchQuery.getCurrentSearchDepth() <= SEARCH_DEPTH) {
            String query = searchQuery.getSearchQuery();
            // find in each neighbours
            for(int i=0; i < RoutingTable.getNeighbours().size(); i++)
            {
                Node node = RoutingTable.getNeighbours().get(i);
                try {
                    CommunicationModule.setOutgoingSocketTimeout(10000);
                    CommunicationModule.sendCommand(query, node.getAddress(), node.getPort());
                    DatagramPacket incoming = CommunicationModule.waitForReply();
                    String jsonData = CommunicationModule.getDataFromIncomingPacket(incoming);
                    searchResult = Helper.fromJson(jsonData, SearchResult.class);
                    // end search if result found form neighbours
                    if (searchResult.isResultFound()) {
                        break;
                    }
                } catch (Exception e) {
                    SystemLogger.info(e.getMessage());
                }
            };
        }

        CommunicationModule.setOutgoingSocketTimeout(0);
        return searchResult;
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
}
