package net.wandroid.md5.ioutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class opens files stored on the SD card.
 * @author Jungbeck
 *
 */
public class SdCardModelFileOpener implements IModelFileOpener{

    private String mExternalStoragePath;//The path to the sdcard. This is often just "/sdcard", but is device dependent
    
    /**
     * Constructor for open models on the sdcard
     * @param sdcardPath path to the sdcard
     */
    public SdCardModelFileOpener(String sdcardPath) {
        mExternalStoragePath=sdcardPath;
    }

    @Override
    public InputStream open(String filePath) throws IOException {
      File file=new File(mExternalStoragePath+filePath);
      FileInputStream ins=new FileInputStream(file);
      return ins;
    }

}
