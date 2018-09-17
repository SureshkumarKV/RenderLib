package in.sureshkumarkv.renderlib;

import android.opengl.GLES30;

import in.sureshkumarkv.renderlib.exception.RbInvalidResourceException;
import in.sureshkumarkv.renderlib.types.RbMatrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by SureshkumarKV on 07/10/2017.
 */

public abstract class RbGeometry {
    private int primitiveMode;
    private int vertCount;
    private int indexCount;

    private int[] mGLBufferNames;
    private int[] mGLVAOName;
    private HashMap<String, AttributeBuffer> mAttributeBuffersMap;

    public abstract float[][] getBuffers();
    public abstract String[] getNames();
    public abstract int[] getBufferIndices();//The buffer which offset, stride and size applies to.
    public abstract int[] getOffsets();
    public abstract int[] getStrides();
    public abstract int[] getSizes();
    public abstract short[] getIndices();

    public abstract int getPrimitiveMode();

    public RbGeometry() {
        mAttributeBuffersMap = new HashMap<>(8);
        mGLBufferNames = new int[0];
        mGLVAOName = new int[1];
    }

    public AttributeBuffer getAttributeBuffer(String name) {
        return mAttributeBuffersMap.get(name);
    }

    void upload() {
        float[][] vertData = getBuffers();
        String[] names = getNames();
        int[] bufferIndices = getBufferIndices();
        int[] offsets = getOffsets();
        int[] strides = getStrides();
        int[] sizes = getSizes();
        short[] vertIndices = getIndices();
        primitiveMode = getPrimitiveMode();

        boolean isIndexed = (vertIndices != null);
        vertCount = isIndexed ? vertIndices.length : vertData[0].length / sizes[0];
        indexCount = isIndexed ? vertIndices.length : 0;

        int bufferCount = vertData.length + (isIndexed ? 1 : 0);
        if (mGLBufferNames != null && mGLBufferNames.length > 0) {
            GLES30.glDeleteBuffers(mGLBufferNames.length, mGLBufferNames, 0);
        }
        mGLBufferNames = new int[bufferCount];
        GLES30.glGenBuffers(mGLBufferNames.length, mGLBufferNames, 0);

        if (indexCount > 0) {
            ByteBuffer bb1 = ByteBuffer.allocateDirect(vertIndices.length * 2);
            bb1.order(ByteOrder.nativeOrder());
            ShortBuffer INDEX_BUFFER = bb1.asShortBuffer();
            INDEX_BUFFER.put(vertIndices);
            INDEX_BUFFER.position(0);

            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mGLBufferNames[0]);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, INDEX_BUFFER.capacity() * 2, INDEX_BUFFER, GLES30.GL_STATIC_DRAW);
        }

        for (int i = (indexCount > 0 ? 1 : 0), vertBuffIndex = 0; i < bufferCount; i++, vertBuffIndex++) {
            ByteBuffer bb1 = ByteBuffer.allocateDirect(vertData[vertBuffIndex].length * 4);
            bb1.order(ByteOrder.nativeOrder());
            FloatBuffer VERTEX_BUFFER = bb1.asFloatBuffer();
            VERTEX_BUFFER.put(vertData[vertBuffIndex]);
            VERTEX_BUFFER.position(0);

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mGLBufferNames[i]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, VERTEX_BUFFER.capacity() * 4, VERTEX_BUFFER, GLES30.GL_STATIC_DRAW);
        }

        HashMap<String, AttributeBuffer> tempBuffersMap = new HashMap<>(mAttributeBuffersMap);
        mAttributeBuffersMap.clear();

        for (int i = 0; i < names.length; i++) {
            AttributeBuffer attributeBuffer = tempBuffersMap.get(names[i]);
            if (attributeBuffer == null) {
                attributeBuffer = new AttributeBuffer(names[i], offsets[i], strides[i], sizes[i], mGLBufferNames[(indexCount > 0 ? 1 : 0) + bufferIndices[i]]);
            } else {
                attributeBuffer.mName = names[i];
                attributeBuffer.mOffset = offsets[i];
                attributeBuffer.mStride = strides[i];
                attributeBuffer.mSize = sizes[i];
                attributeBuffer.mGLName = mGLBufferNames[(indexCount > 0 ? 1 : 0) + bufferIndices[i]];
                tempBuffersMap.remove(names[i]);
            }
            mAttributeBuffersMap.put(names[i], attributeBuffer);
        }

        for (AttributeBuffer attributeBuffer : tempBuffersMap.values()) {
            attributeBuffer.mIsValid = false;
        }
    }

    void unload() {
        GLES30.glDeleteBuffers(mGLBufferNames.length, mGLBufferNames, 0);
        GLES30.glDeleteBuffers(mGLVAOName.length, mGLVAOName, 0);
        Arrays.fill(mGLBufferNames, 0);
        mGLVAOName[0] = 0;

        vertCount = 0;
        indexCount = 0;

        for(AttributeBuffer attributeBuffer: mAttributeBuffersMap.values()){
            attributeBuffer.mIsValid = false;
        }
        mAttributeBuffersMap.clear();
    }

    public void setup(boolean isRecursive, int width, int height, RbInstance instance, RbWorld world, RbScene scene, RbNode node, RbMatrix transform, RbMesh mesh, RbMaterial material, RbListener listener) {
        if (listener != null) {
            listener.onSetupGeometry(isRecursive, width, height, instance, world, scene, node, transform, mesh, this, material);
        }

        if(mGLVAOName[0] != 0){
            GLES30.glDeleteVertexArrays(mGLVAOName.length, mGLVAOName, 0);
        }
        GLES30.glGenVertexArrays(mGLVAOName.length, mGLVAOName, 0);

        GLES30.glBindVertexArray(mGLVAOName[0]);
        for (AttributeBuffer attributeBuffer : mAttributeBuffersMap.values()) {
            if (material.getShader() != null) {
                RbShader.Attribute attribute = material.getShader().getAttribute(attributeBuffer.getName());
                if (attribute != null) {
                    attribute.link(attributeBuffer);
                }
            }
        }
        if(indexCount > 0){
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mGLBufferNames[0]);
        }
        GLES30.glBindVertexArray(0);
    }

    public void render(int width, int height, RbInstance instance, RbWorld world, RbScene scene, RbNode node, RbMatrix transform, RbMesh mesh, RbMaterial material, RbListener listener, long deltaTimeNanos) {
        if(material.getShader() != null){
            GLES30.glBindVertexArray(mGLVAOName[0]);

            material.getShader().select(transform.getData(), scene.getCamera());

            RbTexture[] textures = mesh.getMaterial().getTextures();
            if(textures != null){
                for(int i=0; i<textures.length; i++){
                    textures[i].select(i);
                }
            }

            if (listener != null) {
                listener.onRenderGeometry(width, height, instance, world, scene, node, transform, mesh, this, material, deltaTimeNanos);
            }

            if(indexCount > 0){
                GLES30.glDrawElements(primitiveMode, indexCount, GLES30.GL_UNSIGNED_SHORT, 0);
            }else{
                GLES30.glDrawArrays(primitiveMode, 0, vertCount);
            }
            GLES30.glBindVertexArray(0);
        }
    }

    public static class AttributeBuffer {
        private String mName;
        private int mOffset;
        private int mStride;
        private int mSize;
        private int mGLName;
        private boolean mIsValid;

        AttributeBuffer(String name, int offset, int stride, int size, int glName) {
            mName = name;
            mOffset = offset;
            mStride = stride;
            mSize = size;
            mGLName = glName;
            mIsValid = true;
        }

        public String getName() {
            if (!mIsValid) {
                throw new RbInvalidResourceException();
            }

            return mName;
        }

        public int getOffset() {
            if (!mIsValid) {
                throw new RbInvalidResourceException();
            }

            return mOffset;
        }

        public int getStride() {
            if (!mIsValid) {
                throw new RbInvalidResourceException();
            }

            return mStride;
        }

        public int getSize() {
            if (!mIsValid) {
                throw new RbInvalidResourceException();
            }

            return mSize;
        }

        int getValue() {
            if (!mIsValid) {
                throw new RbInvalidResourceException();
            }

            return mGLName;
        }
    }
}
