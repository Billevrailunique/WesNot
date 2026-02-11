package test;

import util.PriorityQueue;
import util.PriorityQueueMinHeap;

// Tests for PriorityQueue and PriorityQueueMinHeap classes
class PriorityQueueTest {
    private enum T {
        CAT,
        DOG,
        SAUROPOD,
        PLESIOSAUR,
        ANOMALOCARIS
    }
    public static void main(String[] args) {
        //System.out.println("First test with unsorted array priority queue:");
        PriorityQueue<T> p1 = new PriorityQueue<>();
        p1.addOrDecrease(T.SAUROPOD, 5);
        p1.addOrDecrease(T.ANOMALOCARIS, 2);
        p1.addOrDecrease(T.PLESIOSAUR, 3);
        p1.addOrDecrease(T.CAT, 0);
        p1.addOrDecrease(T.DOG, 1);
        //System.out.println(p1);

        //System.out.println(p1.peek());
        //System.out.println(p1.extractMin());
        //System.out.println(p1.extractMin());
        //System.out.println(p1.extractMin());

        //System.out.println("Second test with min heap priority queue:");

        //System.out.println("Test 1: Decrease priority reorders correctly");
        PriorityQueueMinHeap<T> heap = new PriorityQueueMinHeap<>();
        heap.addOrDecrease(T.CAT, 5);
        heap.addOrDecrease(T.DOG, 4);
        heap.addOrDecrease(T.SAUROPOD, 6);
        // Now decrease priority of SAUROPOD
        heap.addOrDecrease(T.SAUROPOD, 2);
        assert heap.extractMin() == T.SAUROPOD;

        //System.out.println("Test 2: Re-adding with worse priority has no effect");
        heap = new PriorityQueueMinHeap<>();
        heap.addOrDecrease(T.CAT, 2);
        heap.addOrDecrease(T.PLESIOSAUR, 3);
        heap.addOrDecrease(T.CAT, 5); // Should be ignored
        assert heap.extractMin() == T.CAT;

        //System.out.println("Test 3: Insert many, extract all in order");
        heap = new PriorityQueueMinHeap<>();
        heap.addOrDecrease(T.CAT, 3);
        heap.addOrDecrease(T.DOG, 1);
        heap.addOrDecrease(T.SAUROPOD, 4);
        heap.addOrDecrease(T.PLESIOSAUR, 0);
        heap.addOrDecrease(T.ANOMALOCARIS, 2);
        assert heap.extractMin() == T.PLESIOSAUR;
        assert heap.extractMin() == T.DOG;
        assert heap.extractMin() == T.ANOMALOCARIS;
        assert heap.extractMin() == T.CAT;
        assert heap.extractMin() == T.SAUROPOD;
        assert heap.extractMin() == null;

        //System.out.println("Test 4: Empty queue operations");
        heap = new PriorityQueueMinHeap<>();
        assert heap.peek() == null;
        assert heap.extractMin() == null;
    }
}
