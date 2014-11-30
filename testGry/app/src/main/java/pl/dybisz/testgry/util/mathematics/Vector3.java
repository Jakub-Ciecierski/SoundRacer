package pl.dybisz.testgry.util.mathematics;

import android.util.Log;

/**
 * Created by user on 2014-11-29.
 */
public class Vector3 extends Vector {
    public Vector3(float x, float y, float z) {
        super.x = x;
        super.y = y;
        super.z = z;
    }

    public void multiplyBy(float scalar) {
        super.x *= scalar;
        super.y *= scalar;
        super.z *= scalar;
    }

    public void add(Vector3 vec) {
        super.x += vec.getX();
        super.y += vec.getY();
        super.z += vec.getZ();
    }

    public void print() {
        Log.i("", "x: " + x + " y:" + y + " z:" + z);
    }

    public void print(String tag) {
        Log.i(tag, "x: " + x + " y:" + y + " z:" + z);
    }

    public float getX() {
        return super.x;
    }

    public float getY() {
        return super.y;
    }

    public float getZ() {
        return super.z;
    }

    public void setX(float x) {
        super.x = x;
    }

    public void setY(float y) {
        super.y = y;
    }

    public void setZ(float z) {
        super.z = z;
    }
}
