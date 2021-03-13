package com.noahliu.realtimechartdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private boolean isRunning = false;
    private LineChart chart;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart = findViewById(R.id.lineChart);
        /**載入圖表*/
        initChart();

        Button btStop,btStart,btReset;
        btStart = findViewById(R.id.button_RunData);
        btStop = findViewById(R.id.button_Stop);
        btReset = findViewById(R.id.button_Reset);
        /**開始跑圖表*/
        btStart.setOnClickListener(v->{
            startRun();
        });
        /**停止跑圖表*/
        btStop.setOnClickListener(v->{
            isRunning = false;
        });
        /**重置圖表*/
        btReset.setOnClickListener(v->{
            chart.clear();
            initChart();
        });
    }
    /**開始跑圖表*/
    private void startRun(){
        if (isRunning)return;
        if (thread != null) thread.interrupt();
//            Runnable runnable = new Runnable() {@Override public void run() {}};
        //簡略寫法
        isRunning = true;
        Runnable runnable  = ()->{
            //取亂數
            addData((int)(Math.random()*(60-40+1))+40);
        };
//            thread = new Thread(new Runnable()
//            {@Override public void run() {runOnUiThread(runnable);}});
        //簡略寫法
        thread =  new Thread(()->{
            while (isRunning) {
                runOnUiThread(runnable);
                if (!isRunning)break;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    /**載入圖表*/
    private void initChart(){
        chart.getDescription().setEnabled(false);//設置不要圖表標籤
        chart.setTouchEnabled(false);//設置不可觸碰
        chart.setDragEnabled(false);//設置不可互動
        //設置單一線數據
        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
        chart.setData(data);
        //設置左下角標籤
        Legend l =  chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        //設置Ｘ軸
        XAxis x =  chart.getXAxis();
        x.setTextColor(Color.BLACK);
        x.setDrawGridLines(true);//畫X軸線
        x.setPosition(XAxis.XAxisPosition.BOTTOM);//把標籤放底部
        x.setLabelCount(5,true);//設置顯示5個標籤
        //設置X軸標籤內容物
        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "No. "+Math.round(value);
            }
        });
        //
        YAxis y = chart.getAxisLeft();
        y.setTextColor(Color.BLACK);
        y.setDrawGridLines(true);
        y.setAxisMaximum(100);//最高100
        y.setAxisMinimum(0);//最低0
        chart.getAxisRight().setEnabled(false);//右邊Y軸不可視
        chart.setVisibleXRange(0,50);//設置顯示範圍
    }
    /**新增資料*/
    private void addData(float inputData){
        LineData data =  chart.getData();//取得原數據
        ILineDataSet set = data.getDataSetByIndex(0);//取得曲線(因為只有一條，故為0，若有多條則需指定)
        if (set == null){
            set = createSet();
            data.addDataSet(set);//如果是第一次跑則需要載入數據
        }
        data.addEntry(new Entry(set.getEntryCount(),inputData),0);//新增數據點
        //
        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.setVisibleXRange(0,50);//設置可見範圍
        chart.moveViewToX(data.getEntryCount());//將可視焦點放在最新一個數據，使圖表可移動
    }
    /**設置數據線的樣式*/
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "隨機數據");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.GRAY);
        set.setLineWidth(2);
        set.setDrawCircles(false);
        set.setFillColor(Color.RED);
        set.setFillAlpha(50);
        set.setDrawFilled(true);
        set.setValueTextColor(Color.BLACK);
        set.setDrawValues(false);
        return set;
    }
}