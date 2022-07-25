package com.example.tripplanner.models;

import java.util.HashMap;
import java.util.Set;

/**
 * This class represents a document and will keep track of the term frequencies.
 */
public class Document implements Comparable<Document> {

    private static final String TAG = "Document";
    // Maps a term to the number of times this terms appears in this document.
    private HashMap<String, Integer> termFrequency;

    //All the attractions that the user has in their trips
    private String combinedAtrsDocument;
    private String userId;

    public Document(String userId, String combinedAtrsDocument) {
        this.userId = userId;
        this.combinedAtrsDocument = combinedAtrsDocument;
        termFrequency = new HashMap<String, Integer>();
        readAndPreProcess(combinedAtrsDocument);
    }

    /**
     * This method reads string and does some pre-processing:
     * Every word is converted to lower case.
     * Every character that is not a letter or a digit is removed.
     * And the termFrequency map is built.
     */
    private void readAndPreProcess(String combinedAtrsDocument) {
        String[] words = combinedAtrsDocument.split(" ");
        for (int i = 0; i < words.length; i++) {
            String filteredWord = words[i].replaceAll("[^A-Za-z0-9]", "").toLowerCase();
            words[i] = filteredWord;
            if (!(filteredWord.equalsIgnoreCase(""))) {
                if (termFrequency.containsKey(filteredWord)) {
                    int oldCount = termFrequency.get(filteredWord);
                    termFrequency.put(filteredWord, ++oldCount);

                } else {
                    termFrequency.put(filteredWord, 1);
                }
            }
        }
    }

    public double getTermFrequency(String word) {
        if (termFrequency.containsKey(word)) {
            return termFrequency.get(word);
        } else {
            return 0;
        }
    }

    /**
     * This method returns a set of all the terms which occur in this document.
     */
    public Set<String> getTermList() {
        return termFrequency.keySet();
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return combinedAtrsDocument;
    }

    @Override
    public int compareTo(Document other) {
        return userId.compareTo(other.userId);
    }
}
