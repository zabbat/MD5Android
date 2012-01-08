package net.wandroid.md5.model;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import static android.opengl.GLES20.*;

import net.wandroid.md5.gles20lib.Gles20Lib;
import net.wandroid.md5.ioutils.IModelFileOpener;
import net.wandroid.md5.model.math.Vec3;


/**
 * Class representing a mesh
 * @author Jungbeck 
 *
 */
public class Mesh {

    private static final int TRIANGLE_SIZE = 3;
    private static final int VERTEX_SIZE = 3;
    private static final int TEX_COORD_SIZE = 2;
    protected int numverts; // number of vertices in the mesh
    protected int numtris; // number of triangles in the mesh
    protected int numweights; // number of weights in the mesh

    protected Vertex[] vertex; // vertices of the mesh
    protected Triangle[] tri; // triangles in the mesh
    protected Weight[] weights; // weights of the mesh
    //TODO: why not Vertex object?
    private float[] vertexData; // currentframes vertexdata
    
    private FloatBuffer vertexBuffer; // vertex buffer
    private ShortBuffer indexBuffer; // index buffer
    protected Texture texture; // texture of this mesh
    protected String folderPath; // folder to load texture from
    private IModelFileOpener modelOpener; // type of opener

    /**
     * fills the index buffer with the mesh index data 
     */
    private void initIndexBuffer() {
        short indexData[] = new short[tri.length * TRIANGLE_SIZE];
        for (int t = 0; t < tri.length; t++) {// every triangle
            for (int i = 0; i < TRIANGLE_SIZE; i++) {// for every index in the the triangle
                indexData[t * TRIANGLE_SIZE + i] = (short) tri[t].vertIndex[i];
            }
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(indexData.length * Short.SIZE
                / 8);
        bb.order(ByteOrder.nativeOrder());
        indexBuffer = bb.asShortBuffer().put(indexData);
        indexBuffer.position(0);
        
        //TODO: can triangle reference be released now? 
    }
    
    /**
     * This function calculates the positions of the vertices, depending 
     * on how the joints have been moving. 
     * @param joints joints to be used for the calculations
     */
    private void computeVertexPosition(Joint joints[]) {

        if (vertexData == null) {// if called for the first time, initiate
            vertexData = new float[numverts * VERTEX_SIZE];
        }
        for (int jj = 0; jj < vertex.length; jj++) {// for every vertex
            Vertex v = vertex[jj];
            Vec3 pos = new Vec3(0, 0, 0);

            //calculate position for the vertex
            for (int ii = v.startWeight; ii < v.countWeight + v.startWeight; ii++) { // for every weight the vertex depends on
                Vec3 temp = new Vec3(0, 0, 0);
                Weight w = weights[ii]; // a weight that affects this vertex
                Joint j = joints[w.joint]; // the joint of the weight
                Vec3 wv = j.q.rotate(w.pos); // rotate the weight depending on the joints position
                
                temp = temp.add(wv); //add the weights position after rotated to the vertex position
                temp = j.position.add(temp);// add the joints position
                temp = temp.scale(w.bias); // scale it, depending on how much this vertex depends on this weight
                pos = pos.add(temp); // add all the results to the vertex

            }
            
            vertexData[jj * VERTEX_SIZE + 0] = pos.getX();
            vertexData[jj * VERTEX_SIZE + 1] = pos.getY();
            vertexData[jj * VERTEX_SIZE + 2] = pos.getZ();
        }
        
    }

    /**
     * fills the vertex buffer with the mesh vertex data
     * @param j joints the mesh depends on
     */
    public void initVertexData(Joint j[]) {

        computeVertexPosition(j);// calculate the correct position for the vertices

        ByteBuffer bb = ByteBuffer.allocateDirect(vertexData.length
                * Float.SIZE / 8);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer().put(vertexData);
        vertexBuffer.position(0);

        initIndexBuffer();// init the indices

    }

    /**
     * Loads texture from file. Must be called from a function in a Render -class, since it
     * must use a valid GLES20 reference 
     */
    public void loadTexture() {
        /*
         * Some devices GPU does not support non power of 2 textures ( dimensions must be 1x1,2x2,4x4 and so on) 
         * however, using BitmapFactory, android will resize the texture so that it will look like having
         * the same width and height as the file states. This can be different from the actual size,
         * since the screens pixels might be bigger/smaller in x direction than in the y direction, causing the 
         * returned bitmap being non power of 2. The options must state that inScaled is false.
         *  
         */
        Options opt = new Options(); 
        opt.inScaled = false;
        
        Bitmap bmp;
        try {
            bmp = BitmapFactory.decodeStream(modelOpener.open(folderPath), null, opt);
        } catch (IOException e) {
            throw new ModelParseException("could not find texture " + folderPath);
        }
        texture = new Texture(bmp);
        if (texture != null) {
            float texCoords[] = generateTexCoords();
            texture.setTexCoords(texCoords);
            texture.init();
        }
    }

    /**
     * generates texture coords for the mesh
     * @return all coordinates in a float[] object
     */
    private float[] generateTexCoords() {
        float f[] = new float[numverts * TEX_COORD_SIZE];
        for (int i = 0; i < numverts; i++) {
            f[i * TEX_COORD_SIZE] = vertex[i].s;
            f[i * TEX_COORD_SIZE + 1] = vertex[i].t;
        }
        return f;
    }

    /**
     * Draws the mesh using a shader
     * @param program the shader program to be used
     */
    public void draw( int program) {
        if (texture != null) {
            glEnable(GL_TEXTURE_2D);
            glActiveTexture(GL_TEXTURE0);
            texture.bind();
            int u_texture=Gles20Lib.location("u_texture", program);
            glUniform1f(u_texture, 0);
            int a_texCoord=Gles20Lib.location("a_texCoord", program);
            glVertexAttribPointer(a_texCoord, TEX_COORD_SIZE, GL_FLOAT, false, 0, texture.getTexCoordPointer());
            glEnableVertexAttribArray(a_texCoord);        
        }

        int a_position = Gles20Lib.location("a_position", program);
        glVertexAttribPointer(a_position, VERTEX_SIZE, GL_FLOAT, false, 0, vertexBuffer);
        glEnableVertexAttribArray(a_position);
        glDrawElements(GL_TRIANGLES, indexBuffer.capacity(), GL_UNSIGNED_SHORT,
                indexBuffer);
    }

    /**
     * fixes the texture path. Some 3d Programs uses absolute path to the textures, and this must be changed to the relative path
     * @param path path to the folder that contains the textures
     * @param modelOpener type of opening
     */
    public void setTexturePath(String path,IModelFileOpener modelOpener) {
        this.modelOpener=modelOpener;
        int end = folderPath.lastIndexOf("\\");//find the last folder
        String pngName = folderPath.substring(end + 1);// remove path
        pngName = path + pngName.substring(0, pngName.lastIndexOf("."))
                + ".png";// make sure file format is supported by android.
        folderPath = pngName;

    }
}
