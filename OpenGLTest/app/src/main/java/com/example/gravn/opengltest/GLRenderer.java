package com.example.gravn.opengltest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Gravn on 11/05/2016.
 */
public class GLRenderer
{
    private Context context;

    //DisplayMetrics and scale
    public float screenWidth = 1920;
    public float screenHeight = 1080;
    float screenScaleUniform = 1.0f;
    float defaultScreenWidth = 1920;
    float defaultScreenHeight = 1080;

    //ViewportMatrix
    float[] projectionMatrix = new float[16];
    float[] viewMatrix = new float[16];
    float[] projectionAndViewMatrix = new float[16];

    //TEMP
    int[] textureIDs = new int[2];
    int[] texturenames;
    int textures;

    //Shaders
    int vertexShader;
    int fragmentShader;

    int program;

    // Geometric variables
    public static float[] vertices;
    public static short[] indices;
    public static float[] uvs;
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer uvBuffer;
    //public Sprite sprite;
    public int[] resourceIDs;


    public GLRenderer(Context context,int[] resourceIDs)
    {
        this.context = context;
        this.resourceIDs = resourceIDs;
        this.texturenames = new int[resourceIDs.length];
    }

    //Sets up most GLES configurations
    public void CreateSurface()
    {
        SetupScaling();
        SetupVertices();
        SetupImage();
        //SetupText();

        GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Create the shaders, solid color
        int vertexShader = GLRenderUtil.loadShader(GLES20.GL_VERTEX_SHADER, GLRenderUtil.vs_SolidColor);
        int fragmentShader = GLRenderUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, GLRenderUtil.fs_SolidColor);

        GLRenderUtil.sp_SolidColor = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(GLRenderUtil.sp_SolidColor, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(GLRenderUtil.sp_SolidColor, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(GLRenderUtil.sp_SolidColor);                  // creates OpenGL ES program executables

        // Create the shaders, images
        vertexShader = GLRenderUtil.loadShader(GLES20.GL_VERTEX_SHADER, GLRenderUtil.vs_Image);
        fragmentShader = GLRenderUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, GLRenderUtil.fs_Image);

        GLRenderUtil.sp_Image = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(GLRenderUtil.sp_Image, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(GLRenderUtil.sp_Image, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(GLRenderUtil.sp_Image);                  // creates OpenGL ES program executables

        // Set our shader programm
        GLES20.glUseProgram(GLRenderUtil.sp_Image);
    }

    public void ChangeSurface(int width,int height)
    {
        screenWidth = width;
        screenHeight = height;

        GLES20.glViewport(0,0,width,height);

        for(int i=0;i<16;i++)
        {
            projectionMatrix[i] = 0.0f;
            viewMatrix[i] = 0.0f;
            projectionAndViewMatrix[i] = 0.0f;
        }

        Matrix.orthoM(projectionMatrix,0,0.0f,2048,0.0f,4096,0.0f,50.0f);
        Matrix.setLookAtM(viewMatrix,0,0.0f,0.0f,1.0f,0.0f,0.0f,0.0f,0.0f,1.0f,0.0f);
        Matrix.multiplyMM(projectionAndViewMatrix,0,projectionMatrix,0,viewMatrix,0);
        SetupScaling();
    }

    public void StartRender()
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    public void RenderObject(float[] vertices,float[] spriteInfo)
    {
        UpdateBuffers(vertices,spriteInfo);

        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(GLRenderUtil.sp_Image, "vPosition");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        // Get handle to texture coordinates location
        int mTexCoordLoc = GLES20.glGetAttribLocation(GLRenderUtil.sp_Image, "a_texCoord" );

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray ( mTexCoordLoc );

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);

        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(GLRenderUtil.sp_Image, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, projectionAndViewMatrix, 0);

        // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation (GLRenderUtil.sp_Image, "s_texture" );

        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i ( mSamplerLoc,(int)spriteInfo[0]);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+(int)spriteInfo[0]);
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,(int)spriteInfo[0]);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+(int)spriteInfo[0]);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,(int)spriteInfo[0]);



        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }

    public void EndRender()
    {
        // Disable vertex array
       // GLES20.glDisableVertexAttribArray(mPositionHandle);
      //  GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }

    //UTILITY:

    public void SetupScaling()
    {
        float scaleX = (int)(context.getResources().getDisplayMetrics().widthPixels ) / defaultScreenWidth;
        float scaleY = (int)(context.getResources().getDisplayMetrics().heightPixels) / defaultScreenHeight;

        if(scaleX > scaleY)
        {
            screenScaleUniform = scaleY;
        }
        else
        {
            screenScaleUniform = scaleX;
        }
    }

    public void SetupVertices()
    {
        indices = new short[] {0,1,2,0,2,3};

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    private void UpdateBuffers(float[] vertices,float[] spriteInfo)
    {
        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        uvs = new float[]
                {
                        //V0: Xstart,Ystart
                        spriteInfo[1],spriteInfo[2],
                        //V2:Xstart,Ystart+Height
                        spriteInfo[1],spriteInfo[2]+spriteInfo[4],
                        //V3:Xstart+width,Ystart+height
                        spriteInfo[1]+spriteInfo[3],spriteInfo[2]+spriteInfo[4],
                        //V1:Xstart+width,Ystart
                        spriteInfo[1]+spriteInfo[3],spriteInfo[2]
                };

        // The texture buffer
        ByteBuffer uvbb = ByteBuffer.allocateDirect(uvs.length * 4);
        uvbb.order(ByteOrder.nativeOrder());
        uvBuffer = uvbb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);
    }
    //default setup, assuming one bitmap pr object (using full image)
    //Spritesheet/Atlas Setup is another option(later)
    public void SetupImage()
    {
        // Generate Textures, if more needed, alter these numbers.
        GLES20.glGenTextures(resourceIDs.length,texturenames,0);

        for(int i=0;i<resourceIDs.length;i++)
        {
            // Temporary create a bitmap
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resourceIDs[i]);

            // Bind texture to texturename
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0+i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[i]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

            // We are done using the bitmap so we should recycle it.
            bmp.recycle();
        }
    }
}
