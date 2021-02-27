package rocha.thattes;

import com.google.common.graph.ValueGraph;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class RochaThattes {
    public static <T> List<List<T>> getCycles(ValueGraph<T,Double> graph) {
        Set<T> activeVertices = new HashSet<T>(graph.nodes());
        MessageMap<T> messages = new MessageMap(graph);
        int maxIter = activeVertices.size()+1; //stop condition just in case
        int iter = 0;
        //inital iteration
        iterate(true, activeVertices, messages, graph);
        deactivateVertices(activeVertices,messages);

        while(!activeVertices.isEmpty() && iter < maxIter){
            iterate(activeVertices, messages, graph);
            deactivateVertices(activeVertices,messages);
            iter++;
        }

        return messages.getCycles();
    }
    public static <T> void iterate (boolean inital, Set<T> activeVertices, MessageMap<T> messages, ValueGraph<T,Double> graph) {
        if (inital)
            messages.initialMessage();
        else {
            iterate(activeVertices,messages,graph);
        }
    }

    public static <T> void iterate (Set<T> activeVertices, MessageMap<T> messages, ValueGraph<T,Double> graph) {
        //send messages to all vertices
        for (T vertex : activeVertices) {
            messages.send(vertex);
        }
        //receive messages
        for (T vertex : activeVertices) {
            messages.receive(vertex);
        }
    }

    public static <T> void deactivateVertices (Set<T> activeVertices, MessageMap<T> messages) {
         for (Iterator<T> it = activeVertices.iterator(); it.hasNext(); ){
            T vertex = it.next();
            if(messages.receivedMessages(vertex)){
                it.remove();
                messages.clearOut(vertex);
                messages.clearIn(vertex);
            }
        }
   }
}
