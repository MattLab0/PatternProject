package mat.pesci.patternproject;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;

public class LineConstructor {
    // List for all the points
    ArrayList<Point> points;

    // Constructor
    public LineConstructor(ArrayList<Point> allPoints) {
        this.points = allPoints;
    }

    public ArrayList<Line> getLines(int n) {
        //create 2 List(s) for the horizontal and vertical lines in order to avoid
        //possible artifact between the last horizontal lines and the first vertical one
        ArrayList<Line> lineHorizontal = new ArrayList<>();
        ArrayList<Line> lineVertical = new ArrayList<>();

        //lines pass through at least 1 point, if n<1 throw an exception
        if (n < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lines have to pass through one point at least!");
        }
        //points are ordered with increasing X (primary) and Y (secondary)
        // (x,y)=(0,0)->(0,1)->(0-2)->...->(1,0)->(1,1)->(0-2)->...
        points.sort(Comparator.comparingInt(Point::getY));
        points.sort(Comparator.comparingInt(Point::getX));
        //confront each pair of subsequent points, to find vertical lines
        for (int i = 1; i < points.size(); i++) {
            //if points are on the same X, and they are neighbours (distance=1)
            if (points.get(i).getX() == points.get(i - 1).getX() &&
                    (points.get(i).getY() - points.get(i - 1).getY()) == 1) {
                //add a line to the List
                lineVertical.add(new Line(points.get(i - 1), points.get(i)));
            }
        }
        //points are ordered with increasing Y (primary) and X (secondary)
        // (x,y)=(0,0)->(1,0)->(2,0)->...->(0,1)->(1,1)->(2,1)->...
        points.sort(Comparator.comparingInt(Point::getY));
        //confront each pair of subsequent points, to find horizontal lines
        for (int i = 1; i < points.size(); i++) { //
            //if points are on the same Y, and they are neighbours (distance=1)
            if (points.get(i).getY() == points.get(i - 1).getY() &&
                    (points.get(i).getX() - points.get(i - 1).getX()) == 1) {
                lineHorizontal.add(new Line(points.get(i - 1), points.get(i)));
            }
        }
        // Merges smaller lines until forming each possible line,
        // return only lines with at least N points
        return bottomUpMerge(lineHorizontal, lineVertical, n);

    }

    private ArrayList<Line> bottomUpMerge(ArrayList<Line> lineHorizontal, ArrayList<Line> lineVertical, int n) {

        int complexLevel = 2; //2-points lines are already formed, the complexity start at this value
        final ArrayList<Line> supportLines = new ArrayList<>();
        for (Line ln : lineVertical) {
            supportLines.add(new Line(ln.getFirst(), ln.getLast()));
        }
        for (Line ln : lineHorizontal) {
            supportLines.add(new Line(ln.getFirst(), ln.getLast()));
        }

            //continue until there are no more line to merge
            while (lineVertical.size() > 1) {
                //confront each pair of subsequent vertical lines
                for (int i = 1; i < lineVertical.size(); i++) {
                    // if n-complex lines are on the same axis, and they have points in common (at least one)
                    // -case same Y: successive line's first X is lower or equal to the last X of the precedent one
                    if (lineVertical.get(i).getFirst().getX() == lineVertical.get(i - 1).getLast().getX() &&
                            lineVertical.get(i).getFirst().getY() <= lineVertical.get(i - 1).getLast().getY()) {
                        //update the precedent line with the only point missing (last point of successive line)
                        lineVertical.get(i - 1).addPoint(lineVertical.get(i).getLast());
                    }
                }
                //there are still lines of lower complexity, that need to be eliminated before updating the complete List
                int finalComplexLevel = complexLevel;
                lineVertical.removeIf(u -> u.getPoints().size() <= finalComplexLevel);
                complexLevel++;

                for (Line ln : lineVertical) {
                    supportLines.add(new Line(ln.getPoints()));
                }
            }

            complexLevel = 2;
            while (lineHorizontal.size() > 1) {
                //confront each pair of subsequent horizontal lines
                for (int i = 1; i < lineHorizontal.size(); i++) {
                    // if n-complex lines are on the same axis, and they have points in common (at least one):
                    // -case same X: successive line's first Y is lower or equal to the last Y of the precedent one
                    if ((lineHorizontal.get(i).getFirst().getY() == lineHorizontal.get(i - 1).getLast().getY() &&
                            lineHorizontal.get(i).getFirst().getX() <= lineHorizontal.get(i - 1).getLast().getX())) {
                        //update the precedent line with the only point missing (last point of successive line)
                        lineHorizontal.get(i - 1).addPoint(lineHorizontal.get(i).getLast());
                    }
                }

                //there are still lines of complexity n-1, that need to be eliminated before returning the List
                int finalComplexLevel = complexLevel;
                lineHorizontal.removeIf(u -> u.getPoints().size() <= finalComplexLevel);
                complexLevel++;

                for (Line ln : lineHorizontal) {
                    supportLines.add(new Line(ln.getPoints()));
                }
            }

            //return the complete list, removing each lines with less than N points
            if (n > 1) {
                supportLines.removeIf(u -> u.getPoints().size() < n);
                return supportLines;
            }
            return supportLines;
        }
}


