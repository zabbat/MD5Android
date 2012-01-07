package net.wandroid.md5;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;

public class AssetModelOpener implements IModelOpener{
    private AssetManager  mAssetManager;
    public AssetModelOpener(AssetManager assetManager) {
        mAssetManager=assetManager;
    }
    @Override
    public InputStream open(String filePath) throws IOException {
        return mAssetManager.open(filePath);
    }

}
