package mat.pesci.patternproject;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;

public class LineConstructor {
    //List for all the points
    ArrayList<Point> points;

    //constructor
    public LineConstructor(ArrayList<Point> allPoints) {
        this.points = allPoints;
    }

    public ArrayList<Line> getLines(int n) {
        //create 2 List(s) for the horizontal and vertical lines in order to avoid
        //possible artifact between the last horizontal lines and the first vertical one
        ArrayList<Line> lineHorizontal = new ArrayList<>();
        ArrayList<Line> lineVertical = new ArrayList<>();

        //lines are formed by a minimum of 2 points, if n=1 throw an exception
        if (n < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lines must have at least 1 Point!");
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

        // else merge smaller lines until reaching n-points ones
        return bottomUpMerge(lineHorizontal,lineVertical, n);
    }

    private ArrayList<Line> bottomUpMerge(ArrayList<Line> lineHorizontal, ArrayList<Line> lineVertical, int n) {

        int complexLevel=2;
        final ArrayList<Line> supportLines = new ArrayList<Line>();
        for(Line ln : lineVertical){
            supportLines.add(new Line(ln.getFirst(),ln.getLast()));
        }
        //2-points lines are already formed, continue until n-points lines are build
        while(lineVertical.size()>1) {
        /*    //confront each pair of subsequent horizontal lines
            for (int i = 1; i < lineHorizontal.size(); i++) {
                // if n-complex lines are on the same axis, and they have points in common (at least one):
                // -case same X: successive line's first Y is lower or equal to the last Y of the precedent one
                if ((lineHorizontal.get(i).getFirst().getX() == lineHorizontal.get(i - 1).getLast().getX() &&
                        lineHorizontal.get(i).getFirst().getY() <= lineHorizontal.get(i - 1).getLast().getY()))  {
                    //update the precedent line with the only point missing (last point of successive line)
                    lineHorizontal.get(i - 1).addPoint(lineHorizontal.get(i).getLast());
                }
            }*/
            //confront each pair of subsequent vertical lines
            for (int i=1; i< lineVertical.size();i++){
                // -case same Y: successive line's first X is lower or equal to the last X of the precedent one
                if (lineVertical.get(i).getFirst().getY() == lineVertical.get(i - 1).getLast().getY() &&
                        lineVertical.get(i).getFirst().getX() <= lineVertical.get(i - 1).getLast().getX()){
                    //update the precedent line with the only point missing (last point of successive line)
                    lineVertical.get(i - 1).addPoint(lineVertical.get(i).getLast());
                }
            }
            //there are still lines of complexity n-1, that need to be eliminated before returning the List
            //val complexLevel1 = complexLevel;
            //Predicate<Line> filter = u -> (u.getPoints().size() <= complexLevel);
            //lineHorizontal.removeIf(u -> u.getPoints().size() <= finalComplexLevel);
            //removeSimpleLine(lineVertical, complexLevel);

            for (Iterator<Line> lnIterator = lineVertical.iterator(); lnIterator.hasNext();) {
                Line ln2= lnIterator.next();
                if (ln2.getPoints().size() <= complexLevel) {
                    lnIterator.remove();
                }
            }


            complexLevel++;

            for(Line ln : lineVertical){
                supportLines.add(new Line(ln.getPoints()));
            }

        }
        //return the complete list if not empty, otherwise throw an exception
        return supportLines;
    }



}


