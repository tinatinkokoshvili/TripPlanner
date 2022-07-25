package com.example.tripplanner;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.tripplanner.algorithms.Corpus;
import com.example.tripplanner.algorithms.Document;
import com.example.tripplanner.algorithms.RouteGenerator;
import com.example.tripplanner.algorithms.VectorSpaceModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

public class RouteGeneratorTest {
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


}
