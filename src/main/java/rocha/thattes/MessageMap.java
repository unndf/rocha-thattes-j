package rocha.thattes;

import com.google.common.graph.ValueGraph;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.ListIterator;

public class MessageMap<T> {
    private ValueGraph<T,Double> graph;
    private Map<T, List<List<T>>> msgIn;
    private Map<T, List<List<T>>> msgOut;
    private List<List<T>> cycles;

    public MessageMap(ValueGraph<T, Double> g) {
        this.graph = g;
        Set<T> verts = g.nodes();
        msgIn = new HashMap<>();
        msgOut = new HashMap<>();
        cycles = new ArrayList<>();

        for (T v : verts) {
            msgOut.put(v, new ArrayList<>());
            msgIn.put(v, new ArrayList<>());
        }
    }

    public void send(T vert) {
        clearOut(vert);
        List<List<T>> out = msgOut.get(vert);
        List<List<T>> in = msgIn.get(vert);
        for (ListIterator<List<T>> i = in.listIterator(); i.hasNext(); ){
            List<T> seq = i.next();
            //dont forward the sequence if for the sequence {v0, v1, ... v}, v == v0
            //this means we detected a cycle
            if (!seq.isEmpty() && seq.get(0).equals(vert)){
                seq.add(vert);
                if (min(seq).equals(vert)) {
                    cycles.add(seq);
                }
            } 
            //dont forward the sequence if for the sequence {v0, v1, ... vk, vk+1, ..., vk+n} seq contains vk
            //we detected a cycle and will be emitted by another vertex
            else if (!seq.isEmpty() && seq.contains(vert)) {
                continue;
            } else {
                List<T> newSeq = new ArrayList<>(seq);
                newSeq.add(vert);
                out.add(newSeq);
            }
        }
    }

    public void receive(T vert){
        clearIn(vert);
        List<List<T>> in = msgIn.get(vert);

        Set<T> preds = graph.predecessors(vert);

        for (T pred : preds) {
            List<List<T>> msgs = msgOut.get(pred);
            if (!msgs.isEmpty()) {
                msgs.forEach (m -> in.add(m));
            }
        }
    }

    public void initialMessage() {
        Set<T> verts = graph.nodes();

        for (T vert : verts) {
            List<List<T>> msg = msgOut.get(vert);
            List<T> seq = new ArrayList<>();
            seq.add(vert);
            msg.add(seq);
            msgOut.put(vert,msg);
        }

        for (T vert : verts) {
           receive(vert); 
        }
    }

    public void clearIn(T key){
        msgIn.get(key).clear();
    }

    public void clearOut(T key){
        msgOut.get(key).clear();
    }

    public List<List<T>> getMessagesIn (T vert) {
        return msgIn.get(vert);
    }

    public List<List<T>> getMessagesOut (T vert) {
        return msgOut.get(vert);
    }

    public List<List<T>> getCycles () {
        return cycles;
    }

    private T min(List<T> seq) {
        Set<T> nodes = graph.nodes();
        for (T vert : nodes) {
            if (seq.contains(vert))
                return vert;
        }
        //TODO: find a solution for needing a return value
        return null;
    }

    public boolean receivedMessages (T vert) {
        return msgIn.get(vert).isEmpty();
    }
}
