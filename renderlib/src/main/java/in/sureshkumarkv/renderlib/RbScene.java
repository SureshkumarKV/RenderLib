package in.sureshkumarkv.renderlib;

import android.opengl.GLES30;

/**
 * Created by SureshkumarKV on 07/10/2017.
 */

public class RbScene {
    private ALPHABLEND mAlphaBlend = ALPHABLEND.NONE;
    private CULLFACE mCullFace = CULLFACE.BACK;
    private DEPTHTEST mDepthTest = DEPTHTEST.LEQUAL;

    private RbCamera mCamera;
    private RbNode mRootNode;

    public RbScene(){
        mCamera = new RbCamera();
    }

    public void setCamera(RbCamera camera){
        mCamera = camera;
    }

    public RbCamera getCamera(){
        return mCamera;
    }

    public void setRootNode(RbNode node) {
        mRootNode = node;
    }

    public RbNode getRootNode() {
        return mRootNode;
    }

    public void setBlendFunc(ALPHABLEND alphaBlend){
        mAlphaBlend = alphaBlend;
    }

    public ALPHABLEND getBlendFunc(){
        return mAlphaBlend;
    }

    public void setCullface(CULLFACE cullFace){
        mCullFace = cullFace;
    }

    public CULLFACE getCullface(){
        return mCullFace;
    }

    public void setDepthTest(DEPTHTEST depthTest){
        mDepthTest = depthTest;
    }

    public DEPTHTEST getDepthTest(){
        return mDepthTest;
    }

    void setup(boolean isRecursive, int width, int height, RbInstance instance, RbWorld world, RbListener listener) {
        if(listener != null){
            listener.onSetupScene(isRecursive, width, height, instance, world, this);
        }

        if(isRecursive){
            if (mRootNode != null) {
                mRootNode.setup(isRecursive, width, height, instance, world, this, listener, null);
            }
        }
    }

    void render(int width, int height, RbInstance instance, RbWorld world, RbListener listener, long deltaTimeNanos) {
        if(listener != null){
            listener.onRenderScene(width, height, instance, world, this, deltaTimeNanos);
        }

        instance.setAlphaBlend(mAlphaBlend);
        instance.setDepthTest(mDepthTest);
        instance.setCullface(mCullFace);

        if (mRootNode != null) {
            mRootNode.render(width, height, instance, world, this, listener, null, deltaTimeNanos);
        }
    }

    public static enum ALPHABLEND{
        NONE(0, 0),
        ONE$ONE_MINUS_SRC_ALPHA(GLES30.GL_ONE, GLES30.GL_ONE_MINUS_SRC_ALPHA),
        SRC_ALPHA$ONE_MINUS_SRC_ALPHA(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA),
        SRC_ALPHA$ONE(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE),
        SRC_COLOR$GL_ONE(GLES30.GL_SRC_COLOR, GLES30.GL_ONE);

        private final int lValue;
        private final int rValue;

        private ALPHABLEND(final int lValue, final int rValue){
            this.lValue = lValue;
            this.rValue = rValue;
        }

        public int getLValue(){
            return lValue;
        }

        public int getRValue(){
            return rValue;
        }
    };

    public static enum CULLFACE{
        NONE(0),
        FRONT(GLES30.GL_FRONT),
        BACK(GLES30.GL_BACK),
        FRONT_AND_BACK(GLES30.GL_FRONT_AND_BACK);

        private final int value;

        private CULLFACE(final int newValue){
            value = newValue;
        }

        public int getValue(){
            return value;
        }
    };

    public static enum DEPTHTEST{
        NONE(0),
        LESS(GLES30.GL_LESS),
        LEQUAL(GLES30.GL_LEQUAL),
        GREATER(GLES30.GL_GREATER),
        GEQUAL(GLES30.GL_GEQUAL),
        ALWAYS(GLES30.GL_ALWAYS);

        private final int value;

        private DEPTHTEST(final int newValue){
            value = newValue;
        }

        public int getValue(){
            return value;
        }
    };
}
