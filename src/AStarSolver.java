import java.util.*;


public class AStarSolver {

    public Node solve(Node source) {

        /* Use a HashSet in order efficiently search for nodes that are in the closed set */

        HashSet<Node> explored = new HashSet<>();

        /*
           Use a PriorityQueue in order to efficiently access the node that has the least
           euclideanDistance to the goal node
         */

        Comparator<Node> comparator = new NodeComparator();

        PriorityQueue<Node> priorityQueue = new PriorityQueue<Node>(comparator);

        /* Insert the initial state in the PriorityQueue */

        priorityQueue.add(source);

        /* Search as long as there are still unexplored states */

        while(!priorityQueue.isEmpty()) {

            /*
               Remove the node that has the least sum of accumulated and estimated euclideanDistance to the target,
               the removal of which has O(1) time complexity with the use of a priorityQueue
            */

            Node current = priorityQueue.poll();

            /* If the current state is the goal, then return it */

            if (current.isFinal())
                return current;

            /* If the current state has already been developed then skip the following steps */

            if (explored.contains(current)) continue;

            explored.add(current);

            /* Insert into the PriorityQueue the states that can be reached from current */

            priorityQueue.addAll(current.next());
        }

        /* If the priorityQueue is empty, then no solution is found */

        return null;
    }
}
