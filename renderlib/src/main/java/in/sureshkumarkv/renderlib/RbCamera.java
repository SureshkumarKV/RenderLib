package in.sureshkumarkv.renderlib;

import in.sureshkumarkv.renderlib.types.RbMatrix;

/**
 * Created by SureshkumarKV on 07/10/2017.
 */

public class RbCamera {
    private RbMatrix mProjectionTransform;
    private RbMatrix mViewTransform;

    public RbCamera() {
        mProjectionTransform = new RbMatrix();
        mViewTransform = new RbMatrix();
    }

    public void setOrthographic(float width, float height, float near, float far) {
        mProjectionTransform.orthoM(-width / 2, width / 2, -height / 2, height / 2, near, far);
    }

    public void setPerspective(float fovY, float aspect, float near, float far) {
        mProjectionTransform.perspectiveM(fovY, aspect, near, far);
    }

    public void setLookAt(float eyeX, float eyeY, float eyeZ,   float atX, float atY, float atZ,   float upX, float upY, float upZ) {
        mViewTransform.setLookAtM(eyeX, eyeY, eyeZ,   atX, atY, atZ,   upX, upY, upZ);
    }

    public RbMatrix getProjectionTransform(){
        return mProjectionTransform;
    }

    public RbMatrix getViewTransform(){
        return mViewTransform;
    }
}
