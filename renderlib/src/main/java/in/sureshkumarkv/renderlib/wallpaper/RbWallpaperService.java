package in.sureshkumarkv.renderlib.wallpaper;

import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;

/**
 * Created by SureshkumarKV on 10/10/2017.
 */

public abstract class RbWallpaperService extends WallpaperService {
    public interface RbWallpaperEngine {
        void onCreate(GLSurfaceView glSurfaceView);
        void onResize(int width, int height);
        void onTouch(MotionEvent event);
        void onOffset(float xOffset, float yOffset);
        void onShown();
        void onHidden();
        void onDestroy();
    }

    private Class<? extends RbWallpaperEngine> mEngineClass;

    public RbWallpaperService(Class<? extends RbWallpaperEngine> engineClass){
        mEngineClass = engineClass;
    }

    @Override
    public final void onCreate() {//Don't override this in subclasses. Do everything in onCreateSurface. This endures each instance is separate and avoid issues.
        super.onCreate();
    }

    @Override
    public Engine onCreateEngine() {
        return new WallpaperEngine();
    }

    private class WallpaperEngine extends Engine{
        private int mWidth, mHeight;
        private GLSurfaceView mGLSurfaceView;
        private RbWallpaperEngine mEngineObject;

        final public void onCreate(SurfaceHolder surfaceHolder) {
            try{
                mEngineObject = mEngineClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mGLSurfaceView = new GLSurfaceView(RbWallpaperService.this) {
                @Override
                public SurfaceHolder getHolder() {
                    return getSurfaceHolder();
                }
            };

            mEngineObject.onCreate(mGLSurfaceView);
        }

        final public void onDestroy() {
            mEngineObject.onDestroy();
            mEngineObject = null;
        }

        @Override
        final public void onTouchEvent(final MotionEvent event){
            mEngineObject.onTouch(event);
        }

        @Override
        final public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset){
            mEngineObject.onOffset(xOffset, yOffset);
        }

        @Override
        final public void onVisibilityChanged(final boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                if(mGLSurfaceView != null){
                    mGLSurfaceView.onResume();
                }
                mEngineObject.onShown();

            } else {
                if(mGLSurfaceView != null){
                    mGLSurfaceView.onPause();
                }
                mEngineObject.onHidden();
            }
        }

        @Override
        final public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //Make getWidth() and getHeight() of mGLSurfaceView report updated fullscreen size.
            DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
            mWidth = metrics.widthPixels;
            mHeight = metrics.heightPixels;
            mGLSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(mWidth, mHeight));
            mGLSurfaceView.layout(0, 0, mWidth, mHeight);

            super.onSurfaceChanged(holder, format, width, height);

            mEngineObject.onResize(mWidth, mHeight);
        }
    }
}
