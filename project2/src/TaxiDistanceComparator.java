import java.util.Comparator;


public class TaxiDistanceComparator implements Comparator<ResultTaxi> {
    //comparator used for the PriorityQueue
    //The largest combined distance Route should be the first element
    @Override
    public int compare(ResultTaxi x, ResultTaxi y)
    {
        if (x.combinedDistance() < y.combinedDistance())
            return 1;
        return -1;
    }
}
