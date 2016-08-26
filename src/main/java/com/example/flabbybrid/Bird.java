package com.example.flabbybrid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by liuxian on 2016/8/24.
 */

public class Bird {

    /**
     * 小鸟在屏幕高度为2/3的位置
     * 小鸟的宽度为30dp
     */
    private static final float RADIO_POS_HEIGHT  = 2 /3F;
    private static final int BIRD_SIZE = 30;
    private int x;
    private int y;//小鸟的横纵坐标
    private int mWidth;
    private int mHeight;//小鸟的宽高
    private Bitmap bitmap;
    private RectF rect = new RectF();//绘图范围

    public Bird(Context context , int gameWidth , int gameHeight , Bitmap bitmap){
        this.bitmap = bitmap;
        //鸟的位置
        x = gameWidth / 2 - bitmap.getWidth() / 2;
        y = (int)(gameHeight * RADIO_POS_HEIGHT);
        //计算鸟的宽度和高
        mWidth = Util.dp2px(context , BIRD_SIZE);
        mHeight = (int)(mWidth * 1.0f / bitmap.getWidth() * bitmap.getHeight());
    }

    public void draw(Canvas canvas){
        rect.set(x, y, x + mWidth , y + mHeight);
        canvas.drawBitmap(bitmap , null , rect , null);
    }

    public int getY(){
        return  y;
    }

    public void setY(int y){
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public void setX(int x){
        this.x = x;
    }

    public int getWidth(){
        return mWidth;
    }

    public int getmHeight(){
        return mHeight;
    }
}
