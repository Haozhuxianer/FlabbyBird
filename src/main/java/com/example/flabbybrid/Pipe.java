package com.example.flabbybrid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by liuxian on 2016/8/24.
 */

public class Pipe {
    /**
     * 上下管道间距
     * 管道最大高度
     * 管道最小高度
     */
    private static final float RADIO_BETWEEN_UP_DOWN = 1 / 5F;
    private static final float RADIO_MAX_HEIGHT = 2 / 5F;
    private static final float RADIO_MIN_HEIGHT = 1 / 5F;
    private int x;//管道横坐标
    private int height;//上管道高度
    private int margin;//上下管道间距
    private Bitmap mTop;//上管道图片
    private Bitmap mBotton;//下管道图片
    private static Random random = new Random();

    public Pipe(Context context , int gameWidth , int gameHeight , Bitmap top , Bitmap bottom){
        margin = (int)(gameHeight *RADIO_BETWEEN_UP_DOWN);
        //默认从左边出来
        x = gameWidth;
        mTop = top;
        mBotton = bottom;
        randomHeight(gameHeight);
    }
    /**
     * 随机产生一个高度
     */
     private void randomHeight(int gameHeight){
         height = random.nextInt((int) (gameHeight * (RADIO_MAX_HEIGHT - RADIO_MIN_HEIGHT)));
         height = (int) (height + gameHeight *RADIO_MIN_HEIGHT);
     }

    public  void draw(Canvas mCanvas , RectF rect){
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        mCanvas.translate(x , -(rect.bottom - height));
        mCanvas.drawBitmap(mTop , null , rect , null);
        mCanvas.translate(0 , (rect.bottom - height) + height + margin);
        mCanvas.drawBitmap(mBotton , null , rect , null);
        mCanvas.restore();
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public boolean touchBird(Bird mBird){
        if(mBird.getX() + mBird.getWidth() > x && (mBird.getY() < height || mBird.getY() + mBird.getmHeight() >height + margin)){
            return  true;
        }
        return false;
    }
}
