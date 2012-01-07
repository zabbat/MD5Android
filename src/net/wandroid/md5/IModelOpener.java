package net.wandroid.md5;

import java.io.IOException;
import java.io.InputStream;

public interface IModelOpener {

    InputStream open(String filePath) throws IOException;
    
}
