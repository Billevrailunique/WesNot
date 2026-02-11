package util;

import java.util.ArrayList;
import java.util.HashMap;

public class PriorityQueueMinHeap<T> {
    ArrayList<Pair<T, Integer>> nodes = new ArrayList<>();
    HashMap<T, Integer> positions = new HashMap<>();

    private static int parent(int i) {
        return (i-1)/2;
    }

    private static int leftChild(int i) {
        return 2 * i + 1;
    }

    private static int rightChild(int i) {
        return 2 * i + 2;
    }

    /**
     * Returns the element that would be extracted next. This element's priority value is equal to the minimum priority value.
     * The returned element is not removed.
     * @return the element with the lowest priority. {@code null} if the queue is empty. 
     */
    public T peek() {
        if (nodes.isEmpty()) return null;
        return nodes.getFirst().first();
    }

    /**
     * Inserts an element into the priority queue.
     * If the element is already in the queue, and the new priority is lower, it'll update its priority to the new one.
     * If the new priority is higher, nothing is done.
     * @param element the element to be added
     * @param newPriority the priority that will be associated to this element
     */
    public void addOrDecrease(T element, int newPriority) {
        Integer index = positions.get(element);
        if (index != null) {
            int prevPriority = nodes.get(index).second();
            if (newPriority < prevPriority) {
                nodes.get(index).setSecond(newPriority);
                this.bubbleUp(index);
            }
        } else {
            Pair<T, Integer> newNode = new Pair<>(element, newPriority);
            nodes.add(newNode); // Possible performance improvement: don't add the newNode directly since it will likely be moved. More complex though.
            positions.put(newNode.first(), nodes.size()-1);
            this.bubbleUp(nodes.size()-1);
        }
    }

    /**
     * Bubbles up the node at {@code index} up in order to restore the min-heap property (every parent is <= their children)
     * The bubble will float upwards while its priority is lower than its parent.
     * @param index index of the node to be bubbled up
     */
    private void bubbleUp(int index) {
        if (index < 0 || index >= nodes.size()) {
            throw new IndexOutOfBoundsException("Tried to bubbleUp outside of the array. Index:" + index + ". Nodes: " + nodes);
        }
        Pair<T, Integer> bubble = nodes.get(index);
        Pair<T, Integer> parentNode = nodes.get(parent(index));
        while (index > 0 && parentNode.second() > bubble.second()) {
            // Pull the parent node down:
            nodes.set(index, parentNode);
            positions.put(parentNode.first(), index);
            // update for next loop
            index = parent(index);
            parentNode = nodes.get(parent(index));
        }
        nodes.set(index, bubble);
        positions.put(bubble.first(), index);
    }

    /**
     * Deletes and returns the lowest priority element.
     * @return lowest priority element, {@code null} if empty
     */
    public T extractMin() {
        if (nodes.isEmpty()) return null;
        if (nodes.size() == 1) {
            T res = nodes.getFirst().first();
            nodes.removeFirst();
            positions.clear();
            return res;
        }
        // Removes the last element and puts it at the start
        T res = nodes.getFirst().first();
        nodes.set(0, nodes.getLast());
        positions.put(nodes.getFirst().first(), 0);
        nodes.removeLast();
        positions.remove(res);
        // We must now sinkDown the head of the heap
        this.sinkDown(0);
        return res;
    }

    private void sinkDown(int index) {
        if (index < 0 || index >= nodes.size()) {
            throw new IndexOutOfBoundsException("Tried to sinkDown outside of the array. Index:" + index + ". Nodes: " + nodes);
        }
        Pair<T, Integer> rock = nodes.get(index);
        int iLeft = leftChild(index);
        int iRight = rightChild(index);
        while (iLeft < nodes.size()) { // we have at least one child
            // Finding the minimal Child:
            Pair<T, Integer> nMin = nodes.get(iLeft); // Temporary minimum-priority child node
            int iMin = iLeft; // Index for this minimum-priority child
            if (iRight < nodes.size() && nodes.get(iRight).second() < nMin.second()) { // If there is a right child and its priority is low
                nMin = nodes.get(iRight);
                iMin = iRight;
            }

            // Swapping the minimal child up if necessary
            // If not necessary then the rock has finished sinking
            if (nMin.second() < rock.second()) {
                nodes.set(index, nMin); // Move the smallest child up (rock will be moved down at the end)
                positions.put(nMin.first(), index);
            } else break;
            index = iMin;
            iLeft = leftChild(iMin);
            iRight = rightChild(iMin);
        }
        nodes.set(index, rock);
        positions.put(rock.first(), index);
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
        return this.nodes.toString() + "\n" + this.positions.toString();
    }

    public int size() {
        return this.nodes.size();
    }
}
