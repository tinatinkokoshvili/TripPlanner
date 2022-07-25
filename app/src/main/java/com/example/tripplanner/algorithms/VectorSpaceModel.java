package com.example.tripplanner.algorithms;

import com.example.tripplanner.models.Corpus;
import com.example.tripplanner.models.Document;

import java.util.HashMap;
import java.util.Set;

/**
 * This class takes a corpus and creates the tf-idf vectors for each document and computes
 * the cosine similarity between documents.
 */
public class VectorSpaceModel {
    private static final String TAG = "VectorSpaceModel";
    private Corpus corpus;

    /**
     * The tf-idf weight vectors.
     * The hashmap maps a document to another hashmap.
     * The second hashmap maps a term to its tf-idf weight for this document.
     */
    private HashMap<Document, HashMap<String, Double>> tfIdfWeights;

    /**
     * Using the corpus, it will generate tf-idf vectors for each document.
     */
    public VectorSpaceModel(Corpus corpus) {
        this.corpus = corpus;
        tfIdfWeights = new HashMap<Document, HashMap<String, Double>>();

        createTfIdfWeights();
    }

    private void createTfIdfWeights() {
        Set<String> terms = corpus.getInvertedIndex().keySet();

        for (Document document : corpus.getDocuments()) {
            HashMap<String, Double> weights = new HashMap<String, Double>();

            for (String term : terms) {
                double tf = document.getTermFrequency(term);
                double idf = corpus.getInverseDocumentFrequency(term);
                double weight = tf * idf;
                weights.put(term, weight);
            }
            tfIdfWeights.put(document, weights);
        }
    }

    /**
     * This method returns the magnitude of a vector.
     */
    private double getMagnitude(Document document) {
        double magnitude = 0;
        HashMap<String, Double> weights = tfIdfWeights.get(document);

        for (double weight : weights.values()) {
            magnitude += weight * weight;
        }

        return Math.sqrt(magnitude);
    }

    /**
     * This function takes two documents and return the dot product.
     */
    private double getDotProduct(Document d1, Document d2) {
        double product = 0;
        HashMap<String, Double> weights1 = tfIdfWeights.get(d1);
        HashMap<String, Double> weights2 = tfIdfWeights.get(d2);

        for (String term : weights1.keySet()) {
            product += weights1.get(term) * weights2.get(term);
        }

        return product;
    }

    /**
     * Returns the cosine similarity of two documents.
     * Ranges from 0 (not similar) to 1 (very similar).
     */
    public double cosineSimilarity(Document d1, Document d2) {
        return getDotProduct(d1, d2) / (getMagnitude(d1) * getMagnitude(d2));
    }
}