package com.wellsun.pushdoor.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.wellsun.pushdoor.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * date     : 2023-04-03
 * author   : ZhaoZheng
 * describe :
 */
public class SlideStepView extends View {
    //先分析我们这次需要哪些预备的属性

    //存放下面文字集合
    private List<String> texts;
    //文字大小
    private int mTextSize;
    //文字常规颜色
    private int mColorTextDefault;
    //文字被选择时候的颜色
    private int mColorTextSelect;
    //圆和文字之间的距离
    private int mMarginTop;
    //线段和圆圈常规的颜色
    private int mColorCircleDefault;
    //圆圈被选中的的颜色
    private int mColorCircleSelect;
    //中间线段的整个长度
    private float mLineLength;
    //中间线段宽度
    private int mLineHeight;
    //圆圈的半径
    private int mCircleRadius;
    //选中后蓝色的宽度
    private int mSelectCircleStroke;
    //当前选中的下标
    private int mSelectPosition;

    //保存每个TextView的测量矩形数据
    private List<Rect> mBounds;

    //各种画笔
    private Paint mTextPaint;
    private Paint mLinePaint;
    private Paint mCirclePaint;
    private Paint mCircleSelectPaint;


    //记录画的圆的坐标
    ArrayList<Float[]> circleLoaction = new ArrayList<>();

    public SlideStepView(Context context) {
        this(context, null);
    }

    public SlideStepView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ResourceAsColor")
    public SlideStepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化数基本属性
        init();
    }

    private void init() {
        //初始化数据源容器
        texts = new ArrayList<>();
        mBounds = new ArrayList<>();

        //添加加数据
        texts.add("故障提示");
        texts.add("故障分配");
        texts.add("故障原因");
        texts.add("故障处理完毕");

        //将当前选中为1
        mSelectPosition = 0;
        mMarginTop = 10;
        mCircleRadius = 30;
        mSelectCircleStroke = 3;

        //初始化文字属性
        mColorTextDefault = Color.GRAY;
        mColorTextSelect = Color.BLUE;
        mTextSize = 20;
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mColorTextDefault);
        mTextPaint.setAntiAlias(true);

        //初始化圆圈属性
//        mColorCircleDefault = Color.argb(255, 131, 139, 139);
        mColorCircleDefault = Color.parseColor("#838B8B");
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mColorCircleDefault);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);

        //初始化被选中的圆圈
        mColorCircleSelect = Color.BLUE;
        mCircleSelectPaint = new Paint();
        mCircleSelectPaint.setColor(mColorCircleSelect);
        mCircleSelectPaint.setStyle(Paint.Style.FILL);
        mCircleSelectPaint.setAntiAlias(true);//画笔设置抗拒是
