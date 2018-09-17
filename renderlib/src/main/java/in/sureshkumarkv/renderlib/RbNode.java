package in.sureshkumarkv.renderlib;

import in.sureshkumarkv.renderlib.types.RbMatrix;

/**
 * Created by SureshkumarKV on 07/10/2017.
 */

public class RbNode {
    private RbMatrix mTransform;
    private RbMatrix mTempTransform;

    private RbNode[] mChildren;
    private in.sureshkumarkv.renderlib.RbMesh[] mMeshes;

    public RbNode() {
        mTransform = new RbMatrix();
        mTempTransform = new RbMatrix();

        mChildren = new RbNode[0];
        mMeshes = new in.sureshkumarkv.renderlib.RbMesh[0];
    }

    public RbMatrix getTransform(){
        return mTransform;
    }

    public void addMesh(in.sureshkumarkv.renderlib.RbMesh mesh) {
        in.sureshkumarkv.renderlib.RbMesh[] temp = new in.sureshkumarkv.renderlib.RbMesh[mMeshes.length + 1];
        for (int i = 0; i < mMeshes.length; i++) {
            temp[i] = mMeshes[i];
        }
        temp[mMeshes.length] = mesh;
        mMeshes = temp;
    }

    public void removeMesh(in.sureshkumarkv.renderlib.RbMesh mesh) {
        in.sureshkumarkv.renderlib.RbMesh[] temp = new in.sureshkumarkv.renderlib.RbMesh[mMeshes.length - 1];
        for (int i = 0, index = 0; i < mMeshes.length; i++) {
            if (mMeshes[i] != mesh) {
                temp[index++] = mMeshes[i];
            }
        }
        mMeshes = temp;
    }

    public in.sureshkumarkv.renderlib.RbMesh[] getMeshes() {
        return mMeshes;
    }

    public void addChild(RbNode node) {
        RbNode[] temp = new RbNode[mChildren.length + 1];
        for (int i = 0; i < mChildren.length; i++) {
            temp[i] = mChildren[i];
        }
        temp[mChildren.length] = node;
        mChildren = temp;
    }

    public void removeChild(RbNode node) {
        RbNode[] temp = new RbNode[mChildren.length - 1];
        for (int i = 0, index = 0; i < mChildren.length; i++) {
            if (mChildren[i] != node) {
                temp[index++] = mChildren[i];
            }
        }
        mChildren = temp;
    }

    public RbNode[] getChildren() {
        return mChildren;
    }

    void setup(boolean isRecursive, int width, int height, in.sureshkumarkv.renderlib.RbInstance instance, in.sureshkumarkv.renderlib.RbWorld world, in.sureshkumarkv.renderlib.RbScene scene, in.sureshkumarkv.renderlib.RbListener listener, RbMatrix parentTransform) {
        if(parentTransform != null){
            mTempTransform.setMultiply(parentTransform, mTransform);
        }else{
            mTempTransform.setMatrix(mTransform);
        }

        if (listener != null) {
            listener.onSetupNode(isRecursive, width, height, instance, world, scene, this, mTempTransform);
        }

        for (in.sureshkumarkv.renderlib.RbMesh mesh : mMeshes) {
            mesh.setup(isRecursive, width, height, instance, world, scene, this, mTempTransform, listener);
        }

        if(isRecursive){
            for (RbNode child : mChildren) {
                child.setup(isRecursive, width, height, instance, world, scene, listener, mTempTransform);
            }
        }
    }

    void render(int width, int height, in.sureshkumarkv.renderlib.RbInstance instance, in.sureshkumarkv.renderlib.RbWorld world, in.sureshkumarkv.renderlib.RbScene scene, in.sureshkumarkv.renderlib.RbListener listener, RbMatrix parentTransform, long deltaTimeNanos) {
        if(parentTransform != null){
            mTempTransform.setMultiply(parentTransform, mTransform);
        }else{
            mTempTransform.setMatrix(mTransform);
        }

        if (listener != null) {
            listener.onRenderNode(width, height, instance, world, scene, this, mTempTransform, deltaTimeNanos);
        }

        for (in.sureshkumarkv.renderlib.RbMesh mesh : mMeshes) {
            mesh.render(width, height, instance, world, scene, this, mTempTransform, listener, deltaTimeNanos);
        }

        for (RbNode child : mChildren) {
            child.render(width, height, instance, world, scene, listener, mTempTransform, deltaTimeNanos);
        }
    }
}
