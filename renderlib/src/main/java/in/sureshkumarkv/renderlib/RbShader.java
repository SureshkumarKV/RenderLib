package in.sureshkumarkv.renderlib;

import android.content.Context;
import android.opengl.GLES30;

import in.sureshkumarkv.renderlib.exception.RbInvalidResourceException;

import java.io.InputStream;
import java.util.HashMap;

import static android.content.res.AssetManager.ACCESS_BUFFER;

/**
 * Created by SureshkumarKV on 07/10/2017.
 */

public class RbShader {
    private RbInstance mInstance;

    private String mVertexShaderCode;
    private String mFragmentShaderCode;

    private int[] mGLName;
    private HashMap<String, Attribute> mAttributesMap;
    private HashMap<String, Uniform> mUniformsMap;

    private RbShader.Uniform mModelMatrixUniform;
    private RbShader.Uniform mViewMatrixUniform;
    private RbShader.Uniform mProjectionMatrixUniform;
    private RbShader.Uniform mTexture2DSamplers;

    public RbShader(String vertexShaderCode, String fragmentShaderCode) {
        mVertexShaderCode = vertexShaderCode;
        mFragmentShaderCode = fragmentShaderCode;

        mGLName = new int[3];

        mAttributesMap = new HashMap<>();
        mUniformsMap = new HashMap<>();
    }

    public RbShader(RbInstance instance, InputStream vertexShaderCode, InputStream fragmentShaderCode) {
        mInstance = instance;

        byte[] data;
        try{
            data = new byte[vertexShaderCode.available()];
            vertexShaderCode.read(data);
            mVertexShaderCode = new String(data);
            vertexShaderCode.close();

            data = new byte[fragmentShaderCode.available()];
            fragmentShaderCode.read(data);
            mFragmentShaderCode = new String(data);
            fragmentShaderCode.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(vertexShaderCode != null){
                try{
                    vertexShaderCode.close();
                }catch (Exception e1){

                }
            }
            if(fragmentShaderCode != null){
                try{
                    fragmentShaderCode.close();
                }catch (Exception e1){

                }
            }
        }

        mGLName = new int[3];

        mAttributesMap = new HashMap<>();
        mUniformsMap = new HashMap<>();
    }

    public String getVertexShaderCode() {
        return mVertexShaderCode;
    }

    public String getFragmentShaderCode() {
        return mFragmentShaderCode;
    }

    public Attribute getAttribute(String name){
        return mAttributesMap.get(name);
    }

    public Uniform getUniform(String name){
        return mUniformsMap.get(name);
    }

    void select(float[] modelMatrix, RbCamera camera){
        mInstance.setShader(this);

        if(mModelMatrixUniform != null){
            mModelMatrixUniform.set(modelMatrix);
        }
        if(mViewMatrixUniform != null){
            mViewMatrixUniform.set(camera.getViewTransform().getData());
        }
        if(mProjectionMatrixUniform != null){
            mProjectionMatrixUniform.set(camera.getProjectionTransform().getData());
        }
        if(mTexture2DSamplers != null){
            mTexture2DSamplers.setArray(new int[]{0, 1, 2, 3}, 1, 4);
        }
    }

    void upload() {
        if (mGLName[2] != 0) {
            GLES30.glDeleteShader(mGLName[0]);
            GLES30.glDeleteShader(mGLName[1]);
            GLES30.glDeleteProgram(mGLName[2]);
        }
        mGLName[0] = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
        mGLName[1] = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);
        mGLName[2] = GLES30.glCreateProgram();

        HashMap<String, Attribute> tempAttributesMap = new HashMap<>(mAttributesMap);
        HashMap<String, Uniform> tempUniformsMap = new HashMap<>(mUniformsMap);
        mAttributesMap.clear();
        mUniformsMap.clear();

        if (GLUtil.compileShader(getVertexShaderCode(), mGLName[0])) {
            if (GLUtil.compileShader(getFragmentShaderCode(), mGLName[1])) {
                GLUtil.createShaderProgram(mGLName[0], mGLName[1], mGLName[2]);
            }
        }

        GLES30.glUseProgram(mGLName[2]);

        int[] count_length_size_type = new int[4];
        byte[] nameBuffer = new byte[512];
        GLES30.glGetProgramiv(mGLName[2], GLES30.GL_ACTIVE_ATTRIBUTES, count_length_size_type, 0);
        for (int i = 0; i < count_length_size_type[0]; ++i) {
            GLES30.glGetActiveAttrib(mGLName[2], i, nameBuffer.length, count_length_size_type, 1, count_length_size_type, 2, count_length_size_type, 3, nameBuffer, 0);
            String name = new String(nameBuffer, 0, count_length_size_type[1]);
            int location = GLES30.glGetAttribLocation(mGLName[2], name);

            Attribute attribute = tempAttributesMap.get(name);
            if(attribute == null){
                attribute = new Attribute(name, location);
            }else{
                attribute.mName = name;
                attribute.mLocation = location;
                tempAttributesMap.remove(name);
            }
            mAttributesMap.put(name, attribute);
        }

