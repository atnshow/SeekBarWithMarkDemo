package com.ec.android.view.seekbarwithmark;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * 下方带有刻度的SeekBar
 *
 * @author EC
 */
public class SeekBarWithMark extends LinearLayout {
    private static final String TAG = SeekBarWithMark.class.getSimpleName();

    private static final boolean DEBUG = false;
    //
    private SeekBar mSeekBar;
    //装载刻度框的Layout
    private LinearLayout mBottomLL;
    //根据 mMarkItemNum 来选择，整除(mMarkItemNum - 1)最好
    private int MAX = 80;
    //刻度的数目，默认9
    private int mMarkItemNum = 9;
    //相邻刻度的间距
    private int mSpacing = MAX / (mMarkItemNum - 1);
    //当前选中的刻度
    private int nowMarkItem;
    //刻度的下标名称数组
    private String[] mMarkDescArray = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8"};
    //
    private OnSelectItemListener mSelectItemListener;
    //是否第一次渲染
    private boolean isFirstInflate = true;
    //是否显示刻度指示点
    private boolean isShowPoint = true;
    //SeekBar的ProgressDrawable
    private Drawable mProgressDrawable;
    //SeekBar的Thumb
    private Drawable mThumbDrawable;
    //刻度的文字大小
    private float mMarkTextSize = 13;

    private SeekBarWithMark(Context context) {
        super(context);
    }

    public SeekBarWithMark(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarWithMark(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarWithMark);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            //
            if (attr == R.styleable.SeekBarWithMark_sbw_markItemNum) {
                this.mMarkItemNum = a.getInteger(attr, 9);
                this.MAX = (mMarkItemNum - 1) * 10;
                this.mSpacing = MAX / (mMarkItemNum - 1);
            } else if (attr == R.styleable.SeekBarWithMark_sbw_markDescArray) {
                String content = a.getString(attr);
                if (!TextUtils.isEmpty(content)) {
                    //以|作为分隔符
                    String[] strings = content.split("\\|");
                    this.mMarkDescArray = strings;
                }
            } else if (attr == R.styleable.SeekBarWithMark_sbw_isShowPoint) {
                this.isShowPoint = a.getBoolean(attr, false);
            } else if (attr == R.styleable.SeekBarWithMark_sbw_progressDrawabler) {
                Drawable drawable = a.getDrawable(attr);
                if (drawable != null) {
                    this.mProgressDrawable = a.getDrawable(attr);
                }
            } else if (attr == R.styleable.SeekBarWithMark_sbw_thumbDrawable) {
                Drawable drawable = a.getDrawable(attr);
                if (drawable != null) {
                    this.mThumbDrawable = drawable;
                }
            } else if (attr == R.styleable.SeekBarWithMark_sbw_markTextSize) {
                this.mMarkTextSize = ScreenUtils.pxToDp(getContext(), a.getDimensionPixelSize(attr, 13));
            }
        }