//        mCircleSelectPaint.setStrokeWidth(mSelectCircleStroke);

        //设置线段属性
        mLineHeight = 10;
        mLinePaint = new Paint();
        mLinePaint.setColor(mColorCircleDefault);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(mLineHeight);
        mLinePaint.setAntiAlias(true);

        //测量TextView
        measureText();
    }

    private void measureText() {
        for (int i = 0; i < texts.size(); i++) {
            Rect rect = new Rect();
            mTextPaint.getTextBounds(texts.get(i), 0, texts.get(i).length(), rect);
            mBounds.add(rect);
        }
    }

    /**
     * 重写测量方式
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height;  //自定义view总高度
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = mMarginTop + 2 * mCircleRadius + mBounds.get(0).height(); //总高度
            //高度
            Log.i("wangjitao:", "mMarginTop:" + mMarginTop + ",mCircleRadius:" + mCircleRadius + ",mBounds:"
                    + mBounds.get(0).height() + ",height" + height);
        }
        //保存测量结果
        setMeasuredDimension(widthSize, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //计算线段整条线段长度(总控件宽度 - Padding - 最左边和最右边的两个圆的直径)
        mLineLength = getWidth() - getPaddingLeft() - getPaddingRight() - mCircleRadius * 2;
    }

    /**
     * 绘制view
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {  //文字在上面
        mLinePaint.setColor(mColorCircleDefault); //新加
        int startTextY = mBounds.get(0).height(); //文字高度
        //绘制线条
        canvas.drawLine(mCircleRadius, mCircleRadius + startTextY + mMarginTop, getWidth() - mCircleRadius, mCircleRadius + startTextY + mMarginTop, mLinePaint);

        //开是循环绘制view
        for (int i = 0; i < texts.size(); i++) {
            mTextPaint.setColor(mColorCircleDefault);
            if (mSelectPosition >= i) {
                //绘制选中的圆圈
                canvas.drawCircle(mCircleRadius + ((mLineLength / (texts.size() - 1)) * i), mCircleRadius + startTextY + mMarginTop, mCircleRadius, mCircleSelectPaint);
                mTextPaint.setColor(mColorCircleSelect);

                Log.v("选择是那个", mSelectPosition + "     " + i);
                //画出线选择颜色
                if (mSelectPosition == i) { //新加
                    mLinePaint.setColor(mColorCircleSelect);
                    canvas.drawLine(mCircleRadius, mCircleRadius + startTextY + mMarginTop, mCircleRadius + ((mLineLength / (texts.size() - 1)) * i), mCircleRadius + startTextY + mMarginTop, mLinePaint);
                }

            } else {
                //绘制默中的圆圈
                canvas.drawCircle(mCircleRadius + ((mLineLength / (texts.size() - 1)) * i), mCircleRadius + startTextY + mMarginTop, mCircleRadius, mCirclePaint);
            }

            //记录画圆的位置
            circleLoaction.add(new Float[]{mCircleRadius + ((mLineLength / (texts.size() - 1)) * i), Float.valueOf((mCircleRadius + startTextY + mMarginTop))});

            //绘制文字
            Log.i("wangjitao", "现在：" + startTextY);
            if (i == 0) {
                canvas.drawText(texts.get(i), 0, startTextY, mTextPaint);
            } else if (i == texts.size() - 1) {
                canvas.drawText(texts.get(i), getWidth() - mBounds.get(i).width(), startTextY, mTextPaint);
            } else {
                canvas.drawText(texts.get(i), mCircleRadius + ((mLineLength / (texts.size() - 1)) * i) - (mBounds.get(i).width() / 2), startTextY, mTextPaint);
            }
        }


    }
  /*  @Override
    protected void onDraw(Canvas canvas) {  //文字在下面
        //绘制线条
        canvas.drawLine(mCircleRadius, mCircleRadius, getWidth() - mCircleRadius, mCircleRadius, mLinePaint);

        //开是循环绘制view
        for (int i = 0; i < texts.size(); i++) {
            mTextPaint.setColor(mColorCircleDefault);
            if (mSelectPosition == i) {
                //绘制选中的圆圈
                canvas.drawCircle(mCircleRadius + ((mLineLength / (texts.size() - 1)) * i), mCircleRadius, mCircleRadius, mCircleSelectPaint);
                mTextPaint.setColor(mColorCircleSelect);
            } else {
                //绘制默中的圆圈
                canvas.drawCircle(mCircleRadius + ((mLineLength / (texts.size() - 1)) * i), mCircleRadius, mCircleRadius, mCirclePaint);
            }
            //绘制文字
            //这里要对基线进行理解
            int startTextY = mCircleRadius * 2 + mMarginTop + getPaddingTop(); //以前
            Log.i("wangjitao", "以前：" + startTextY);
            //现在是这样的，首先获取基线对象
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            startTextY = getHeight() - (int) fontMetrics.bottom;
            Log.i("wangjitao", "现在：" + startTextY);
            if (i == 0) {
                canvas.drawText(texts.get(i), 0, startTextY, mTextPaint);
            } else if (i == texts.size() - 1) {
                canvas.drawText(texts.get(i), getWidth() - mBounds.get(i).width(), startTextY, mTextPaint);
            } else {

                canvas.drawText(texts.get(i), mCircleRadius + ((mLineLength / (texts.size() - 1)) * i) - (mBounds.get(i).width() / 2), startTextY, mTextPaint);
            }
        }
    }*/

    private float downX;
    private float upX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            //按下手指的时候记录下按下的位置
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                Log.e("位置按下", "手指按下:  getX:" + downX);
                Log.e("位置按下", "手指按下:  getY:" + event.getY());
                clickCircle(event.getX(), event.getY());

                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("位置移动", "手指滑动: ");
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                Log.e("位置抬起", "手指抬起: " + upX);
                if (downX - upX > 50) {
                    downX = 0;
                    upX = 0;
                    //向左滑动
                    //判断做滑动的时候当前选择点时候在在初始状态下
                    if (mSelectPosition != 0) {
                        //更新view
                        mSelectPosition--;
                    }
                    invalidate();
                } else if (upX - downX > 50) {
                    //向右滑动
                    downX = 0;
                    upX = 0;
                    //判断做滑动的时候当前选择点时候在最后一个点上
                    if (mSelectPosition != texts.size() - 1) {
                        //更新view
                        mSelectPosition++;
                    }
                    invalidate();
                } else {
                    downX = 0;
                    upX = 0;
                }
                break;
        }
        return true;
    }


    private void clickCircle(float x, float y) {
        for (int i = 0; i < circleLoaction.size(); i++) {
            Float[] floats = circleLoaction.get(i);
            //判断  手指点的是否在园内
            if (floats[0] + 80 > x && floats[0] - 80 <= x && floats[1] + 80 > y && floats[1] - 80 <= y) {
                Log.v("位置按下", "是否在园内" + i);
                mSelectPosition = i;
                invalidate();
                break;
            }

        }
    }


}
