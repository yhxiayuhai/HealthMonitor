package com.example.user.HealthMonitor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by user on 2016/11/29.
 */
public class Histogram extends View{
    private Paint xLinePaint;//坐标轴 轴线 画笔
    private Paint hLinePaint;//坐标轴水平内部 虚线画笔
    private Paint titlePaint;//绘制文本的画笔
    private Paint paint;//矩形画笔 柱状图的样式信息
    private int[] progress;//7条矩形
    private int[] aniProgress;//实现动画的值
    private final int TRUE = 1;//在柱状图上显示数字
    private int[] text;//是否显示相应条形图的数字
    private String[] ySteps;//坐标轴左侧的数标
    private String[] xWeeks;//坐标轴底部的星期数

    private HistogramAnimation ani;

    public Histogram(Context context){
        super(context);
        init(context, null);
    }

    public Histogram(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }
    private void init(Context context, AttributeSet attrs){
        ySteps = new String[]{"8k","6k","4k","2k","0"};
        xWeeks = new String[]{"周一","周二","周三","周四","周五","周六","周日"};
        text = new int[]{0, 0, 0, 0, 0, 0, 0};
        aniProgress = new int[]{0, 0, 0, 0, 0, 0, 0};
        ani = new HistogramAnimation();
        ani.setDuration(1000);

        xLinePaint = new Paint();
        hLinePaint = new Paint();
        titlePaint = new Paint();
        paint = new Paint();

        xLinePaint.setColor(Color.DKGRAY);
        hLinePaint.setColor(Color.LTGRAY);
        titlePaint.setColor(Color.BLACK);
    }

    public void setText(int[] text){
        this.text = text;
        this.postInvalidate();//可以子线程 更新视图的方法
    }

    public void setProgress(int[] progress){
        this.progress = progress;
        this.startAnimation(ani);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return super.onTouchEvent(event);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight() - 50;

        //绘制坐标线
        int startX = dip2px(getContext(), 50);
        int startY = dip2px(getContext(), 10);
        int stopX = dip2px(getContext(), 50);
        int stopY = dip2px(getContext(), 320);
        canvas.drawLine(50, 10, 50, height, xLinePaint);
        canvas.drawLine(50, height, width - 10, height, xLinePaint);

        //绘制坐标内部的水平线
        int leftHeight = height - 20;//左侧外周的，需要划分的区域
        int hPerHeight = leftHeight / 4;//分成四部分
        hLinePaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < 4; i++) {
            canvas.drawLine(50, 20 + i * hPerHeight, width - 10, 20 + i * hPerHeight, hLinePaint);
        }

        //绘制Y轴坐标
        titlePaint.setTextAlign(Paint.Align.RIGHT);
        titlePaint.setTextSize(20);
        titlePaint.setAntiAlias(true);
        titlePaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < ySteps.length; i++) {
            canvas.drawText(ySteps[i], 40, 20 + i * hPerHeight, titlePaint);
        }

        //绘制X轴坐标
        int xAxisLength = width - 30;
        int columCount = xWeeks.length + 1;
        int step = xAxisLength / columCount;
        for (int i = 0; i < columCount - 1; i++) {
            canvas.drawText(xWeeks[i], 55 + step * (i + 1), height + 30, titlePaint);
        }

        //绘制矩形
        if (aniProgress != null && aniProgress.length > 0) {
            for (int i = 0; i < aniProgress.length; i++) {//循环遍历将7条柱状图形画出来
                int value = aniProgress[i];
                paint.setAntiAlias(true);//抗锯齿效果
                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(20);
                paint.setColor(Color.parseColor("#6DCAEC"));//字体颜色
                Rect rect = new Rect();//柱状图的形状
                rect.left = 30 + step * (i + 1) - 30;
                rect.right = 30 + step * (i + 1) + 30;
                int rh = (int) (leftHeight - leftHeight * (value / 8000.0));
                rect.top = rh + 20;
                rect.bottom = height;

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.column);
                canvas.drawBitmap(bitmap, null, rect, paint);

                if (this.text[i] == TRUE) {
                    canvas.drawText(value + "", 30 + step * (i + 1) - 30, rh + 10, paint);
                }
            }
        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private class HistogramAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                for (int i = 0; i < aniProgress.length; i++) {
                    aniProgress[i] = (int) (progress[i] * interpolatedTime);
                }
            } else {
                for (int i = 0; i < aniProgress.length; i++) {
                    aniProgress[i] = progress[i];
                }
            }
            postInvalidate();
        }
    }
}
