package rocha.thattes;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

public class MessageBuffer<E> {
    private List<E> buffer;
    private int messageLength = 0;
    private int numMessages = 0;

    /**
     * Initalize MessageBuffer
     */
    public MessageBuffer() {
        this.buffer = new ArrayList<E>();
    }

    /** 
     * Initalize MessageBuffer's internal buffer this with expected capacity
     * The upper bound for this *should* be the `number of incoming edges` * `max path length`
     * @param expectedCapacity the expected capacity to set the internal buffer to
     */
    public MessageBuffer(int expectedCapacity) {
        this.buffer = new ArrayList<E>(expectedCapacity);
    }

    /** 
     * Appends the contents of other to the end of this MessageBuffer
     * performs a shallow copy
     * @param other the buffer to append
     */
    public synchronized void append(MessageBuffer<E> other) {
        //only appends if the the length of messages match
        if (other.messageLength == this.messageLength) {
            this.buffer.addAll(other.buffer);
            this.numMessages += other.numMessages;
        } else { //silently fail....
        }
    }

    /** 
     * Appends next param to the end of all messages in the buffer
     * eg. append(3)
     * [1,2],[2,5],[9,2] -> [1,2,3],[2,5,3],[9,2,3]
     * @param next the object to append
     */
    public void append(E next) {
        if (numMessages == 0) {
            buffer.add(next);
            this.numMessages = 1;
        } else {
            ListIterator<E> it = buffer.listIterator();
            int i = 0;
            while (it.hasNext()) {
                E curr = it.next();

                if(i == messageLength - 1){
                    it.add(next);
                }
                i++;
                i %= messageLength;
            }
        }
        this.messageLength++;
    }

    /** returns a view of the messages as a List
     */
    public List<List<E>> listView(){
        List<List<E>> view = new ArrayList<>();
        for (int i = 0; i < numMessages; i++) {
            int from = i * messageLength;
            int to = from + messageLength;
            view.add(buffer.subList(from, to));
        }
        return view;
    }
   
    /**
     * clears the buffer
     */
    public void clear() {
        buffer.clear();
    }
}
