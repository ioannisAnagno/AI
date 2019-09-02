
import java.util.ArrayList;

/* A class that encapsulates a junction, i.e. a connection of rodes */

public class Junction {

    private Point point; /* The coordinates of the junction */
    private ArrayList<Point> neighbours; /* The points of the neighbour nodes */

    public ArrayList<Point> getNeighbours() {
        return neighbours;
    }

    public Junction(Point point) {
        this.point = point;
        neighbours = new ArrayList<>();
    }

    public void addNeighbour(Point point){

        neighbours.add(point);
    }

    // Two nodes are equal if the have the same coordinates

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Junction connection = (Junction) o;

        return (Double.compare(connection.point.getLatitude(), point.getLatitude()) == 0 && Double.compare(connection.point.getLongitude(), point.getLongitude()) == 0);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(point.getLatitude());
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(point.getLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

}
