import java.util.*;


public class AStarSolver {

    public ArrayList<Node> solve(Node source) {

        /*
           In order to return all the possible minimum paths we use an ArrayList to
           store all the nodes and therefore all the paths that have the same distance
         */

        /* Define the level of accuracy that we will allow */

        double accuracy = 1;    /* Assume that the paths within 1 meter have the same distance */

        ArrayList<Node> results = new ArrayList<>();
        double minDistance = Double.MAX_VALUE;
        Boolean foundSolution = false;

        /* Use a HashSet in order efficiently search for nodes that are in the closed set */

        HashMap<Integer,Node> explored = new HashMap<>();


        Node current;
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

            if (foundSolution == false) {
                /*
                 * Remove the node that has the least sum of accumulated and estimated
                 * euclideanDistance to the target, the removal of which has O(1) time
                 * complexity with the use of a priorityQueue
                 */

                current = priorityQueue.poll();



                /* If the current state is the goal, then return it */

                if (current.isFinal()) {
                    results.add(current);
                    foundSolution = true;
                    continue;
                }

                /*
                 * If the current state has already been developed then skip the following steps
                 */

                if (explored.containsKey(current.hashCode())) {
                    if(Math.abs(explored.get(current.hashCode()).getTotalDistance() - current.getTotalDistance()) < accuracy) {
                        priorityQueue.addAll(current.next());
                    }
                    continue;
                }

                explored.put(current.hashCode(),current);

                /* Insert into the PriorityQueue the states that can be reached from current */

                priorityQueue.addAll(current.next());

            }
            else {
                current = priorityQueue.poll();


                if (current.getTotalDistance() - results.get(0).getTotalDistance() > accuracy) {

                    return results;
                }

                if (current.isFinal()) {

                    results.add(current);
                    continue;
                }

                if (explored.containsKey(current.hashCode())) {
                    if(Math.abs(explored.get(current.hashCode()).getTotalDistance() - current.getTotalDistance()) < accuracy) {
                        priorityQueue.addAll(current.next());

                    }
                    continue;
                }

                explored.put(current.hashCode(),current);

                /* Insert into the PriorityQueue the states that can be reached from current */

                priorityQueue.addAll(current.next());


            }

        }
        return results;
    }
}