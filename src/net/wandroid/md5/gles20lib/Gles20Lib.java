package net.wandroid.md5.gles20lib;

import static android.opengl.GLES20.*;

/**
 * Static class that handles shader programs
 * @author Jungbeck
 *
 */
public class Gles20Lib {

    /**
     * compiles and link a shader program. Throws a Gles20Exception if  compilation
     * or linking fails
     * @param vertexShaderString the vertex program as a string
     * @param fragmentShaderString the shader program as a string
     * @return the shader program
     */
    public static int compileAndLinkProgram(String vertexShaderString,
            String fragmentShaderString) {
        
        
        int vertexShader=compileShader(GL_VERTEX_SHADER, vertexShaderString);
        int fragmentShader=compileShader(GL_FRAGMENT_SHADER, fragmentShaderString);

        int program= glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);

        int[] tmp=new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, tmp, 0);
        if(tmp[0]==0){
            String errorInfo=glGetShaderInfoLog(program);
            throw new Gles20Exception(errorInfo);
        }
        
        return program; 
    }

    /**
     * Compiles shader 
     * @param shaderType type of shader, should be either GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
     * @param shaderString the shader as a string
     * @return the shader
     */
    private static int compileShader(int shaderType,String shaderString){
        int shader=glCreateShader(shaderType);
        glShaderSource(shader, shaderString);
        glCompileShader(shader);
        int[] tmp=new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, tmp, 0);
        if(tmp[0]==0){
            String errorInfo=glGetShaderInfoLog(shader);
            throw new Gles20Exception(errorInfo);
        }
        return shader;
    }

    /**
     * finds the location in a shader program and returns it
     * @param locationName name of the location to be found. Attributes must start with 'a_' and uniforms with 'u_'
     * @param shaderProgram the program where the location should be found
     * @return the location
     */
    public static int location(String locationName, int shaderProgram) {
        int location=0;
        if(locationName.startsWith("a_")){
            location=glGetAttribLocation(shaderProgram, locationName);
        }else if(locationName.startsWith("u_")){
            location=glGetUniformLocation(shaderProgram, locationName);
        }else{
            throw new Gles20Exception("could not find location:"+locationName+", must start with 'u_' or a_");
        }
        return location;
    }
    
}
