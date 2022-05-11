package mat.pesci.patternproject;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpaceService {

    // List for the points
    private static List<Point> points = new ArrayList<>();

    // Returns all the Points (to be shown)
    public List<Point> findAllPoints() {
        return points;
    }

    // Saves the Points in the List and return the Point (to be shown)
    public Point savePoint(Point point) {
        points.add(point);
        return point;
    }

    // Deletes the Points in the List
    public void deleteAllPoints(){
        points.clear();
    }

    // Returns size of the List
    public int count() {
        return points.size();
    }

    // Confronts the coordinates of a point to check if it is already present
    public boolean existsByXAndY(int x, int y) {
        for (Point p : points)
            if (p.getX()==x && p.getY()==y)
                return true;
        return false;
    }
}
