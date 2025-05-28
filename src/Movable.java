import processing.core.PImage;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Movable extends ActiveAnimatedEntity {
    private PathingStrategy pathingStrategy;

    public Movable(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, PathingStrategy pathingStrategy) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.pathingStrategy = pathingStrategy;
    }

    public Point nextPosition(WorldModel world, Point destPos) {
        Predicate<Point> canPassThrough = (p) -> !this.isInvalidMove(world, p);
        BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.adjacent(p2);
        Function<Point, Stream<Point>> potentialNeighbors = PathingStrategy.CARDINAL_NEIGHBORS;

        List<Point> path = this.pathingStrategy.computePath(this.getPosition(), destPos, canPassThrough, withinReach, potentialNeighbors);
        if (path.isEmpty()) {
            return this.getPosition();
        }
        return path.get(0);
    }

    public abstract boolean moveTo(WorldModel model, Entity target, EventScheduler scheduler);

    /**
     * The entity can move to destination if it's not occupied.
     */
    public boolean isInvalidMove(WorldModel world, Point destination) {
        return world.isOccupied(destination);
    }
}
