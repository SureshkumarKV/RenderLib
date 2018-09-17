package in.sureshkumarkv.renderlib;

import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLES30;
import android.opengl.GLUtils;

/**
 * Created by SureshkumarKV on 07/10/2017.
 */

public abstract class RbTexture {
    private WRAP_MODE mWrappingModeS = WRAP_MODE.CLAMP_TO_EDGE;
    private WRAP_MODE mWrappingModeT = WRAP_MODE.CLAMP_TO_EDGE;
    private FILTER_MODE mFilterModeMin = FILTER_MODE.LINEAR;
    private FILTER_MODE mFilterModeMax = FILTER_MODE.LINEAR;
    private boolean mIsMipmapped = false;

    private int[] mGLName = new int[1];

    public abstract Bitmap getBitmap();
    public abstract int getType();

    public WRAP_MODE getWrappingModeS(){
        return mWrappingModeS;
    }

    public WRAP_MODE getWrappingModeT(){
        return mWrappingModeT;
    }

    public void setWrappingMode(WRAP_MODE s, WRAP_MODE t){
        mWrappingModeS = s;
        mWrappingModeT = t;
    }

    public FILTER_MODE getFilterModeMin(){
        return mFilterModeMin;
    }

    public FILTER_MODE getFilterModeMag(){
        return mFilterModeMax;
    }

    public void setFilterMode(FILTER_MODE min, FILTER_MODE max){
        mFilterModeMin = min;
        mFilterModeMax = max;
    }

    public void setMipmapped(boolean isMipmapped){
        mIsMipmapped = isMipmapped;
    }

    public boolean isMipmapped(){
        return mIsMipmapped;
    }

    void upload(){
        int type = getType();

        if(mGLName[0] == 0){
            GLES30.glGenTextures(1, mGLName, 0);
        }
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(type, mGLName[0]);
        GLUtils.texImage2D(type, 0, getBitmap(), 0);

        GLES30.glTexParameteri(type, GLES30.GL_TEXTURE_MIN_FILTER, mFilterModeMin.getValue());
        GLES30.glTexParameteri(type, GLES30.GL_TEXTURE_MAG_FILTER, mFilterModeMax.getValue());
        GLES30.glTexParameteri(type, GLES30.GL_TEXTURE_WRAP_S, mWrappingModeS.getValue());
        GLES30.glTexParameteri(type, GLES30.GL_TEXTURE_WRAP_T, mWrappingModeT.getValue());
        if(mIsMipmapped){
            GLES30.glGenerateMipmap(type);
        }

        GLES30.glBindTexture(type, 0);
    }

    void unload(){
        GLES30.glDeleteTextures(1, mGLName, 0);
        mGLName[0] = 0;
    }

    void select(int samplerIndex){
        if(mGLName[0] == 0){
            upload();
        }

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + samplerIndex);
        GLES30.glBindTexture(getType(), mGLName[0]);
    }

    public enum WRAP_MODE{
        CLAMP_TO_EDGE(GLES30.GL_CLAMP_TO_EDGE),
        REPEAT(GLES30.GL_REPEAT);

        private final int value;

        private WRAP_MODE(final int newValue){
            value = newValue;
        }

        public int getValue(){
            return value;
        }
    };

    public enum FILTER_MODE{
        NEAREST(GLES30.GL_NEAREST),
        LINEAR(GLES30.GL_LINEAR);

        private final int value;

        private FILTER_MODE(final int newValue){
            value = newValue;
        }

        public int getValue(){
            return value;
        }
    };
}
