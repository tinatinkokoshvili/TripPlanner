package com.example.tripplanner;

import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Graph;

import java.util.LinkedList;
import java.util.List;

public class RouteGenerator {
    static List<Attraction> pickedAtrList;
    static List<Attraction> atrRoute;
    static int numNodes;
    static Graph graph;

    public RouteGenerator(List<Attraction> pickedAtrList) {
        this.pickedAtrList = pickedAtrList;
        atrRoute = new LinkedList<>();
        numNodes = pickedAtrList.size();
        graph = new Graph(numNodes);
    }

    public static List<Attraction> getRouteList() {
        createGraph();
        return atrRoute;
    }

    private static void createGraph() {
        //TODO

    }


    private static Graph getMST(Graph graph) {
        return graph;
    }

}
