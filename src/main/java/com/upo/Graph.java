package com.upo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.upo.utils.MatrixReader;

public class Graph {

    private Set<Integer> vertices = new HashSet<>();
    private List<Set<Integer>> edges = new ArrayList<>();
    private Set<Integer> supportVertices = new HashSet<>();

    public Graph(String filename) {
        List<Integer>[] adjacencyList = MatrixReader.matrixToList(MatrixReader.readGraph(filename));

        for (int i = 0; i < adjacencyList.length; i++) {
            vertices.add(i);

            Set<Integer> edgesOfI = new HashSet<>();
            List<Integer> list = adjacencyList[i];

            for (int j = 0; j < list.size(); j++) {
                if (list.get(j) == 1 && i != j) {
                    edgesOfI.add(j);
                }
            }
            edges.add(edgesOfI);
        }

        for (int i = 0; i < edges.size(); i++) {
            if (edges.get(i).size() == 0) {
                supportVertices.add(i);
            }
        }
    }

    public Set<Integer> getVertices() {
        return vertices;
    }

    public List<Set<Integer>> getEdges() {
        return edges;
    }

    public Set<Integer> getEdges(int i) {
        return edges.get(i);
    }

    public Set<Integer> getSupportVertices() {
        return supportVertices;
    }

    public static void main(String[] args) {
        Graph graph = new Graph("random/rnd_10_20_1.txt");

        System.out.println(graph.getVertices());
        System.out.println(graph.getEdges());
        System.out.println(graph.getSupportVertices());
    }
}