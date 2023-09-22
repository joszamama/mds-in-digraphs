package com.upo.phases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.upo.utils.enums.VertexFunction;
import com.upo.Graph;
import com.upo.utils.enums.Compute;

public class Grasp {

    private Graph graph;

    private Integer numOfInitialSolutions;
    private Boolean uniformAlpha;
    private Double alpha;
    private VertexFunction mode;

    public Grasp(Graph graph, Integer numOfInitialSolutions, Boolean uniformAlpha, Double alpha,
            VertexFunction mode) {
        this.graph = graph;
        this.numOfInitialSolutions = numOfInitialSolutions;
        this.uniformAlpha = uniformAlpha;
        this.alpha = alpha;
        this.mode = mode;
    }

    public List<Set<Integer>> computeInitialSolution() {
        List<Set<Integer>> solutions = new ArrayList<>();
        double alfa = 0.000000;

        Set<Integer> initialSolution = new HashSet<>();
        initialSolution.addAll(graph.getMandatoryVertices());

        for (int i = 0; i < numOfInitialSolutions; i++) {
            Set<Integer> solution = new HashSet<>(Set.copyOf(initialSolution));
            if (uniformAlpha) {
                while (!graph.isDominatingSet(solution)) {
                    solution.add(getNextVertex(solution, alfa));
                }
                alfa = alfa + (double) 1 / numOfInitialSolutions;
            } else {
                while (!graph.isDominatingSet(solution)) {
                    solution.add(getNextVertex(solution, alpha));
                }
            }
            solution = removeRedundantVertices(solution);
            solutions.add(solution);

        }
        solutions.sort((s2, s1) -> s1.size() - s2.size());
        return solutions;
    }

