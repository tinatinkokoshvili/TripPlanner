package com.example.tripplanner;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.tripplanner.algorithms.Corpus;
import com.example.tripplanner.algorithms.Document;
import com.example.tripplanner.algorithms.VectorSpaceModel;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CosineSimilarityTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


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



}
