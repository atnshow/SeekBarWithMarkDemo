package com.ec.android.demo.seekbarwithmarkdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ec.android.view.seekbarwithmark.SeekBarWithMark;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //XML实现
        SeekBarWithMark xmlSeekBar = (SeekBarWithMark) findViewById(R.id.xmlSeekBar);

        xmlSeekBar.setOnSelectItemListener(new SeekBarWithMark.OnSelectItemListener() {
            @Override
            public void selectItem(int nowSelectItemNum, String val) {
                Toast.makeText(getApplicationContext(), nowSelectItemNum + "---" + val, Toast.LENGTH_SHORT).show();
                Log.i(TAG, nowSelectItemNum + "---" + val);
            }
        });
        //
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.contentPanel);
        //代码实现
        SeekBarWithMark seekBarWithMark = new SeekBarWithMark.Builder(this).setMarkItemNum(4).setmMarkDescArray(new String[]{"100", "200", "500", "800"}).setIsShowPoint(false).setLayoutParams(new ViewGroup.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260, getResources().getDisplayMetrics()), ViewGroup.LayoutParams.WRAP_CONTENT)).setMarkTextSize(13).create();
//        seekBarForWater.setLayoutParams(new ViewGroup.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260, getResources().getDisplayMetrics()), ViewGroup.LayoutParams.WRAP_CONTENT));
        viewGroup.addView(seekBarWithMark);
        //
        seekBarWithMark.setOnSelectItemListener(new SeekBarWithMark.OnSelectItemListener() {
            @Override
            public void selectItem(int nowSelectItemNum, String val) {
                Toast.makeText(getApplicationContext(), nowSelectItemNum + "---" + val, Toast.LENGTH_SHORT).show();
                Log.i(TAG, nowSelectItemNum + "---" + val);
            }
        });
    }
}
