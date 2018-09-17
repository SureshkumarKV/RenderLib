package in.sureshkumarkv.renderlib.types;

/**
 * Created by SureshkumarKV on 08/10/2017.
 */

public class RbMatrix {
    private static float[] mTempData = new float[16];

    private float[] mData;

    public RbMatrix() {
        mData = new float[16];
        android.opengl.Matrix.setIdentityM(mData, 0);
    }

    public void setMatrix(RbMatrix matrix) {
        float[] data = matrix.mData;
        for (int i = 0; i < 16; i++) {
            mData[i] = data[i];
        }
    }

    public void setMultiply(RbMatrix matrixLHS, RbMatrix matrixRHS) {
        android.opengl.Matrix.multiplyMM(mData, 0, matrixLHS.getData(), 0, matrixRHS.getData(), 0);
    }

    public void multiply(RbMatrix matrix) {
        android.opengl.Matrix.multiplyMM(mTempData, 0, mData, 0, matrix.mData, 0);
        for (int i = 0; i < 16; i++) {
            mData[i] = mTempData[i];
        }
    }


    public float[] getData() {
        return mData;
    }

    public void orthoM(float left, float right, float bottom, float top, float near, float far) {
        android.opengl.Matrix.orthoM(mData, 0, left, right, bottom, top, near, far);
    }

    public void setLookAtM(float eyeX, float eyeY, float eyeZ, float atX, float atY, float atZ, float upX, float upY, float upZ) {
        android.opengl.Matrix.setLookAtM(mData, 0, eyeX, eyeY, eyeZ, atX, atY, atZ, upX, upY, upZ);
    }

    public void perspectiveM(float fovY, float aspect, float near, float far) {
        android.opengl.Matrix.perspectiveM(mData, 0, fovY, aspect, near, far);
    }

    public void scale(float x, float y, float z) {
        android.opengl.Matrix.scaleM(mData, 0, x, y, z);
    }

    public void rotate(float angle, float x, float y, float z) {
        android.opengl.Matrix.rotateM(mData, 0, angle, x, y, z);
    }

    public void reset() {
        android.opengl.Matrix.setIdentityM(mData, 0);
    }
}
