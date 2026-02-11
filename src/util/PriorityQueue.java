package util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * PriorityQueue data structure: similar to a queue but each element has an associated priority.
 * Lower priority value means the element will be taken out of the list sooner.
 * 
 * @param <T> The type of elements stored in the queue.
 */
public class PriorityQueue<T> {
    ArrayList<Pair<T, Integer>> nodes = new ArrayList<>();
    HashMap<T, Integer> positions = new HashMap<>();

    /**
     * Returns the element that would be extracted next. This element's priority value is equal to the minimum priority value.
     * The returned element is not removed.
     * @return the element with the lowest priority. {@code null} if the queue is empty. 
     */
    public T peek() {
        if (nodes.isEmpty()) return null;
        Pair<T, Integer> min = nodes.get(0);
        for (int i = 0; i < nodes.size(); i++) {
            Pair<T, Integer> node = nodes.get(i);
            if (node.second() < min.second()) { // keeping lowest priority
                min = node;
            }
        }
        return min.first();
    }

    /**
     * Inserts an element into the priority queue.
     * If the element is already in the queue, and the new priority is lower, it'll update its priority to the new one.
     * If the new priority is higher, nothing is done.
     * @param element the element to be added
     * @param priority the priority that will be associated to this element
     */
    public void addOrDecrease(T element, int priority) {
        Integer index = positions.get(element);
        if (index != null) {
            int prevPriority = nodes.get(index).second();
            if (priority < prevPriority) nodes.get(index).setSecond(priority);
        } else {
            positions.put(element, nodes.size());
            nodes.add(new Pair<T, Integer>(element, priority));
        }
    }

    /**
     * Deletes and returns the lowest priority element.
     * @return lowest priority element, {@code null} if empty
     */
    public T extractMin() {
        if (nodes.isEmpty()) return null;
        int minIndex = 0;
        Pair<T, Integer> min = nodes.get(0);
        for (int i = 0; i < nodes.size(); i++) {
            Pair<T, Integer> node = nodes.get(i);
            if (node.second() < min.second()) { // keeping lowest priority
                min = node;
                minIndex = i;
            }
        }

        for (int i = minIndex; i < nodes.size()-1; i++) {
            nodes.set(i, nodes.get(i+1));
            positions.put(nodes.get(i).first(), i);
        }
        nodes.remove(nodes.size() - 1);
        positions.remove(min);
        return min.first();
    }

    /**
     * Checks if the queue is empty
     * @return {@code true} if the queue contains no elements, otherwise {@code false}
     */
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public String toString() {
        return this.nodes.toString();
    }

    public int size() {
        return this.nodes.size();
    }
}