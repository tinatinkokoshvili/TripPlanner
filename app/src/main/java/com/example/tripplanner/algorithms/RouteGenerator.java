package com.example.tripplanner.algorithms;

import android.util.Log;

import com.example.tripplanner.models.Attraction;
import com.example.tripplanner.models.Graph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class RouteGenerator {
    private static final String TAG = "RouteGenerator";
    private static List<Attraction> pickedAtrList;
    private static int[][] durationMatrix;
    private static List<Attraction> atrRoute;
    private static LinkedList<Integer> atrIndexRouteWithDuplicates;
    private static LinkedList<Integer> atrIndexRoute;
    private static int numNodes;
    //static Graph graph;

    public RouteGenerator(int numNodes) {
        atrRoute = new LinkedList<>();
        atrIndexRouteWithDuplicates = new LinkedList<>();
        this.numNodes = numNodes;
    }

    public RouteGenerator(List<Attraction> pickedAtrList, int[][] durationMatrix) {
        this.pickedAtrList = pickedAtrList;
        this.durationMatrix = durationMatrix;
        atrRoute = new LinkedList<>();
        atrIndexRouteWithDuplicates = new LinkedList<>();
        atrIndexRoute = new LinkedList<>();
        numNodes = pickedAtrList.size();
        Log.i(TAG, "PickedAtrList size " + pickedAtrList.size());
    }

    public static List<Attraction> getRouteList() {
        // Run MST
        int[] parentArray = primMST(durationMatrix);
        boolean[] visited = new boolean[numNodes];
        // Create graph from parent[]
        int[][] mstGraph = createGraphFromParentArray(durationMatrix, parentArray);
        // Run DFS to build atrIndexRoute
        atrIndexRouteWithDuplicates = dfsBuildAtrIndexRoute(atrIndexRouteWithDuplicates, mstGraph,
                numNodes - 1, visited, 0);
        atrIndexRoute = removeDuplicateNodes(atrIndexRouteWithDuplicates, numNodes);
        createRouteListFromIndexList();
        for (int i = 0; i < atrRoute.size(); i++) {
            Log.i(TAG, "Attraction " + i + " " + atrRoute.get(i).name);
        }
        return atrRoute;
    }

    static int minKeyWithTreeMap(TreeMap<Integer, Integer> keysMap, Boolean includedInMST[]) {
        Log.i(TAG, "keysMap size before polling " + keysMap.size());
        Map.Entry<Integer, Integer> lowestEntry = keysMap.pollFirstEntry();
        while (lowestEntry != null && includedInMST[lowestEntry.getValue()]) {
            lowestEntry = keysMap.pollFirstEntry();
        }
        return lowestEntry.getValue();
    }

    // Function to find the vertex with minimum key
    // value, from the set of vertices not yet included in MST
    static int minKey(int key[], Boolean includedInMST[]) {
        int min = Integer.MAX_VALUE;
        int min_index = -1;

        for (int v = 0; v < numNodes; v++)
            if (includedInMST[v] == false && key[v] < min) {
                min = key[v];
                min_index = v;
            }

        return min_index;
    }

    // Function to print the constructed MST stored in parent[]
    static void printMST(int parent[], int graph[][]) {
        for (int i = 0; i < numNodes - 1; i++)
            Log.i(TAG, parent[i] + " - " + i + "\t" + graph[i][parent[i]]);
    }

    // Function to print the graph
    static void printGraph(int graph[][]) {
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[0].length; j++) {
                Log.i(TAG, "graph " + i + " - " + j + " " + graph[i][j]);
            }
        }
    }

    public static int[] primMST(int[][] graph) {
        int parent[] = new int[numNodes];
        // Key is nodekey and value is node
        int key[] = new int[numNodes];
        // To represent set of vertices already included in MST
        Boolean includedInMST[] = new Boolean[numNodes];
        for (int i = 0; i < numNodes; i++) {
            key[i] = Integer.MAX_VALUE;
            includedInMST[i] = false;
        }
        // Make userLocation's key 0, so that it is picked first, make userLocation root
        key[numNodes - 1] = 0;
        parent[numNodes - 1] = -1;
        for (int count = 0; count < numNodes - 1; count++) {
            // Pick thd minimum key vertex from the set of vertices
            // not yet included in MST
            int u = minKey(key, includedInMST);
            includedInMST[u] = true;
            // Update key value and parent index of the adjacent
            // (not yet in MST) vertices of the picked vertex.
            for (int v = 0; v < numNodes; v++) {
                // Update the key only if graph[u][v] is smaller than key[v]
                if (graph[u][v] != 0 && includedInMST[v] == false && graph[u][v] < key[v]) {
                    parent[v] = u;
                    key[v] = graph[u][v];
                }
            }
        }

        return parent;
    }

    public static LinkedList<Integer> dfsBuildAtrIndexRoute(LinkedList<Integer> atrIndexRouteWithDuplicatesInner,
                                                            int[][] graph, int curNode,
                                                            boolean[] visited, int visitedNum) {
        Log.i(TAG, curNode + " ");
        if (visitedNum == visited.length) {
            return atrIndexRouteWithDuplicatesInner;
        }
        visited[curNode] = true;
        atrIndexRouteWithDuplicatesInner.add(curNode);
        for (int neighbor = 0; neighbor < graph[curNode].length; neighbor++) {
            // For metric TSP algorithm, we need a list of all visited vertices
            if (graph[curNode][neighbor] != -1 && visited[neighbor]) {
                atrIndexRouteWithDuplicatesInner.add(neighbor);
            }
            if (graph[curNode][neighbor] != -1 && (!visited[neighbor])) {
                dfsBuildAtrIndexRoute(atrIndexRouteWithDuplicatesInner, graph, neighbor, visited, visitedNum);
            }
        }
        return atrIndexRouteWithDuplicatesInner;
    }

    static void createRouteListFromIndexList() {
        for (int i = 0; i < atrIndexRoute.size(); i++) {
            atrRoute.add(pickedAtrList.get(atrIndexRoute.get(i)));
        }
    }

    public static int[][] createGraphFromParentArray(int[][] initGraph, int[] parent) {
        int[][] mstGraph = new int[initGraph.length][initGraph[0].length];
        for (int i = 0; i < mstGraph.length; i++) {
            for (int j = 0; j < mstGraph[0].length; j++) {
                mstGraph[i][j] = -1;
            }
        }
        for (int i = 0; i < parent.length - 1; i++) {
            int u = i;
            int v = parent[i];
            mstGraph[u][v] = initGraph[u][v];
            mstGraph[v][u] = initGraph[v][u];
        }
        return mstGraph;
    }

    public static LinkedList<Integer> removeDuplicateNodes(List<Integer> list, int numVertices){
        LinkedList<Integer> atrIndexRoute = new LinkedList<>();
        boolean[] visited = new boolean[numVertices];
        for (int i = 0; i < list.size(); i++) {
            if (!visited[list.get(i)]) {
                atrIndexRoute.add(list.get(i));
                visited[list.get(i)] = true;
            }
        }
        return atrIndexRoute;
    }
}
