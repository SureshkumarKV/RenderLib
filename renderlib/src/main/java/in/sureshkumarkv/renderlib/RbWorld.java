package in.sureshkumarkv.renderlib;

import android.graphics.Color;
import android.opengl.GLES30;

/**
 * Created by SureshkumarKV on 07/10/2017.
 */

public class RbWorld {
    private int mBackgroundColor;

    private float mCroppingX, mCroppingY, mCroppingWidth, mCroppingHeight;

    private RbScene[] mScenes;

    public RbWorld(){
        mBackgroundColor = 0xff2f2f2f;

        mCroppingX = 0;
        mCroppingY = 0;
        mCroppingWidth = 1;
        mCroppingHeight = 1;

        mScenes = new RbScene[0];
    }

    public void setBackgroundColor(int bkgColor){
        mBackgroundColor = bkgColor;
    }

    public int getBackgroundColor(){
        return mBackgroundColor;
    }

    public void setViewport(float croppingX, float croppingY, float croppingWidth, float croppingHeight){
        mCroppingX = croppingX;
        mCroppingY = croppingY;
        mCroppingWidth = croppingWidth;
        mCroppingHeight = croppingHeight;
    }

    public void addScene(RbScene scene) {
        RbScene[] temp = new RbScene[mScenes.length + 1];
        for(int i=0; i<mScenes.length; i++){
            temp[i] = mScenes[i];
        }
        temp[mScenes.length] = scene;
        mScenes = temp;
    }

    public void removeScene(RbScene scene) {
        RbScene[] temp = new RbScene[mScenes.length - 1];
        for(int i=0, index = 0; i<mScenes.length; i++){
            if(mScenes[i] != scene){
                temp[index++] = mScenes[i];
            }
        }
        mScenes = temp;
    }

    public RbScene[] getScenes(){
        return mScenes;
    }

    void setup(boolean isRecursive, int width, int height, RbInstance instance, RbListener listener) {
        if(listener != null){
            listener.onSetupWorld(isRecursive, width, height, instance, this);
        }

        GLES30.glClearColor(Color.red(mBackgroundColor)/255.0f, Color.green(mBackgroundColor)/255.0f, Color.blue(mBackgroundColor)/255.0f, Color.alpha(mBackgroundColor)/255.0f);

        int viewportWidth = (int)(width/mCroppingWidth);
        int viewportHeight = (int)(height/mCroppingHeight);
        int viewportX = -(int)(mCroppingX * viewportWidth);
        int viewportY = -(int)(mCroppingY * viewportHeight);
        GLES30.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);

        if(isRecursive){
            for(RbScene scene: mScenes){
                scene.setup(isRecursive, width, height, instance, this, listener);
            }
        }
    }

    void render(int width, int height, RbInstance instance, RbListener listener, long deltaTimeNanos) {
        if(listener != null){
            listener.onRenderWorld(width, height, instance, this, deltaTimeNanos);
        }

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_STENCIL_BUFFER_BIT);

        for(RbScene scene: mScenes){
            scene.render(width, height, instance, this, listener, deltaTimeNanos);
        }
    }
}
