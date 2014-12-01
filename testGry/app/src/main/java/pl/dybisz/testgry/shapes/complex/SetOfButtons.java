package pl.dybisz.testgry.shapes.complex;

import android.content.Context;
import android.opengl.GLES20;


import pl.dybisz.testgry.R;
import pl.dybisz.testgry.shapes.basic.Button;
import pl.dybisz.testgry.util.MoveType;
import pl.dybisz.testgry.util.ShadersController;
import pl.dybisz.testgry.util.TexturesLoader;

/**
 * Created by user on 2014-11-26.
 */
public class SetOfButtons {
    public static final float UP_BUTTON_HEIGHT_SCREEN_RATIO = 0.10f;
    public static final float UP_BUTTON_WIDTH_SCREEN_RATIO = 0.10f;
    private Button upButton;
    private Button downButton;
    private Button leftButton;
    private Button rightButton;
    public static float screenWidth;
    public static float screenHeight;
    private int program;
    private Context context;
    private int textureId;


    public SetOfButtons(Context context) {
        /* Compile standard shaders and program for THIS triangle */
        program = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.textureVertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.textureFragmentShader));
        textureId = TexturesLoader.loadTexture(context, R.drawable.move_arrow );
        initializeButtons();
    }

    private void initializeButtons() {
        leftButton = new Button(
                new float[]{-1f, 1f, 0f},
                new float[]{1f, -1f, 0},
                MoveType.MOVE_LEFT,
                textureId,
                program);
        rightButton = new Button(
                new float[]{-1f, 1f, 0f},
                new float[]{1f, -1f, 0},
                MoveType.MOVE_RIGHT,
                textureId,
                program);
        upButton = new Button(
                new float[]{-1f, 1f, 0f},
                new float[]{1f, -1f, 0},
                MoveType.MOVE_UP,
                textureId,
                program);
        downButton = new Button(
                new float[]{-1f, 1f, 0f},
                new float[]{1f, -1f, 0},
                MoveType.MOVE_DOWN,
                textureId,
                program);
    }

    public void draw(float[] mvpMatrix) {
        leftButton.draw(mvpMatrix);
        rightButton.draw(mvpMatrix);
        upButton.draw(mvpMatrix);
        downButton.draw(mvpMatrix);
    }

    public void setDimensions(float width, float height) {
        this.screenWidth = width;
        this.screenHeight = height;
        float ratio = width / height;

        /* Left button setup */
        leftButton.updateTransformations(
                new float[]{
                        -ratio + (UP_BUTTON_WIDTH_SCREEN_RATIO * screenWidth) / ((width > height) ? screenHeight : screenWidth),
                        0.0f,
                        0.0f},
                new float[]{
                        (UP_BUTTON_WIDTH_SCREEN_RATIO * screenWidth) / screenWidth,
                        (UP_BUTTON_HEIGHT_SCREEN_RATIO * screenHeight) / screenHeight,
                        1.0f}
        );
        /* Right button setup */
        rightButton.updateTransformations(
                new float[]{
                        ratio - (UP_BUTTON_WIDTH_SCREEN_RATIO * screenWidth) / ((width > height) ? screenHeight : screenWidth),
                        0.0f,
                        0.0f},
                new float[]{
                        (UP_BUTTON_WIDTH_SCREEN_RATIO * screenWidth) / screenWidth,
                        (UP_BUTTON_HEIGHT_SCREEN_RATIO * screenHeight) / screenHeight,
                        1.0f}
        );

        /* Up button setup */
        upButton.updateTransformations(
                new float[]{
                        //ratio - (UP_BUTTON_WIDTH_SCREEN_RATIO * screenWidth) / ((width > height) ? screenHeight : screenWidth),
                        0.0f,
                        1.0f - (UP_BUTTON_WIDTH_SCREEN_RATIO * screenWidth) / ((width > height) ? screenHeight : screenWidth),
                        0.0f},
                new float[]{
                        (UP_BUTTON_WIDTH_SCREEN_RATIO * screenWidth) / screenWidth,
                        (UP_BUTTON_HEIGHT_SCREEN_RATIO * screenHeight) / screenHeight,
                        1.0f}
        );

        /* Down button setup */
        downButton.updateTransformations(
                new float[]{
                        //ratio - (UP_BUTTON_WIDTH_SCREEN_RATIO * screenWidth) / ((width > height) ? screenHeight : screenWidth),
                        0.0f,
                        -1.0f + (UP_BUTTON_WIDTH_SCREEN_RATIO * screenWidth) / ((width > height) ? screenHeight : screenWidth),
                        0.0f},
                new float[]{
                        (UP_BUTTON_WIDTH_SCREEN_RATIO * screenWidth) / screenWidth,
                        (UP_BUTTON_HEIGHT_SCREEN_RATIO * screenHeight) / screenHeight,
                        1.0f}
        );
    }
}