        GLES30.glGetProgramiv(mGLName[2], GLES30.GL_ACTIVE_UNIFORMS, count_length_size_type, 0);
        for (int i = 0; i < count_length_size_type[0]; ++i) {
            GLES30.glGetActiveUniform(mGLName[2], i, nameBuffer.length, count_length_size_type, 1, count_length_size_type, 2, count_length_size_type, 3, nameBuffer, 0);
            String name = new String(nameBuffer, 0, count_length_size_type[1]);
            int location = GLES30.glGetUniformLocation(mGLName[2], name);

            Uniform uniform = tempUniformsMap.get(name);
            if(uniform == null){
                uniform = new Uniform(name, location);
            }else{
                uniform.mName = name;
                uniform.mLocation = location;
                tempUniformsMap.remove(name);
            }
            mUniformsMap.put(name, uniform);
        }

        for(Attribute attribute: tempAttributesMap.values()){
            attribute.mIsValid = false;
        }
        for(Uniform uniform: tempUniformsMap.values()){
            uniform.mIsValid = false;
        }

        mModelMatrixUniform = getUniform(RbInstance.UNIFORM_NAME_MODEL_MATRIX);
        mViewMatrixUniform = getUniform(RbInstance.UNIFORM_NAME_VIEW_MATRIX);
        mProjectionMatrixUniform = getUniform(RbInstance.UNIFORM_NAME_PROJECTION_MATRIX);
        mTexture2DSamplers = getUniform(RbInstance.UNIFORM_NAME_TEXTURE2D_SAMPLERS);
    }

    void unload() {
        if(mGLName[2] != 0){
            GLES30.glDeleteShader(mGLName[0]);
            GLES30.glDeleteShader(mGLName[1]);
            GLES30.glDeleteProgram(mGLName[2]);
            mGLName[0] = mGLName[1] = mGLName[2] = 0;

            for(Attribute attribute: mAttributesMap.values()){
                attribute.mIsValid = false;
            }
            for(Uniform uniform: mUniformsMap.values()){
                uniform.mIsValid = false;
            }

            mAttributesMap.clear();
            mUniformsMap.clear();
        }
    }

    int getValue(){
        return mGLName[2];
    }

    public static class Attribute{
        private String mName;
        private int mLocation;
        private boolean mIsValid;

        Attribute(String name, int location){
            mName = name;
            mLocation = location;
            mIsValid = true;
        }

        public String getName(){
            if(!mIsValid){
                throw new RbInvalidResourceException();
            }

            return mName;
        }

        void link(RbGeometry.AttributeBuffer attributeBuffer){
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, attributeBuffer.getValue());
            GLES30.glEnableVertexAttribArray(mLocation);
            GLES30.glVertexAttribPointer(mLocation, attributeBuffer.getSize(), GLES30.GL_FLOAT, false, attributeBuffer.getStride(), attributeBuffer.getOffset());
        }

        int getLocation(){
            if(!mIsValid){
                throw new RbInvalidResourceException();
            }

            return mLocation;
        }
    }

    public static class Uniform{
        private String mName;
        private int mLocation;
        private boolean mIsValid;

        Uniform(String name, int location){
            mName = name;
            mLocation = location;
            mIsValid = true;
        }

        public String getName(){
            if(!mIsValid){
                throw new RbInvalidResourceException();
            }

            return mName;
        }

        int getLocation(){
            if(!mIsValid){
                throw new RbInvalidResourceException();
            }

            return mLocation;
        }

//        public void set(CrTexture texture) {
//        if(!mIsValid){
//            throw new InvalidResourceException();
//        }
//            GLES30.glUniform1i(mLocation, texture.getSamplerIndex());
//            texture.select();
//        }

        public void set(int x) {
            if(!mIsValid){
                throw new RbInvalidResourceException();
            }

            GLES30.glUniform1i(mLocation, x);
        }

        public void set(float x) {
            if(!mIsValid){
                throw new RbInvalidResourceException();
            }

            GLES30.glUniform1f(mLocation, x);
        }

        public void set(float x, float y) {
            if(!mIsValid){
                throw new RbInvalidResourceException();
            }

            GLES30.glUniform2f(mLocation, x, y);
        }

        public void set(float x, float y, float z) {
            if(!mIsValid){
                throw new RbInvalidResourceException();
            }

            GLES30.glUniform3f(mLocation, x, y, z);
        }

        public void set(float x, float y, float z, float w) {
            if(!mIsValid){
                throw new RbInvalidResourceException();
            }

            GLES30.glUniform4f(mLocation, x, y, z, w);
        }

        public void set(float[] data) {
            if(!mIsValid){
                throw new RbInvalidResourceException();
            }

            switch (data.length) {
                case 4:
                    GLES30.glUniform4fv(mLocation, 1, data, 0);
                    break;

                default:
                    GLES30.glUniformMatrix4fv(mLocation, data.length/16, false, data, 0);
            }
        }

        public void setArray(float[] data, int size, int count){
            if(!mIsValid){
                throw new RbInvalidResourceException();
            }

            switch (size) {
                case 1:
                    GLES30.glUniform1fv(mLocation, count, data, 0);
                    break;

                default:
                    break;
            }
        }

        public void setArray(int[] data, int size, int count){
            if(!mIsValid){
                throw new RbInvalidResourceException();
            }

            switch (size) {
                case 1:
                    GLES30.glUniform1iv(mLocation, count, data, 0);
                    break;

                default:
                    break;
            }
        }
    }
}
