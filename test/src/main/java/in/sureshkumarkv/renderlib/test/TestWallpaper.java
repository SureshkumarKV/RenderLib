package in.sureshkumarkv.renderlib.test;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import in.sureshkumarkv.renderlib.AndroidUtil;
import in.sureshkumarkv.renderlib.RbCamera;
import in.sureshkumarkv.renderlib.RbGeometry;
import in.sureshkumarkv.renderlib.RbInstance;
import in.sureshkumarkv.renderlib.RbListener;
import in.sureshkumarkv.renderlib.RbMaterial;
import in.sureshkumarkv.renderlib.RbMesh;
import in.sureshkumarkv.renderlib.RbNode;
import in.sureshkumarkv.renderlib.RbScene;
import in.sureshkumarkv.renderlib.RbShader;
import in.sureshkumarkv.renderlib.RbTexture;
import in.sureshkumarkv.renderlib.RbWorld;
import in.sureshkumarkv.renderlib.geometry.RbRectangleGeometry;
import in.sureshkumarkv.renderlib.texture.RbBitmapTexture;
import in.sureshkumarkv.renderlib.types.RbMatrix;
import in.sureshkumarkv.renderlib.wallpaper.RbWallpaperService;

/**
 * Created by SureshkumarKV on 10/10/2017.
 */

public class TestWallpaper extends RbWallpaperService {
    public TestWallpaper(){
        super(TestEngine.class);
    }

    public static class TestEngine implements RbWallpaperEngine {
        RbInstance mInstance;

        @Override
        public void onCreate(GLSurfaceView glSurfaceView) {
            mInstance = new RbInstance();
            RbWorld world = new RbWorld();
            RbScene scene = new RbScene();
            final RbNode node = new RbNode();
            RbMesh mesh = new RbMesh();
            RbGeometry geometry = new RbRectangleGeometry(2, 2, 0, 0, 1, 1, true);
            RbMaterial material = new RbMaterial();
            final RbShader shader = new RbShader(mInstance, AndroidUtil.getAssetFile(glSurfaceView.getContext(), "shader/texture/phong.vs.glsl"), AndroidUtil.getAssetFile(glSurfaceView.getContext(), "shader/texture/phong.fs.glsl"));
            final RbTexture texture = RbBitmapTexture.fromAsset(glSurfaceView.getContext(), "texture/test.png");

            material.setShader(shader);
            material.addTexture(texture);
            mesh.setMaterial(material);
            mesh.addGeometry(geometry);
            node.addMesh(mesh);
            scene.setRootNode(node);
            world.addScene(scene);
            mInstance.setWorld(world);

            mInstance.setResolution(1);
            mInstance.setFPS(60);

            mInstance.setListener(new RbListener() {
                @Override
                public void onRenderGeometry(int width, int height, RbInstance instance, RbWorld world, RbScene scene, RbNode node, RbMatrix transform, RbMesh mesh, RbGeometry geometry, RbMaterial material, long deltaTimeNanos){

                }
            });

            mInstance.setView(glSurfaceView);
        }

        @Override
        public void onResize(int width, int height){

        }

        @Override
        public void onTouch(MotionEvent event) {

        }

        @Override
        public void onOffset(float xOffset, float yOffset) {

        }

        @Override
        public void onShown() {
            mInstance.setActive(true);
        }

        @Override
        public void onHidden() {
            mInstance.setActive(false);
        }

        @Override
        public void onDestroy(){

        }
    }
}
