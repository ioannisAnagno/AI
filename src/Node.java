import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class Node {

    private Point point;    /* The location of the point */
    private Point goalPoint;    /* The location of the goal point */
    private double accumulatedDistance; /* The euclideanDistance traveled so far */
    private Node parent; //previous node
    private Map<Junction, Junction> junctions; //pointer to the HashMap containing all junctions
    private double totalDistance;   //sum of real euclideanDistance and estimation


    /*0 The sum of the accumulated euclideanDistance traveled so far and the estimated euclideanDistance to the target */

    public double getTotalDistance(){
        return totalDistance;
    }

    //used to print the nodes from start to finish at the end of the search

    public Node getParent() {
        return parent;
    }

    public Node(Point point, Point goalPoint, double accumulatedDistance, Node parent, Map<Junction, Junction> junctions, double totalDistance) {
        this.point = point;
        this.goalPoint = goalPoint;
        this.accumulatedDistance = accumulatedDistance;
        this.parent = parent;
        this.junctions = junctions;
        this.totalDistance = totalDistance;
    }

    /*  Check if this node has the same coordinates as the client */

    public boolean isFinal(){
        return goalPoint.euclideanDistance(point) == 0;
    }

    //calculates the next nodes to be accessed from this node
    //that is nodes with same location or same road_id

    public Collection<Node> next () {

        Collection<Node> nodes = new ArrayList<>();

        /* Use the Junction that is located on the RoadState's coordinates in order to find next states */

        Junction tempJunction = new Junction(point);
        Junction junction = junctions.get(tempJunction);

        if (junction != null) {

            ArrayList<Point> neighbours = junction.getNeighbours();
            Iterator<Point> iterator = neighbours.iterator();

            while (iterator.hasNext()) {

                Point neighbourPoint = iterator.next();

                double distance = point.euclideanDistance(neighbourPoint);
                double newAccumulatedDistance = accumulatedDistance + distance;
                double newEstimatedDistance = neighbourPoint.euclideanDistance(goalPoint);
                double newTotalDistance = newAccumulatedDistance + newEstimatedDistance;
                Node newNode = new Node(neighbourPoint, goalPoint, newAccumulatedDistance,this, junctions, newTotalDistance);
                nodes.add(newNode);
            }
        }
        return nodes;
    }

    /* Print the coordinates of the node */
    @Override
    public String toString() {
        return ""+ point.getX() + "," + point.getY() + ",0";
    }

    //equals and hashcode calculated based on the fields location and road_id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return (Double.compare(node.point.getX(), point.getX()) == 0 && Double.compare(node.point.getY(), point.getY()) == 0);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(point.getX());
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(point.getY());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

