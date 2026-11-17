package com.hrms.retirement;

import java.util.*;

// Offboarding engine executing Topological Sort on a DAG with cycle detection
public class OffboardingEngine {

    public static List<String> generateOffboardingChecklist() {
        // Step 1: Define Departmental Tasks (Nodes)
        List<String> tasks = Arrays.asList(
            "Submit Exit Form",            // Task 0
            "Audit Pension Eligibility",   // Task 1
            "Back Up Work Files",          // Task 2
            "Return Laptop & Keycard",     // Task 3
            "Revoke System Access",        // Task 4
            "Calculate Final Payout",      // Task 5
            "Issue Pension Check",         // Task 6
            "Conduct Exit Interview"      // Task 7
        );

        int numTasks = tasks.size();
        List<List<Integer>> adj = new ArrayList<>();
        int[] inDegree = new int[numTasks];

        for (int i = 0; i < numTasks; i++) {
            adj.add(new ArrayList<>());
        }

        // Step 2: Define Dependencies (Edges: Node A must happen before Node B)
        addDependency(adj, inDegree, 0, 1); // Exit Form -> Pension Audit
        addDependency(adj, inDegree, 0, 2); // Exit Form -> File Backup
        addDependency(adj, inDegree, 2, 3); // File Backup -> Return Laptop
        addDependency(adj, inDegree, 3, 4); // Return Laptop -> Revoke Access
        addDependency(adj, inDegree, 1, 5); // Pension Audit -> Final Payout Math
        addDependency(adj, inDegree, 5, 6); // Final Payout Math -> Issue Check
        addDependency(adj, inDegree, 4, 7); // Revoke Access -> Exit Interview
        addDependency(adj, inDegree, 6, 7); // Issue Check -> Exit Interview

        // Step 3: Kahn's Algorithm for Topological Sort
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numTasks; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        List<String> orderedChecklist = new ArrayList<>();
        int countProcessed = 0;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            orderedChecklist.add(tasks.get(u));
            countProcessed++;

            for (int v : adj.get(u)) {
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.add(v);
                }
            }
        }

        // Cycle Detection Check: If count processed != total tasks, a loop exists!
        if (countProcessed != numTasks) {
            System.err.println("[ERROR] Circular dependency deadlock detected in offboarding tasks!");
            return new ArrayList<>();
        }

        return orderedChecklist;
    }

    private static void addDependency(List<List<Integer>> adj, int[] inDegree, int u, int v) {
        adj.get(u).add(v);
        inDegree[v]++;
    }
}