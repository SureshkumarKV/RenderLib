package in.sureshkumarkv.renderlib.geometry;

import in.sureshkumarkv.renderlib.RbGeometry;

/**
 * Created by SureshkumarKV on 07/10/2017.
 */

public class RbNinepatchGeometry extends RbGeometry {
    @Override
    public float[][] getBuffers() {
        return new float[0][];
    }

    @Override
    public String[] getNames() {
        return new String[0];
    }

    @Override
    public int[] getBufferIndices() {
        return new int[0];
    }

    @Override
    public int[] getOffsets() {
        return new int[0];
    }

    @Override
    public int[] getStrides() {
        return new int[0];
    }

    @Override
    public int[] getSizes() {
        return new int[0];
    }

    @Override
    public short[] getIndices() {
        return new short[0];
    }

    @Override
    public int getPrimitiveMode() {
        return 0;
    }
}
