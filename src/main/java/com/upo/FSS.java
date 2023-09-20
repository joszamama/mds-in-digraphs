package com.upo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.upo.utils.enums.VertexFunction;
import com.upo.utils.enums.Compute;

public class FSS {

    private Graph graph;

    private Integer numOfInitialSolutions;
    private Boolean uniformAlpha;
    private Double alpha;

    public FSS(Graph graph, Integer numOfInitialSolutions, Boolean uniformAlpha, Double alpha) {
        this.graph = graph;
        this.numOfInitialSolutions = numOfInitialSolutions;
        this.uniformAlpha = uniformAlpha;
        this.alpha = alpha;
    }

    public List<Set<Integer>> computeInitialSolutionGreedy() {
        List<Set<Integer>> solutions = new ArrayList<>();
        double alfa = 0.000000;

        Set<Integer> initialSolution = new HashSet<>();
        initialSolution.addAll(graph.getMandatoryVertices());

        for (int i = 0; i < numOfInitialSolutions; i++) {
            System.out.println(alfa);
            Set<Integer> solution = new HashSet<>(Set.copyOf(initialSolution));
            if (uniformAlpha) {
                while (!graph.isDominatingSet(solution)) {
                    solution.add(graph.getNextVertex(solution, alfa));
                }
                alfa = alfa + (double) 1 / numOfInitialSolutions;
            } else {
                while (!graph.isDominatingSet(solution)) {
                    solution.add(graph.getNextVertex(solution, alpha));
                }
            }
            solution = graph.removeRedundantVertices(solution);
            solutions.add(solution);
            
        }
        solutions.sort((s1, s2) -> s1.size() - s2.size());
        return solutions;
    }

    public static void main(String[] args) {
        Graph graph = new Graph("random/rnd_50_20_1.txt", Compute.SOURCE, VertexFunction.GF3);

        FSS fss = new FSS(graph, 1000, true, 0.2);

        System.out.println(fss.computeInitialSolutionGreedy());
    }
}
