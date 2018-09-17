package in.sureshkumarkv.renderlib;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.Choreographer;
import android.view.Surface;

import in.sureshkumarkv.renderlib.RbScene.ALPHABLEND;
import in.sureshkumarkv.renderlib.RbScene.CULLFACE;
import in.sureshkumarkv.renderlib.RbScene.DEPTHTEST;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by SureshkumarKV on 07/10/2017.
 */

public class RbInstance {
    static {
        System.loadLibrary("native-lib");
    }
    private static native void setSize(Surface surface, int width, int height);

    public static String ATTRIBUTE_NAME_VERTEX_COORDINATES = "aVertCoord";
    public static String ATTRIBUTE_NAME_TEXTURE_COORDINATES = "aTexCoord";

    public static String UNIFORM_NAME_MODEL_MATRIX = "uModelMatrix";
    public static String UNIFORM_NAME_VIEW_MATRIX = "uViewMatrix";
    public static String UNIFORM_NAME_PROJECTION_MATRIX = "uProjMatrix";
    public static String UNIFORM_NAME_TEXTURE2D_SAMPLERS = "uTexture2DSamplers";

    private GLSurfaceView mGLSurfaceView;
    private Handler mHandler;

    private int mWidth, mHeight;
    private float mResolution;
    private boolean mCallSetSize;

    private Choreographer.FrameCallback mFrameCallback;
    private long mTimeToWaitNanos;
    private long mLastFrameTimeNanos;
    private long mDeltaTimeRealNanos;
    private long mNextFrameTime;

    private ALPHABLEND mAlphaBlend = ALPHABLEND.NONE;
    private CULLFACE mCullFace = CULLFACE.BACK;
    private DEPTHTEST mDepthTest = DEPTHTEST.LEQUAL;
    private RbShader mShader;

    private RbWorld mWorld;
    private RbListener mListener;

    public RbInstance() {
        mWidth = Integer.MIN_VALUE;
        mHeight = Integer.MIN_VALUE;
        mResolution = 1;
        mCallSetSize = true;

        mFrameCallback = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                if((frameTimeNanos - mNextFrameTime) >= -8000000 || mNextFrameTime == 0){
                    mNextFrameTime = frameTimeNanos + mTimeToWaitNanos;
                    mDeltaTimeRealNanos = Math.min(1000000000, frameTimeNanos-mLastFrameTimeNanos);
                    mLastFrameTimeNanos = frameTimeNanos;

                    if(mGLSurfaceView != null){
                        mGLSurfaceView.requestRender();
                    }
                }

                Choreographer.getInstance().postFrameCallback(this);
            }
        };
        setActive(true);
    }

    public void setActive(boolean isActive){
        Choreographer.getInstance().removeFrameCallback(mFrameCallback);
        if(isActive){
            Choreographer.getInstance().postFrameCallback(mFrameCallback);
        }
    }

    public void setView(GLSurfaceView glSurfaceView) {
        mGLSurfaceView = glSurfaceView;
        mHandler = new Handler(glSurfaceView.getContext().getMainLooper());

        if (glSurfaceView != null) {
            mGLSurfaceView.setEGLContextClientVersion(3);
            mGLSurfaceView.setPreserveEGLContextOnPause(true);
            mGLSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
                @Override
                public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                    GLUtil.uploadOrUnloadAll(mWorld, true);
                }

                @Override
                public void onSurfaceChanged(GL10 gl, int width, int height) {
                    System.out.println("DD>>>>>>>>>>>>>>>>>>>>>>>> "+mWidth+" , "+mHeight+" >> "+mGLSurfaceView.getWidth()+" , "+mGLSurfaceView.getHeight()+" <<<< "+ mCallSetSize);

                    if(mCallSetSize){//The mGLSurfaceView.onPause() & mGLSurfaceView.onResume() calls below will trigger another onSurfaceChanged. Set mCallSetSize=false to ignore it.
                        mWidth = (int) (width * mResolution);
                        mHeight = (int) (height * mResolution);
                        mCallSetSize = false;

                        if(mWorld != null){
                            mWorld.setup(true, mWidth, mHeight, RbInstance.this, mListener);
                        }

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(mGLSurfaceView.getHolder().getSurface().isValid()){
                                    mGLSurfaceView.onPause();
                                    setSize(mGLSurfaceView.getHolder().getSurface(), mWidth, mHeight);//mGLSurfaceView.getHolder().setFixedSize(mWidth, mHeight);
                                    mGLSurfaceView.onResume();

                                    System.gc ();
                                    System.runFinalization ();
                                }
                            }
                        });

                    }else{
                        mCallSetSize = true;
                    }
                }

                @Override
                public void onDrawFrame(GL10 gl) {
                    if (mWorld != null) {
                        mWorld.render(mWidth, mHeight, RbInstance.this, mListener, mDeltaTimeRealNanos);
                    }
                }
            });

            mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
    }

    public void setFPS(int fps) {
        mTimeToWaitNanos = fps==0? Long.MAX_VALUE: (long)(1e9f/fps);
        if(mGLSurfaceView != null){
            mGLSurfaceView.requestRender();
        }
    }

    public int getFPS() {
        return mTimeToWaitNanos == Long.MAX_VALUE? 0: Math.round(1e9f/mTimeToWaitNanos);
    }

    public void setResolution(float resolution){
        if(mResolution != resolution){
            mResolution = Math.min(1, Math.max(0, resolution));

            if(mGLSurfaceView != null){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mGLSurfaceView.getHolder().getSurface().isValid()) {
                            mCallSetSize = true;
                            mGLSurfaceView.onPause();//Make onSurfaceChanged to be triggered.
                            mGLSurfaceView.onResume();
                        }
                    }
                });
            }
        }
    }

    public void requestDraw() {
        if(mGLSurfaceView != null){
            mGLSurfaceView.requestRender();
        }
    }

    public void setWorld(RbWorld world) {
        mWorld = world;
    }

    public void setListener(RbListener listener) {
        mListener = listener;
    }

    void setAlphaBlend(ALPHABLEND alphaBlend){
        if(mAlphaBlend != alphaBlend){
            mAlphaBlend = alphaBlend;

            if(mAlphaBlend == ALPHABLEND.NONE){
                GLES30.glDisable(GLES30.GL_BLEND);
            }else{
                GLES30.glEnable(GLES30.GL_BLEND);
                GLES30.glBlendFunc(mAlphaBlend.getLValue(), mAlphaBlend.getRValue());
            }
        }
    }

    void setCullface(CULLFACE cullFace){
        if(mCullFace != cullFace){
            mCullFace = cullFace;

            if(cullFace == CULLFACE.NONE){
                GLES30.glDisable(GLES30.GL_CULL_FACE);
            }else{
                GLES30.glEnable(GLES30.GL_CULL_FACE);
                GLES30.glCullFace(cullFace.getValue());
            }
        }
    }

    void setDepthTest(DEPTHTEST depthTest){
        if(mDepthTest != depthTest){
            mDepthTest = depthTest;

            if(mDepthTest == DEPTHTEST.NONE){
                GLES30.glDisable(GLES30.GL_DEPTH_TEST);
            }else{
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                GLES30.glDepthFunc(mDepthTest.getValue());
            }
        }
    }

    void setShader(RbShader shader){
        if(mShader != shader){
            mShader = shader;
            GLES30.glUseProgram(shader.getValue());
        }
    }
}
