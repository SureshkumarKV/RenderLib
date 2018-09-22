package in.sureshkumarkv.renderlib;

import in.sureshkumarkv.renderlib.types.RbMatrix;

/**
 * Created by SureshkumarKV on 07/10/2017.
 */

public abstract class RbListener {
    public void onCreateWorld(RbWorld world){}

    public void onSetupWorld(boolean isRecursive, int width, int height, RbInstance instance, RbWorld world){}
    public void onSetupScene(boolean isRecursive, int width, int height, RbInstance instance, RbWorld world, RbScene scene){}
    public void onSetupNode(boolean isRecursive, int width, int height, RbInstance instance, RbWorld world, RbScene scene, RbNode node, RbMatrix transform){}
    public void onSetupMesh(boolean isRecursive, int width, int height, RbInstance instance, RbWorld world, RbScene scene, RbNode node, RbMatrix transform, RbMesh mesh){}
    public void onSetupGeometry(boolean isRecursive, int width, int height, RbInstance instance, RbWorld world, RbScene scene, RbNode node, RbMatrix transform, RbMesh mesh, RbGeometry geometry, RbMaterial material){}

    public void onRenderWorld(int width, int height, RbInstance instance, RbWorld world, long deltaTimeNanos){}
    public void onRenderScene(int width, int height, RbInstance instance, RbWorld world, RbScene scene, long deltaTimeNanos){}
    public void onRenderNode(int width, int height, RbInstance instance, RbWorld world, RbScene scene, RbNode node, RbMatrix transform, long deltaTimeNanos){}
    public void onRenderMesh(int width, int height, RbInstance instance, RbWorld world, RbScene scene, RbNode node, RbMatrix transform, RbMesh mesh, long deltaTimeNanos){}
    public void onRenderGeometry(int width, int height, RbInstance instance, RbWorld world, RbScene scene, RbNode node, RbMatrix transform, RbMesh mesh, RbGeometry geometry, RbMaterial material, long deltaTimeNanos){}
}
