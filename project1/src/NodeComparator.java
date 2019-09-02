import java.util.Comparator;


public class NodeComparator implements Comparator<Node> {

    /*
       Compares two nodes based on the total euclideanDistance to the target,
       i.e. the sum of the accumulated euclideanDistance and the estimated euclideanDistance
       to the target
     */

    @Override
    public int compare(Node node1, Node node2)
    {
        if (node1.getTotalDistance() < node2.getTotalDistance())
            return -1;
        return 1;
    }
}
