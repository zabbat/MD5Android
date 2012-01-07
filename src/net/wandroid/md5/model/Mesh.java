package net.wandroid.md5.model;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import static android.opengl.GLES20.*;
import android.os.Environment;

import net.wandroid.md5.gles20lib.Gles20Lib;
import net.wandroid.md5.ioutils.IModelFileOpener;
import net.wandroid.md5.model.math.Vec3;

public class Mesh {

    protected int numverts;
    protected int numtris;
    protected int numweights;

    protected Vertex[] vertex;
    protected Triangle[] tri;
    protected Weight[] weights;
    private float[] vertexData; // currentframes vertexdata
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    protected Texture texture;
    protected String folderPath;
    private IModelFileOpener modelOpener;

    private void initIndexBuffer() {
        short indexData[] = new short[tri.length * 3];
        for (int t = 0; t < tri.length; t++) {// every triangle
            for (int i = 0; i < 3; i++) {// for every index in the the triangle
                indexData[t * 3 + i] = (short) tri[t].vertIndex[i];
            }
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(indexData.length * Short.SIZE
                / 8);
        bb.order(ByteOrder.nativeOrder());
        indexBuffer = bb.asShortBuffer().put(indexData);
        indexBuffer.position(0);
    }

    private void computeVertexPosition(Joint joints[]) {

        if (vertexData == null) {
            vertexData = new float[numverts * 3];
        }
        for (int jj = 0; jj < vertex.length; jj++) {
            Vertex v = vertex[jj];
            Vec3 pos = new Vec3(0, 0, 0);

            for (int ii = v.startWeight; ii < v.countWeight + v.startWeight; ii++) {
                Vec3 temp = new Vec3(0, 0, 0);
                Weight w = weights[ii];
                Joint j = joints[w.joint];
                Vec3 wv = j.q.rotate(w.pos);// .scale(w.bias);
                // wv=wv.scale(w.bias);
                temp = temp.add(wv);
                temp = j.position.add(temp);
                temp = temp.scale(w.bias);
                pos = pos.add(temp);

            }
            // Log.v("vertex",""+pos);
            vertexData[jj * 3 + 0] = pos.getX();
            vertexData[jj * 3 + 1] = pos.getY();
            vertexData[jj * 3 + 2] = pos.getZ();
        }
        // Log.v("vertex","done");
    }

    public void initVertexData(Joint j[]) {

        computeVertexPosition(j);// init vertexData

        ByteBuffer bb = ByteBuffer.allocateDirect(vertexData.length
                * Float.SIZE / 8);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer().put(vertexData);
        vertexBuffer.position(0);

        initIndexBuffer();

    }

    public void loadTexture() {
        Options opt = new Options();
        opt.inScaled = false;

        //Bitmap bmp = BitmapFactory.decodeFile(folderPath, opt);
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

    private float[] generateTexCoords() {
        float f[] = new float[numverts * 2];
        for (int i = 0; i < numverts; i++) {
            f[i * 2] = vertex[i].s;
            f[i * 2 + 1] = vertex[i].t;
        }
        return f;
    }

    public void draw( int program) {
        // TODO: texture


        if (texture != null) {
            glEnable(GL_TEXTURE_2D);
            glActiveTexture(GL_TEXTURE0);
            texture.bind();
            int u_texture=Gles20Lib.location("u_texture", program);
            glUniform1f(u_texture, 0);
            int a_texCoord=Gles20Lib.location("a_texCoord", program);
            glVertexAttribPointer(a_texCoord, 2, GL_FLOAT, false, 0, texture.getTexCoordPointer());
            glEnableVertexAttribArray(a_texCoord);        
        }

        int a_position = Gles20Lib.location("a_position", program);
        glVertexAttribPointer(a_position, 3, GL_FLOAT, false, 0, vertexBuffer);
        glEnableVertexAttribArray(a_position);
        glDrawElements(GL_TRIANGLES, indexBuffer.capacity(), GL_UNSIGNED_SHORT,
                indexBuffer);
    }

    public void setTexturePath(String path,IModelFileOpener modelOpener) {
        this.modelOpener=modelOpener;
        int end = folderPath.lastIndexOf("\\");
        String pngName = folderPath.substring(end + 1);
        pngName = path + pngName.substring(0, pngName.lastIndexOf("."))
                + ".png";
        folderPath = pngName;

    }
}
