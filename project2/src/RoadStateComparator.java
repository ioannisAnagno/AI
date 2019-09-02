import java.util.Comparator;


public class RoadStateComparator implements Comparator<RoadState> {
    //Compares 2 RoadStates based on the total distance to target
    //That is the sum of combined distance so far and the minimum distance left
    @Override
    public int compare(RoadState x, RoadState y)
    {
        if (x.combinedTotalDistance() < y.combinedTotalDistance())
            return -1;
        return 1;
    }
}

