package com.example.tripplanner;

import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Log;

import com.example.tripplanner.algorithms.RouteGenerator;
import com.example.tripplanner.models.Attraction;

import java.util.LinkedList;
import java.util.List;

public class RouteGeneratorTest {

    @Test
    public void initMatrixNotSquare() throws Exception {
        List<Attraction> atrListTest = new LinkedList<>();
        int[][] initGraphTest = { { 0, 2, 3 }, { 2, 0, 6 } };
        assertThrows(Exception.class, () -> {
            new RouteGenerator(atrListTest, initGraphTest);
        });
    }

    @Test
    public void removeDuplicatesTest() {
        List<Integer> list = new LinkedList<>();
        list.add(2);
        list.add(0);
        list.add(1);
        list.add(0);
        list.add(2);
        RouteGenerator testRtGenerator = new RouteGenerator(list.size());
        LinkedList<Integer> result = testRtGenerator.removeDuplicateNodes(list, list.size());

        assertEquals((Integer) 2, result.get(0));
        assertEquals((Integer) 0, result.get(1));
        assertEquals((Integer) 1, result.get(2));
    }

    @Test
    public void createGraphFromParentArrayTest() {
        int[][] initGraphTest = { { 0, 2, 3 }, { 2, 0, 6 }, {3, 6, 0} };
        RouteGenerator testRtGenerator = new RouteGenerator(initGraphTest.length);
        int[] parent = { 2, 0, -1 };
        int[][] graph = testRtGenerator.createGraphFromParentArray(initGraphTest, parent);

        assertEquals(-1, graph[0][0]);
        assertEquals(2, graph[0][1]);
        assertEquals(3, graph[0][2]);
        assertEquals(2, graph[1][0]);
        assertEquals(-1, graph[1][1]);
        assertEquals(-1, graph[1][2]);
    }

    @Test
    public void findMST() {
        int[][] arr1 = { { 0, 2, 3 }, { 2, 0, 6 }, {3, 6, 0} };
        RouteGenerator testRtGenerator = new RouteGenerator(arr1.length);
        int[] mst = testRtGenerator.primMST(arr1);
        int[][] mstGraph = testRtGenerator.createGraphFromParentArray(arr1, mst);

        assertEquals(-1, mstGraph[0][0]);
        assertEquals(2, mstGraph[0][1]);
        assertEquals(3, mstGraph[0][2]);
        assertEquals(2, mstGraph[1][0]);
        assertEquals(-1, mstGraph[1][1]);
        assertEquals(-1, mstGraph[1][2]);
    }

    @Test
    public void dfsOnMst() {
        int[][] arr1 = { { 0, 2, 3 }, { 2, 0, 6 }, {3, 6, 0} };
        RouteGenerator testRtGenerator = new RouteGenerator(arr1.length);
        int[] mst = testRtGenerator.primMST(arr1);
        int[][] mstGraph = testRtGenerator.createGraphFromParentArray(arr1, mst);
        boolean[] visited = new boolean[mstGraph.length];
        int visitedNum = 0;
        LinkedList<Integer> indexAllVisitedRoute = new LinkedList<>();
        indexAllVisitedRoute = testRtGenerator.dfsBuildAtrIndexRoute(indexAllVisitedRoute, mstGraph,
                mstGraph.length - 1, visited, visitedNum);

        assertEquals((Integer) 2, indexAllVisitedRoute.get(0));
        assertEquals((Integer) 0, indexAllVisitedRoute.get(1));
        assertEquals((Integer) 1, indexAllVisitedRoute.get(2));
        assertEquals((Integer) 0, indexAllVisitedRoute.get(3));
        assertEquals((Integer) 2, indexAllVisitedRoute.get(4));
    }
}