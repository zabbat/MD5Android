package net.wandroid.md5;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SdCardOpener implements IModelOpener{

    private String externalStoragePath;
    
    public SdCardOpener(String sdcardPath) {
        externalStoragePath=sdcardPath;
    }

    @Override
    public InputStream open(String filePath) throws IOException {
      File file=new File(externalStoragePath+filePath);
      FileInputStream ins=new FileInputStream(file);
      return ins;
    }

}
