
public class ResultTaxi {

    private RoadState result;
    private int taxiId;
    private double rating;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double combinedDistance(){
        return result.combinedTotalDistance();
    }

    public int getTaxiId() {
        return taxiId;
    }

    public RoadState getResult() {
        return result;
    }

    public ResultTaxi(RoadState result, int taxiId) {
        this.result = result;
        this.taxiId = taxiId;
    }
}
