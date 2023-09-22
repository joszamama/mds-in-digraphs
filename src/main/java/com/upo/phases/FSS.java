package com.upo.phases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.upo.Graph;
import com.upo.utils.enums.Compute;
import com.upo.utils.enums.VertexFunction;

public class FSS {

    private Graph graph;
    private Grasp grasp;

    private List<Set<Integer>> solutions;
    private List<Set<Integer>> superEliteSet;
    private List<Set<Integer>> eliteSet;
    private List<Set<Integer>> kSet;

    private int stoppingCriterion;
    private int stagnationCriterion;

    private double eliteSetPercentage;
    private double superEliteSetPercentage;

    private Set<Integer> baseSolution;
    private double cutK;
    private double cutB;
    private double reconstructionAlpha;

    public FSS(Graph graph, Grasp grasp, int stoppingCriterion,
            int stagnationCriterion, double eliteSetPercentage, double superEliteSetPercentage, double cutK,
            double cutB, double reconstructionAlpha) {
        this.graph = graph;
        this.grasp = grasp;
        this.solutions = grasp.computeInitialSolution();
        this.stoppingCriterion = stoppingCriterion;
        this.stagnationCriterion = stagnationCriterion;
        this.eliteSetPercentage = eliteSetPercentage;
        this.superEliteSetPercentage = superEliteSetPercentage;
        this.cutK = cutK;
        this.cutB = cutB;
        this.reconstructionAlpha = reconstructionAlpha;
    }

    private void slashSolutions(List<Set<Integer>> solution) {
        superEliteSet = solution.subList(0, (int) Math.ceil(solution.size() * superEliteSetPercentage));
        eliteSet = solution.subList(0, (int) Math.ceil(solution.size() * eliteSetPercentage));

        kSet = eliteSet.subList(0, (int) Math.ceil(eliteSet.size() * cutK));
        baseSolution = superEliteSet.get((int) Math.random() * superEliteSet.size());
    }

    private Set<Integer> computeFixedSet() {
        Map<Integer, Integer> frequency = new HashMap<>();
        for (Set<Integer> solution : kSet) {
            for (Integer vertex : solution) {
                if (!graph.getMandatoryVertices().contains(vertex)) {
                    if (frequency.containsKey(vertex)) {
                        frequency.put(vertex, frequency.get(vertex) + 1);
                    } else {
                        frequency.put(vertex, 1);
                    }
                }
            }
        }
        Map<Integer, List<Integer>> groupByFrequency = new HashMap<>();
        for (Integer vertex : frequency.keySet()) {
            if (groupByFrequency.containsKey(frequency.get(vertex))) {
                groupByFrequency.get(frequency.get(vertex)).add(vertex);
            } else {
                List<Integer> list = new ArrayList<>();
                list.add(vertex);
                groupByFrequency.put(frequency.get(vertex), list);
            }
        }

        Set<Integer> fixedSet = new HashSet<>();
        int fixCount = (int) Math.ceil(cutB * baseSolution.size());
        List<Integer> keys = new ArrayList<>(groupByFrequency.keySet());
        keys.sort((a, b) -> b.compareTo(a));

        for (Integer frequencyKey : keys) {
            List<Integer> vertices = groupByFrequency.get(frequencyKey);
            for (int vertex : vertices) {
                if (fixCount <= 0) {
                    break;
                }
                if (baseSolution.contains(vertex)) {
                    fixedSet.add(vertex);
                    fixCount--;
                }
            }
        }
        return fixedSet;
    }

    public Set<Integer> regenerateSolution(Set<Integer> fixedSet) {
        while (!graph.isDominatingSet(fixedSet)) {
            fixedSet.add(grasp.getNextVertex(fixedSet, reconstructionAlpha));
        }
        fixedSet = grasp.removeRedundantVertices(fixedSet);
        return fixedSet;
    }

    public Set<Integer> computeSolution() {
        int iteration = 0;
        int stagnation = 0;
        int staggedCounter = 1;
        while (iteration < stoppingCriterion) {
            if (stagnation > stagnationCriterion) {
                staggedCounter++;
                cutB = (1 - (1 / (Math.pow(2, staggedCounter))));
                stagnation = 0;
            }
            slashSolutions(solutions);
            Set<Integer> fixedSet = computeFixedSet();
            Set<Integer> solution = regenerateSolution(fixedSet);
            if (solution.size() < eliteSet.get(eliteSet.size() - 1).size()) {
                stagnation = 0;
            } else {
                stagnation++;
            }
            solutions.add(fixedSet);
            solutions.sort((a, b) -> a.size() - b.size());
            iteration++;
        }
        return solutions.get(0);
    }

    public static void main(String[] args) {
        Graph graph = new Graph("random/rnd_5000_20_1.txt", Compute.SOURCE);
        Grasp grasp = new Grasp(graph, 100, true, 0.5, VertexFunction.GF1);

        FSS fss = new FSS(graph, grasp, 1000, 100, 1.0, 0.1, 0.3, 0.1,
                0.1);

        Set<Integer> solution = fss.computeSolution();
        System.out.println("Best found solution: " + solution + " with size: " + solution.size());
    }

}
