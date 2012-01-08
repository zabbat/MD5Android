package net.wandroid.md5.ioutils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface between android and model files, handling opening of files
 * This to ensure less view code dependency (from Activity) in the model classes. 
 * @author Jungbeck
 *
 */
public interface IModelFileOpener {
    /**
     * Open a file and returns it as a InputStream
     * @param filePath relative file path
     * @return the InputStream of the file
     * @throws IOException
     */
    InputStream open(String filePath) throws IOException;
    
}
