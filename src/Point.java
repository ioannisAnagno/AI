public class Point {
    private double X;
    private double Y;

    public Point(double x, double y) {
        X = x;
        Y = y;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public void setX(double x) {
        X = x;
    }

    public void setY(double y) {
        Y = y;
    }

    public double euclideanDistance(Point point) {

        return Math.sqrt(Math.pow(X-point.getX(),2)+Math.pow(Y-point.getY(),2));
    }
}
