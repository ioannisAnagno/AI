public class Point {

    private double latitude;
    private double longitude;


    public Point() {}


    public Point(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double euclideanDistance(Point point) {

        int R = 6378137;   /* Earth's mean radius in meter */
        double dLat = rad(latitude -point.getLatitude());
        double dLong = rad(longitude-point.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rad(latitude)) * Math.cos(rad(point.getLatitude())) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
        return R*c;
    }

    /* Method to convert from degrees to rad */

    private static double rad(double deg) {
        return deg*Math.PI/180;
    }
}
