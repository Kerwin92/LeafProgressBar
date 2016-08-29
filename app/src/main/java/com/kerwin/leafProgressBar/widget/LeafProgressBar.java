package com.kerwin.leafProgressBar.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.cpxiao.leafprogressbar.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by kerwin on 8/26/16.
 * LeafLoadingView
 */
public class LeafProgressBar extends View {
    private static final boolean DEBUG = true;
    private static final String TAG = LeafProgressBar.class.getSimpleName();

    private Random mRandom = new Random();
    private Matrix mMatrix = new Matrix();

    /**
     * 默认背景色
     */
    private static final int BACKGROUND_COLOR = 0xfffce49b;
    /**
     * 橙色，进度条已完成颜色
     */
    private static final int ORANGE_COLOR = 0xffffa800;
    /**
     * 淡白色，进度条未完成颜色
     */
    private static final int WHITE_COLOR = 0xfffce49b;
    /**
     * 风扇背景色
     */
    private static final int FEN_BG_COLOR = 0xfffccd50;

    /**
     * 默认最大振幅，控件高度的百分比，取值范围[0,100]
     */
    private static final int DEFAULT_MAX_AMPLITUDE = 70;
    /**
     * 默认最小振幅，控件高度的百分比，取值范围[0,100]
     */
    private static final int DEFAULT_MIN_AMPLITUDE = 20;
    /**
     * 总进度
     */
    private static final int TOTAL_PROGRESS = 100;
    /**
     * 默认叶子飘动一个周期所花的时间
     */
    private static final long LEAF_FLOAT_TIME = 3000;
    /**
     * 默认叶子旋转一周需要的时间
     */
    private static final long LEAF_ROTATE_TIME = 2000;
    /**
     * 默认风扇旋转一周需要的时间
     */
    private static final int FEN_ROTATE_TIME = 1000;
    /**
     * 用于控制绘制的进度条和背景之间的Margin，控件高度的百分比，取值范围[0,50]
     */
    private static final int MARGIN = 10;


    /**
     * 风扇
     */
    private Bitmap mFanBitmap;
    private long mFenRotateTime = FEN_ROTATE_TIME;
    /**
     * 顺时针
     */
    private boolean isFenClockwise = true;
    /**
     * margin值
     */
    private int mMargin = MARGIN;
    /**
     * 最大振幅
     */
    private int mMaxAmplitude = DEFAULT_MAX_AMPLITUDE;
    /**
     * 最小振幅
     */
    private int mMinAmplitude = DEFAULT_MIN_AMPLITUDE;

    /**
     * 叶子飘动一个周期所花的时间
     */
    private long mLeafFloatTime = LEAF_FLOAT_TIME;
    /**
     * 叶子旋转一周需要的时间
     */
    private long mLeafRotateTime = LEAF_ROTATE_TIME;
    /**
     * 叶子
     */
    private Bitmap mLeafBitmap;
    //控件的长宽
    private int mViewWidth, mViewHeight;

    //画笔
    private Paint mBitmapPaint, mBgPaint, mWhitePaint, mOrangePaint, mShadePaint, mFanBgPaint, mFanCirclePaint, mFanTextPaint;
    //rect
    private RectF mViewRect, mProgressBaseRectF, mProgressRectF, mShadeRectF, mLeftArcRectF, mFenRectF;
    /**
     * 当前进度
     */
    private float mProgress = 0f;
    /**
     * 产生出的叶子信息
     */
    private List<Leaf> mLeafList;
    /**
     * 加载完毕时消失次数控制
     */
    private float coefficient = 1;

