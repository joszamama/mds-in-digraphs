package com.upo.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MatrixReader {

    public static int[][] readGraph(String filename) {
        int[][] adjMatrix = null;

        try (Scanner scanner = new Scanner(new File("./src/main/resources/graphs/" + filename))) {
            String firstLine = scanner.nextLine();
            int nodes = firstLine.split(" ").length;
            adjMatrix = new int[nodes][nodes];

            // Populate the adjacency matrix with values from the file
            adjMatrix[0] = parseLine(firstLine);
            for (int i = 1; i < nodes; i++) {
                String line = scanner.nextLine();
                adjMatrix[i] = parseLine(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
            e.printStackTrace();
            return null;
        }

        return adjMatrix;

    }

    private static int[] parseLine(String line) {
        String[] parts = line.split(" ");
        int[] values = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            values[i] = Integer.parseInt(parts[i]);
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public static List<Integer>[] matrixToList(int[][] adjMatrix) {
        List<Integer>[] adjList = new ArrayList[adjMatrix.length];
        for (int i = 0; i < adjMatrix.length; i++) {
            adjList[i] = new ArrayList<>();
            for (int j = 0; j < adjMatrix[i].length; j++) {
                adjList[i].add(adjMatrix[i][j]);
            }
        }
        return adjList;
    }
}
