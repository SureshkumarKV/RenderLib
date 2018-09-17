package in.sureshkumarkv.renderlib;

import android.opengl.GLES30;
import android.opengl.GLU;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by SureshkumarKV on 08/10/2017.
 */

class GLUtil {
    private static final String LOG_TAG = "GLUtil";

    public static void printError(){
        boolean errorsPresent = false;
        for(int code = GLES30.glGetError(); code != 0 ; code = GLES30.glGetError()){
            errorsPresent = true;
            Log.e(LOG_TAG, GLU.gluErrorString(code));
        }

        if(!errorsPresent){
            Log.e(LOG_TAG, "No errors");
        }
    }

    public static void uploadOrUnloadAll(in.sureshkumarkv.renderlib.RbWorld world, boolean isUpload) {
        if(world != null){
            ArrayList<in.sureshkumarkv.renderlib.RbNode> nodes = new ArrayList<>();

            for(in.sureshkumarkv.renderlib.RbScene scene: world.getScenes()){
                if(scene.getRootNode() != null){
                    nodes.add(scene.getRootNode());
                }
            }

            while(!nodes.isEmpty()){
                in.sureshkumarkv.renderlib.RbNode node = nodes.remove(nodes.size()-1);

                for(in.sureshkumarkv.renderlib.RbNode child: node.getChildren()){
                    nodes.add(child);
                }

                for(in.sureshkumarkv.renderlib.RbMesh mesh: node.getMeshes()){
                    if(mesh.getMaterial() != null){
                        if(mesh.getMaterial().getShader() != null){
                            if(isUpload){
                                mesh.getMaterial().getShader().upload();
                            }else{
                                mesh.getMaterial().getShader().unload();
                            }
                        }

                        if(mesh.getMaterial().getTextures() != null){
                            for(RbTexture texture: mesh.getMaterial().getTextures()){
                                if(isUpload){
                                    texture.upload();
                                }else{
                                    texture.unload();
                                }
                            }
                        }
                    }

                    for(in.sureshkumarkv.renderlib.RbGeometry geometry: mesh.getGeometries()){
                        if(isUpload){
                            geometry.upload();
                        }else{
                            geometry.unload();
                        }
                    }
                }
            }
        }
    }

    static boolean compileShader(String code, int shaderHandle){
        if(!GLES30.glIsShader(shaderHandle)){
            Log.e(LOG_TAG, "Invalid shader name");
            return false;
        }

        GLES30.glShaderSource(shaderHandle, code);
        GLES30.glCompileShader(shaderHandle);
        final int[] compileStatus = new int[1];
        GLES30.glGetShaderiv(shaderHandle, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0){
            Log.e(LOG_TAG, GLES30.glGetShaderInfoLog(shaderHandle));
            GLES30.glDeleteShader(shaderHandle);
            throw new RuntimeException("Error creating shader.");
        }

        return true;
    }

    static void createShaderProgram(int vertexShader, int fragmentShader, int programHandle){
        GLES30.glAttachShader(programHandle, vertexShader);
        GLES30.glAttachShader(programHandle, fragmentShader);

        GLES30.glLinkProgram(programHandle);

        final int[] linkStatus = new int[1];
        GLES30.glGetProgramiv(programHandle, GLES30.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0){
            Log.e(LOG_TAG, GLES30.glGetProgramInfoLog(programHandle));
            GLES30.glDeleteProgram(programHandle);
            throw new RuntimeException("Error creating shader program.");
        }
    }
}
