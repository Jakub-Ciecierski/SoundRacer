package pl.dybisz.testgry.util.animation;

import pl.dybisz.testgry.shapes.basic.Obstacle;
import pl.dybisz.testgry.shapes.complex.GameBoard;
import pl.dybisz.testgry.util.obstacles.AvailableObstacleBuffer;
import pl.dybisz.testgry.util.obstacles.RenderedObstacleBuffer;

/**
 * Class will communicate both: {@link pl.dybisz.testgry.util.obstacles.AvailableObstacleBuffer}
 * and {@link pl.dybisz.testgry.util.obstacles.RenderedObstacleBuffer} to perform animation
 * of each obstacle defined in {@link pl.dybisz.testgry.shapes.complex.ObstaclesWorkshop}
 * <p></p>
 * Created by dybisz on 2014-12-01.
 */
public class ObstacleWorkshopAnimation {
    /**
     * Holds obstacles ready to be drawn.
     * See {@link pl.dybisz.testgry.util.obstacles.AvailableObstacleBuffer}
     * for more info.
     */
    private AvailableObstacleBuffer availableObstacles = new AvailableObstacleBuffer();
    /**
     * Holds obstacles which are currently being drawn.
     * See {@link pl.dybisz.testgry.util.obstacles.RenderedObstacleBuffer}
     * for more info.
     */
    private RenderedObstacleBuffer renderedObstacles = new RenderedObstacleBuffer();
    /**
     * Since obstacles are generated less often than
     * {@link pl.dybisz.testgry.shapes.complex.GameBoard} is animated
     * we need to add another counter.
     */
    private float spawnCounter = 0.0f;

    public void generateNextFrame() {
        /* First, move every obstacle on the scene */
        renderedObstacles.render();

        /* If there are obstacles on the scene which crossed the vanish point,
        move them to availableObstacles buffer */
        if (renderedObstacles.areThereAnyFreeObstacles())
            availableObstacles.addToBuffer(renderedObstacles.getFreeObstacles
                    (GameBoard.LATTICE_BOTTOM_RIGHT_CORNER.getZ()));

        /* Increase spawnCounter and check if there are any 2 available
        obstacles to draw */
        spawnCounter++;
        if (spawnCounter == GameBoard.OBSTACLE_GENERATION_TIME) {
            renderedObstacles.addToBuffer(availableObstacles.getObstacles(2));
            spawnCounter = 0;
        }
    }

    /**
     * Automatically adds passed obstacles array to available buffer.
     *
     * @param obstacles Array of obstacles to animate.
     */
    public ObstacleWorkshopAnimation(Obstacle[] obstacles) {
        availableObstacles.addToBuffer(obstacles);
    }
}
