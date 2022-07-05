package com.example.tripplanner.models;

import android.util.Log;

import java.util.HashMap;

public class Graph {
    private static final String TAG = "graph";
    private HashMap<Attraction, Integer> nodeMap;
    private int[][] graph;

    public Graph(int numNodes) {
        nodeMap = new HashMap<>();
        graph = new int[numNodes][numNodes];
    }

    public void addEdge(Attraction v, Attraction u, int weight) {
        graph[nodeMap.get(v)][nodeMap.get(u)] = weight;
        graph[nodeMap.get(u)][nodeMap.get(v)] = weight;
    }

    public void deleteEdge(Attraction v, Attraction u) {
        // Since the distance or travel duration cannot be negative,
        // we pick -1 to indicate non-existing edge
        graph[nodeMap.get(v)][nodeMap.get(u)] = -1;
        graph[nodeMap.get(u)][nodeMap.get(v)] = -1;
    }

    public int getWeight(Attraction v, Attraction u) {
        return graph[nodeMap.get(v)][nodeMap.get(u)];
    }

    public int addVertex(Attraction v) {
        if (nodeMap.containsKey(v)) {
            return nodeMap.get(v);
        } else {
            if (nodeMap.size() + 1 > graph.length) {
                Log.e(TAG, "Cannot add the vertex");
                return -1;
            } else {
                // First node will have index 0 assigned
                nodeMap.put(v, nodeMap.size());
                return nodeMap.size() - 1;
            }
        }
    }

    public void deleteVertex(Attraction v) {
        for (int i = 0; i < graph.length; i++) {
            graph[nodeMap.get(v)][i] = -1;
            graph[i][nodeMap.get(v)] = -1;
        }
        nodeMap.remove(v);
    }

    public int getNumNodes() {
        return nodeMap.size();
    }

}