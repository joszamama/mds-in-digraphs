package com.upo.phases;

import java.util.List;
import java.util.Set;

import com.upo.Graph;
import com.upo.utils.enums.Compute;
import com.upo.utils.enums.VertexFunction;

public class FSS {

    private List<Set<Integer>> solutions;
    private List<Set<Integer>> superEliteSet;
    private List<Set<Integer>> eliteSet;

    private int stoppingCriterion;
    private int stagnationCriterion;

    private double eliteSetPercentage;
    private double superEliteSetPercentage;

    private Set<Integer> baseSolution;
    private double cutB;
    private double reconstructionAlpha;

    public FSS(List<Set<Integer>> solutions, int stoppingCriterion, int stagnationCriterion, double eliteSetPercentage,
            double superEliteSetPercentage, double cutB, double reconstructionAlpha) {
        this.solutions = solutions;
        this.stoppingCriterion = stoppingCriterion;
        this.stagnationCriterion = stagnationCriterion;
        this.eliteSetPercentage = eliteSetPercentage;
        this.superEliteSetPercentage = superEliteSetPercentage;
        this.cutB = cutB;
        this.reconstructionAlpha = reconstructionAlpha;
    }

    private void slashSolutions(List<Set<Integer>> solution) {
        superEliteSet = solution.subList(0, (int) Math.ceil(solution.size() * superEliteSetPercentage));
        eliteSet = solution.subList(0, (int) Math.ceil(solution.size() * eliteSetPercentage));

        baseSolution = superEliteSet.get((int) Math.random() * superEliteSet.size());
    }

    public static void main(String[] args) {
        Graph graph = new Graph("random/rnd_500_20_1.txt", Compute.SOURCE);
        Grasp grasp = new Grasp(graph, 100, true, 0.5, VertexFunction.GF4);

        FSS fss = new FSS(grasp.computeInitialSolution(), 1000, 100, 0.1, 1.0, 0.5, 0.5);
    }

}
