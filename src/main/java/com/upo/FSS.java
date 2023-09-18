package com.upo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.upo.utils.enums.Mode;
import com.upo.utils.enums.Type;

public class FSS {

    private Graph graph;

    public FSS(Graph graph) {
        this.graph = graph;
    }

    public List<Set<Integer>> computeInitialSolution(int numOfSolutions) {
        List<Set<Integer>> solutions = new ArrayList<>();

        Set<Integer> initialSolution = new HashSet<>();
        initialSolution.addAll(graph.getMandatoryVertices());

        for (int i = 0; i < numOfSolutions; i++) {
            Set<Integer> solution = new HashSet<>(Set.copyOf(initialSolution));
            while (!graph.isDominatingSet(solution)) {
                solution.add(graph.getNextVertex(solution));
            }
            solution = graph.removeRedundantVertices(solution);
            solutions.add(solution);
        }

        return solutions;
    }

    public static void main(String[] args) {
        Graph graph = new Graph("random/rnd_500_50_1.txt", Type.SOURCE, Mode.RANDOM);

        FSS fss = new FSS(graph);

        System.out.println(fss.computeInitialSolution(5));
    }
}
