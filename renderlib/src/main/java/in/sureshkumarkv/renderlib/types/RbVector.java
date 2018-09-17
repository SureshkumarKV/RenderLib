package in.sureshkumarkv.renderlib.types;

/**
 * Created by SureshkumarKV on 08/10/2017.
 */

public class RbVector {
    private float[] mData;

    public RbVector(float x, float y, float z){
        mData = new float[]{x, y, z};
    }

    public float[] getData(){
        return mData;
    }
}
