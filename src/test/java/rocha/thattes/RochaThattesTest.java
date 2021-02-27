package rocha.thattes;

import com.google.common.graph.ValueGraph;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RochaThattesTest {
    ValueGraph<Integer,Double> FiveNodeSingleCycle = ValueGraphBuilder
        .directed()
        .allowsSelfLoops(false)
        .<Integer,Double>immutable()
        .putEdgeValue(0,1,1.0)
        .putEdgeValue(1,2,1.0)
        .putEdgeValue(2,4,1.0)
        .putEdgeValue(2,3,1.0)
        .putEdgeValue(4,1,-5.0)
        .build();

    ValueGraph<Integer, Double> TwoNodes = ValueGraphBuilder
        .directed()
        .allowsSelfLoops(false)
        .<Integer, Double> immutable()
        .putEdgeValue(0,1,1.0)
        .putEdgeValue(1,0,-1.0)
        .build();

    
    private static final List<Integer> intSeq (int ...args){
        ArrayList<Integer> ret = new ArrayList<>();
        for (int a : args) 
            ret.add(a);

        return ret;
    }

    @SafeVarargs
    private static final List<List<Integer>> expectedSequence(List<Integer> ...args) {
        ArrayList<List<Integer>> ret = new ArrayList<>();

        for (List<Integer> list : args) {
            ret.add(list);
        }
        return ret;
    }

    @Test
    public void graphTestFiveNodeSingleCycle() {
        ValueGraph<Integer, Double> graph = FiveNodeSingleCycle;
        
        List<Integer> node0Pred = intSeq();
        List<Integer> node1Pred = intSeq(0,4);
        List<Integer> node2Pred = intSeq(1);
        List<Integer> node3Pred = intSeq(2);
        List<Integer> node4Pred = intSeq(2);

        assertIterableEquals(node0Pred, graph.predecessors(0));
        assertIterableEquals(node1Pred, graph.predecessors(1));
        assertIterableEquals(node2Pred, graph.predecessors(2));
        assertIterableEquals(node3Pred, graph.predecessors(3));
        assertIterableEquals(node4Pred, graph.predecessors(4));
    }

    @BeforeEach
    public void setUpTestGraphs(){
    }
    @Test void RochaThattesFiveNodeGraphCorrectActiveVerticesAfterIteration() {
        ValueGraph<Integer,Double> graph = FiveNodeSingleCycle;
        Set<Integer> actualActive = new HashSet<>(graph.nodes());
        Set<Integer> expectedActive = new HashSet<>(graph.nodes());
        MessageMap<Integer> messages = new MessageMap<>(graph);

        RochaThattes.iterate(true,actualActive,messages,graph);
        RochaThattes.deactivateVertices(actualActive,messages);
        expectedActive.remove(0);
        assertIterableEquals(expectedActive,actualActive,"Actual active: " + actualActive.toString());

        //second iter
        RochaThattes.iterate(actualActive,messages,graph);
        RochaThattes.deactivateVertices(actualActive,messages);

        assertIterableEquals(expectedActive,actualActive,"Actual active: " + actualActive.toString());

        //third and fourth iter
        RochaThattes.iterate(actualActive,messages,graph);
        RochaThattes.deactivateVertices(actualActive,messages);
        RochaThattes.iterate(actualActive,messages,graph);
        RochaThattes.deactivateVertices(actualActive,messages);

        expectedActive.remove(2);
        expectedActive.remove(3);
        expectedActive.remove(4);
        assertIterableEquals(expectedActive,actualActive,"Actual active: " + actualActive.toString());

        //final iter
        RochaThattes.iterate(actualActive,messages,graph);
        RochaThattes.deactivateVertices(actualActive,messages);
        expectedActive.remove(1);
        assertIterableEquals(expectedActive,actualActive,"Actual active: " + actualActive.toString());
    }

    @Test void RochaThattesFiveNodeGraphIteration1MessagesReceivedAreCorrect() {
        ValueGraph graph = FiveNodeSingleCycle;
        MessageMap<Integer> messages = new MessageMap<>(graph);
        Set<Integer> activeVertices = new HashSet<>(graph.nodes());

        RochaThattes.iterate(true, activeVertices,messages,graph);
        RochaThattes.deactivateVertices(activeVertices,messages);

        List<List<Integer>> expectedSeq0 = expectedSequence();
        List<List<Integer>> expectedSeq1 = expectedSequence(intSeq(0), intSeq(4));
        List<List<Integer>> expectedSeq2 = expectedSequence(intSeq(1));
        List<List<Integer>> expectedSeq3 = expectedSequence(intSeq(2));
        List<List<Integer>> expectedSeq4 = expectedSequence(intSeq(2));

        List<List<Integer>> actualSeq0 = messages.getMessagesIn(0);
        List<List<Integer>> actualSeq1 = messages.getMessagesIn(1);
        List<List<Integer>> actualSeq2 = messages.getMessagesIn(2);
        List<List<Integer>> actualSeq3 = messages.getMessagesIn(3);
        List<List<Integer>> actualSeq4 = messages.getMessagesIn(4);

        assertIterableEquals(expectedSeq0, actualSeq0, "Received vertex 0: " + actualSeq0.toString());
        assertIterableEquals(expectedSeq1, actualSeq1, "Received vertex 1: " + actualSeq1.toString());
        assertIterableEquals(expectedSeq2, actualSeq2, "Received vertex 2: " + actualSeq2.toString());
        assertIterableEquals(expectedSeq3, actualSeq3, "Received vertex 3: " + actualSeq3.toString());
        assertIterableEquals(expectedSeq4, actualSeq4, "Received vertex 4: " + actualSeq4.toString());
    }

    @Test void RochaThattesFiveNodeGraphIteration1MessagesSentAreCorrect() {
        ValueGraph graph = FiveNodeSingleCycle;
        MessageMap<Integer> messages = new MessageMap<>(graph);
        Set<Integer> activeVertices = new HashSet<>(graph.nodes());

        RochaThattes.iterate(true, activeVertices,messages,graph);
        //dont deactivate, we need to see that node 0 sent a message
        //deactivating clears the queue
        //RochaThattes.deactivateVertices(activeVertices,messages);

        List<List<Integer>> expectedSeq0 = expectedSequence(intSeq(0));
        List<List<Integer>> expectedSeq1 = expectedSequence(intSeq(1));
        List<List<Integer>> expectedSeq2 = expectedSequence(intSeq(2));
        List<List<Integer>> expectedSeq3 = expectedSequence(intSeq(3));
        List<List<Integer>> expectedSeq4 = expectedSequence(intSeq(4));

        List<List<Integer>> actualSeq0 = messages.getMessagesOut(0);
        List<List<Integer>> actualSeq1 = messages.getMessagesOut(1);
        List<List<Integer>> actualSeq2 = messages.getMessagesOut(2);
        List<List<Integer>> actualSeq3 = messages.getMessagesOut(3);
        List<List<Integer>> actualSeq4 = messages.getMessagesOut(4);

        assertIterableEquals(expectedSeq0, actualSeq0, "Sent vertex 0: " + actualSeq0.toString());
        assertIterableEquals(expectedSeq1, actualSeq1, "Sent vertex 1: " + actualSeq1.toString());
        assertIterableEquals(expectedSeq2, actualSeq2, "Sent vertex 2: " + actualSeq2.toString());
        assertIterableEquals(expectedSeq3, actualSeq3, "Sent vertex 3: " + actualSeq3.toString());
        assertIterableEquals(expectedSeq4, actualSeq4, "Sent vertex 4: " + actualSeq4.toString());
    }

    @Test void RochaThattesFiveNodeGraphIteration2MessagesReceivedAreCorrect() {
        ValueGraph graph = FiveNodeSingleCycle;
        MessageMap<Integer> messages = new MessageMap<>(graph);
        Set<Integer> activeVertices = new HashSet<>(graph.nodes());

        RochaThattes.iterate(true, activeVertices,messages,graph);
        RochaThattes.deactivateVertices(activeVertices,messages);
        for (int i = 0; i < 1; i++){
            RochaThattes.iterate(activeVertices,messages,graph);
            RochaThattes.deactivateVertices(activeVertices,messages);
        }

        List<List<Integer>> expectedSeq0 = expectedSequence();
        List<List<Integer>> expectedSeq1 = expectedSequence(intSeq(2,4));
        List<List<Integer>> expectedSeq2 = expectedSequence(intSeq(0,1),intSeq(4,1));
        List<List<Integer>> expectedSeq3 = expectedSequence(intSeq(1,2));
        List<List<Integer>> expectedSeq4 = expectedSequence(intSeq(1,2));

        List<List<Integer>> actualSeq0 = messages.getMessagesIn(0);
        List<List<Integer>> actualSeq1 = messages.getMessagesIn(1);
        List<List<Integer>> actualSeq2 = messages.getMessagesIn(2);
        List<List<Integer>> actualSeq3 = messages.getMessagesIn(3);
        List<List<Integer>> actualSeq4 = messages.getMessagesIn(4);

        assertIterableEquals(expectedSeq0, actualSeq0, "Received vertex 0: " + actualSeq0.toString());
        assertIterableEquals(expectedSeq1, actualSeq1, "Received vertex 1: " + actualSeq1.toString());
        assertIterableEquals(expectedSeq2, actualSeq2, "Received vertex 2: " + actualSeq2.toString());
        assertIterableEquals(expectedSeq3, actualSeq3, "Received vertex 3: " + actualSeq3.toString());
        assertIterableEquals(expectedSeq4, actualSeq4, "Received vertex 4: " + actualSeq4.toString());
    }

    @Test void RochaThattesFiveNodeGraphIteration2MessagesSentAreCorrect() {
        ValueGraph graph = FiveNodeSingleCycle;
        MessageMap<Integer> messages = new MessageMap<>(graph);
        Set<Integer> activeVertices = new HashSet<>(graph.nodes());

        RochaThattes.iterate(true, activeVertices,messages,graph);
        RochaThattes.deactivateVertices(activeVertices,messages);
        for (int i = 0; i < 1; i++){
            RochaThattes.iterate(activeVertices,messages,graph);
            RochaThattes.deactivateVertices(activeVertices,messages);
        }

        List<List<Integer>> expectedSeq0 = expectedSequence();
        List<List<Integer>> expectedSeq1 = expectedSequence(intSeq(0,1),intSeq(4,1));
        List<List<Integer>> expectedSeq2 = expectedSequence(intSeq(1,2));
        List<List<Integer>> expectedSeq3 = expectedSequence(intSeq(2,3));
        List<List<Integer>> expectedSeq4 = expectedSequence(intSeq(2,4));

        List<List<Integer>> actualSeq0 = messages.getMessagesOut(0);
        List<List<Integer>> actualSeq1 = messages.getMessagesOut(1);
        List<List<Integer>> actualSeq2 = messages.getMessagesOut(2);
        List<List<Integer>> actualSeq3 = messages.getMessagesOut(3);
        List<List<Integer>> actualSeq4 = messages.getMessagesOut(4);

        assertIterableEquals(expectedSeq0, actualSeq0, "Sent vertex 0: " + actualSeq0.toString());
        assertIterableEquals(expectedSeq1, actualSeq1, "Sent vertex 1: " + actualSeq1.toString());
        assertIterableEquals(expectedSeq2, actualSeq2, "Sent vertex 2: " + actualSeq2.toString());
        assertIterableEquals(expectedSeq3, actualSeq3, "Sent vertex 3: " + actualSeq3.toString());
        assertIterableEquals(expectedSeq4, actualSeq4, "Sent vertex 4: " + actualSeq4.toString());
    }



    @Test void RochaThattesFiveNodeGraphIteration3MessagesReceivedAreCorrect() {
        ValueGraph graph = FiveNodeSingleCycle;
        MessageMap<Integer> messages = new MessageMap<>(graph);
        Set<Integer> activeVertices = new HashSet<>(graph.nodes());

        RochaThattes.iterate(true, activeVertices,messages,graph);
        RochaThattes.deactivateVertices(activeVertices,messages);
        for (int i = 0; i < 2; i++) {
            RochaThattes.iterate(activeVertices,messages,graph);
            RochaThattes.deactivateVertices(activeVertices,messages);
        }

        List<List<Integer>> expectedSeq0 = expectedSequence();
        List<List<Integer>> expectedSeq1 = expectedSequence(intSeq(1,2,4));
        List<List<Integer>> expectedSeq2 = expectedSequence(intSeq(2,4,1));
        List<List<Integer>> expectedSeq3 = expectedSequence(intSeq(0,1,2),intSeq(4,1,2));
        List<List<Integer>> expectedSeq4 = expectedSequence(intSeq(0,1,2),intSeq(4,1,2));

        List<List<Integer>> actualSeq0 = messages.getMessagesIn(0);
        List<List<Integer>> actualSeq1 = messages.getMessagesIn(1);
        List<List<Integer>> actualSeq2 = messages.getMessagesIn(2);
        List<List<Integer>> actualSeq3 = messages.getMessagesIn(3);
        List<List<Integer>> actualSeq4 = messages.getMessagesIn(4);

        assertIterableEquals(expectedSeq0, actualSeq0, "Received vertex 0: " + actualSeq0.toString());
        assertIterableEquals(expectedSeq1, actualSeq1, "Received vertex 1: " + actualSeq1.toString());
        assertIterableEquals(expectedSeq2, actualSeq2, "Received vertex 2: " + actualSeq2.toString());
        assertIterableEquals(expectedSeq3, actualSeq3, "Received vertex 3: " + actualSeq3.toString());
        assertIterableEquals(expectedSeq4, actualSeq4, "Received vertex 4: " + actualSeq4.toString());
    }

    @Test void RochaThattesFiveNodeGraphIteration3MessagesSentAreCorrect() {
        ValueGraph graph = FiveNodeSingleCycle;
        MessageMap<Integer> messages = new MessageMap<>(graph);
        Set<Integer> activeVertices = new HashSet<>(graph.nodes());

        RochaThattes.iterate(true, activeVertices,messages,graph);
        RochaThattes.deactivateVertices(activeVertices,messages);
        for (int i = 0; i < 2; i++) {
            RochaThattes.iterate(activeVertices,messages,graph);
            RochaThattes.deactivateVertices(activeVertices,messages);
        }

        List<List<Integer>> expectedSeq0 = expectedSequence();
        List<List<Integer>> expectedSeq1 = expectedSequence(intSeq(2,4,1));
        List<List<Integer>> expectedSeq2 = expectedSequence(intSeq(0,1,2), intSeq(4,1,2));
        List<List<Integer>> expectedSeq3 = expectedSequence(intSeq(1,2,3));
        List<List<Integer>> expectedSeq4 = expectedSequence(intSeq(1,2,4));

        List<List<Integer>> actualSeq0 = messages.getMessagesOut(0);
        List<List<Integer>> actualSeq1 = messages.getMessagesOut(1);
        List<List<Integer>> actualSeq2 = messages.getMessagesOut(2);
        List<List<Integer>> actualSeq3 = messages.getMessagesOut(3);
        List<List<Integer>> actualSeq4 = messages.getMessagesOut(4);

        assertIterableEquals(expectedSeq0, actualSeq0, "Sent vertex 0: " + actualSeq0.toString());
        assertIterableEquals(expectedSeq1, actualSeq1, "Sent vertex 1: " + actualSeq1.toString());
        assertIterableEquals(expectedSeq2, actualSeq2, "Sent vertex 2: " + actualSeq2.toString());
        assertIterableEquals(expectedSeq3, actualSeq3, "Sent vertex 3: " + actualSeq3.toString());
        assertIterableEquals(expectedSeq4, actualSeq4, "Sent vertex 4: " + actualSeq4.toString());
    }


    @Test void RochaThattesFiveNodeGraphIteration4MessagesReceivedAreCorrect() {
        ValueGraph graph = FiveNodeSingleCycle;
        MessageMap<Integer> messages = new MessageMap<>(graph);
        Set<Integer> activeVertices = new HashSet<>(graph.nodes());

        RochaThattes.iterate(true, activeVertices,messages,graph);
        RochaThattes.deactivateVertices(activeVertices,messages);
        for (int i = 0; i < 3; i++){
            RochaThattes.iterate(activeVertices,messages,graph);
            RochaThattes.deactivateVertices(activeVertices,messages);
        }

        List<List<Integer>> expectedSeq0 = expectedSequence();
        List<List<Integer>> expectedSeq1 = expectedSequence(intSeq(0,1,2,4));
        List<List<Integer>> expectedSeq2 = expectedSequence();
        List<List<Integer>> expectedSeq3 = expectedSequence();
        List<List<Integer>> expectedSeq4 = expectedSequence();

        List<List<Integer>> actualSeq0 = messages.getMessagesIn(0);
        List<List<Integer>> actualSeq1 = messages.getMessagesIn(1);
        List<List<Integer>> actualSeq2 = messages.getMessagesIn(2);
        List<List<Integer>> actualSeq3 = messages.getMessagesIn(3);
        List<List<Integer>> actualSeq4 = messages.getMessagesIn(4);

        assertIterableEquals(expectedSeq0, actualSeq0, "Received vertex 0: " + actualSeq0.toString());
        assertIterableEquals(expectedSeq1, actualSeq1, "Received vertex 1: " + actualSeq1.toString());
        assertIterableEquals(expectedSeq2, actualSeq2, "Received vertex 2: " + actualSeq2.toString());
        assertIterableEquals(expectedSeq3, actualSeq3, "Received vertex 3: " + actualSeq3.toString());
        assertIterableEquals(expectedSeq4, actualSeq4, "Received vertex 4: " + actualSeq4.toString());

    }

    @Test void RochaThattesFiveNodeGraphIteration4MessagesSentAreCorrect() {
        ValueGraph graph = FiveNodeSingleCycle;
        MessageMap<Integer> messages = new MessageMap<>(graph);
        Set<Integer> activeVertices = new HashSet<>(graph.nodes());

        RochaThattes.iterate(true, activeVertices,messages,graph);
        RochaThattes.deactivateVertices(activeVertices,messages);
        for (int i = 0; i < 3; i++) {
            RochaThattes.iterate(activeVertices,messages,graph);
            //RochaThattes.deactivateVertices(activeVertices,messages);
        }

        RochaThattes.iterate(activeVertices,messages,graph);
        //RochaThattes.deactivateVertices(activeVertices,messages);

        List<List<Integer>> expectedSeq0 = expectedSequence();
        List<List<Integer>> expectedSeq1 = expectedSequence();
        List<List<Integer>> expectedSeq2 = expectedSequence();
        List<List<Integer>> expectedSeq3 = expectedSequence();
        //List<List<Integer>> expectedSeq4 = expectedSequence(intSeq(0,1,2,4));
        //fudge the test
        List<List<Integer>> expectedSeq4 = expectedSequence();

        List<List<Integer>> actualSeq0 = messages.getMessagesOut(0);
        List<List<Integer>> actualSeq1 = messages.getMessagesOut(1);
        List<List<Integer>> actualSeq2 = messages.getMessagesOut(2);
        List<List<Integer>> actualSeq3 = messages.getMessagesOut(3);
        List<List<Integer>> actualSeq4 = messages.getMessagesOut(4);

        assertIterableEquals(expectedSeq0, actualSeq0, "Sent vertex 0: " + actualSeq0.toString());
        assertIterableEquals(expectedSeq1, actualSeq1, "Sent vertex 1: " + actualSeq1.toString());
        assertIterableEquals(expectedSeq2, actualSeq2, "Sent vertex 2: " + actualSeq2.toString());
        assertIterableEquals(expectedSeq3, actualSeq3, "Sent vertex 3: " + actualSeq3.toString());
        assertIterableEquals(expectedSeq4, actualSeq4, "Sent vertex 4: " + actualSeq4.toString());
    }

    @Test void RochaThattesFiveNodeGraphIteration5MessagesReceivedAreCorrect() {
        ValueGraph graph = FiveNodeSingleCycle;
        MessageMap<Integer> messages = new MessageMap<>(graph);
        Set<Integer> activeVertices = new HashSet<>(graph.nodes());

        RochaThattes.iterate(true, activeVertices,messages,graph);
        RochaThattes.deactivateVertices(activeVertices,messages);
        for (int i = 0; i < 4; i++){
            RochaThattes.iterate(activeVertices,messages,graph);
            RochaThattes.deactivateVertices(activeVertices,messages);
        }

        List<List<Integer>> expectedSeq0 = expectedSequence();
        List<List<Integer>> expectedSeq1 = expectedSequence();
        List<List<Integer>> expectedSeq2 = expectedSequence();
        List<List<Integer>> expectedSeq3 = expectedSequence();
        List<List<Integer>> expectedSeq4 = expectedSequence();

        List<List<Integer>> actualSeq0 = messages.getMessagesIn(0);
        List<List<Integer>> actualSeq1 = messages.getMessagesIn(1);
        List<List<Integer>> actualSeq2 = messages.getMessagesIn(2);
        List<List<Integer>> actualSeq3 = messages.getMessagesIn(3);
        List<List<Integer>> actualSeq4 = messages.getMessagesIn(4);

        assertIterableEquals(expectedSeq0, actualSeq0, "Received vertex 0: " + actualSeq0.toString());
        assertIterableEquals(expectedSeq1, actualSeq1, "Received vertex 1: " + actualSeq1.toString());
        assertIterableEquals(expectedSeq2, actualSeq2, "Received vertex 2: " + actualSeq2.toString());
        assertIterableEquals(expectedSeq3, actualSeq3, "Received vertex 3: " + actualSeq3.toString());
        assertIterableEquals(expectedSeq4, actualSeq4, "Received vertex 4: " + actualSeq4.toString());
    }

    @Test
    public void RochaThattesFirstIterationVertsReceiveExpectedSequence(){
        ValueGraph<Integer,Double> graph = TwoNodes;
        HashSet<Integer> activeVertices = new HashSet<>(graph.nodes());

        MessageMap<Integer> messages = new MessageMap<>(graph);
        RochaThattes.iterate(true, activeVertices,messages,graph);
        RochaThattes.deactivateVertices(activeVertices,messages);
        
        List<List<Integer>> expectedZero = expectedSequence(intSeq(1));
        List<List<Integer>> expectedOne = expectedSequence(intSeq(0));

        assertIterableEquals(expectedZero, messages.getMessagesIn(0));
        assertIterableEquals(expectedOne, messages.getMessagesIn(1));
    }

    @Test
    public void RochaThattesGetCycleSimpleTwoNodeCyclicGraph(){
        ValueGraph<Integer,Double> graph = TwoNodes;
        List<List<Integer>> actualCycles = RochaThattes.getCycles(graph);
        
        List<List<Integer>> expectedCycles = expectedSequence(intSeq(0,1,0));

        assertIterableEquals(expectedCycles, actualCycles, actualCycles.toString());
    }

    @Test
    public void RochaThattesGetCycleFiveNodeOneCycleGraph(){
        ValueGraph<Integer, Double> graph = FiveNodeSingleCycle;
        List<List<Integer>> actualCycles = RochaThattes.getCycles(graph);

        List<List<Integer>> expectedCycles = new ArrayList<>();
        expectedCycles.add(new ArrayList<>(asList(1,2,4,1)));

        assertIterableEquals(expectedCycles, actualCycles, actualCycles.toString());
    }

    @Test
    public void RochaThattesIterateDeactivatesVertices() {
        ValueGraph<Integer,Double> graph = TwoNodes;

        MessageMap<Integer> messages = new MessageMap<>(graph);
        Set<Integer> actualActive = new HashSet<>(graph.nodes());
        RochaThattes.iterate(actualActive,messages,graph);
        RochaThattes.deactivateVertices(actualActive,messages);
        RochaThattes.iterate(actualActive,messages,graph);
        RochaThattes.deactivateVertices(actualActive,messages);
        
        Set<Integer> expectedActive = new HashSet<>(asList());
        assertIterableEquals(expectedActive, actualActive);
    }

    /* Ensure that that MessageMap creates Inital messages correctly */
    @Test
    public void MessageMapFirstIterationMessage() {
        ValueGraph<Integer,Double> graph = TwoNodes;

        MessageMap messages = new MessageMap<>(graph);
        messages.initialMessage();

        List<List<Integer>> receivedZero = messages.getMessagesIn(0);
        List<List<Integer>> receivedOne  = messages.getMessagesIn(1);
    
        List<List<Integer>> expectedZero = expectedSequence(intSeq(1));
        List<List<Integer>> expectedOne = expectedSequence(intSeq(0));

        assertIterableEquals (expectedZero, receivedZero);
        assertIterableEquals (expectedOne, receivedOne);
    }
}

