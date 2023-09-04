package com.upo;

import java.util.HashSet;
import java.util.Set;

import com.upo.utils.Type;

public class FSS {
    
    private Graph graph;
    private Set<Integer> initialSolution = new HashSet<>();
    private Set<Integer> bestSolution = new HashSet<>();
    private long time = 0;

    public FSS(Graph graph) {
        this.graph = graph;
    }

    public Set<Integer> getInitialSolution() {
        return initialSolution;
    }

    public Set<Integer> getBestSolution() {
        return bestSolution;
    }

    public long getTime() {
        return time / 1000000000;
    }

    public void computeInitialSolution() {
        long startTime = System.nanoTime();
        initialSolution.addAll(graph.getMandatoryVertices());
        while (!graph.isDominatingSet(initialSolution)) {
            initialSolution.add(graph.getNextBestVertex(initialSolution));
        }
        initialSolution = graph.removeRedundantVertices(initialSolution);
        long endTime = System.nanoTime();
        time = endTime - startTime;
    }

    public static void main(String[] args) {
        Graph graph = new Graph("random/rnd_20_20_1.txt", Type.SINK);

        FSS fss = new FSS(graph);
        fss.computeInitialSolution();

        System.out.println(fss.getInitialSolution());
        System.out.println(fss.getTime());
    }
}
