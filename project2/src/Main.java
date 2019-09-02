import java.io.*;
import java.util.*;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPTermParser;

public class Main {

    public static void main(String[] args)  throws JIPSyntaxErrorException,IOException {

        JIPEngine jip = new JIPEngine();
        jip.consultFile("../prolog.pl");
        JIPTermParser parser = jip.getTermParser();

        JIPQuery jipQuery;
        JIPTerm term;

        double latitude, longitude;
        String line;

        ArrayList<String> temp = new ArrayList<>();
        int i;

        File file = new File("../csv/newClient.csv");
        BufferedReader in = new BufferedReader(new FileReader(file));

        in.readLine();
        line = in.readLine();
        String[] clientData = line.split(",");

        latitude = Double.parseDouble(clientData[0]);
        longitude = Double.parseDouble(clientData[1]);

        Point clientCurrentPoint = new Point(latitude, longitude);

        latitude = Double.parseDouble(clientData[2]);
        longitude = Double.parseDouble(clientData[3]);


        Point clientDestinationPoint = new Point(latitude, longitude);

        String clientCurrentTime = clientData[4];
        String numberOfPersons = clientData[5];
        String clientLanguage = clientData[6];

        file = new File("../csv/traffic.csv");
        in = new BufferedReader(new FileReader(file));

        in.readLine();
        line = in.readLine();

        while (line != null) {

            String[] trafficData = line.split(",");

            // Skip the lines that do not contain information about the traffic of the road

            if (trafficData.length == 3) {

                jipQuery = jip.openSynchronousQuery(parser.parseTerm("findTrafficCost(" + trafficData[2].replace("|" , ",") + "," + clientCurrentTime + ",TC)."));

                term = jipQuery.nextSolution();

                // Insert certain terms

                if (!term.getVariablesTable().get("TC").toString().equals("0"))
                    jip.asserta(parser.parseTerm("trafficCost(" + trafficData[0] + "," + term.getVariablesTable().get("TC").toString() + ")."));
            }

            line = in.readLine();
        }


        String[] linesData;

        Iterator<String> iterator = temp.iterator();

        file = new File("../csv/lines.csv");
        in = new BufferedReader(new FileReader(file));
        in.readLine();

        line = in.readLine();

        while (line != null) {

            // jiprolog doesn't recognize symbol '%'

            linesData = line.replace("%" , "").replaceAll("\".*\"" , "").split(",",-1);

            StringBuilder builder = new StringBuilder();

            for (i = 0; i < linesData.length; i++) {


                // Ignore the name of the road

                if (i == 2) continue;

                if (linesData[i].length() == 0) builder.append("e,");
                else builder.append(linesData[i]).append(",");

            }


            //is no toll value is given add an empty stop at the end else remove the extra ',' at the end

            if (linesData.length == 17) builder.append("e");
            else builder.deleteCharAt(builder.length()-1);

            jipQuery = jip.openSynchronousQuery(parser.parseTerm("isDrivable(" + builder.toString() +")."));

            term = jipQuery.nextSolution();

            if (term != null){

                //if we can drive is this road given
                //find the cost to drive through this road
                //if nothing is given assume it is an one way road the way the nodes are given

                if (linesData[3].equals("-1"))
                    jip.asserta(parser.parseTerm("direction(" + linesData[0] + ",-1)."));
                else if (linesData[3].equals("no"))
                    jip.asserta(parser.parseTerm("direction(" + linesData[0] + ",0)."));
                else
                    jip.asserta(parser.parseTerm("direction(" + linesData[0] + ",1)."));

                jipQuery = jip.openSynchronousQuery(parser.parseTerm("findCost(" + builder.toString() + "," + clientCurrentTime +",Cost)."));

                term = jipQuery.nextSolution();

                if (term == null){

                    System.out.println("Error with findCost");

                }
                else {

                    // Check if we have stored information regarding the traffic of the road on our database

                    JIPQuery newJipQuery = jip.openSynchronousQuery(parser.parseTerm("trafficCost(" + linesData[0] +",TC)."));
                    JIPTerm newTerm = newJipQuery.nextSolution();
                    double newCost;
                    if (newTerm == null){
                        newCost = 1.5;
                    }
                    else {
                        newCost = Double.parseDouble(newTerm.getVariablesTable().get("TC").toString());
                    }

                    newCost = newCost * Double.parseDouble(term.getVariablesTable().get("Cost").toString());


                    jip.asserta(parser.parseTerm("cost(" + linesData[0] + "," + newCost + ")."));


                }
            }

            line = in.readLine();
        }

        temp.clear();

        file = new File("../csv/nodes.csv");

        in = new BufferedReader(new FileReader(file));

        in.readLine();

        String current = skipRoadName(in.readLine());
        String[] temp1 = current.split(",");
        String next = skipRoadName(in.readLine());
        String[] temp2;

        while (next != null) {

            temp2 = current.split(",");
            if (temp1[2].equals(temp2[2])) {

                jipQuery = jip.openSynchronousQuery(parser.parseTerm("direction(" + temp1[2] +",X)."));
                term = jipQuery.nextSolution();

                if (term != null) {

                    //Add a term in the form of next(current_X,current_Y,next_X,next_Y,Line_id_of_next)
                    //depending on the way we can cross every line
                    int direction = Integer.parseInt(term.getVariablesTable().get("X").toString());
                    if (direction >= 0)
                        jip.asserta(parser.parseTerm("nextNode(" + temp1[0] + "," + temp1[1] + "," + temp2[0] + "," + temp2[1] + "," + temp2[2] + ")."));
                    if (direction <= 0)
                        jip.asserta(parser.parseTerm("nextNode(" + temp2[0] + "," + temp2[1] + "," + temp1[0] + "," + temp1[1] + "," + temp1[2] + ")."));
                }
            }

            temp.add(current);
            current = next;
            temp1 = temp2;
            next = skipRoadName(in.readLine());
        }

        temp.add(current);

        Point[] roadPoints = new Point[temp.size()];

        int[] roadIds = new int[temp.size()];
        iterator = temp.iterator();


        i = 0;

        while (iterator.hasNext()) {

            String[] data = iterator.next().split(",");
            latitude = Double.parseDouble(data[0]);
            longitude = Double.parseDouble(data[1]);
            roadPoints[i] = new Point(latitude, longitude);
            roadIds[i] = Integer.parseInt(data[2]);
            i++;
        }


        temp.clear();
        file = new File("../csv/newTaxis.csv");
        in = new BufferedReader(new FileReader(file));
        /*skip first line and read location and id by splitting rest of the lines by ',' */

        line = in.readLine();
        while (line != null) {

            String[] taxiData = line.split(",");
            jipQuery = jip.openSynchronousQuery(parser.parseTerm("isValidTaxi(" + taxiData[3] + "," + taxiData[4] + "," + numberOfPersons + ")."));
            term = jipQuery.nextSolution();

            if (term != null) {

                if (taxiData[7].equals("yes")){
                    jip.asserta(parser.parseTerm("longDistance(" + taxiData[2] + ")"));
                }

                temp.add(line);

                String[] language = taxiData[5].split("\\|");
                StringBuilder taxiLanguages = new StringBuilder();
                taxiLanguages.append("[");
                for(int j=0; j < language.length; j++){

                    taxiLanguages.append(language[j]).append(",");
                }

                taxiLanguages.deleteCharAt(taxiLanguages.length()-1);
                taxiLanguages.append("]");

                jipQuery = jip.openSynchronousQuery(parser.parseTerm("findRating(" + taxiLanguages.toString() + "," + taxiData[6] + "," + clientLanguage + ",Rating)."));

                term = jipQuery.nextSolution();
                jip.asserta(parser.parseTerm("rating(" + taxiData[2] + "," + term.getVariablesTable().get("Rating").toString() + ")."));

            }

            line = in.readLine();
        }

        double[][] taxiInfo = new double[temp.size()][4];
        iterator = temp.iterator();

        i = 0;

        // Sort the taxis based on the least distance required

        while (iterator.hasNext()) {
            String[] taxiData = iterator.next().split(",");

            taxiInfo[i][0] = Double.parseDouble(taxiData[0]);
            taxiInfo[i][1] = Double.parseDouble(taxiData[1]);
            taxiInfo[i][2] = Double.parseDouble(taxiData[2]);

            Point taxiPoint = new Point(taxiInfo[i][0], taxiInfo[i][1]);

            //4th value of taxi_info is the least distance required to reach the client
            taxiInfo[i][3] = taxiPoint.euclideanDistance(clientCurrentPoint);

            i++;
        }

        Arrays.sort(taxiInfo, new Comparator<double[]>() {
            @Override
            public int compare(final double[] entry1, final double[] entry2) {
                if (entry1[3]<entry2[3]) return -1;
                else return 1;
            }
        });


        double currentMin = clientCurrentPoint.euclideanDistance(roadPoints[0]);
        double destinationMin = clientDestinationPoint.euclideanDistance(roadPoints[0]);

        int currentPos = 0;
        int destinationPos = 0;

        for (i = 1; i < roadIds.length; i++) {

            double cur = clientCurrentPoint.euclideanDistance(roadPoints[i]);
            double destination = clientDestinationPoint.euclideanDistance(roadPoints[i]);

            if (cur < currentMin) {
                currentMin = cur;
                currentPos = i;
            }
            if (destination < destinationMin) {
                destinationMin = destination;
                destinationPos = i;
            }
        }


        clientCurrentPoint.setPoint(roadPoints[currentPos].getLatitude(), roadPoints[currentPos].getLongitude());
        clientDestinationPoint.setPoint(roadPoints[destinationPos].getLatitude(), roadPoints[destinationPos].getLongitude());


        int j;
        int currentNode;

        // The client should choose the number of the best taxis to display


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


        // Number of solutions already found

        int solutionsFound = 0;

        // double minDistance = Double.MAX_VALUE;

        double distance = clientCurrentPoint.euclideanDistance(clientDestinationPoint);

        AStarSolver AStar = new AStarSolver();

        RoadState initial = new RoadState(clientCurrentPoint, clientDestinationPoint,0 ,0 , null, distance, jip);

        RoadState destination = AStar.solve(initial);

        if (destination == null) {

            System.out.println("Your destination is unreachable");
            System.exit(1);
        }

        Comparator<ResultTaxi> comparator = new TaxiDistanceComparator();
        PriorityQueue<ResultTaxi> queue =
                new PriorityQueue<>(comparator);

        // For each taxi find the best route


        for (j = 0; j < taxiInfo.length; j++) {


            Point taxiPoint = new Point(taxiInfo[j][0], taxiInfo[j][1]);


            distance = taxiPoint.euclideanDistance(roadPoints[0]);
            double min = distance;
            currentNode =  0;

            for (i = 1; i < roadIds.length; i++) {

                distance = taxiPoint.euclideanDistance(roadPoints[i]);

                if (distance < min) {
                    min = distance;
                    currentNode = i;
                }
            }

            distance = clientCurrentPoint.euclideanDistance(roadPoints[currentNode]);


            // Create a new A* solver

            AStar = new AStarSolver();

            initial = new RoadState(roadPoints[currentNode], clientCurrentPoint,0 ,0 , null, distance, jip);

            RoadState result = AStar.solve(initial);//, minDistance);

            if (result != null) {

                // Skip the taxi if it is not capable of serving long distance requests and it is needed

                if (result.realTotalDistance() + destination.realTotalDistance() > 15000) {

                    jipQuery = jip.openSynchronousQuery(parser.parseTerm("longDistance(" + (int)taxiInfo[j][2] + ")."));
                    term = jipQuery.nextSolution();

                    // If the taxi cannot travel long distances then it is not a valid choice

                    if (term == null)
                        continue;
                }

                //check the distance needed for this taxi

                queue.add(new ResultTaxi(result,(int) taxiInfo[j][2]));
                solutionsFound++;

            }
        }

        if (solutionsFound == 0) {
            System.out.println("No solution found.");
        }
        else {
            ResultTaxi[] finalTaxis = new ResultTaxi[solutionsFound];

            for (i = solutionsFound-1; i >= 0; i--) {
                finalTaxis[i] = queue.poll();
            }

            System.out.println("Here are these drivers based on the time needed to reach you:");
            double testing_distance = finalTaxis[0].combinedDistance();
            for(i=0; i < solutionsFound; i++) {
                String tempStr;
                switch (i){
                    case 0: tempStr = "-st"; break;
                    case 1: tempStr = "-nd"; break;
                    case 2: tempStr = "-rd"; break;
                    default : tempStr = "-th";
                }
                if (i == 0)
                    System.out.println("Taxi: " + finalTaxis[i].getTaxiId() + " is the quickest to reach your location.");
                else
                    System.out.println("Taxi: " + finalTaxis[i].getTaxiId() + " is the " + (i+1) + tempStr +" quickest to reach your location slower by " + java.lang.Math.round(finalTaxis[i].combinedDistance()/testing_distance*100-100) + "%.");
            }
            //find the rating of each taxi
            for(i=0; i < solutionsFound; i++) {

                jipQuery = jip.openSynchronousQuery(parser.parseTerm("rating(" + finalTaxis[i].getTaxiId() + ",Rating)."));
                term = jipQuery.nextSolution();
                if (term == null){
                    System.out.println("Something went wrong with rating");
                }
                else{
                    finalTaxis[i].setRating(Double.parseDouble(term.getVariablesTable().get("Rating").toString()));
                }
            }


            // Sort the taxis based on their rating

            System.out.println("Here are the taxis based on rating:");
            Arrays.sort(finalTaxis, new Comparator<ResultTaxi>() {
                @Override
                public int compare(final ResultTaxi entry1,final ResultTaxi entry2) {
                    if (entry1.getRating()>entry2.getRating()) return -1;
                    else return 1;
                }
            });
            for(i=0; i < solutionsFound; i++) {
                String s_temp;
                switch (i){
                    case 0: s_temp = "-st"; break;
                    case 1: s_temp = "-nd"; break;
                    case 2: s_temp = "-rd"; break;
                    default : s_temp = "-th";
                }

                System.out.print("Taxi: " + finalTaxis[i].getTaxiId());
                System.out.println(" has the " + (i+1) + s_temp + " best rating of: " +  finalTaxis[i].getRating() + ".");
            }


            // At this point the customer chooses the taxi that he wants


            System.out.print("Enter taxi id you would like: ");

            try{

                int taxiId = Integer.parseInt(br.readLine());
                for(i=0; i < solutionsFound; i++){

                    if (finalTaxis[i].getTaxiId() == taxiId)
                        break;
                }

                if (i >= solutionsFound) {
                    System.out.println("No valid id");
                    System.exit(0);
                }
                else{

                    System.out.println("Printing the route the taxi has to follow");
                    printSolution(finalTaxis[i].getResult());
                }

                System.out.println("The route that the taxi has to follow:");
                printSolution(destination);

            } catch(NumberFormatException nfe){
                System.err.println("The input is not valid");
            }
        }
    }

    private static void printSolution(RoadState state) {
        if (state.getParent() != null) {
            printSolution(state.getParent());
        }
        System.out.println(state);
    }

    private static String skipRoadName(String node){
        if (node == null) return null;
        String[] a;
        a = node.split(",");
        return a[0] + "," + a[1] + "," + a[2];
    }


}