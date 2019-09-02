import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPTermParser;

import java.util.ArrayList;
import java.util.Collection;

import com.ugos.jiprolog.engine.JIPEngine;

public class RoadState {
    private Point currentLocation;  // The coordinates of the node
    private Point clientLocation; // The coordinates of the client
    private double accumulatedDistance; // The distance traveled so far
    private double weightedDistance;
    private RoadState parent; // previous node
    private double estimatedDistance;
    private JIPEngine jip;

    public double combinedTotalDistance(){ return weightedDistance + estimatedDistance; }

    public double realTotalDistance(){ return accumulatedDistance + estimatedDistance; }

    public RoadState getParent() {
        return parent;
    }


    public RoadState(Point currentLocation, Point clientLocation, double accumulatedDistance, double weightedDistance, RoadState parent, double estimatedDistance, JIPEngine jip) {
        this.currentLocation = currentLocation;
        this.clientLocation = clientLocation;
        this.accumulatedDistance = accumulatedDistance;
        this.weightedDistance = weightedDistance;
        this.parent = parent;
        this.estimatedDistance = estimatedDistance;
        this.jip = jip;
    }

    /*
            Check if this node has the same coordinates as the client
        */
    public boolean isFinal() {

        return (currentLocation.getLatitude() == clientLocation.getLatitude() && currentLocation.getLongitude() == clientLocation.getLongitude());
    }

    public Collection<RoadState> next () {

        Collection<RoadState> states = new ArrayList<>();
        JIPTermParser parser = jip.getTermParser();
        JIPQuery jipQuery = jip.openSynchronousQuery(parser.parseTerm("nextNode(" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + ",X ,Y , Z)."));
        JIPTerm term = jipQuery.nextSolution();

        // Add all the possible nodes

        while (term != null) {

            JIPQuery jipQuery1 = jip.openSynchronousQuery(parser.parseTerm("cost(" + term.getVariablesTable().get("Z").toString() + ",C)."));
            JIPTerm term1 = jipQuery1.nextSolution();
            if(term1==null){

                System.out.println("Something went wrong: no cost found for road :" + term.getVariablesTable().get("Z").toString());

            }
            else {

                Point newLocation = new Point();
                newLocation.setLatitude(Double.parseDouble(term.getVariablesTable().get("X").toString()));
                newLocation.setLongitude(Double.parseDouble(term.getVariablesTable().get("Y").toString()));

                double newAccumulatedDistance = accumulatedDistance + currentLocation.euclideanDistance(newLocation);

                double newCombinedDistance = weightedDistance + currentLocation.euclideanDistance(newLocation)*Double.parseDouble(term1.getVariablesTable().get("C").toString());

                double newEstimatedDistance = newLocation.euclideanDistance(clientLocation);

                states.add(new RoadState(newLocation, clientLocation, newAccumulatedDistance, newCombinedDistance, this, newEstimatedDistance, jip));
            }

            term = jipQuery.nextSolution();
        }

        return states;
    }

    // Print the coordinates of the node

    @Override
    public String toString() {
        return ""+ currentLocation.getLatitude() + "," + currentLocation.getLongitude() + ",0";
    }

    //equals and hashcode calculated based on the fields location
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoadState roadState = (RoadState) o;

        return (Double.compare(roadState.currentLocation.getLatitude(), currentLocation.getLatitude()) == 0 && Double.compare(roadState.currentLocation.getLongitude(), currentLocation.getLongitude()) == 0);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(currentLocation.getLatitude());
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(currentLocation.getLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
