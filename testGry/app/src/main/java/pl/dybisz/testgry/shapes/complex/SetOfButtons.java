package pl.dybisz.testgry.shapes.complex;

import android.content.Context;
import android.opengl.GLES20;


import pl.dybisz.testgry.GameRenderer;
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
    public static final float SWITCH_CAMERA_BUTTON_WIDTH_SCREEN_RATIO = 0.25f;
    private Button upButton;
    private Button downButton;
    private Button leftButton;
    private Button rightButton;
    private Button switchCamera;
    public static float screenWidth;
    public static float screenHeight;
    private int program;
    private Context context;
    private int leftArrowTexture;
    private int developerCameraTexture;
    private final int playerCameraTexture;


    public SetOfButtons(Context context) {
        /* Compile standard shaders and program for THIS triangle */
        program = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.textureVertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.textureFragmentShader));
        leftArrowTexture = TexturesLoader.loadTexture(context, R.drawable.move_arrow);
        developerCameraTexture = TexturesLoader.loadTexture(context, R.drawable.developer_camera);
        playerCameraTexture = TexturesLoader.loadTexture(context, R.drawable.player_camera);
        initializeButtons();
    }

    private void initializeButtons() {
        leftButton = new Button(
                new float[]{-1f, 1f, 0f},
                new float[]{1f, -1f, 0},
                MoveType.MOVE_LEFT,
                leftArrowTexture,
                program);
        rightButton = new Button(
                new float[]{-1f, 1f, 0f},
                new float[]{1f, -1f, 0},
                MoveType.MOVE_RIGHT,
                leftArrowTexture,
                program);
        upButton = new Button(
                new float[]{-1f, 1f, 0f},
                new float[]{1f, -1f, 0},
                MoveType.MOVE_UP,
                leftArrowTexture,
                program);
        downButton = new Button(
                new float[]{-1f, 1f, 0f},
                new float[]{1f, -1f, 0},
                MoveType.MOVE_DOWN,
                leftArrowTexture,
                program);
        switchCamera = new Button(
                new float[]{-1f, 1f, 0f},
                new float[]{1f, -1f, 0},
                MoveType.MOVE_LEFT, // just for now - it will take normal UV
                playerCameraTexture,
                program);

    }

    public void draw(float[] mvpMatrix) {
        switch (GameRenderer.currentCamera) {
            case DEVELOPER_CAMERA:
                leftButton.draw(mvpMatrix, leftArrowTexture);
                rightButton.draw(mvpMatrix, leftArrowTexture);
                upButton.draw(mvpMatrix, leftArrowTexture);
                downButton.draw(mvpMatrix, leftArrowTexture);
                switchCamera.draw(mvpMatrix, developerCameraTexture);
                break;
            case PLAYER_CAMERA:
                switchCamera.draw(mvpMatrix,playerCameraTexture);
                break;
        }
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

        switchCamera.updateTransformations(
                new float[]{
                        ratio - (SWITCH_CAMERA_BUTTON_WIDTH_SCREEN_RATIO * screenWidth) / ((width > height) ? screenHeight : screenWidth),
                        -1.0f + (UP_BUTTON_WIDTH_SCREEN_RATIO * screenWidth) / ((width > height) ? screenHeight : screenWidth),
                        0.0f
                },
                new float[]{
                        (SWITCH_CAMERA_BUTTON_WIDTH_SCREEN_RATIO * screenWidth) / screenWidth,
                        (UP_BUTTON_HEIGHT_SCREEN_RATIO * screenHeight) / screenHeight,
                        1.0f}
        );
    }
}
