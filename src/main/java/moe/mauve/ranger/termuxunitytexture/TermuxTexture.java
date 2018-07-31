package moe.mauve.ranger.termuxunitytexture;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.termux.terminal.TerminalSession;

/**
 * Created by Mauve on 2018-03-10.
 */

public class TermuxTexture {
    private TerminalRendererClone renderer;
    private TerminalSession session;

    private Bitmap bitmap;
    private boolean shouldRender = true;
    private int[] textureHandle = null;
    private Canvas canvas;
    private int scroll = 0;

    public  TermuxTexture(int width, int height, TerminalSession session) {
        this.session = session;
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(this.bitmap);
    }

    public void setScroll(int scroll) {
        if(scroll != this.scroll) {
            this.scroll = scroll;
            this.shouldRender = true;
        }
    }

    public int render(){
        final Bitmap bitmap = renderBitmap();
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0, bitmap, 0);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureHandle[0];
    }

    public void setShouldRender(){
        this.shouldRender = true;
    }

    int[] getTextureHandle(){
        if(textureHandle != null)
            return  textureHandle;
        textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        return textureHandle;
    }



    Bitmap renderBitmap(){
        if(shouldRender) {
            canvas.drawColor(Color.BLACK);

            renderer.render(session.getEmulator(), canvas, scroll, -1, -1, -1, -1);

            shouldRender = false;
        }

        return bitmap;
    }
}
