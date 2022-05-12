package mat.pesci.patternproject;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.Comparator;

// The LineConstructor class takes as an input a List of points
// and via getLines(n) return a List containing all the line
// passing through at least "n" point
public class LineConstructor {

    // List for all the points
    ArrayList<Point> points;

    // Constructor
    public LineConstructor(ArrayList<Point> allPoints) {
        this.points = allPoints;
    }

    // This method return a List containing all the line (passing through at least N points)
    public ArrayList<Line> getLines(int n) {

        // Create 2 List(s) for the horizontal and vertical lines, in order to avoid
        // possible artifact between the last vertical lines and the first horizontal one
        ArrayList<Line> lineHorizontal = new ArrayList<>();
        ArrayList<Line> lineVertical = new ArrayList<>();

        // A line has at least 1 point, if n<1 throw an exception
        if (n < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lines have to pass through one point at least!");
        }

        // Points are ALREADY ordered with increasing X (primary) and Y (secondary)
        // (x,y)=(0,0)->(0,1)->(0-2)->...->(1,0)->(1,1)->(0-2)->...
        // Confront each pair of subsequent points, to find vertical lines
        for (int i = 1; i < points.size(); i++) {
            // If points are on the same X, and they are neighbours (distance=1)
            // Math.abs(distance) is not necessary since points are ordered
            if (points.get(i).getX() == points.get(i - 1).getX() &&
                    (points.get(i).getY() - points.get(i - 1).getY()) == 1) {
                // Add a line to the List, constructed from the Points
                lineVertical.add(new Line(points.get(i - 1), points.get(i)));
            }
        }

        // Points are ordered with increasing Y (primary) and X (secondary)
        // (x,y)=(0,0)->(1,0)->(2,0)->...->(0,1)->(1,1)->(2,1)->...
        points.sort(Comparator.comparingInt(Point::getY));
        // Confront each pair of subsequent points, to find horizontal lines
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

    // This method merge the 2-points horizontal and vertical lines
    // to form all the possible complex lines (3 points or more),
    // after removing the lines containing less than N points and
    // return a List containing all the correct lines
    private ArrayList<Line> bottomUpMerge(ArrayList<Line> lineHorizontal, ArrayList<Line> lineVertical, int n) {

        int complexLevel = 2; //2-points lines are already formed, the complexity start at this value

        // List to store the forming complex lines
        final ArrayList<Line> supportLines = new ArrayList<>();

        // The basic lines are saved before operating on these Lists
        for (Line ln : lineVertical) {
            supportLines.add(new Line(ln.getFirst(), ln.getLast()));
        }
        for (Line ln : lineHorizontal) {
            supportLines.add(new Line(ln.getFirst(), ln.getLast()));
        }

        // Continue until there are no more vertical line to merge
        while (lineVertical.size() > 1) {
            // Confront each pair of subsequent vertical lines
            for (int i = 1; i < lineVertical.size(); i++) {
                // If lines are on the same axis, and they have points in common (at least one):
                // -case same Y: successive line's first X is lower or equal to the last X of the precedent one
                // -case same X: successive line's first Y is lower or equal to the last Y of the precedent one
                if (lineVertical.get(i).getFirst().getX() == lineVertical.get(i - 1).getLast().getX() &&
                        lineVertical.get(i).getFirst().getY() <= lineVertical.get(i - 1).getLast().getY()) {
                    // Update the precedent line with the only point missing (last point of successive line)
                    lineVertical.get(i - 1).addPoint(lineVertical.get(i).getLast());
                }
            }
            // There are still lines of lower complexity, that are already saved
            // and need to be eliminated before updating the complete List
            // the old complexity (number of points in the lines) is used as threshold
            int finalComplexLevel = complexLevel;
            lineVertical.removeIf(u -> u.getPoints().size() <= finalComplexLevel);
            complexLevel++; // Update complexity level

            // Save in the storage List the increasing complex lines
            for (Line ln : lineVertical) {
                supportLines.add(new Line(ln.getPoints()));
            }
        }

        complexLevel = 2; // Reset complexity level to restart the merging

        // Same as the previous merging but for the horizontal lines
        while (lineHorizontal.size() > 1) {
            for (int i = 1; i < lineHorizontal.size(); i++) {
                if ((lineHorizontal.get(i).getFirst().getY() == lineHorizontal.get(i - 1).getLast().getY() &&
                        lineHorizontal.get(i).getFirst().getX() <= lineHorizontal.get(i - 1).getLast().getX())) {
                    lineHorizontal.get(i - 1).addPoint(lineHorizontal.get(i).getLast());
                }
            }

            int finalComplexLevel = complexLevel;
            lineHorizontal.removeIf(u -> u.getPoints().size() <= finalComplexLevel);
            complexLevel++;

            for (Line ln : lineHorizontal) {
                supportLines.add(new Line(ln.getPoints()));
            }
        }

        // Return the List removing each Line with less than N points,
        // if it's empty throw a custom "Not Found" error
        if (n > 1) {
            supportLines.removeIf(u -> u.getPoints().size() < n);
            if (supportLines.isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Try lower N or insert more points");
            }
            return supportLines;
        }

        // If n=1 return the complete list (if not empty)
        // otherwise throw a custom "Not Found" error
        if (supportLines.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Try to POST two neighbour points");
        }
        return supportLines;
    }
}


