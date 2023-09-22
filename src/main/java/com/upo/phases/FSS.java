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

    // tanto el elite como el superelite no se van haciendo mas grande, siempre son el 10% y el 25% de lo que era inicialmente
    private double eliteSetPercentage;
    private double superEliteSetPercentage;

    private Set<Integer> baseSolution;
    private double cutK;
    private double cutB = 0.5;
    private double reconstructionAlpha;

    private long seconds;

    public FSS(Graph graph, Grasp grasp, int stoppingCriterion,
            int stagnationCriterion, double eliteSetPercentage, double superEliteSetPercentage, double cutK,
            double reconstructionAlpha) {
        this.graph = graph;
        this.grasp = grasp;
        this.stoppingCriterion = stoppingCriterion;
        this.stagnationCriterion = stagnationCriterion;
        this.eliteSetPercentage = eliteSetPercentage;
        this.superEliteSetPercentage = superEliteSetPercentage;
        this.cutK = cutK;
        this.reconstructionAlpha = reconstructionAlpha;
    }

    // quedarme con el 100% de las mejores soluciones inicialmente
    private void slashSolutions(List<Set<Integer>> solution) {
        this.superEliteSet = solution.subList(0, (int) Math.ceil(solution.size() * superEliteSetPercentage));
        this.eliteSet = solution.subList(0, (int) Math.ceil(solution.size() * eliteSetPercentage));

        this.kSet = eliteSet.subList(0, (int) Math.ceil(eliteSet.size() * cutK));
        this.baseSolution = superEliteSet.get((int) Math.random() * superEliteSet.size());
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
        long startTime = System.currentTimeMillis();
        this.solutions = grasp.computeInitialSolution();
        int iteration = 0;
        int stagnation = 0;
        int staggedCounter = 1;
        while (iteration < stoppingCriterion) {
            if (stagnation >= stagnationCriterion) {
                System.out.println("Stagged");
                System.out.println("Previous CutB: " + cutB);
                staggedCounter++;
                cutB = (1 - (1 / (Math.pow(2, staggedCounter))));
                stagnation = 0;
                System.out.println("New CutB: " + cutB);
            }
            slashSolutions(solutions);
            Set<Integer> fixedSet = computeFixedSet();
            Set<Integer> solution = regenerateSolution(fixedSet);
            if (solution.size() < eliteSet.get(eliteSet.size() - 1).size()) {
                stagnation = 0;
            } else {
                stagnation++;
            }
            this.solutions.add(fixedSet);
            this.solutions.sort((a, b) -> a.size() - b.size());
            iteration++;
        }
        long endTime = System.currentTimeMillis();
        seconds = endTime - startTime;
        System.out.println("Seconds: " + (double) seconds / 1000);
        return solutions.get(0);
    }

    public static void main(String[] args) {
        Graph graph = new Graph("random/rnd_2000_20_1.txt", Compute.SOURCE);
        Grasp grasp = new Grasp(graph, 100, true, 0.5, VertexFunction.GF1);

        FSS fss = new FSS(graph, grasp, 1000, 100, 0.25, 0.1, 0.3,
                0.2);

        Set<Integer> solution = fss.computeSolution();
        System.out.println("Best found solution: " + solution + " with size: " + solution.size());
    }

}
