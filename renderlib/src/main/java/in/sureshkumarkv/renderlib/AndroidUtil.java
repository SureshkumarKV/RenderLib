package in.sureshkumarkv.renderlib;

import android.content.Context;

import java.io.InputStream;

import static android.content.res.AssetManager.ACCESS_BUFFER;

/**
 * Created by SureshkumarKV on 16/09/2018.
 */

public class AndroidUtil {
    public static InputStream getAssetFile(Context context, String path){
        try{
            return context.getAssets().open(path, ACCESS_BUFFER);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getBytes(InputStream inputStream){
        byte[] data = null;

        try{
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try{
                    inputStream.close();
                }catch (Exception e1){

                }
            }
        }

        return data;
    }
}