    public Integer getNextVertex(Set<Integer> vertexSet, Double alpha) {
        Integer selectedVertex = -1;
        switch (mode) {
            case GF1: {
                List<Integer> ranking = new ArrayList<>(graph.getVertexRanking());
                ranking.removeAll(vertexSet);
                ranking.removeAll(graph.dominates(vertexSet));

                int threshold = (int) Math.ceil(graph.getEdges(ranking.get(0)).get(1).size()
                        - (alpha * (graph.getEdges(ranking.get(0)).get(1).size()
                                - graph.getEdges(ranking.get(ranking.size() - 1)).get(1).size())));

                List<Integer> candidates = new ArrayList<>();
                for (int i = 0; i < ranking.size(); i++) {
                    if (graph.getEdges(ranking.get(i)).get(1).size() >= threshold) {
                        candidates.add(ranking.get(i));
                    }
                }
                selectedVertex = candidates.get((int) (Math.random() * candidates.size()));
            }
                break;
            case GF2: {
                List<Integer> ranking = new ArrayList<>(graph.getVertexRanking());
                ranking.removeAll(vertexSet);

                int threshold = (int) Math.ceil(graph.getEdges(ranking.get(0)).get(1).size()
                        - (alpha * (graph.getEdges(ranking.get(0)).get(1).size()
                                - graph.getEdges(ranking.get(ranking.size() - 1)).get(1).size())));

                List<Integer> candidates = new ArrayList<>();
                for (int i = 0; i < ranking.size(); i++) {
                    if (graph.getEdges(ranking.get(i)).get(1).size() >= threshold) {
                        candidates.add(ranking.get(i));
                    }
                }
                selectedVertex = candidates.get((int) (Math.random() * candidates.size()));
            }
                break;
            case GF3: {
                Map<Integer, List<Integer>> vertexDynamicRanking = new HashMap<>();
                for (int vertex : graph.getVertices()) {
                    Set<Integer> dominatedVerticesByVertex = new HashSet<>(Set.of(vertex));
                    dominatedVerticesByVertex.removeAll(vertexSet);
                    dominatedVerticesByVertex.removeAll(graph.dominates(vertexSet));
                    int numberOfDominatedVertices = dominatedVerticesByVertex.size();

                    if (vertexDynamicRanking.containsKey(numberOfDominatedVertices)) {
                        vertexDynamicRanking.get(numberOfDominatedVertices).add(vertex);
                    } else {
                        List<Integer> verticesWithSameRanking = new ArrayList<>();
                        verticesWithSameRanking.add(vertex);
                        vertexDynamicRanking.put(numberOfDominatedVertices, verticesWithSameRanking);
                    }
                }
                int threshold = (int) Math.ceil(Collections.max(vertexDynamicRanking.keySet())
                        - (alpha * (Collections.max(vertexDynamicRanking.keySet())
                                - Collections.min(vertexDynamicRanking.keySet()))));

                List<Integer> candidates = new ArrayList<>();
                for (int i = 0; i < vertexDynamicRanking.get(threshold).size(); i++) {
                    if (graph.getEdges(vertexDynamicRanking.get(threshold).get(i)).get(1).size() >= threshold) {
                        candidates.add(vertexDynamicRanking.get(threshold).get(i));
                    }
                }
                selectedVertex = candidates.get((int) (Math.random() * candidates.size()));
            }
                break;
            case GF4: {
                Map<Integer, List<Integer>> vertexDynamicRanking = new HashMap<>();

                for (int vertex : graph.getVertices()) {
                    Set<Integer> dominatedVerticesByVertex = new HashSet<>(Set.of(vertex));
                    dominatedVerticesByVertex.removeAll(vertexSet);
                    int numberOfDominatedVertices = dominatedVerticesByVertex.size();

                    if (vertexDynamicRanking.containsKey(numberOfDominatedVertices)) {
                        vertexDynamicRanking.get(numberOfDominatedVertices).add(vertex);
                    } else {
                        List<Integer> verticesWithSameRanking = new ArrayList<>();
                        verticesWithSameRanking.add(vertex);
                        vertexDynamicRanking.put(numberOfDominatedVertices, verticesWithSameRanking);
                    }
                }
                int threshold = (int) Math.ceil(Collections.max(vertexDynamicRanking.keySet())
                        - (alpha * (Collections.max(vertexDynamicRanking.keySet())
                                - Collections.min(vertexDynamicRanking.keySet()))));

                List<Integer> candidates = new ArrayList<>();
                for (int i = 0; i < vertexDynamicRanking.get(threshold).size(); i++) {
                    if (graph.getEdges(vertexDynamicRanking.get(threshold).get(i)).get(1).size() >= threshold) {
                        candidates.add(vertexDynamicRanking.get(threshold).get(i));
                    }
                }
                selectedVertex = candidates.get((int) (Math.random() * candidates.size()));
            }
                break;
            default:
                break;
        }
        return selectedVertex;
    }

    public Set<Integer> removeRedundantVertices(Set<Integer> dominatingSet) {
        boolean isRedundant = false;

        List<Integer> checkingSet = new ArrayList<>(dominatingSet);
        checkingSet.sort((a, b) -> graph.getEdges(a).get(1).size() - graph.getEdges(b).get(1).size());
        List<Integer> runningSet = new ArrayList<>(checkingSet);

        for (int i = 0; i < runningSet.size(); i++) {
            int vertexToRemove = checkingSet.get(i);
            checkingSet.remove(i);
            if (graph.isDominatingSet(new HashSet<>(checkingSet))) {
                dominatingSet.remove(vertexToRemove);
                isRedundant = true;
                break;
            } else {
                checkingSet.add(vertexToRemove);
            }
        }
        if (isRedundant) {
            dominatingSet = removeRedundantVertices(dominatingSet);
        }
        return dominatingSet;
    }

    public static void main(String[] args) {
        Graph graph = new Graph("random/rnd_500_20_1.txt", Compute.SOURCE);

        Grasp grasp = new Grasp(graph, 100, true, 0.5, VertexFunction.GF4);
    }
}
