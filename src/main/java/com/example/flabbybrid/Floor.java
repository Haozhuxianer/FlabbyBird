package com.example.flabbybrid;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * Created by liuxian on 2016/8/24.
 */

public class Floor {
    /**
     * 地板位置在游戏界面的4 /5的位置
     */
    private static final float FLOOR_Y_POS_RADIO = 4 / 5F;
    private int x;
    private int y;
    private BitmapShader mFloorShader;//填充物
    private int mGameWidth;
    private int mGameHeight;
    public Floor(int gameWidth , int gameHeight , Bitmap floorBg){
        mGameHeight = gameHeight;
        mGameWidth = gameWidth;
        y = (int)(gameHeight * FLOOR_Y_POS_RADIO);
        mFloorShader = new BitmapShader(floorBg , Shader.TileMode.REPEAT , Shader.TileMode.CLAMP);
    }

    public void draw(Canvas mCanvas , Paint mPaint){
        if(-x > mGameWidth){
            x = x % mGameWidth;
        }
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        mCanvas.translate(x , y);
        mPaint.setShader(mFloorShader);
        mCanvas.drawRect(x , 0 , -x + mGameWidth, mGameHeight - y , mPaint);
        mCanvas.restore();
        mPaint.setShader(null);
    }
    public int getX(){
        return  x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }
}
