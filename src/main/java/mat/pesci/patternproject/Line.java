package mat.pesci.patternproject;

import java.util.ArrayList;

public class Line {
    //list for the points
    private ArrayList<Point> points;

    //constructor of basic Point list
    public Line(Point first, Point  last) {
        points = new ArrayList<>();
        points.add(first);
        points.add(last);
    }

    //getters and setters
    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }


}
