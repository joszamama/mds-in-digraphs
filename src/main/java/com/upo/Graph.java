package com.upo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.upo.utils.MatrixReader;
import com.upo.utils.Type;

public class Graph {

    private Set<Integer> vertices = new HashSet<>();
    private List<List<Set<Integer>>> edges = new ArrayList<>();
    private Set<Integer> mandatoryVertices = new HashSet<>();
    private HashMap<Integer, Set<Integer>> vertexRanking = new HashMap<>();
    private Type type;

    public Graph(String filename, Type type) {
        List<Integer>[] adjacencyList = MatrixReader.matrixToList(MatrixReader.readGraph(filename));

        this.type = type;
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

            if (type == Type.SOURCE) {
                if (vertexRanking.containsKey(outgoingEdges.size())) {
                    vertexRanking.get(outgoingEdges.size()).add(i);
                } else {
                    Set<Integer> verticesWithSameRanking = new HashSet<>();
                    verticesWithSameRanking.add(i);
                    vertexRanking.put(outgoingEdges.size(), verticesWithSameRanking);
                }

                for (int j = 0; j < edges.size(); j++) {
                    if (edges.get(j).get(0).isEmpty()) {
                        mandatoryVertices.add(j);
                    }
                }
            } else if (type == Type.SINK) {
                if (vertexRanking.containsKey(incomingEdges.size())) {
                    vertexRanking.get(incomingEdges.size()).add(i);
                } else {
                    Set<Integer> verticesWithSameRanking = new HashSet<>();
                    verticesWithSameRanking.add(i);
                    vertexRanking.put(incomingEdges.size(), verticesWithSameRanking);
                }
                for (int j = 0; j < edges.size(); j++) {
                    if (edges.get(j).get(1).isEmpty()) {
                        mandatoryVertices.add(i);
                    }
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

    public HashMap<Integer, Set<Integer>> getVertexRanking() {
        return vertexRanking;
    }

    public Set<Integer> getMandatoryVertices() {
        return mandatoryVertices;
    }

    public Integer getNextBestVertexFast(Set<Integer> vertexSet) {
        Integer bestVertex = -1;

        while (bestVertex == -1) {
            for (int degree : vertexRanking.keySet()) {
                for (int vertex : vertexRanking.get(degree)) {
                    if (!vertexSet.contains(vertex)) {
                        bestVertex = vertex;
                        break;
                    }
                }
            }
        }
        return bestVertex;
    }

    // en funcion del grado
    public Integer getNextBestVertexSlow(Set<Integer> vertexSet) {
        Set<Integer> dominatedVertices = dominates(vertexSet);
        Integer bestVertex = -1;

        while (bestVertex == -1) {
            for (int degree : vertexRanking.keySet()) {
                for (int vertex : vertexRanking.get(degree)) {
                    if (!dominatedVertices.contains(vertex)) {
                        bestVertex = vertex;
                        break;
                    }
                }
            }
        }
        return bestVertex;
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

    // check the time complexity of this method - ve eliminando los vertices que
    // tengan menor grado a mas
    public Set<Integer> removeRedundantVertices(Set<Integer> dominatingSet) {
        List<Integer> checkingSet = new ArrayList<>(dominatingSet);
        for (int i = 0; i < checkingSet.size(); i++) {
            Set<Integer> newDominatingSet = new HashSet<>(dominatingSet);
            newDominatingSet.remove(checkingSet.get(i));
            if (isDominatingSet(newDominatingSet)) {
                dominatingSet.remove(checkingSet.get(i));
            }
        }
        return dominatingSet;
    }

    public static void main(String[] args) {
        Graph graph = new Graph("random/rnd_20_20_1.txt", Type.SOURCE);

        Set<Integer> dominatingSet = new HashSet<>();
        dominatingSet.add(0);
        dominatingSet.add(1);
        dominatingSet.add(2);
        dominatingSet.add(4);
        dominatingSet.add(9);

        System.out.println(graph.getNextBestVertexFast(dominatingSet));
    }
}