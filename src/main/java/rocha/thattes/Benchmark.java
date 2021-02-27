package rocha.thattes;

import com.google.common.graph.ValueGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class Benchmark {
    public static void main (String[] args) {
        int iter = 100000;
        System.out.println("Starting small benchmark 3 base 3 quote `" + iter + " `iterations");
        long small = smallGraph3Base3Quote(iter);

        /*
        System.out.println("Starting medium benchmark 5 base 20 quote `" + iter + " `iterations");
        long med = mediumGraph5Base20Quote(iter);

        System.out.println("Starting large benchmark 10 base 150 quote `" + iter + " `iterations");
        long large = largeGraph10Base150Quote(iter);

        System.out.println("Starting huge benchmark 10 base 500 quote `" + iter + " `iterations");
        long huge = hugeGraph10Base500Quote(iter);
        */
        System.out.println("small benchmark 3 base 3 quote avg: " + small + " over " + iter + " iterations");
        //System.out.println("medium benchmark 3 base 3 quote avg: " + med + " over " + iter + " iterations");
        //System.out.println("large benchmark 3 base 3 quote avg: " + large + " over " + iter + " iterations");
        //System.out.println("huge benchmark 3 base 3 quote avg: " + huge + " over " + iter + " iterations");
    }

    public static long timeIter(int i, ValueGraph<Integer,Double> graph){
        long start;
        long end;
        long elapsed;
        long average = 0;
        
        while (i > 0) {
            start = System.nanoTime();
            RochaThattes.getCycles(graph);
            end = System.nanoTime();
            elapsed = end - start;
            average = (average + elapsed) / 2l;
            i--;
        }

        return average;
    }

    public static long smallGraph3Base3Quote(int i) {
        ValueGraph<Integer,Double> graph = createmodelCurrencyGraph(3,3,2);
        return timeIter(i,graph);
    }

    public static long mediumGraph5Base20Quote(int i) {
        ValueGraph<Integer,Double> graph = createmodelCurrencyGraph(5,20,2);
        return timeIter(i,graph);
    }

    public static long largeGraph10Base150Quote(int i) {
        ValueGraph<Integer,Double> graph = createmodelCurrencyGraph(10,150,2);
        return timeIter(i,graph);
    }

    public static long hugeGraph10Base500Quote(int i) {
        ValueGraph<Integer,Double> graph = createmodelCurrencyGraph(10,500,2);
        return timeIter(i,graph);
    }

    private static ValueGraph<Integer,Double> createmodelCurrencyGraph(int numBaseCurr, int numQuoteCurr, int numNegativeCycles) {
        MutableValueGraph<Integer,Double> graph = ValueGraphBuilder
            .directed()
            .allowsSelfLoops(false)
            .build();

        Set<Integer> baseCurr = fill(0, numBaseCurr);
        Set<Integer> quoteCurr = fill (numBaseCurr, numBaseCurr + numQuoteCurr);

        //System.out.println("Base Curr: " + baseCurr.toString());
        //System.out.println("Quote Curr: " + quoteCurr.toString());

        connectBase(baseCurr, graph);
        connectQuote(baseCurr, quoteCurr, numNegativeCycles, graph);

        //System.out.println("edges : " + graph.edges().toString());
        return graph;
    }

    private static Set<Integer> fill (int lower, int upper) {
        Set<Integer> set = new HashSet<>();
        for (int i = lower; i < upper; i++){
            set.add(i);
        }
        return set;
    }

    private static MutableValueGraph<Integer,Double> connectBase (Set<Integer> base, MutableValueGraph<Integer,Double> graph){
        for (int u : base) {
            for (int v : base) {
                if (u < v)
                    graph.putEdgeValue(u,v,1.0);
                else if (u == v)
                    continue;
                else
                    graph.putEdgeValue(u,v,-1.0);
            }
        }
        return graph;
    }

    private static MutableValueGraph<Integer,Double> connectQuote (Set<Integer> base, Set<Integer> quote, int neg, MutableValueGraph<Integer,Double> graph){
        for (int b : base) {
            for (int q : quote){
                graph.putEdgeValue(b,q,1.0);
                if (neg > 0){
                    graph.putEdgeValue(q,b,-6.0);
                    neg--;
                } else {
                    graph.putEdgeValue(q,b,-1.0);
                }
            }
        }
        return graph;
    }
}
