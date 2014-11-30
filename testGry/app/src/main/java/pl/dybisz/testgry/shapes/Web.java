package pl.dybisz.testgry.shapes;

/**
 * Created by user on 2014-11-23.
 */
public class Web {
    private Line[] webX = new Line[100];
    private Line[] webY = new Line[100];

    public Web() {
        fillOutWebsData();

    }

    private void fillOutWebsData() {
        int X=-50;
        int Y=-50;
        for(int i=0;i<100;i++){
            webX[i] = new Line(new float[]{1f, 1.0f,1.0f,1.0f},
                    new float[]{-200f,0f,X,
                            200f,0f,X});
            X+=2;
        }
        for(int i=0;i<100;i++){
            webY[i] = new Line(new float[]{1f, 1.0f,1.0f,1.0f},
                    new float[]{Y,0f,-200f,
                            Y,0f,200f});
            Y+=2;
        }
    }

    public void draw(float[] matrix) {
        for(int i=0;i<100;i++){
            webX[i].draw(matrix);
        }
        for(int i=0;i<100;i++){
            webY[i].draw(matrix);
        }
    }
}
