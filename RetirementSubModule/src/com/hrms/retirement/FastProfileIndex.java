package com.hrms.retirement;

import java.util.ArrayList;
import java.util.List;

// Fast Profile Index implementing a custom Hash Table with Quadratic Probing to prevent collisions
public class FastProfileIndex {
    private static class HashNode {
        int key;
        Employee value;
        boolean isDeleted;

        HashNode(int key, Employee value) {
            this.key = key;
            this.value = value;
            this.isDeleted = false;
        }
    }

    private HashNode[] table;
    private int capacity;
    private int size;

    public FastProfileIndex(int capacity) {
        this.capacity = capacity;
        this.table = new HashNode[capacity];
        this.size = 0;
    }

    private int hashFunction(int key) {
        return Math.abs(key) % capacity;
    }

    // ADD OPERATION: Put new record into the hash index using quadratic probing
    public void put(int key, Employee value) {
        if (size >= capacity / 2) {
            resize();
        }
        int hash = hashFunction(key);
        int i = 0;
        while (table[(hash + i * i) % capacity] != null && !table[(hash + i * i) % capacity].isDeleted) {
            if (table[(hash + i * i) % capacity].key == key) {
                table[(hash + i * i) % capacity].value = value; // Update value if key exists
                return;
            }
            i++;
        }
        int index = (hash + i * i) % capacity;
        table[index] = new HashNode(key, value);
        size++;
    }

    // LOOKUP / SEARCH OPERATION: O(1) average time search by key
    public Employee get(int key) {
        int hash = hashFunction(key);
        int i = 0;
        while (table[(hash + i * i) % capacity] != null) {
            int index = (hash + i * i) % capacity;
            if (!table[index].isDeleted && table[index].key == key) {
                return table[index].value;
            }
            i++;
            if (i > capacity) break; // Safety exit loop
        }
        return null;
    }

    // REMOVAL OPERATION: Remove employee record using soft deletion flag
    public boolean remove(int key) {
        int hash = hashFunction(key);
        int i = 0;
        while (table[(hash + i * i) % capacity] != null) {
            int index = (hash + i * i) % capacity;
            if (!table[index].isDeleted && table[index].key == key) {
                table[index].isDeleted = true;
                size--;
                return true;
            }
            i++;
            if (i > capacity) break;
        }
        return false;
    }

    // Double capacity when load factor exceeds threshold
    private void resize() {
        HashNode[] oldTable = table;
        capacity *= 2;
        table = new HashNode[capacity];
        size = 0;
        for (HashNode node : oldTable) {
            if (node != null && !node.isDeleted) {
                put(node.key, node.value);
            }
        }
    }

    // Retrieve all active employees into a flat list
    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        for (HashNode node : table) {
            if (node != null && !node.isDeleted) {
                list.add(node.value);
            }
        }
        return list;
    }

    public int getSize() { return size; }
}