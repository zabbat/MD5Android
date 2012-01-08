package net.wandroid.md5.gles20lib;

/**
 * Exception thrown when gles 2.0 fails.
 * Since rendering is often done in the Render interface, this 
 * exception must be unchecked.
 * @author Jungbeck
 *
 */
public class Gles20Exception extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public Gles20Exception(String msg) {
        super(msg);
    }
    
}
