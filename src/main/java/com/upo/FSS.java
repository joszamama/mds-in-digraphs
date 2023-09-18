package com.upo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.upo.utils.enums.Mode;
import com.upo.utils.enums.Type;

public class FSS {

    private Graph graph;

    private Integer numOfInitialSolutions;
    private Double alpha;

    public FSS(Graph graph, Integer numOfInitialSolutions, Double alpha) {
        this.graph = graph;
        this.numOfInitialSolutions = numOfInitialSolutions;
        this.alpha = alpha;
    }

    public List<Set<Integer>> computeInitialSolution() {
        List<Set<Integer>> solutions = new ArrayList<>();

        Set<Integer> initialSolution = new HashSet<>();
        initialSolution.addAll(graph.getMandatoryVertices());

        for (int i = 0; i < numOfInitialSolutions; i++) {
            Set<Integer> solution = new HashSet<>(Set.copyOf(initialSolution));
            while (!graph.isDominatingSet(solution)) {
                solution.add(graph.getNextVertex(solution, alpha));
            }
            solution = graph.removeRedundantVertices(solution);
            solutions.add(solution);
        }

        solutions.sort((s1, s2) -> s1.size() - s2.size());

        return solutions;
    }

    public static void main(String[] args) {
        Graph graph = new Graph("random/rnd_5000_20_1.txt", Type.SOURCE, Mode.A);

        FSS fss = new FSS(graph, 200, 0.3);

        System.out.println(fss.computeInitialSolution());
    }
}
