
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        try {

            double coordinateX, coordinateY;
            ArrayList<String> temp = new ArrayList<>();
            String line;

            /* Open the file client.csv that contains the coordinates of the client*/

            File file = new File("../csv/client.csv");
            BufferedReader in = new BufferedReader(new FileReader(file));

            /* Ignore the first line */

            in.readLine();

            line = in.readLine();
            String[] data = line.split(",");

            coordinateX = Double.parseDouble(data[0]);
            coordinateY = Double.parseDouble(data[1]);

            Point clientPoint = new Point(coordinateX, coordinateY);

            /* Open the file taxis.csv that contains coordinates and the ids of the taxis */

            file = new File("../csv/taxis.csv");
            in = new BufferedReader(new FileReader(file));

            /* Ignore the first line */

            in.readLine();

            line = in.readLine();

            while (line != null) {

                temp.add(line);
                line = in.readLine();

            }

            int[] taxiIds = new int[temp.size()];
            Point[] taxiPoints = new Point[temp.size()];

            Iterator<String> myIterator = temp.iterator();
            int i = 0;

            while (myIterator.hasNext()) {

                data = myIterator.next().split(",");

                coordinateX = Double.parseDouble(data[0]); /* Coordinate X of the taxi */
                coordinateY = Double.parseDouble(data[1]); /* Coordinate Y of the taxi */

                taxiPoints[i] = new Point(coordinateX, coordinateY);

                taxiIds[i] = Integer.parseInt(data[2]); /* The id of the taxi */

                i++;

            }

            /* Open file nodes.csv that contains the coordinates and the ids for  all roads */

            file = new File("../csv/nodes.csv");
            in = new BufferedReader(new FileReader(file));

            /* Ignore the first line */

            in.readLine();

            line = in.readLine();
            temp.clear();

            while (line != null) {

                temp.add(line);
                line = in.readLine();

            }

            Point[] roadPoints = new Point[temp.size()];
            int[] roadId = new int[temp.size()];

            myIterator = temp.iterator();
            i = 0;

            while (myIterator.hasNext()) {

                data = myIterator.next().split(",");
                coordinateX = Double.parseDouble(data[0]);
                coordinateY = Double.parseDouble(data[1]);

                roadPoints[i] = new Point(coordinateX, coordinateY);

                roadId[i] = Integer.parseInt(data[2]);
                i++;
            }

            temp.clear();

            /* Create a HashSet for all the junctions */

            HashMap<Junction, Junction> junctions = new HashMap<>();

            for (i=0; i < roadId.length; i++) {

                Junction tempJunction = new Junction(roadPoints[i]);

                /* Check if there is a point with the same coordinates */

                Junction junction = junctions.get(tempJunction);

                if (junction == null) {
                    /* If not, then add it to the map */
                    junctions.put(tempJunction, tempJunction);
                    junction = tempJunction;
                }

                if (i > 0) {
                    if (roadId[i] == roadId[i-1]) {
                        junction.addNeighbour(roadPoints[i-1]);
                    }
                }

                if (i+1 < roadId.length) {
                    if (roadId[i] == roadId[i+1]) {
                        junction.addNeighbour(roadPoints[i+1]);
                    }
                }

            }

            /*
               Find the closest node to the client and assume that the client position
               is there, as his initial coordinates may correspond
               to a point that is unreachable from the road, for example a park
             */

            double distance = clientPoint.euclideanDistance(roadPoints[0]);
            double min = distance;
            int minPos = 0;

            for (i = 1; i < roadId.length; i++) {

                distance = clientPoint.euclideanDistance(roadPoints[i]);

                if (distance < min) {
                    min = distance;
                    minPos = i;
                }
            }

            clientPoint.setX(roadPoints[minPos].getX());
            clientPoint.setY(roadPoints[minPos].getY());

            /* Initialize minDistance variable, which is the shortest path from a taxi to a client, to infinity */

            double minDistance = Double.MAX_VALUE;
            int minTaxiId = -1;

            /* For each taxi find the best path to the client using A* */

            for (i = 0; i < taxiIds.length; i++) {

                /* Find the closest node from each taxis initial coordinates */

                distance = taxiPoints[i].euclideanDistance(roadPoints[0]);

                min = distance;
                minPos= 0;

                for (int j = 1; j < roadId.length; j++) {

                    distance = taxiPoints[i].euclideanDistance(roadPoints[j]);

                    if (distance < min) {
                        min = distance;
                        minPos = j;
                    }
                }

                distance = clientPoint.euclideanDistance(roadPoints[minPos]);

                // Create a new A* solver

                AStarSolver solver = new AStarSolver();
                Node source = new Node(roadPoints[minPos], clientPoint, 0,null, junctions, distance);
                Node result = solver.solve(source);

                if (result != null) {

                    distance = result.getTotalDistance();
                    if (distance < minDistance) {
                        minDistance = distance;
                        minTaxiId = taxiIds[i];
                    }

                    System.out.println("Taxi with id = " + taxiIds[i] + " needs a total euclideanDistance of " + result.getTotalDistance());
                    System.out.println("The shortest path from this taxi is the following");
                    printPath(result);
                }

                else {

                    System.out.println("There is no path from taxi with id = " + taxiIds[i]);
                }

            }
            System.out.println("The shortest path is " + minDistance +  " from the taxi with id =  " + minTaxiId);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
    }

    /* Prints the states visited from start to finish */
    private static void printPath(Node state) {
        if (state.getParent() != null) {
            printPath(state.getParent());
        }
        System.out.println(state);
    }
}
