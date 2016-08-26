package com.example.flabbybrid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxian on 2016/8/23.
 */

public class GameFlabbyBrid extends SurfaceView implements SurfaceHolder.Callback , Runnable {

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Thread t;
    private boolean isRunning;
    private Paint mPaint;
    /**
     * 当前View的尺寸
     */
    private int mWidth;
    private int mHeight;
    private RectF mGamePanelRect = new RectF();

    private Bitmap mBg;
    /**
     * 鸟
     */
    private Bird mBird;
    private Bitmap mBirdBitmap;
    /**
     * 地板
     */
    private Floor mFloor;
    private Bitmap mFloorBg;
    private int mSpeed;
    /**
     * 管道
     * 管道宽度为60
     */
    private Bitmap mPipeTop;
    private Bitmap mPipeBottom;
    private RectF mPipeRect;
    private int mPipeWidth;
    private static final int PIPE_WIDTH = 80;
    private List<Pipe> mPipes = new ArrayList<Pipe>();
    private List<Pipe> mNeedRemovePipe = new ArrayList<Pipe>();
    private final int PIPE_DIS_BETWEEN_TWO = Util.dp2px(getContext() , 100);
    private int mTmpMoveDistance;
    /**
     * 分数
     */
    private final int[] mNums = new int[] {
            R.drawable.n0 , R.drawable.n1 , R.drawable.n2 , R.drawable.n3 , R.drawable.n4 ,
            R.drawable.n5 , R.drawable.n6, R.drawable.n7 , R.drawable.n8 , R.drawable.n9
    };
    private Bitmap[] mNumBitmap;
    private int mGrade = 0;
    private int mRemovedPipe = 0;
    private static final float RADIO_SINGLE_NUM_HEIGHT = 1 / 15F;//单个字数的高度为1/15
    private int mSingleGradeWidth;
    private int mSingleGradeHeight;
    private RectF mSingleNumRectf;

    /**
     * 游戏的状态
     */
    private enum GameStatus{
        WAITING , RUNNING , STOP;
    }
    private GameStatus mStatus = GameStatus.WAITING;
    private static final int TOUCH_UP_SIZE = -16;
    private final int mBirdUpDis = Util.dp2px(getContext() , TOUCH_UP_SIZE);
    private int mTipBirdDis;
    private final int mAutoDownSpeed = Util.dp2px(getContext() , 2);

    public GameFlabbyBrid(Context context){
        this(context,null);
    }
    public GameFlabbyBrid(Context context , AttributeSet attributeSet){
        super(context , attributeSet);

        mHolder = getHolder();
        mHolder.addCallback(this);
        setZOrderOnTop(true);//设置画布，透明背景
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        setFocusable(true);//设置可获得焦点
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);//设置屏幕常亮

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        initBitmaps();

