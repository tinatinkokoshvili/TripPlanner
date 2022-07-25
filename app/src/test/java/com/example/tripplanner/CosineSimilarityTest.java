package com.example.tripplanner;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.tripplanner.algorithms.Corpus;
import com.example.tripplanner.algorithms.Document;
import com.example.tripplanner.algorithms.VectorSpaceModel;

import java.util.ArrayList;
import java.util.TreeMap;

public class CosineSimilarityTest {

    @Test
    public void twoVerySimilarStrings() {
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

        VectorSpaceModel vectorSpace = new VectorSpaceModel(testCorpus);
        TreeMap<Double, String> cosineSimilarity = new TreeMap<Double, String>();

        for (int i = 0; i < testDocuments.size(); i++) {
            Document doc1 = testDocuments.get(0);
            Document doc2 = testDocuments.get(i);
            cosineSimilarity.put(vectorSpace.cosineSimilarity(doc1, doc2), "text" + (i + 1));
        }
        assertEquals("text1", cosineSimilarity.get(cosineSimilarity.lastKey()));
        cosineSimilarity.pollLastEntry();
        assertEquals("text2", cosineSimilarity.get(cosineSimilarity.lastKey()));
    }

    @Test
    public void emptyStringIncluded() {
        String userID = "userID";
        String text0 = "I love eating popcorn But sometimes I like fish better";
        String text1 = "fish is good this is popcorn and I am eating popcorn house is far";
        String text2 = "your house is very close to my house";
        String text3 = "";
        ArrayList<Document> testDocuments = new ArrayList<Document>();
        testDocuments.add(new Document(userID, text0));
        testDocuments.add(new Document(userID, text1));
        testDocuments.add(new Document(userID, text2));
        testDocuments.add(new Document(userID, text3));
        Corpus testCorpus = new Corpus(testDocuments);

        VectorSpaceModel vectorSpace = new VectorSpaceModel(testCorpus);
        TreeMap<Double, String> cosineSimilarity = new TreeMap<Double, String>();

        for (int i = 0; i < testDocuments.size(); i++) {
            Document doc1 = testDocuments.get(0);
            Document doc2 = testDocuments.get(i);
            cosineSimilarity.put(vectorSpace.cosineSimilarity(doc1, doc2), "text" + (i));
        }
        assertEquals("text3", cosineSimilarity.get(cosineSimilarity.lastKey()));
        cosineSimilarity.pollLastEntry();
        assertEquals("text0", cosineSimilarity.get(cosineSimilarity.lastKey()));
        cosineSimilarity.pollLastEntry();
        assertEquals("text1", cosineSimilarity.get(cosineSimilarity.lastKey()));
        cosineSimilarity.pollLastEntry();
        assertEquals("text2", cosineSimilarity.get(cosineSimilarity.lastKey()));
    }

    @Test
    public void totallyDifferentDocs() {
        String userID = "userID";
        String text0 = "one two three";
        String text1 = "four five six seven";
        String text2 = "eight nine ten eleven twelve";
        String text3 = "thirteen fourteen fifteen";
        ArrayList<Document> testDocuments = new ArrayList<Document>();
        testDocuments.add(new Document(userID, text0));
        testDocuments.add(new Document(userID, text1));
        testDocuments.add(new Document(userID, text2));
        testDocuments.add(new Document(userID, text3));
        Corpus testCorpus = new Corpus(testDocuments);

        VectorSpaceModel vectorSpace = new VectorSpaceModel(testCorpus);
        TreeMap<Double, String> cosineSimilarity = new TreeMap<Double, String>();

        for (int i = 0; i < testDocuments.size(); i++) {
            Document doc1 = testDocuments.get(0);
            Document doc2 = testDocuments.get(i);
            cosineSimilarity.put(vectorSpace.cosineSimilarity(doc1, doc2), "text" + (i));
            System.out.println("doc" + i + " val " + vectorSpace.cosineSimilarity(doc1, doc2));
        }
        assertEquals(1.0, cosineSimilarity.lastKey(), 0.001);
        cosineSimilarity.pollLastEntry();
        assertEquals(0.0, cosineSimilarity.lastKey(), 0.001);
    }







}
