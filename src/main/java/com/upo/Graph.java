package com.upo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.upo.utils.MatrixReader;

public class Graph {

    private Set<Integer> vertices = new HashSet<>();
    private List<List<Set<Integer>>> edges = new ArrayList<>();
    private Set<Integer> mandatoryVertices = new HashSet<>();
    private Type type;

    public Graph(String filename, Type type) {
        List<Integer>[] adjacencyList = MatrixReader.matrixToList(MatrixReader.readGraph(filename));

        for (int i = 0; i < adjacencyList.length; i++) {
            vertices.add(i);
            edges.add(new ArrayList<>());

            Set<Integer> incomingEdges = new HashSet<>();
            Set<Integer> outgoingEdges = new HashSet<>();

            for (int j = 0; j < adjacencyList[i].size(); j++) {
                if (adjacencyList[i].get(j) == 1 && i != j) {
                    outgoingEdges.add(j);
                }
            }

            for (int j = 0; j < adjacencyList.length; j++) {
                if (adjacencyList[j].get(i) == 1 && i != j) {
                    incomingEdges.add(j);
                }
            }

            edges.get(i).add(incomingEdges);
            edges.get(i).add(outgoingEdges);
        }

        for (int i = 0; i < edges.size(); i++) {
            if (edges.get(i).get(0).isEmpty() && edges.get(i).get(1).isEmpty()) {
                mandatoryVertices.add(i);
            }
        }

        if (type == Type.SOURCE) {
            this.type = Type.SOURCE;
            for (int i = 0; i < edges.size(); i++) {
                if (edges.get(i).get(0).isEmpty()) {
                    mandatoryVertices.add(i);
                }
            }
        } else if (type == Type.SINK) {
            this.type = Type.SINK;
            for (int i = 0; i < edges.size(); i++) {
                if (edges.get(i).get(1).isEmpty()) {
                    mandatoryVertices.add(i);
                }
            }
        }

    }

    public Set<Integer> getVertices() {
        return vertices;
    }

    public List<List<Set<Integer>>> getEdges() {
        return edges;
    }

    public List<Set<Integer>> getEdges(int vertex) {
        return edges.get(vertex);
    }

    public Set<Integer> getMandatoryVertices() {
        return mandatoryVertices;
    }

    public Set<Integer> dominates(Set<Integer> dominatingSet) {
        Set<Integer> dominatedVertices = new HashSet<>();
        if (this.type == Type.SOURCE) {
            for (int vertex : dominatingSet) {
                dominatedVertices.add(vertex);
                dominatedVertices.addAll(edges.get(vertex).get(1));
            }
        } else if (this.type == Type.SINK) {
            for (int vertex : dominatingSet) {
                dominatedVertices.add(vertex);
                dominatedVertices.addAll(edges.get(vertex).get(0));
            }
        }
        return dominatedVertices;
    }

    public boolean isDominatingSet(Set<Integer> dominatingSet) {
        Set<Integer> dominatedVertices = dominates(dominatingSet);
        return dominatedVertices.containsAll(vertices);
    }

    public static void main(String[] args) {
        Graph graph = new Graph("test/b.txt", Type.SOURCE);

        System.out.println(graph.getVertices());
        System.out.println(graph.getEdges());
        System.out.println(graph.getMandatoryVertices());

        Set<Integer> dominatingSet = new HashSet<>();
        dominatingSet.add(0);
        dominatingSet.add(3);

        System.out.println(graph.dominates(dominatingSet));
        System.out.println(graph.isDominatingSet(dominatingSet));
    }
}