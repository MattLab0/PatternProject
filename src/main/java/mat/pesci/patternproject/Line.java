package mat.pesci.patternproject;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import java.util.ArrayList;
import java.util.stream.Stream;

//to avoid "first" and "last" to be shown as properties
@JsonIncludeProperties({ "points" })
public class Line {
    //list for the points
    private ArrayList<Point> points;

    //constructor of basic Point list
    public Line(Point first, Point  last) {
        points = new ArrayList<>();
        points.add(first);
        points.add(last);

    }

    public Line(ArrayList<Point> points2) {
        points = new ArrayList<>();
        points.addAll(points2);
    }


    //method to add a Point to the line List
    public void addPoint(Point point) {
        this.points.add(point);
    }

    //getters and setters
    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    //getters for the first and last Point in a Line

    public Point getFirst() {
        return points.get(0);
    }

    public Point getLast() {
        return points.get(points.size()-1);
    }
}

