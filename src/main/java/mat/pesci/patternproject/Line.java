package mat.pesci.patternproject;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import java.util.ArrayList;


// To avoid "first" and "last" to be shown as properties
@JsonIncludeProperties({ "points" })
// The Line class holds a List of points,
// it can be instantiated and manipulated via public methods
public class Line {

    // List for the points
    private ArrayList<Point> points;

    // Constructor of basic Point List (from 2 points)
    public Line(Point first, Point  last) {
        points = new ArrayList<>();
        points.add(first);
        points.add(last);

    }

    // Constructor of complex Point List (from List of points)
    public Line(ArrayList<Point> pointsArrayList) {
        points = new ArrayList<>();
        points.addAll(pointsArrayList);
    }

    // Method to add a Point to the Line List
    public void addPoint(Point point) {
        this.points.add(point);
    }

    // Getters and setters

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    // Getters for the first and last Point in a Line

    public Point getFirst() {
        return points.get(0);
    }

    public Point getLast() {
        return points.get(points.size()-1);
    }
}

