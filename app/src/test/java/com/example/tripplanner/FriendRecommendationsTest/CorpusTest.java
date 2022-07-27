package com.example.tripplanner.FriendRecommendationsTest;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.tripplanner.models.Corpus;
import com.example.tripplanner.models.Document;
import com.example.tripplanner.algorithms.VectorSpaceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class CorpusTest {

    @Test
    public void getDocumentsTest() {
        String userID = "userID";
        String text1 = "I love eating popcorn But sometimes I like fish better";
        String text2 = "fish is good this is popcorn and I am eating popcorn house is far";
        String text3 = "your house is very close to my house";
        String text4 = "Computer is too heavy can you help me better better";
        ArrayList<Document> testDocuments = new ArrayList<Document>();
        Document doc1 = new Document(userID, text1);
        Document doc2 = new Document(userID, text2);
        Document doc3 = new Document(userID, text3);
        Document doc4 = new Document(userID, text4);
        testDocuments.add(doc1);
        testDocuments.add(doc2);
        testDocuments.add(doc3);
        testDocuments.add(doc4);
        Corpus testCorpus = new Corpus(testDocuments);

        ArrayList<Document> list = testCorpus.getDocuments();
        assertEquals(4, list.size());
        assertEquals(doc1, list.get(0));
        assertEquals(doc2, list.get(1));
        assertEquals(doc3, list.get(2));
        assertEquals(doc4, list.get(3));
    }

    @Test
    public void getInvertedIndexTest() {
        String userID = "userID";
        String text1 = "I love eating popcorn But sometimes I like fish better";
        String text2 = "fish is good this is popcorn and I am eating popcorn house is far";
        ArrayList<Document> testDocuments = new ArrayList<Document>();
        Document doc1 = new Document(userID, text1);
        Document doc2 = new Document(userID, text2);
        testDocuments.add(doc1);
        testDocuments.add(doc2);
        Corpus testCorpus = new Corpus(testDocuments);

        HashMap<String, Set<Document>> invertedIndexMap = testCorpus.getInvertedIndex();
        assertEquals(1, invertedIndexMap.get("love").size());
        assertEquals(2, invertedIndexMap.get("popcorn").size());
        assertEquals(1, invertedIndexMap.get("far").size());
    }

    @Test
    public void getInverseDocumentFrequencyTest() {
        String userID = "userID";
        String text1 = "I love eating popcorn But sometimes I like fish better";
        String text2 = "fish is good this is popcorn and I am eating popcorn house is far";
        String text3 = "your house is very close to my house";
        String text4 = "Computer is too heavy can you help me better better";
        ArrayList<Document> testDocuments = new ArrayList<Document>();
        testDocuments.add(new Document(userID, text1));
        testDocuments.add(new Document(userID, text2));
        testDocuments.add(new Document(userID, text3));
        testDocuments.add(new Document(userID, text4));
        Corpus testCorpus = new Corpus(testDocuments);

        double loveIdf = testCorpus.getInverseDocumentFrequency("love");
        double popcornIdf = testCorpus.getInverseDocumentFrequency("popcorn");
        double goodIdf = testCorpus.getInverseDocumentFrequency("good");
        double houseIdf = testCorpus.getInverseDocumentFrequency("house");
        assertEquals(0.602, loveIdf, 0.01);
        assertEquals(0.301, popcornIdf, 0.01);
        assertEquals(0.602, goodIdf, 0.01);
        assertEquals(0.301, houseIdf, 0.01);
    }
}