        mSpeed = Util.dp2px(getContext() , 5);
        mPipeWidth = Util.dp2px(getContext() , PIPE_WIDTH);
    }

    private void initBitmaps(){
        mBg = loadImageByResId(R.drawable.bg1);
        mBirdBitmap = loadImageByResId(R.drawable.b1);
        mFloorBg = loadImageByResId(R.drawable.floor_bg2);
        mPipeTop = loadImageByResId(R.drawable.g2);
        mPipeBottom = loadImageByResId(R.drawable.g1);
        mNumBitmap = new Bitmap[mNums.length];
        for (int i = 0 ; i < mNumBitmap.length ; i++){
            mNumBitmap[i] = loadImageByResId(mNums[i]);
        }
    }

    public void surfaceCreated(SurfaceHolder holder){
        isRunning = true;//开启线程
        t = new Thread(this);
        t.start();
    }

    public void surfaceChanged(SurfaceHolder holder , int format , int width , int height){

    }

    public void surfaceDestroyed(SurfaceHolder holder){
        isRunning = false;//通知关闭线程
    }

    public  void run(){
        while (isRunning){
            long start = System.currentTimeMillis();
            logic();
            draw();
            long end = System.currentTimeMillis();

            try {
                if(end - start < 50){
                    Thread.sleep(50 - (end - start));
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    private void draw(){
        try {
            mCanvas = mHolder.lockCanvas();
            if(mCanvas != null){
                drawBg();
                drawBird();
                drawPipe();
                drawFloor();
                drawGrade();
                mFloor.setX(mFloor.getX() - mSpeed);
            }
        }catch (Exception e){
        }finally {
            if(mCanvas != null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    private Bitmap loadImageByResId(int resId){
        return BitmapFactory.decodeResource(getResources() , resId);
    }

    private void drawBg(){
        mCanvas.drawBitmap(mBg , null, mGamePanelRect , null);
    }

    private void drawBird(){
        mBird.draw(mCanvas);
    }

    private void drawFloor(){
        mFloor.draw(mCanvas , mPaint);
    }

    private void drawPipe(){
        for(Pipe pipe : mPipes){
            pipe.setX(pipe.getX() - mSpeed);
            pipe.draw(mCanvas , mPipeRect);
        }
    }

    private void drawGrade(){
        String grade = mGrade + "";
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        mCanvas.translate(mWidth / 2 - grade.length() * mSingleGradeWidth / 2, 1f / 8 * mHeight);
        for(int i = 0 ; i < grade.length() ; i++){
            String numStr = grade.substring(i , i + 1);
            int num = Integer.valueOf(numStr);
            mCanvas.drawBitmap(mNumBitmap[num] , null , mSingleNumRectf , null);
            mCanvas.translate(mSingleGradeWidth , 0);
        }
        mCanvas.restore();
    }
    protected void onSizeChanged(int w , int h , int oldw , int oldh){
        super.onSizeChanged(w , h , oldw , oldh);

        mWidth = w;
        mHeight = h;
        mGamePanelRect.set(0 , 0 , w , h);

        mBird = new Bird(getContext() , mWidth , mHeight , mBirdBitmap);
        mFloor = new Floor(mWidth , mHeight , mFloorBg);

        mPipeRect = new RectF(0 , 0 , mPipeWidth , mHeight);
        /*Pipe pipe = new Pipe(getContext() , w , h , mPipeTop , mPipeBottom);
        mPipes.add(pipe);*/

        mSingleGradeHeight = (int) (h * RADIO_SINGLE_NUM_HEIGHT);
        mSingleGradeWidth = (int) (mSingleGradeHeight * 1.0f / mNumBitmap[0].getHeight() * mNumBitmap[0].getWidth());
        mSingleNumRectf = new RectF(0 , 0 , mSingleGradeWidth , mSingleGradeHeight);
    }

    private void logic(){
        switch (mStatus){
            case  RUNNING:
                mGrade = 0;
                mFloor.setX(mFloor.getX() - mSpeed);
                logicPipe();
                mTipBirdDis += mAutoDownSpeed;
                mBird.setY(mBird.getY() + mTipBirdDis);

                mGrade += mRemovedPipe;
                for (Pipe pipe : mPipes){
                    if(pipe.getX() + mPipeWidth < mBird.getX()){
                        mGrade++;
                    }
                }
                checkGameover();
                break;
            case STOP://鸟落下
                if(mBird.getY() < mFloor.getY() - mBird.getWidth()){
                    mTipBirdDis += mAutoDownSpeed;
                    mBird.setY(mBird.getY() + mTipBirdDis);
                }else {
                    mStatus = GameStatus.WAITING;
                    initPos();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 重置鸟的位置数据
     */
    private void initPos(){
        mPipes.clear();
        mNeedRemovePipe.clear();
        mBird.setY(mHeight * 2 / 3);
        mTipBirdDis = 0;
        mRemovedPipe = 0;
    }

    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            switch (mStatus){
                case WAITING:
                    mStatus = GameStatus.RUNNING;
                    break;
                case RUNNING:
                    mTipBirdDis = mBirdUpDis;
                    break;
            }
        }
        return true;
    }

    private void logicPipe(){
        for(Pipe pipe : mPipes){
            if(pipe.getX() < -mPipeWidth){
                mNeedRemovePipe.add(pipe);
                mRemovedPipe++;
                continue;
            }
            pipe.setX(pipe.getX() - mSpeed);
        }
        mPipes.removeAll(mNeedRemovePipe);
        for(Pipe pipe : mPipes){
            pipe.setX(pipe.getX() - mSpeed);
        }
        mTmpMoveDistance += mSpeed;
        if(mTmpMoveDistance >= PIPE_DIS_BETWEEN_TWO){
            Pipe pipe = new Pipe(getContext() , getWidth() , getHeight() , mPipeTop , mPipeBottom);
            mPipes.add(pipe);
            mTmpMoveDistance = 0;
        }
    }

    private void checkGameover(){
        if(mBird.getY() > mFloor.getY() - mBird.getmHeight()){
            mStatus = GameStatus.STOP;
        }
        for(Pipe wall : mPipes){
            if(wall.getX() + mPipeWidth < mBird.getX()){
                continue;
            }
            if(wall.touchBird(mBird)){
                mStatus = GameStatus.STOP;
                break;
            }
        }
    }
}
