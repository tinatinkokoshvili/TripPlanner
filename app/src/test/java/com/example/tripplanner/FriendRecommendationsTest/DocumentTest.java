package com.example.tripplanner.FriendRecommendationsTest;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.tripplanner.models.Document;

public class DocumentTest {
    @Test
    public void getTermFrequencyTest() {
        String userID = "userId";
        String testString = "dobby is good a and kind dobby is nice and nice";
        Document testDoc = new Document(userID, testString);
        assertEquals(2, testDoc.getTermFrequency("dobby"), 0.1);
        assertEquals(1, testDoc.getTermFrequency("kind"), 0.1);
        assertEquals(2, testDoc.getTermFrequency("nice"), 0.1);
    }

    @Test
    public void upperAndLowerCharGetTermFrequencyTest() {
        String userID = "userId";
        String testString = "Dobby is good a and kind dobby is nice and nice";
        Document testDoc = new Document(userID, testString);
        assertEquals(2, testDoc.getTermFrequency("dobby"), 0.1);
    }

    @Test
    public void getTermListTest() {
        String userID = "userId";
        String testString = "dobby is good and kind dobby is nice and nice";
        Document testDoc = new Document(userID, testString);
        assertEquals(6, testDoc.getTermList().size());
        assertTrue(testDoc.getTermList().contains("dobby"));
        assertTrue(testDoc.getTermList().contains("is"));
        assertTrue(testDoc.getTermList().contains("good"));
        assertTrue(testDoc.getTermList().contains("and"));
        assertTrue(testDoc.getTermList().contains("kind"));
        assertTrue(testDoc.getTermList().contains("nice"));
    }

    @Test
    public void getUserIdTest() {
        String userID = "userId";
        String testString = "dobby is good and kind dobby is nice and nice";
        Document testDoc = new Document(userID, testString);
        assertEquals(userID, testDoc.getUserId());
    }

    @Test
    public void toStringTest() {
        String userID = "userId";
        String testString = "dobby is good and kind dobby is nice and nice";
        Document testDoc = new Document(userID, testString);
        assertEquals(testString, testDoc.toString());
    }

    @Test
    public void compareTo() {
        String userID = "userId";
        String testString1 = "dobby is good and kind dobby is nice and nice";
        String testString2 = "what is this";
        Document testDoc1 = new Document(userID, testString1);
        Document testDoc2 = new Document(userID, testString2);
        assertEquals(testString1.compareTo(testString2), testDoc1.compareTo(testDoc2));
    }
}
