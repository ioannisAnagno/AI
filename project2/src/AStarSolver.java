import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class AStarSolver {

    public RoadState solve(RoadState initial) {//, double minDistance) {

        Set<RoadState> developed = new HashSet<>();

        Comparator<RoadState> comparator = new RoadStateComparator();
        PriorityQueue<RoadState> queue =
                new PriorityQueue<>(comparator);

        queue.add(initial);
        // While the queue contains states to be examined.
        while(!queue.isEmpty()){

            RoadState s = queue.poll();

            if (s.isFinal())
                return s;

            if (developed.contains(s))
                continue;

            developed.add(s);

            //if (s.combinedTotalDistance() > minDistance)
            //    return null;

            // Find all the nodes that can be reached from the current one
            queue.addAll(s.next());
        }

        return null;
    }
}