    public LeafProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBitmap();
        initPaint();
        mLeafList = (new LeafFactory()).generateLeafs();
    }

    private void initBitmap() {
        mLeafBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.leaf);

        mFanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fan);
    }

    private void initPaint() {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);//抗锯齿
        mBitmapPaint.setDither(true);//防抖动
        mBitmapPaint.setFilterBitmap(true);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(BACKGROUND_COLOR);

        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(WHITE_COLOR);

        mOrangePaint = new Paint();
        mOrangePaint.setAntiAlias(true);
        mOrangePaint.setColor(ORANGE_COLOR);

        mShadePaint = new Paint();
        mShadePaint.setAntiAlias(true);
        mShadePaint.setStyle(Paint.Style.STROKE);
        mShadePaint.setColor(BACKGROUND_COLOR);

        mFanBgPaint = new Paint();
        mFanBgPaint.setAntiAlias(true);
        mFanBgPaint.setColor(FEN_BG_COLOR);

        mFanCirclePaint = new Paint();
        mFanCirclePaint.setAntiAlias(true);
        mFanCirclePaint.setStyle(Paint.Style.STROKE);
        mFanCirclePaint.setColor(Color.WHITE);

        mFanTextPaint = new Paint();
        mFanTextPaint.setColor(Color.WHITE);
        mFanTextPaint.setTextSize(10);
        mFanTextPaint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mProgress += 0.5f;
        //绘制背景
        drawProgress(canvas, mViewRect, mBgPaint, true, true);
        //绘制进度条，所有部分
        drawProgress(canvas, mProgressBaseRectF, mWhitePaint, true, false);
        //绘制叶子
        drawLeafs(canvas);
        //绘制进度条，完成部分
        drawProgress(canvas);
        //绘制遮罩
        mShadePaint.setStrokeWidth((mViewHeight - mProgressBaseRectF.height()) / 2);
        drawProgress(canvas, mShadeRectF, mShadePaint, true, true);
        //绘制风扇
        drawFan(canvas);
        if (mProgress >= TOTAL_PROGRESS) {
            finishLoad(canvas);
            if (coefficient < 10) {
                postInvalidate();
            }
        } else {
            postInvalidate();
        }
    }


    /**
     * @param canvas      canvas
     * @param rect        绘制区域
     * @param paint       paint
     * @param hasLeftArc  是否绘制左半圆
     * @param hasRightArc 是否绘制右半圆
     */
    private void drawProgress(Canvas canvas, RectF rect, Paint paint, boolean hasLeftArc, boolean hasRightArc) {
        float w = rect.width();
        float h = rect.height();
        if (!(hasLeftArc || hasRightArc)) {//w<h的判断是做啥的
            canvas.drawRect(rect, paint);
        }
        Path path = new Path();
        if (!hasLeftArc) {
            path.moveTo(rect.left, rect.top);
            path.lineTo(rect.right - h / 2, rect.top);
            path.arcTo(new RectF(rect.right - h, rect.top, rect.right, rect.bottom), 270, 180);//右侧半圆
            path.lineTo(rect.left + h / 2, rect.bottom);
            path.close();
        } else if (!hasRightArc) {
            path.moveTo(rect.left + h / 2, rect.top);
            path.lineTo(rect.right, rect.top);
            path.lineTo(rect.right, rect.bottom);
            path.lineTo(rect.left + h / 2, rect.bottom);
            path.arcTo(new RectF(rect.left, rect.top, rect.left + h, rect.bottom), 90, 180);//左侧半圆
            path.close();
        } else {
            path.moveTo(rect.left + h / 2, rect.top);
            path.lineTo(rect.right - h / 2, rect.top);
            path.arcTo(new RectF(rect.right - h, rect.top, rect.right, rect.bottom), 270, 180);//右侧半圆
            path.lineTo(rect.right - h / 2, rect.bottom);
            path.lineTo(rect.left + h / 2, rect.bottom);
            path.arcTo(new RectF(rect.left, rect.top, rect.left + h, rect.bottom), 90, 180);//左侧半圆
            path.close();
        }
        canvas.drawPath(path, paint);
    }

    /**
     * @param canvas canvas
     */
    private void drawProgress(Canvas canvas) {
        if (mProgress >= TOTAL_PROGRESS) {
            mProgress = TOTAL_PROGRESS;
        }
        // 根据当前进度算出进度条的位置
        float mCurrentProgressPosition = mProgressBaseRectF.width() * mProgress / TOTAL_PROGRESS;

        // 当前位置在左侧半圆范围内
        if (mCurrentProgressPosition < mProgressBaseRectF.height()) {
            // 绘制棕色 ARC
            // 单边角度
            int angle = (int) Math.toDegrees(Math.acos((mProgressBaseRectF.height() - mCurrentProgressPosition) / mProgressBaseRectF.height()));
            // 起始的位置
            int startAngle = 180 - angle;
            // 扫过的角度
            int sweepAngle = 2 * angle;
            canvas.drawArc(mLeftArcRectF, startAngle, sweepAngle, false, mOrangePaint);
        } else {
            //            drawProgress(canvas, mProgressBaseRectF, mOrangePaint, true, false);//直接把进度条画满了。。。
            mProgressRectF.right = mCurrentProgressPosition;
            drawProgress(canvas, mProgressRectF, mOrangePaint, true, false);
        }
    }

    /**
     * 绘制叶子
     *
     * @param canvas Canvas
     */
    private void drawLeafs(Canvas canvas) {
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < mLeafList.size(); i++) {
            Leaf leaf = mLeafList.get(i);
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                // 根据叶子的类型和当前时间得出叶子的坐标（x，y）
                getLeafLocation(leaf, currentTime);
                // 根据时间计算旋转角度
                canvas.save();
                // 通过Matrix控制叶子旋转
                float transX = leaf.x;
                float transY = leaf.y;
                mMatrix.reset();
                mMatrix.postTranslate(transX, transY);
                // 通过时间关联旋转角度，则可以直接通过修改LEAF_ROTATE_TIME调节叶子旋转快慢
                float rotateFraction = 1.0f * ((currentTime - leaf.startTime) % leaf.rotateTime) / leaf.rotateTime;
                int angle = (int) (rotateFraction * 360);
                // 根据叶子旋转方向确定叶子当前旋转角度
                int rotate = leaf.isClockwise ? angle + leaf.rotateAngle : -angle + leaf.rotateAngle;
                mMatrix.postRotate(rotate, transX + mLeafBitmap.getWidth() / 2, transY + mLeafBitmap.getHeight() / 2);
                canvas.drawBitmap(mLeafBitmap, mMatrix, mBitmapPaint);
                canvas.restore();
            } else {
                if (DEBUG) {
                    Log.d(TAG, "drawLeafs: ........." + currentTime + "," + leaf.startTime + "," + (leaf.startTime - currentTime));
                }
            }
        }
    }

    /**
     * 绘制风扇
     *
     * @param canvas
     */
    private void drawFan(Canvas canvas) {
        //绘制风扇的底色
        canvas.drawCircle(mFenRectF.centerX(), mFenRectF.centerY(), mFenRectF.width() / 2, mFanBgPaint);
        //绘制最外面圆环
        float delta = 0.05f;
        mFanCirclePaint.setStrokeWidth(mFenRectF.width() * delta);
        canvas.drawCircle(mFenRectF.centerX(), mFenRectF.centerY(), mFenRectF.width() / 2 * (1 - delta / 2), mFanCirclePaint);

        long currentTime = System.currentTimeMillis();
        // 根据时间计算旋转角度
        canvas.save();
        // 通过Matrix控制旋转
        float transX = mFenRectF.centerX() - mFanBitmap.getWidth() / 2;
        float transY = mFenRectF.centerY() - mFanBitmap.getHeight() / 2;
        mMatrix.reset();
        mMatrix.postTranslate(transX, transY);//移动到此位置
        // 通过时间关联旋转角度，则可以直接通过修改ROTATE_TIME调节旋转快慢
        float rotateFraction = 1.0f * ((currentTime) % mFenRotateTime) / mFenRotateTime;
        int angle = (int) (rotateFraction * 360);
        // 根据旋转方向确定叶子旋转角度
        int rotate = isFenClockwise ? angle : -angle;
        mMatrix.postRotate(rotate, transX + mFanBitmap.getWidth() / 2, transY + mFanBitmap.getHeight() / 2);
        //            //绘制风扇的底色
        //            canvas.drawCircle(mFenRectF.centerX(), mFenRectF.centerY(), mFenRectF.width() / 2, mFanBgPaint);
        //            //绘制最外面圆环
        //            float delta = 0.05f;
        //            mFanCirclePaint.setStrokeWidth(mFenRectF.width() * delta);
        //            canvas.drawCircle(mFenRectF.centerX(), mFenRectF.centerY(), mFenRectF.width() / 2 * (1 - delta / 2), mFanCirclePaint);

        canvas.drawBitmap(mFanBitmap, mMatrix, mBitmapPaint);
        canvas.restore();
    }

    void finishLoad(Canvas canvas) {
        //加载完毕时
        mMatrix.reset();
        mMatrix.postTranslate(mFenRectF.centerX() - mFanBitmap.getWidth() / 2, mFenRectF.centerY() - mFanBitmap.getHeight() / 2);//移动到此位置
        canvas.scale((float) 1 / coefficient, (float) 1 / coefficient, mFenRectF.centerX(), mFenRectF.centerY());
        canvas.drawBitmap(mFanBitmap, mMatrix, mBitmapPaint);
        coefficient++;
        //            while (coefficient <= 1) {
        //                //                canvas.scale(coefficient, coefficient, mFenRectF.centerX(), mFenRectF.centerY());
        //                canvas.drawText("100%", mFenRectF.centerX(), mFenRectF.centerY(), mFanTextPaint);
        //                //                coefficient += 0.125;
        //            }
    }

    /**
     * 根据时间获取具体某个leaf的当前展示位置
     *
     * @param leaf
     * @param currentTime
     */
    private void getLeafLocation(Leaf leaf, long currentTime) {
        long intervalTime = currentTime - leaf.startTime;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > leaf.floatTime) {
            leaf.startTime = System.currentTimeMillis() + leaf.floatTime + mRandom.nextLong() % leaf.floatTime;//已经完整飞行过的叶子，至少下下次才能再飞
        }

        float fraction = (float) intervalTime / leaf.floatTime;
        leaf.x = mProgressBaseRectF.width() * (1 - fraction);//匀速飞行，水平位置与飞行时间成正比
        leaf.y = getLocationY(leaf);

        Log.d(TAG, mProgressBaseRectF.width() + ", x=" + leaf.x + ", y=" + leaf.y);
    }

    /**
     * 通过叶子信息获取当前叶子的Y值
     *
     * @param leaf
     * @return
     */
    private int getLocationY(Leaf leaf) {
        //公式： y = A(wx+Q)+h，Q=0
        float w = (float) (2 * Math.PI / mProgressBaseRectF.width());
        float amplitudeM = mProgressBaseRectF.height() * (mMaxAmplitude + mMinAmplitude) / 400;//振幅基准不能大
        float delta = mProgressBaseRectF.height() * (mMaxAmplitude - mMinAmplitude) / 800;//振幅变化范围更不能大
        switch (leaf.type) {
            case XS:
                amplitudeM += (-2) * delta;
                break;
            case S:
                amplitudeM += (-1) * delta;
                break;
            case L:
                amplitudeM += delta;
                break;
            case XL:
                amplitudeM += 2 * delta;
                break;
            default:
                break;
        }
        return (int) ((amplitudeM * Math.sin(w * leaf.x)) + mViewHeight / 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heigthSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heigthSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        //设定wrap_content模式下的默认大小
        if (widthSpecMode == MeasureSpec.AT_MOST && heigthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(800, 200);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(800, heigthSpecSize);
        } else if (heigthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, 200);
        }

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        mViewWidth = w - paddingLeft - paddingRight;//宽度响应padding设置
        mViewHeight = h - paddingBottom - paddingTop;//高度响应padding设置
        mMargin = mViewHeight * mMargin / 100;

        mViewRect = new RectF(0, 0, mViewWidth, mViewHeight);

        mProgressBaseRectF = new RectF(mMargin,
                mMargin,
                mViewWidth - mViewHeight / 2,
                mViewHeight - mMargin);

        mProgressRectF = new RectF(mMargin,
                mMargin,
                mViewWidth - mViewHeight / 2,
                mViewHeight - mMargin);

        mShadeRectF = new RectF(mViewRect.left + mMargin / 2,
                mViewRect.top + mMargin / 2,
                mViewRect.right - mMargin / 2,
                mViewRect.bottom - mMargin / 2);

        mLeftArcRectF = new RectF(mMargin, mMargin, mViewHeight - mMargin, mViewHeight - mMargin);

        mFenRectF = new RectF(mViewWidth - mViewHeight, 0, mViewWidth, mViewHeight);

        //重置图片大小
        int leafBitmapH = mViewHeight / 6;
        int leafBitmapW = leafBitmapH * mLeafBitmap.getWidth() / mLeafBitmap.getHeight();
        mLeafBitmap = Bitmap.createScaledBitmap(mLeafBitmap, leafBitmapW, leafBitmapH, false);

        int fanBitmapH = (int) (mFenRectF.height() * 0.8f);
        int fanBitmapW = fanBitmapH * mFanBitmap.getWidth() / mFanBitmap.getHeight();
        mFanBitmap = Bitmap.createScaledBitmap(mFanBitmap, fanBitmapW, fanBitmapH, false);
    }

    private enum StartType {
        XS, S, M, L, XL
    }

    /**
     * 叶子对象，用来记录叶子主要数据
     */
    private class Leaf {
        // 位置
        float x, y;
        // 控制叶子飘动的幅度
        StartType type;
        // 旋转角度
        int rotateAngle;
        // 是否顺时针旋转
        boolean isClockwise;
        // 起始时间(ms)
        long startTime;
        // 叶子飘动一个周期所花的时间
        long floatTime;
        // 叶子旋转一周需要的时间
        long rotateTime;
    }

    /**
     * Leaf工厂类，负责制造叶子
     */
    private class LeafFactory {
        //叶子数
        private static final int MAX_LEAFS = 8;
        //浮动比例
        private static final float DELTA = 0.1f;
        //用于产生时间差，防止叶子抱团出现
        private long mDeltaStartTime = 0;

        /**
         * 生成一个叶子信息
         */
        private Leaf generateLeaf() {
            Leaf leaf = new Leaf();
            // 随时类型－ 随机振幅
            leaf.type = randomStartType();
            // 随机起始的旋转角度
            leaf.rotateAngle = mRandom.nextInt(360);
            // 随机旋转方向（顺时针或逆时针）
            leaf.isClockwise = mRandom.nextBoolean();
            //随机叶子旋转速度及飘动速度
            leaf.floatTime = (long) (mLeafFloatTime * ((1 - DELTA) + 2 * DELTA * mRandom.nextFloat()));
            leaf.rotateTime = (long) (mLeafRotateTime * ((1 - DELTA) + 2 * DELTA * mRandom.nextFloat()));
            // 为了产生交错的感觉，让开始的时间有一定的随机性
            mDeltaStartTime += (mLeafFloatTime + mRandom.nextFloat() * leaf.floatTime) / MAX_LEAFS;
            leaf.startTime = System.currentTimeMillis() + mDeltaStartTime % leaf.floatTime;
            Log.d(TAG + "aa", "generateLeaf: " + leaf.floatTime);
            Log.d(TAG + "aa", "generateLeaf: " + leaf.rotateTime);
            Log.d(TAG + "aa", "generateLeaf: " + leaf.startTime);
            return leaf;
        }

        private StartType randomStartType() {
            int index = mRandom.nextInt(5);
            switch (index) {
                case 0:
                    return StartType.XS;
                case 1:
                    return StartType.S;
                case 2:
                    return StartType.M;
                case 3:
                    return StartType.L;
                case 4:
                    return StartType.XL;
                default:
                    break;
            }
            return StartType.M;
        }

        /**
         * 根据最大叶子数产生叶子信息
         */
        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        /**
         * 根据传入的叶子数量产生叶子信息
         *
         * @param leafSize
         * @return
         */
        private List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new LinkedList<>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(generateLeaf());
            }
            return leafs;
        }

    }


    /**
     * 获取最大振幅
     */
    public int getMaxAmplitude() {
        return mMaxAmplitude;
    }

    /**
     * 设置最大振幅
     *
     * @param amplitude int
     */
    public void setMaxAmplitude(int amplitude) {
        this.mMaxAmplitude = amplitude;
    }

    /**
     * 获取最小振幅
     */
    public int getMinAmplitude() {
        return mMinAmplitude;
    }

    /**
     * 设置最小振幅
     *
     * @param amplitude int
     */
    public void setMinAmplitude(int amplitude) {
        this.mMinAmplitude = amplitude;
    }


    /**
     * 获取叶子飘完一个周期所花的时间
     */
    public long getLeafFloatTime() {
        return mLeafFloatTime;
    }

    /**
     * 设置叶子飘完一个周期所花的时间
     *
     * @param time long
     */
    public void setLeafFloatTime(long time) {
        this.mLeafFloatTime = time;
    }

    /**
     * 获取叶子旋转一周所花的时间
     */
    public long getLeafRotateTime() {
        return mLeafRotateTime;
    }

    /**
     * 设置叶子旋转一周所花的时间
     *
     * @param time long
     */
    public void setLeafRotateTime(long time) {
        this.mLeafRotateTime = time;
    }

    /**
     * 获取风扇旋转一周所花的时间
     */
    public long getFenRotateTime() {
        return mFenRotateTime;
    }

    /**
     * 设置风扇旋转一周所花的时间
     *
     * @param time long
     */
    public void setFenRotateTime(long time) {
        this.mFenRotateTime = time;
    }

    /**
     * 获取风扇旋转方向
     */
    public boolean getIsFenClockwise() {
        return isFenClockwise;
    }

    /**
     * 设置风扇旋转方向
     *
     * @param isFenClockwise boolean
     */
    public void setIsFenClockwise(boolean isFenClockwise) {
        this.isFenClockwise = isFenClockwise;
    }

    /**
     * 获取进度
     */
    public float getProgress() {
        return mProgress;
    }

    /**
     * 设置进度
     *
     * @param progress int
     */
    public void setProgress(float progress) {
        this.mProgress = progress;
        postInvalidate();
    }

}