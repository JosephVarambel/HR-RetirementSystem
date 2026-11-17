package com.hrms.retirement;

import java.util.ArrayList;
import java.util.Comparator;

// Min/Max Heap implementation for tracking top retirement priority milestones
public class RetirementPriorityQueue {
    private ArrayList<Employee> heap;
    private Comparator<Employee> comparator;

    // Constructor: Takes custom comparator (e.g., sorting highest eligibility score first)
    public RetirementPriorityQueue(Comparator<Employee> comparator) {
        this.heap = new ArrayList<>();
        this.comparator = comparator;
    }

    public void insert(Employee emp) {
        heap.add(emp);
        heapifyUp(heap.size() - 1);
    }

    public Employee extractTop() {
        if (heap.isEmpty()) return null;
        Employee top = heap.get(0);
        Employee last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
        }
        return top;
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (comparator.compare(heap.get(index), heap.get(parent)) < 0) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void heapifyDown(int index) {
        int size = heap.size();
        while (index < size) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size && comparator.compare(heap.get(left), heap.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < size && comparator.compare(heap.get(right), heap.get(smallest)) < 0) {
                smallest = right;
            }
            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        Employee temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }
}