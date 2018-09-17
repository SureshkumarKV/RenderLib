package in.sureshkumarkv.renderlib.geometry;

import android.opengl.GLES30;

import in.sureshkumarkv.renderlib.RbGeometry;
import in.sureshkumarkv.renderlib.RbInstance;

/**
 * Created by SureshkumarKV on 07/10/2017.
 */

public class RbRectangleGeometry extends RbGeometry {
    float width, height;
    float xOffset, yOffset;
    int xSections, ySections;
    boolean isIndexed;

    public RbRectangleGeometry(float width, float height, float xOffset, float yOffset, int xSections, int ySections, boolean isIndexed){
        this.width = width/2;
        this.height = height/2;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xSections = xSections;
        this.ySections = ySections;
        this.isIndexed = isIndexed;
    }

    @Override
    public float[][] getBuffers() {
        if(isIndexed){
            float[] buff = new float[5*(xSections+1)*(ySections+1)];
            int index = 0;
            for(int y=0; y<=ySections; y++){
                for(int x=0; x<=xSections; x++){
                    buff[index + 0] = -width + xOffset + x*2*width/xSections;
                    buff[index + 1] = height + yOffset - y*2*height/ySections;
                    buff[index + 2] = 0.0f;

                    buff[index + 3] = x/(float)xSections;
                    buff[index + 4] = y/(float)ySections;

//					System.out.println("v--> "+buff[index + 0]+" , "+buff[index + 1]+" , "+buff[index + 2]);
                    index += 5;
                }
            }
            return new float[][]{buff};

        }else{
            float[] buff = new float[5*xSections*ySections*6];
            int index = 0;
            for(int y=0; y<ySections; y++){
                for(int x=0; x<xSections; x++){
                    buff[index + 0] = -width + xOffset + x*2*width/xSections;
                    buff[index + 1] = height + yOffset - y*2*height/ySections;
                    buff[index + 2] = 0.0f;
                    buff[index + 3] = x/(float)xSections;
                    buff[index + 4] = y/(float)ySections;

                    buff[index + 5] = -width + xOffset + x*2*width/xSections;
                    buff[index + 6] = height + yOffset - (y+1)*2*height/ySections;
                    buff[index + 7] = 0.0f;
                    buff[index + 8] = x/(float)xSections;
                    buff[index + 9] = (y+1)/(float)ySections;

                    buff[index + 10] = -width + xOffset + (x+1)*2*width/xSections;
                    buff[index + 11] = height + yOffset - (y+1)*2*height/ySections;
                    buff[index + 12] = 0.0f;
                    buff[index + 13] = (x+1)/(float)xSections;
                    buff[index + 14] = (y+1)/(float)ySections;


                    buff[index + 15] = -width + xOffset + x*2*width/xSections;
                    buff[index + 16] = height + yOffset - y*2*height/ySections;
                    buff[index + 17] = 0.0f;
                    buff[index + 18] = x/(float)xSections;
                    buff[index + 19] = y/(float)ySections;

                    buff[index + 20] = -width + xOffset + (x+1)*2*width/xSections;
                    buff[index + 21] = height + yOffset - (y+1)*2*height/ySections;
                    buff[index + 22] = 0.0f;
                    buff[index + 23] = (x+1)/(float)xSections;
                    buff[index + 24] = (y+1)/(float)ySections;

                    buff[index + 25] = -width + xOffset + (x+1)*2*width/xSections;
                    buff[index + 26] = height + yOffset - y*2*height/ySections;
                    buff[index + 27] = 0.0f;
                    buff[index + 28] = (x+1)/(float)xSections;
                    buff[index + 29] = y/(float)ySections;

                    index += 5*6;
                }
            }
            return new float[][]{buff};
        }
    }

    @Override
    public String[] getNames() {
        return new String[]{RbInstance.ATTRIBUTE_NAME_VERTEX_COORDINATES, RbInstance.ATTRIBUTE_NAME_TEXTURE_COORDINATES};
    }

    @Override
    public int[] getBufferIndices() {
        return new int[]{0, 0};
    }

    @Override
    public int[] getOffsets() {
        return new int[]{0, 3*4};
    }

    @Override
    public int[] getStrides() {
        return new int[]{5*4, 5*4};
    }

    @Override
    public int[] getSizes() {
        return new int[]{3, 2};
    }

    @Override
    public short[] getIndices() {
        if(isIndexed){
            short[] buff = new short[xSections*ySections*6];
            int index = 0;
            for(int y=0; y<ySections; y++){
                for(int x=0; x<xSections; x++){
                    buff[index + 0] = (short)(y*(xSections+1) + x);
                    buff[index + 1] = (short)((y+1)*(xSections+1) + x);
                    buff[index + 2] = (short)((y+1)*(xSections+1) + (x+1));

                    buff[index + 3] = (short)(y*(xSections+1) + x);
                    buff[index + 4] = (short)((y+1)*(xSections+1) + (x+1));
                    buff[index + 5] = (short)(y*(xSections+1) + (x+1));

                    index += 6;
                }
            }
            return buff;

        }else{
            return null;
        }
    }

    @Override
    public int getPrimitiveMode() {
        return GLES30.GL_TRIANGLES;
    }
}
