package net.wandroid.md5.ioutils;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;

/**
 * This class opens files stored in the asset folder.
 * @author Jungbeck
 *
 */
public class AssetModelFileOpener implements IModelFileOpener{
    private AssetManager  mAssetManager;// asset manager object for the application
    
    /**
     * Constructor for open models in the asset folder
     * @param assetManager an AssetManager object to handle the files in the asset folder
     */
    public AssetModelFileOpener(AssetManager assetManager) {
        mAssetManager=assetManager;
    }
    @Override
    public InputStream open(String filePath) throws IOException {
        return mAssetManager.open(filePath);
    }

}
