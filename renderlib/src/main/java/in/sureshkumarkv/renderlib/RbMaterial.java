package in.sureshkumarkv.renderlib;

/**
 * Created by SureshkumarKV on 08/10/2017.
 */

public class RbMaterial {
    private RbShader mShader;
    private RbTexture[] mTextures = new RbTexture[0];

    public void setShader(RbShader shader) {
        mShader = shader;
    }

    public RbShader getShader(){
        return mShader;
    }

    public void addTexture(RbTexture Texture) {
        RbTexture[] temp = new RbTexture[mTextures.length + 1];
        for(int i=0; i<mTextures.length; i++){
            temp[i] = mTextures[i];
        }
        temp[mTextures.length] = Texture;
        mTextures = temp;
    }

    public void removeTexture(RbTexture Texture) {
        RbTexture[] temp = new RbTexture[mTextures.length - 1];
        for(int i=0, index = 0; i<mTextures.length; i++){
            if(mTextures[i] != Texture){
                temp[index++] = mTextures[i];
            }
        }
        mTextures = temp;
    }

    public RbTexture[] getTextures(){
        return mTextures;
    }
}
