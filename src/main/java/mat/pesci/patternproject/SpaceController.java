package mat.pesci.patternproject;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SpaceController {

    // SpaceService initialization
    SpaceService spaceService = new SpaceService();

    // GET method at /space returns all the points
    @GetMapping(value = "/space")
    public List<Point> getAllPoints() {
        return spaceService.findAllPoints();
    }

    // GET method at /space/n returns all the lines formed by at least N points
    @GetMapping(value = "/lines/{n}")
    public ArrayList<Line> getLines(@PathVariable int n) {
        LineConstructor lineConstructor =
                new LineConstructor((ArrayList<Point>) this.spaceService.findAllPoints());
        return lineConstructor.getLines(n);
    }

    // If it isn't already present, POST a new point with the syntax /point/x-y
    @PostMapping(value = "/point/{x}-{y}")
    public Point postPoint(@PathVariable int x, @PathVariable int y) {
        //if repository is empty directly POST the Point
        if (spaceService.count() == 0)
            return spaceService.savePoint(new Point(x, y));
        //else if the Point already exist throw an exception
        else if (spaceService.existsByXAndY(x, y))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Point (" + x +"," + y +") already exists!");
        //post the new Point otherwise
        return spaceService.savePoint(new Point(x, y));
    }

    // DELETE all Points at /space
    @DeleteMapping(value = "/space")
    public void deleteAllPoints(){
        spaceService.deleteAllPoints();
    }
}
