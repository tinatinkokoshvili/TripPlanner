package com.example.tripplanner.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class represents a corpus of documents and will create an inverted index for these documents.
 */
public class Corpus {
    private ArrayList<Document> documents;
    private HashMap<String, Set<Document>> invertedIndex;

    public Corpus(ArrayList<Document> documents) {
        this.documents = documents;
        invertedIndex = new HashMap<String, Set<Document>>();

        createInvertedIndex();
    }

    private void createInvertedIndex() {
        for (Document document : documents) {
            Set<String> terms = document.getTermList();

            for (String term : terms) {
                if (invertedIndex.containsKey(term)) {
                    Set<Document> list = invertedIndex.get(term);
                    list.add(document);
                } else {
                    Set<Document> list = new TreeSet<Document>();
                    list.add(document);
                    invertedIndex.put(term, list);
                }
            }
        }
    }

    public double getInverseDocumentFrequency(String term) {
        if (invertedIndex.containsKey(term)) {
            double size = documents.size();
            Set<Document> list = invertedIndex.get(term);
            double documentFrequency = list.size();

            return Math.log10(size / documentFrequency);
        } else {
            return 0;
        }
    }

    public ArrayList<Document> getDocuments() {
        return documents;
    }

    public HashMap<String, Set<Document>> getInvertedIndex() {
        return invertedIndex;
    }
}
