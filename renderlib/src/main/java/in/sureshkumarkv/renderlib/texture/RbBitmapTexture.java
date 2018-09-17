package in.sureshkumarkv.renderlib.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;

import in.sureshkumarkv.renderlib.AndroidUtil;
import in.sureshkumarkv.renderlib.RbTexture;

/**
 * Created by SureshkumarKV on 07/10/2017.
 */

public class RbBitmapTexture extends RbTexture{
    private Bitmap mBitmap;

    public static RbBitmapTexture fromAsset(Context context, String path){
        byte[] data = AndroidUtil.getBytes(AndroidUtil.getAssetFile(context, path));
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return new RbBitmapTexture(bitmap);
    }

    public static RbBitmapTexture fromResource(Context context, int resId){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        return new RbBitmapTexture(bitmap);
    }

    public static RbBitmapTexture fromFile(Context context, String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return new RbBitmapTexture(bitmap);
    }

    private RbBitmapTexture(Bitmap bitmap){
        mBitmap = bitmap;
    }

    @Override
    public Bitmap getBitmap() {
        return mBitmap;
    }

    @Override
    public int getType() {
        return GLES30.GL_TEXTURE_2D;
    }
}
