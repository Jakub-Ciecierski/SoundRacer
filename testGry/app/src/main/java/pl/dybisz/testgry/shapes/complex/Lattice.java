package pl.dybisz.testgry.shapes.complex;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import pl.dybisz.testgry.shapes.basic.Line;
import pl.dybisz.testgry.util.ShadersController;
import pl.dybisz.testgry.util.animation.LatticeAnimation;
import pl.dybisz.testgry.util.mathematics.Vector3;

/**
 * Method is consists of all method needed for
 * initialization, rendering and animation of
 * a simple lattice.
 * <p></p>
 * Lines are divided into horizontal and vertical. For each we
 * have different buffer.
 * <p></p>
 * Only horizontal lines are animated. No big deal.
 * <p></p>
 * Created by dybisz on 2014-12-01.
 */
public class Lattice {
    /**
     * Serves as a class to separate information and drawing from
     * transformations.
     */
    private LatticeAnimation animation;
    /**
     * ID of an OpenGL shader program.
     */
    private int program;
    /**
     * How many horizontal line we render on
     * our grid.
     */
    private int numberOfHorizontalLines;
    /**
     * How many vertical line we render on our lattice.
     */
    private int numberOfVerticalLines;
    /**
     * Array holds all horizontal lines that we render
     * and animate.
     */
    private Line[] horizontalLines;
    /**
     * Array holds all vertical lines that we render.
     */
    private Line[] verticalLines;
    /**
     * Set of vertices coordinates for simple horizontal line.
     * Further modified. By default set to all set to 0.
     */
    private float[] horizontalLinesVertices = new float[6];
    /**
     * Set of vertices coordinates for simple vertical line.
     * Further modified. By default set to all set to 0.
     */
    private float[] verticalLinesVertices = new float[6];
    /**
     * Buffer to transport vertices coordinates from Dalvik's heap
     * to the native one(for horizontal lines).
     */
    private FloatBuffer horizontalVbo;
    /**
     * Buffer to transport vertices coordinates from Dalvik's heap
     * to the native one(for vertical lines).
     */
    private FloatBuffer verticalVbo;

    /**
     * Main constructor. Needs no arguments because most of thing are
     * based on constant defined in
     * {@link pl.dybisz.testgry.shapes.complex.GameBoard game board class}.
     */
    public Lattice() {
        /* Calculating number of lines of each type */
        this.numberOfHorizontalLines =
                (int) (Math.abs(GameBoard.LATTICE_BOTTOM_RIGHT_CORNER.getZ()
                        - GameBoard.LATTICE_LENGTH) / GameBoard.LATTICE_GAP_LENGTH);
        this.numberOfVerticalLines =
                (int) (Math.abs(GameBoard.LATTICE_BOTTOM_RIGHT_CORNER.getX()
                        - GameBoard.LATTICE_WIDTH) / GameBoard.LATTICE_GAP_LENGTH);

        /* Based on number we need, arrays ale initialized to proper size */
        this.horizontalLines = new Line[numberOfHorizontalLines];
        this.verticalLines = new Line[numberOfVerticalLines];

        /* Give each type of line a magnitude */
        horizontalLinesVertices[3] = GameBoard.LATTICE_WIDTH;
        verticalLinesVertices[5] = GameBoard.LATTICE_LENGTH;

        /* Init animation object with calculated vanish and spawn boarders */
        this.animation = new LatticeAnimation(GameBoard.LATTICE_BOTTOM_RIGHT_CORNER.getZ(),
                GameBoard.LATTICE_BOTTOM_RIGHT_CORNER.getZ() + GameBoard.LATTICE_LENGTH);

        /* Shader program compilation */
        this.program = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.vertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.fragmentShader));

        loadBuffers();
        createLines();

    }

    /**
     * Draws horizontal and vertical lines taking into account passed matrix.
     *
     * @param mvpMatrix Matrix for transformations. Commonly we use camera matrix
     *                  for this purpose.
     */
    public void draw(float[] mvpMatrix) {
        for (int i = 0; i < horizontalLines.length; i++) {
            horizontalLines[i].draw(mvpMatrix);
        }
        for (int j = 0; j < verticalLines.length; j++) {
            verticalLines[j].draw(mvpMatrix);
        }
    }

    /**
     * Translates all lines to fit next animation frame.
     */
    public void switchFrame() {
        for (int i = 0; i < horizontalLines.length; i++) {
            animation.generateNextFrame(horizontalLines[i]);
        }
    }

    /**
     * Initializes each line of the lattice.
     */
    private void createLines() {
        for (int i = 1; i <= horizontalLines.length; i++) {
            horizontalLines[i - 1] = new Line(GameBoard.LATTICE_COLOR, horizontalVbo,
                    new Vector3(0f, 0f, i * GameBoard.LATTICE_GAP_LENGTH), program);
        }
        for (int j = 1; j <= verticalLines.length; j++) {
            verticalLines[j - 1] = new Line(GameBoard.LATTICE_COLOR, verticalVbo,
                    new Vector3(j * GameBoard.LATTICE_GAP_LENGTH, 0f, 0f), program);
        }
    }

    /**
     * Loads data into:
     * {@link #horizontalVbo} and {@link #verticalVbo}.
     */
    private void loadBuffers() {
        horizontalVbo = ByteBuffer.allocateDirect(horizontalLinesVertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(horizontalLinesVertices);
        horizontalVbo.position(0);

        verticalVbo = ByteBuffer.allocateDirect(verticalLinesVertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(verticalLinesVertices);
        verticalVbo.position(0);
    }

}
