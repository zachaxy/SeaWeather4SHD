package com.friendlyarm.serial.seaweather4shd.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map.Entry;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.friendlyarm.serial.seaweather4shd.Locater;
import com.friendlyarm.serial.seaweather4shd.R;
import com.friendlyarm.serial.seaweather4shd.Typhoon;
import com.friendlyarm.serial.seaweather4shd.Weather;
import com.friendlyarm.serial.seaweather4shd.tools.Param;
import com.friendlyarm.serial.seaweather4shd.tools.Tools;

/**
 * Created by zhangxin on 2016/3/28.
 */
public class ZoomImageView extends ImageView implements
        ViewTreeObserver.OnGlobalLayoutListener,
        ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
    private boolean mOnce = false;

    // 初始缩放值(正好适合屏幕的缩放值),双击放大达到的值,放大的最大值
    private float mInitScale;
    private float mMidScale;
    private float mMaxScale;
    private ScaleGestureDetector mScaleGestureDetector;

    private Matrix mScaleMatrix;

    // ---------------------------
    // 手指数量的检测,一旦发生改变,那么中心点的坐标看会发生改变,需要记录上次中心点的坐标
    private int mLastPointCount;
    private float mLastX;
    private float mLastY;

    private float mTouchSlop;

    private boolean isCanDrag;

    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    // --------------双击放大缩小---------------------
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;

    public Weather weather;
    public Typhoon typhoon;

    public Locater currentLocation = new Locater(0, 0);

    public static Context zoomContext;

    //点击时弹出详细信息的窗口
    PopupWindow popupWindow;
    //弹出popupwindow的view
    View detailContent;

    private final Bitmap currentIndicator;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // init();
        zoomContext = context;
        Bitmap bitmap = readBitMap(getContext(), R.drawable.t3);

        paint = new Paint();
        // path = new Path();

        this.setImageBitmap(bitmap);

        mScaleMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);

        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        detailContent = inflate(context, R.layout.detail_content_popup_window, null);
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (isAutoScale) {
                            return true;
                        }
                        float x = e.getX();
                        float y = e.getY();

                        // 需要注意的问题是:简单这样直接放大或者缩小的话,它的动画效果是瞬间变化的,这样的效果用户很难接受,所以用一个内部类解决
                        if (getScale() < mMidScale) {
                            // 双击一次,放大为midScale
                            /*
                             * mScaleMatrix.postScale(mMidScale / getScale(),
							 * mMidScale / getScale(), x, y);
							 * setImageMatrix(mScaleMatrix);
							 */
                            postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
                            isAutoScale = true;
                        } else {
                            /*
							 * mScaleMatrix.postScale(mInitScale / getScale(),
							 * mInitScale / getScale(), x, y);
							 * setImageMatrix(mScaleMatrix);
							 */
                            postDelayed(
                                    new AutoScaleRunnable(mInitScale, x, y), 16);
                            isAutoScale = true;
                        }
                        return true;
                    }

                    //单击确定,用来实现点击指定区域,显示对应区域坐标;
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        boolean inFlag = false;
                        float downX = e.getX();
                        float downY = e.getY();
                        Locater lo = getOrignalLocation(downX, downY);

                        //遍历18个区域,看是落在哪个区域中;
                        for (int i = 0; i < Param.seaAreas2.length; i++) {
                            if (Tools.pInQuadrangle(Param.seaAreas2[i],lo)){
                                dismissPopupWindow(); //可能存在的情况是:我之前点击了1,正在显示,现在我又点击了2,那么我就让之前的消失掉;
                                TextView area = (TextView) detailContent.findViewById(R.id.detail_popup_tv_area);
                                area.setText("第"+i+"海区");
                                TextView detal = (TextView) detailContent.findViewById(R.id.detail_popup_tv_content_24);
                                detal.setText(Param.weaherDetail[i]);
                                popupWindow = new PopupWindow(detailContent, -2, -2);
                                //需要注意的是:使用popupwindow,必须设置背景,不然动画效果不能展示
                                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                //传入一个长度为2的数组,将返回该view距离屏幕x,y的距离
                                //int[] location = new int[2];
                                //view.getLocationInWindow(location);
                                popupWindow.showAtLocation(ZoomImageView.this, Gravity.LEFT + Gravity.TOP, (int) e.getX(), (int) e.getY());

                                ScaleAnimation animation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                animation.setDuration(300);
                                detailContent.startAnimation(animation);
                                inFlag = true;
                                break;
                            }
                        }

                        if (!inFlag){ //如果是其他区域,那么也将这个windown取消掉;
                            dismissPopupWindow();
                        }
                        return super.onSingleTapConfirmed(e);
                    }
                });

        currentIndicator = BitmapFactory.decodeResource(
                context.getResources(),R.drawable.location);
    }

    private class AutoScaleRunnable implements Runnable {

        // 缩放的目标值
        private float mTargetScale;
        // 缩放的中心点
        private float x, y;

        private final float BIGGER = 1.07f;
        private final float SMALLER = 0.97f;

        private float tmpScale;

        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;

            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            }
            if (getScale() > mTargetScale) {
                tmpScale = SMALLER;
            }
        }

        @Override
        public void run() {
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            float currentScale = getScale();

            if ((tmpScale > 1.0f && currentScale < mTargetScale)
                    || (tmpScale < 1.0f && currentScale > mTargetScale)) {
                // 这里其实并不是一点一点的放大,而是设置了一个时间间隔,等待到达要求后才瞬间放大
                postDelayed(this, 16);
            } else {
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        Log.d("#######", "onGlobalLayout");

        if (!mOnce) {
            // 得到控件的宽和高
            int width = getWidth();
            int height = getHeight();

            // 得到图片以宽高
            Drawable d = getDrawable();
            if (d == null) {
                return;
            }

            // 实际图片和宽高
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();

            // 先设置个默认的缩放值
            float scale = 1.0f;

            // 如果加载进来的图片比较宽且比较低
            if (dw > width && dh < height) {
                scale = width * 1.0f / dw;
            }

            // 如果加载进来的图片比较高且比较窄
            if (dh > height && dw < width) {
                scale = height * 1.0f / dh;
            }

            // 如果图片的宽和高均大于控件的大小,将其缩小
            if (dh > height && dw > width) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }

            // 如果图片的宽和高均小于控件的大小,将其放大
            if (dh < height && dw < width) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }

            mInitScale = scale;
            mMaxScale = mInitScale * 4;
            mMidScale = mInitScale * 2;

            // 将图片移动到屏幕中心
            int dx = getWidth() / 2 - dw / 2;
            int dy = getHeight() / 2 - dh / 2;

            mScaleMatrix.postTranslate(dx, dy);
            mScaleMatrix.postScale(mInitScale, mInitScale, width / 2,
                    height / 2);
            setImageMatrix(mScaleMatrix);
            mOnce = true;

        }
    }

    /***
     * 获取当前的缩放比例,并与手指触控索要的缩放比例进行对比,注意我们的最大缩放尺度是mMaxScale
     */
    public float getScale() {
        float[] value = new float[9];
        mScaleMatrix.getValues(value);
        return value[Matrix.MSCALE_X];
    }

    /*
     * --------------------------------------------------------------------------
     * ---------------- 与手势相关的操作 注意缩放的区间是init~max
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        // 得到缩放值;
        float scaleFactor = detector.getScaleFactor();
        float scale = getScale();
        if (getDrawable() == null) {
            return true;
        }

        if ((scale < mMaxScale && scaleFactor > 1.0)
                || (scale > mInitScale && scaleFactor < 1.0)) {
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale;
            }
            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }
            // 四个参数:
            // 前两个表示的是横向和纵向的缩放比例,我们需要保存原始图片的宽高比,所以横向和纵向的缩放比是一样的
            // 后两个参数是缩放的中心,我们将其设为手指所触的中心点
            // 但是问题是如果将图片放大,那么图片会铺满整个屏幕,然而在某一点一直缩小,那么最终图片的位置的中心点则不是原来的屏幕的中心点了
            // 而是手势所触的中心点
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());

            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    /***
     * 在缩放的时候检测边界和位置的控制 因此需要获取放大的图片的坐标等消息
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        // 拿到控件的宽高
        int width = getWidth();
        int height = getHeight();

        // 前提是:只有在当前图像是放大的,因为缩小的情况下有间隔是正常的

        // 实际的宽度比控件的宽度大
        if (rect.width() >= width) {
            // 实际的左边与控件的左边有空隙,则向左移动
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            // 实际的右边与控件的右边有空隙,则向右移动
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }

        if (rect.height() >= height) {
            // 实际的上边与控件的上边有空隙,向上移动
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            // 实际的下边与控件的下边有空隙,向下移动
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }

        // 如果是缩小的情况下,那么让其居中显示
        if (rect.width() < width) {
            deltaX = width / 2f - rect.right + rect.width() / 2f;
        }
        if (rect.height() < height) {
            deltaY = height / 2f - rect.bottom + rect.height() / 2f;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /***
     * 获得图片放大缩小以后的宽和高,以及left,right,top,bottom等信息.
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();

        Drawable d = getDrawable();
        if (d != null) {
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    /*
     * --------------------------------------------------------------------------
     * ---------------- 与触控相关的操作
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        // 将双击的动作传递给mGestureDetector,接着马上返回,避免与后面的操作冲突;
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        // 使得mScaleGestureDetector可以拿到触控的缩放比例
        mScaleGestureDetector.onTouchEvent(event);

        // ------------------------------------------------------
        // 这段之间的代码是检测手指触摸到屏幕上的那一时刻的中心坐标,还并未涉及到移动
        // 用来保存当前的坐标中心点
        float x = 0;
        float y = 0;
        int pointCount = event.getPointerCount();
        for (int i = 0; i < pointCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }

        x /= pointCount;
        y /= pointCount;

        if (mLastPointCount != pointCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointCount = pointCount;
        // -----------------------------开始对具体的动作进行检测------------------------------------------------
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 注意:当手指不停在屏幕上滑动时,其实是有顿挫感的,每顿挫一次就会触发一次onTouch的方法,因此该x值的实时改变的.
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }

                if (isCanDrag) {
                    RectF rectF = getMatrixRectF();
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = true;
                        isCheckTopAndBottom = true;

                        // 如果宽高小于控件的宽高,不允许移动
                        if (rectF.width() < getWidth()) {
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        if (rectF.height() < getHeight()) {
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }

                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorderAndCenterWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointCount = 0;
                break;
            default:
                break;

        }
        return true;
    }

    /***
     * 当移动时,进行边界检查
     */
    private void checkBorderAndCenterWhenTranslate() {
        RectF rectf = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectf.top > 0 && isCheckTopAndBottom) {
            deltaY = -rectf.top;
        }

        if (rectf.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rectf.bottom;
        }

        if (rectf.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectf.left;
        }
        if (rectf.right < width && isCheckLeftAndRight) {
            deltaX = width - rectf.right;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /***
     * 判断偏移的距离是否足矣触发移动图片
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {

        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }

    // -------添加覆盖Test-------------------------

    // private Canvas canvas;
    private Paint paint;

    // private Path path;
    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("#######", "onDraw");
        super.onDraw(canvas);

        // path.reset();
        for (int i = 0; i < Param.typhoonPaths.length; i++) {
            Param.typhoonPaths[i].reset();
        }

        float currentScale;
        RectF rect = getMatrixRectF();
        Log.d("$$$$$$$", "rect" + rect.contains(0f, 0f));
        Log.d("$$$$$$$", "rect" + rect.toString());
        Log.d("$$$$$$$", "Center:" + rect.centerX() + " " + rect.centerY());
        // Log.d("$$$$$$$", "rect"+ rect.);

        // 去除锯齿
        paint.setAntiAlias(true);
        // paint.setColor(Color.RED);
        // paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
        currentScale = (rect.right - rect.left) / getWidth();

		/*
		 * canvas.drawCircle(rect.centerX() + 100 * currentScale,
		 * rect.centerY(), 5, paint);
		 */
		/*
		 * if (weather != null) { //int wType =
		 * Param.weatherTypeMap[weather.weatherType]; for (int i = 1; i <
		 * weather.area.size(); i++) { if (weather.area.get(i).equals('1')) {
		 * canvas.drawBitmap(weather.bitmap, rect.centerX() +
		 * Param.seaAreas[i].x * currentScale, rect.centerY() +
		 * +Param.seaAreas[i].y currentScale, paint); } } }
		 */

        //这里是画gps当前位置;
        canvas.drawBitmap(currentIndicator,
                rect.centerX() + currentLocation.x * currentScale,
                rect.centerY() + currentLocation.y * currentScale,
                paint);

        if (Param.bitmaps != null) {
            //fixed: 之前这里是硬编码,写的是 i<=19;之前确实有19个海区,现在是18个海区,所以别硬编码了;
            for (int i = 1; i < Param.seaAreas.length; i++) {
                canvas.drawBitmap(Param.seaAreasWeatherType[i], rect.centerX()
                        + Param.seaAreas[i].x * currentScale, rect.centerY()
                        + Param.seaAreas[i].y * currentScale, paint);
            }
        }
        // 表明此时有台风
        if (!Param.IsTyphonClear) {

            if (Param.typhoonMap.size() > 0) {
                Log.d("###", "台风表不为空");
                int k = 0; // 表示为第几个台风
                for (Entry<Integer, ArrayList<Locater>> entry : Param.typhoonMap
                        .entrySet()) {

                    int typhooNo = entry.getKey();
                    Log.d("###", "onDraw: 当前的台风号" + typhooNo + "  k=" + k);
                    ArrayList<Locater> list = entry.getValue();
                    //目前颜色库中只支持5中台风.
                    paint.setColor(Param.colors[k % 5]);
                    paint.setStyle(Paint.Style.FILL);

                    for (int i = 0; i < list.size(); i++) {
                        float x = rect.centerX() + list.get(i).x * currentScale;
                        float y = rect.centerY() + list.get(i).y * currentScale;
                        // 先绘制该条台风轨迹中的点
                        canvas.drawCircle(x, y, 5, paint);
                        if (list.size() == 1) {
                            //当前列表只有一个元素时,只画点
                            Log.d("###", "onDraw: 当前只有1个点---" + k);
                            paint.setTextSize(15);
                            paint.setShader(null);
                            canvas.drawText("第" + typhooNo + "号台风", x, y, paint);
                        } else {
                            if (i == 0) {
                                // 第一个点设置轨迹的起始位置,并添加台风号码说明.
                                paint.setTextSize(15);
                                paint.setShader(null);
                                canvas.drawText("第" + typhooNo + "号台风", x, y, paint);
                                Param.typhoonPaths[k].moveTo(x, y);
                            } else if (i == list.size() - 1) {
                                Param.typhoonPaths[k].lineTo(x, y);
                                Param.typhoonPaths[k].setLastPoint(x, y);
                                paint.setStyle(Paint.Style.STROKE);
                                Log.d("###", "最后drawPath绘制线条--->" + k);
                                canvas.drawPath(Param.typhoonPaths[k], paint);
                            } else {
                                Param.typhoonPaths[k].lineTo(x, y);
                            }
                        }
                    }
                    k++;
                }
				/*
				 * paint.setStyle(Paint.Style.STROKE);
				 * paint.setColor(Color.RED); canvas.drawPath(path, paint);
				 */
            }
        }

        // canvas.drawBitmap(bitmap, matrix, paint)
		/*
		 * paint.setStyle(Paint.Style.STROKE); Path path = new Path();
		 * path.moveTo(rect.centerX(), rect.centerY());
		 * path.lineTo(rect.centerX() + 100 * currentScale, rect.centerY());
		 * paint.setColor(Color.BLUE); canvas.drawPath(path, paint);
		 * 
		 * paint.setTextSize(20); paint.setShader(null);
		 * paint.setColor(Color.RED); canvas.drawText("中心点", rect.centerX(),
		 * rect.centerY(), paint); canvas.drawText("相对点", rect.centerX() + 100 *
		 * currentScale, rect.centerY(), paint);
		 */

    }

    public static Bitmap readBitMap(Context context, int resId) {

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);

    }


    //根据缩放按下的坐标,还原会为缩放时的坐标;
    public Locater getOrignalLocation(float x, float y) {
        RectF matrixRectF = getMatrixRectF();
        float currentScale = (matrixRectF.right - matrixRectF.left) / getWidth();
        int x0 = (int) ((x - matrixRectF.centerX()) / currentScale + Param.ACTUAL_IMAGE_SIZE / 2);
        int y0 = (int) ((y - matrixRectF.centerY()) / currentScale + Param.ACTUAL_IMAGE_SIZE / 2);
        return new Locater(x0, y0);
    }

    //取消popupwindow的显示,适用场景在放大或者移动的过程中.
    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    // 在解析台风轨迹的时候使用该函数
    // TODO: 2016/11/24 修改坐标
    /*public static Locater transferLocate(Locater l) {
        Locater l2 = new Locater();
        l2.x = (int) (l.x / 1579.0 * 444.0 - 222.0);
        l2.y = (int) (l.y / 1579.0 * 444.0 - 222.0);
        return l2;
    }*/


    //没使用这个方法,一调用崩了...初始化的时机不对;
    private void initBitmaps(Context context) {
        Param.bitmaps[0] = BitmapFactory.decodeResource(context.getResources(),
                Param.weatherTypeMap[0]);
        Param.bitmaps[1] = BitmapFactory.decodeResource(context.getResources(),
                Param.weatherTypeMap[1]);
        Param.bitmaps[2] = BitmapFactory.decodeResource(context.getResources(),
                Param.weatherTypeMap[2]);
        Param.bitmaps[3] = BitmapFactory.decodeResource(context.getResources(),
                Param.weatherTypeMap[3]);
        Param.bitmaps[4] = BitmapFactory.decodeResource(context.getResources(),
                Param.weatherTypeMap[4]);
        Param.bitmaps[5] = BitmapFactory.decodeResource(context.getResources(),
                Param.weatherTypeMap[5]);
        Param.bitmaps[6] = BitmapFactory.decodeResource(context.getResources(),
                Param.weatherTypeMap[6]);
        Param.bitmaps[7] = BitmapFactory.decodeResource(context.getResources(),
                Param.weatherTypeMap[7]);
        Param.bitmaps[8] = BitmapFactory.decodeResource(context.getResources(),
                Param.weatherTypeMap[8]);
        Param.bitmaps[9] = BitmapFactory.decodeResource(context.getResources(),
                Param.weatherTypeMap[9]);
        Param.bitmaps[10] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[10]);
        Param.bitmaps[11] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[11]);
        Param.bitmaps[12] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[12]);
        Param.bitmaps[13] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[13]);
        Param.bitmaps[14] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[14]);
        Param.bitmaps[15] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[15]);
        Param.bitmaps[16] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[16]);
        Param.bitmaps[17] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[17]);
        Param.bitmaps[18] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[18]);
        Param.bitmaps[19] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[19]);
        Param.bitmaps[20] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[20]);
        Param.bitmaps[21] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[21]);
        Param.bitmaps[22] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[22]);
        Param.bitmaps[23] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[23]);
        Param.bitmaps[24] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[24]);
        Param.bitmaps[25] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[25]);
        Param.bitmaps[26] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[26]);
        Param.bitmaps[27] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[27]);
        Param.bitmaps[28] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[28]);
        Param.bitmaps[29] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[29]);
        Param.bitmaps[30] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[30]);
        Param.bitmaps[31] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[31]);
        Param.bitmaps[32] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[32]);
        Param.bitmaps[33] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[33]);
        Param.bitmaps[34] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[0]);
        Param.bitmaps[35] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[0]);
        Param.bitmaps[36] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[0]);
        Param.bitmaps[37] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[0]);
        Param.bitmaps[38] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[0]);
        Param.bitmaps[39] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[0]);
        Param.bitmaps[40] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[0]);
        Param.bitmaps[41] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[0]);
        Param.bitmaps[42] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[0]);
        Param.bitmaps[43] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[43]);
        Param.bitmaps[44] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[44]);
        Param.bitmaps[45] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[45]);
        Param.bitmaps[46] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[46]);
        Param.bitmaps[47] = BitmapFactory.decodeResource(
                context.getResources(), Param.weatherTypeMap[47]);

    }

}
