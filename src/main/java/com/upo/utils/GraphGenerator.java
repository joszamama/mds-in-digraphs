package com.upo.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GraphGenerator {

    public static void generateGraph(int numVertices, double connectivityPercentage, String filePath) {
        int[][] adjacencyMatrix = new int[numVertices][numVertices];

        // Set diagonal elements to 1
        for (int i = 0; i < numVertices; i++) {
            adjacencyMatrix[i][i] = 1;
        }

        Random random = new Random();

        // Set random connections based on connectivity percentage
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if (i != j && adjacencyMatrix[i][j] == 0) {
                    if (random.nextDouble() <= connectivityPercentage) {
                        adjacencyMatrix[i][j] = 1;
                    }
                }
            }
        }

        // Export the adjacency matrix to a text file
        try (FileWriter writer = new FileWriter(filePath)) {
            for (int i = 0; i < numVertices; i++) {
                for (int j = 0; j < numVertices; j++) {
                    writer.write(adjacencyMatrix[i][j] + " ");
                }
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int[] numVerticesList = { 10, 20, 30, 50, 75, 100, 150, 250, 300, 350, 400, 450, 500, 600, 700, 800, 900, 1000,
                1500, 2000, 2500, 3000, 3500, 4000, 5000 };
        double[] connectivityPercentages = { 0.2, 0.3, 0.4, 0.5 };

        for (int numVertices : numVerticesList) {
            for (double connectivityPercentage : connectivityPercentages) {
                for (int i = 0; i < 4; i++) {
                    String filePath = "./src/main/resources/graphs/random/rnd_" + numVertices + "_"
                            + (int) (connectivityPercentage * 100) + "_" + (i + 1) + ".txt";
                    generateGraph(numVertices, connectivityPercentage, filePath);
                    System.out.println("Graph generated and exported to " + filePath);
                }
            }
        }
    }

}