        a.recycle();
        //
        init();
    }

    public SeekBarWithMark(Context mContext, Builder builder) {
        this(mContext);
        //
        this.mMarkItemNum = builder.mMarkItemNum;
        this.MAX = (mMarkItemNum - 1) * 10;
        this.mSpacing = MAX / (mMarkItemNum - 1);
        this.mMarkDescArray = builder.mMarkDescArray;
        this.isShowPoint = builder.isShowPoint;
        this.mProgressDrawable = builder.mProgressDrawable;
        this.mThumbDrawable = builder.mThumbDrawable;
        this.mMarkTextSize = builder.mMarkTextSize;
        //
        if (builder.mLayoutParams != null) {
            this.setLayoutParams(builder.mLayoutParams);
        }
        ////
        init();
    }

    private void init() {
        //
        if (this.mMarkDescArray.length < this.mMarkItemNum) {
            throw new RuntimeException("刻度的下标的名称数组length不能小于刻度的数目");
        }
        //
        this.setOrientation(LinearLayout.VERTICAL);
        this.setGravity(Gravity.CENTER);
        //
        this.mSeekBar = (SeekBar) View.inflate(getContext(), R.layout.seekbarwithmark, null);
        this.mSeekBar.setMax(MAX);
        //设置ProgressDrawable
        if (mProgressDrawable != null) {
            this.mSeekBar.setProgressDrawable(mProgressDrawable);
        }
        //设置Thumb
        if (mThumbDrawable != null) {
            this.mSeekBar.setThumb(mThumbDrawable);
        }
        //
        LayoutParams mSeekBarLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.mSeekBar.setLayoutParams(mSeekBarLp);
        //
        this.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private int shouldInProgress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //
                nowMarkItem = Math.round(progress / mSpacing);

                shouldInProgress = mSpacing * nowMarkItem;
                //
                SeekBarWithMark.this.mSeekBar.setProgress(shouldInProgress);
                //
                if (DEBUG) {
                    Log.e(TAG, "progress---" + progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (DEBUG) {
                    Log.e(TAG, "shouldInProgress---" + shouldInProgress);
                }
                //
                if (mSelectItemListener != null) {
                    mSelectItemListener.selectItem(nowMarkItem, mMarkDescArray[nowMarkItem]);
                }
            }
        });
        ////
        mBottomLL = new LinearLayout(getContext());
        mBottomLL.setOrientation(LinearLayout.HORIZONTAL);

        LayoutParams mBottomLLLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mBottomLLLp.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()), 0, 0);

        mBottomLL.setLayoutParams(mBottomLLLp);
        //设置和mSeekBar的padding值一致
        mBottomLL.setPadding(this.mSeekBar.getPaddingLeft(), 0, this.mSeekBar.getPaddingRight(), 0);
        //
        this.addView(this.mSeekBar);
        this.addView(mBottomLL);
        //
        addAllMarkItem();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //
        if (isFirstInflate) {
            isFirstInflate = false;
            //
            ViewGroup bottomLL = (ViewGroup) this.getChildAt(1);

            View view = bottomLL.getChildAt(0);
            //
            int bottomLLWidth = bottomLL.getWidth();
            int width = view.getWidth();
            //
            int i = bottomLLWidth - width;

            ViewGroup.LayoutParams layoutParams = this.mSeekBar.getLayoutParams();

            layoutParams.width = i;
            this.mSeekBar.setLayoutParams(layoutParams);
        }
    }

    /**
     * 添加 刻度
     */
    private void addAllMarkItem() {
        if (mBottomLL == null) {
            throw new RuntimeException("装载刻度框的Layout不能为null");
        }
        //
        mBottomLL.removeAllViews();
        //
        Drawable drawablePoint = null;
        //
        TextView textView = null;
        LayoutParams tvLp = null;
        for (int i = 0; i < mMarkItemNum; i++) {
            textView = new TextView(getContext());
            //这个width只能设为0dp，可不能设为 LayoutParams.WRAP_CONTENT ，否则它就会使得textView不都是一般大了，会影响刻度精准
            tvLp = new LayoutParams(0, LayoutParams.WRAP_CONTENT);

            tvLp.weight = 1;
            tvLp.gravity = Gravity.CENTER;
            //
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(tvLp);
            //
            if (DEBUG) {
                if (i == 0 || i == 2 || i == 4) {
                    textView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                }
            }
            //
            textView.setTextSize(mMarkTextSize);
            textView.setText(mMarkDescArray[i]);

            if (isShowPoint) {
                if (drawablePoint == null) {
                    drawablePoint = getContext().getResources().getDrawable(R.drawable.point_min_gray);
                }
                textView.setCompoundDrawablesWithIntrinsicBounds(null, drawablePoint, null, null);
                textView.setCompoundDrawablePadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics()));
            }
            //
            mBottomLL.addView(textView);
        }

    }

    public SeekBar getSeekBar() {
        return mSeekBar;
    }

    /**
     * 设置监听选中刻度
     *
     * @param l
     */
    public void setOnSelectItemListener(OnSelectItemListener l) {
        this.mSelectItemListener = l;
    }

    public interface OnSelectItemListener {
        void selectItem(int nowSelectItemNum, String val);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        mSeekBar.setEnabled(enabled);
    }

    /**
     * 设置选中
     *
     * @param selectItemNum 0代表第一个刻度，1代表第二个刻度，以此类推
     */
    public void selectMarkItem(int selectItemNum) {
        //设置当前选中
        nowMarkItem = selectItemNum;
        //
        int shouldInProgress = mSpacing * selectItemNum;
        //
        mSeekBar.setProgress(shouldInProgress);
    }

    /**
     * 当前选中刻度
     *
     * @return
     */
    public int getNowMarkItem() {
        return nowMarkItem;
    }

    /**
     * 代码构建SeekBar，请使用Builder构建
     */
    public static class Builder {
        private final Context mContext;
        //
        //刻度的数目，默认9
        private int mMarkItemNum = 9;
        //刻度的下标名称数组
        private String[] mMarkDescArray;
        //是否显示刻度指示点
        private boolean isShowPoint = true;
        //SeekBar大小
        private ViewGroup.LayoutParams mLayoutParams;
        //SeekBar的ProgressDrawable
        private Drawable mProgressDrawable;
        //SeekBar的Thumb
        private Drawable mThumbDrawable;
        //刻度的文字大小，默认13sp，注意单位为sp
        public float mMarkTextSize = 13;

        public Builder(Context context) {
            this.mContext = context;
        }

        public int getMarkItemNum() {
            return mMarkItemNum;
        }

        public Builder setMarkItemNum(int mMarkItemNum) {
            this.mMarkItemNum = mMarkItemNum;
            return this;
        }

        public String[] getmMarkDescArray() {
            return mMarkDescArray;
        }

        public Builder setmMarkDescArray(String[] mMarkDescArray) {
            this.mMarkDescArray = mMarkDescArray;
            return this;
        }

        public boolean isShowPoint() {
            return isShowPoint;
        }

        public Builder setIsShowPoint(boolean isShowPoint) {
            this.isShowPoint = isShowPoint;
            return this;
        }

        public ViewGroup.LayoutParams getLayoutParams() {
            return mLayoutParams;
        }

        public Builder setLayoutParams(ViewGroup.LayoutParams mLayoutParams) {
            this.mLayoutParams = mLayoutParams;
            return this;
        }

        public Drawable getThumbDrawable() {
            return mThumbDrawable;
        }

        public Builder setThumbDrawable(Drawable mThumbDrawable) {
            this.mThumbDrawable = mThumbDrawable;
            return this;
        }

        public Drawable getProgressDrawable() {
            return mProgressDrawable;
        }

        public Builder setProgressDrawable(Drawable mProgressDrawable) {
            this.mProgressDrawable = mProgressDrawable;
            return this;
        }

        public float getMarkTextSize() {
            return mMarkTextSize;
        }

        public Builder setMarkTextSize(float mMarkTextSize) {
            this.mMarkTextSize = mMarkTextSize;
            return this;
        }

        public SeekBarWithMark create() {
            SeekBarWithMark seekBarWithMark = new SeekBarWithMark(mContext, this);

            return seekBarWithMark;
        }

    }

}
