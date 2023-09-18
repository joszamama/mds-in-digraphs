package com.upo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.upo.utils.MatrixReader;
import com.upo.utils.enums.Mode;
import com.upo.utils.enums.Type;

public class Graph {

    private Set<Integer> vertices = new HashSet<>();
    private List<List<Set<Integer>>> edges = new ArrayList<>();
    private Set<Integer> mandatoryVertices = new HashSet<>();

    private List<Integer> vertexRanking = new ArrayList<>();

    private Type type;
    private Mode mode;

    public Graph(String filename, Type type, Mode mode) {
        this.type = type;
        this.mode = mode;

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

            if (type == Type.SOURCE) {
                for (int j = 0; j < edges.size(); j++) {
                    if (edges.get(j).get(0).isEmpty()) {
                        mandatoryVertices.add(j);
                    }
                }
            } else if (type == Type.SINK) {
                for (int j = 0; j < edges.size(); j++) {
                    if (edges.get(j).get(1).isEmpty()) {
                        mandatoryVertices.add(i);
                    }
                }
            }
        }

        vertexRanking = new ArrayList<>(vertices);

        if (type == Type.SOURCE) {
            Collections.sort(vertexRanking, (b, a) -> edges.get(a).get(1).size() - edges.get(b).get(1).size());
        } else if (type == Type.SINK) {
            Collections.sort(vertexRanking, (b, a) -> edges.get(a).get(0).size() - edges.get(b).get(0).size());
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

    public List<Integer> getVertexRanking() {
        return vertexRanking;
    }

    public Set<Integer> getMandatoryVertices() {
        return mandatoryVertices;
    }

    public Integer getRandomVertex() {
        return (int) (Math.random() * vertices.size());
    }

    public Integer getNextVertex(Set<Integer> vertexSet, Double alpha) {
        Integer bestVertex = -1;
        switch (mode) {
            case A:
                List<Integer> ranking = new ArrayList<>(this.vertexRanking);
                ranking.removeAll(vertexSet);
                int greedyValue = (int) Math.ceil(getEdges(ranking.get(0)).get(1).size()
                        - alpha * (getEdges(ranking.get(0)).get(1).size()
                                + getEdges(ranking.get(ranking.size() - 1)).get(1).size()));
                List<Integer> candidates = new ArrayList<>();
                for (int i = 0; i < ranking.size(); i++) {
                    if (getEdges(ranking.get(i)).get(1).size() >= greedyValue) {
                        candidates.add(ranking.get(i));
                    }
                }
                bestVertex = candidates.get((int) (Math.random() * candidates.size()));
                break;
            default:
                break;
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
        Graph graph = new Graph("random/rnd_10_20_1.txt", Type.SOURCE, Mode.A);

        Set<Integer> dominatingSet = new HashSet<>();
        dominatingSet.add(0);
        dominatingSet.add(1);
        dominatingSet.add(2);
        dominatingSet.add(4);
        dominatingSet.add(9);

        System.out.println(graph.getNextVertex(dominatingSet, 0.2));
    }
}