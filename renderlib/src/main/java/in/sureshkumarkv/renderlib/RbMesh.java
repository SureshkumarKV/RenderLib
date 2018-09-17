package in.sureshkumarkv.renderlib;

import in.sureshkumarkv.renderlib.types.RbMatrix;

/**
 * Created by SureshkumarKV on 08/10/2017.
 */

public class RbMesh {
    private RbMaterial mMaterial;
    private RbGeometry[] mGeometries;

    public RbMesh(){
        mGeometries = new RbGeometry[0];
    }

    public void setMaterial(RbMaterial material) {
        mMaterial = material;
    }

    public RbMaterial getMaterial() {
        return mMaterial;
    }

    public void addGeometry(RbGeometry geometry) {
        RbGeometry[] temp = new RbGeometry[mGeometries.length + 1];
        for(int i=0; i<mGeometries.length; i++){
            temp[i] = mGeometries[i];
        }
        temp[mGeometries.length] = geometry;
        mGeometries = temp;
    }

    public void removeGeometry(RbGeometry geometry) {
        RbGeometry[] temp = new RbGeometry[mGeometries.length - 1];
        for(int i=0, index = 0; i<mGeometries.length; i++){
            if(mGeometries[i] != geometry){
                temp[index++] = mGeometries[i];
            }
        }
        mGeometries = temp;
    }

    public RbGeometry[] getGeometries(){
        return mGeometries;
    }

    public void setup(boolean isRecursive, int width, int height, RbInstance instance, RbWorld world, RbScene scene, RbNode node, RbMatrix transform, RbListener listener) {
        if(listener != null){
            listener.onSetupMesh(isRecursive, width, height, instance, world, scene, node, transform, this);
        }

        if(isRecursive){
            for(RbGeometry geometry: mGeometries){
                geometry.setup(isRecursive, width, height, instance, world, scene, node, transform, this, mMaterial, listener);
            }
        }
    }

    public void render(int width, int height, RbInstance instance, RbWorld world, RbScene scene, RbNode node, RbMatrix transform, RbListener listener, long deltaTimeNanos) {
        if(listener != null){
            listener.onRenderMesh(width, height, instance, world, scene, node, transform, this, deltaTimeNanos);
        }

        for(RbGeometry geometry: mGeometries){
            geometry.render(width, height, instance, world, scene, node, transform, this, mMaterial, listener, deltaTimeNanos);
        }
    }
}
