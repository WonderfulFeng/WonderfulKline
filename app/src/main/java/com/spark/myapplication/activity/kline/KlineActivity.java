package com.spark.myapplication.activity.kline;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.spark.myapplication.R;
import com.spark.myapplication.adapter.CoupleChartGestureListener;
import com.spark.myapplication.data.LocalData;
import com.spark.myapplication.entity.KData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class KlineActivity extends AppCompatActivity {

    @BindView(R.id.combinedChart)
    CombinedChart combinedChart;
    @BindView(R.id.barChart)
    BarChart barChart;
    Unbinder unbinder;
    private KData kData;

    private CombinedData combinedData;
    private XAxis xAxis;
    private YAxis leftY;
    private YAxis rightY;

    private BarData barData;
    private XAxis barXAxis;
    private YAxis barleftY;
    private YAxis barrightY;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, KlineActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline);
        unbinder = ButterKnife.bind(this);
        obtainData();
        initCombinedChart();
        initBarChart();
        setListeners();
        invalidate();
        setOffset();
    }

    private void setListeners() {
        combinedChart.setOnChartGestureListener(new CoupleChartGestureListener(combinedChart, new ArrayList<Chart>() {{
            add(barChart);
        }}));
        barChart.setOnChartGestureListener(new CoupleChartGestureListener(barChart, new ArrayList<Chart>() {{
            add(combinedChart);
        }}));
        combinedChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                Log.e("TAG", "触发了");
                barChart.highlightValues(new Highlight[]{h});
            }

            @Override
            public void onNothingSelected() {
                barChart.highlightValues(null);
            }
        });
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                combinedChart.highlightValues(new Highlight[]{h});
            }

            @Override
            public void onNothingSelected() {
                combinedChart.highlightValues(null);
            }
        });

    }

    private void invalidate() {
        barChart.notifyDataSetChanged();
        combinedChart.notifyDataSetChanged();
        barChart.invalidate();
        combinedChart.invalidate();
    }

    private void initBarChart() {
        initBarChartStyle();
        initBarChartXAxis();
        initBarChartYAxis();
        initBarChartLegend();
        initBarChartDataSet();
        ViewPortHandler viewPortHandler = barChart.getViewPortHandler();
        viewPortHandler.setMaximumScaleX(kData.getCandleEntries().size() / 5);
        barChart.moveViewToX(kData.getCandleEntries().size() - 1);
        Matrix matrixCombin = viewPortHandler.getMatrixTouch();
        final float xscaleCombin = 5;
        matrixCombin.postScale(xscaleCombin, 1f);
        barChart.setData(barData);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                barChart.setAutoScaleMinMaxEnabled(true);
            }
        });
    }

    private void initBarChartDataSet() {
        BarDataSet barDataSet = new BarDataSet(kData.getBarEntries(), "成交量");
        barDataSet.setHighlightEnabled(true);
        barDataSet.setHighLightColor(Color.parseColor("#9194A3"));
        barDataSet.setDrawValues(false);
        barDataSet.setHighLightAlpha(255);
        barDataSet.setColors(kData.getBarColors());
        barDataSet.setBarSpacePercent(0);
        barDataSet.setBarBorderWidth(0.1f);
        barDataSet.setBarBorderColor(Color.parseColor("#9194A3"));
        barData = new BarData(kData.getxLabels(), barDataSet);
    }

    private void initBarChartLegend() {
        Legend legend = barChart.getLegend();
        legend.setEnabled(false);//决定显不显示标签
    }

    private void initBarChartYAxis() {
        barleftY = barChart.getAxisLeft();
        barleftY.setEnabled(true);//设置是否可用
        barleftY.setAxisMinValue(0f);
        barleftY.setLabelCount(4, false);//设置y轴显示数据的近似数目
        barleftY.setDrawGridLines(true);//是否绘制 垂直y轴的表格线
        barleftY.setGridColor(Color.parseColor("#1F2943"));//设置表格线的颜色
        barleftY.setDrawAxisLine(false);//是否绘制轴线
        barleftY.setTextColor(Color.parseColor("#ffffff"));//社会自Y轴数据的颜色
        barleftY.setTextSize(8f);//设置文字大小
        barleftY.setValueFormatter(new LargeValueFormatter());

        barrightY = barChart.getAxisRight();
        barrightY.setEnabled(true);
        barrightY.setAxisMinValue(0f);
        barrightY.setLabelCount(4, false);//设置y轴显示数据的近似数目
        barrightY.setDrawGridLines(true);//是否绘制 垂直y轴的表格线
        barrightY.setGridColor(Color.parseColor("#1F2943"));//设置表格线的颜色
        barrightY.setDrawAxisLine(false);//是否绘制轴线
        barrightY.setTextColor(Color.parseColor("#ffffff"));//社会自Y轴数据的颜色
        barrightY.setTextSize(8f);//设置文字大小
        barrightY.setValueFormatter(new LargeValueFormatter());
    }

    private void initBarChartXAxis() {
        barXAxis = barChart.getXAxis();
        barXAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //X轴数据显示的位置
        barXAxis.setDrawGridLines(true); // 是否绘制垂直X轴的表格线
        barXAxis.setGridColor(Color.parseColor("#1F2943")); // 设置表格线的颜色
        barXAxis.setDrawAxisLine(true);//是否绘制X轴线
        barXAxis.setTextColor(Color.parseColor("#ffffff"));//设置文字颜色
        barXAxis.setTextSize(8f);
    }

    private void initBarChartStyle() {
        barChart.setBackgroundColor(Color.parseColor("#181B2A"));
        barChart.setNoDataTextDescription("暂无数据"); //无数据显示的提示
        barChart.setDescription("");
        barChart.setDrawBorders(true);
        barChart.setBorderWidth(1);
        barChart.setBorderColor(Color.parseColor("#61688A"));
        combinedChart.setTouchEnabled(true);//是否响应触摸事件
        barChart.setDragEnabled(true);
        barChart.setScaleYEnabled(false);
        barChart.setScaleXEnabled(true);//设置X轴方向是否可以缩放
        barChart.setDragDecelerationEnabled(false);
        barChart.setDragDecelerationFrictionCoef(0.2f);
    }

    private void initCombinedChart() {
        combinedData = new CombinedData(kData.getxLabels());
        initCombinedChartStyle();
        initCombinedChartXAxis();
        initCombinedChartYAxis();
        initCombinedChartLegend();
        initCombinedChartLineDataset();
        initCombinedChartDataSet();
        combinedChart.setData(combinedData);
        ViewPortHandler viewPortHandler = combinedChart.getViewPortHandler();
        viewPortHandler.setMaximumScaleX(kData.getCandleEntries().size() / 5);
        combinedChart.moveViewToX(kData.getCandleEntries().size() - 1);
        Matrix matrixCombin = viewPortHandler.getMatrixTouch();
        final float xscaleCombin = 5;
        matrixCombin.postScale(xscaleCombin, 1f);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                combinedChart.setAutoScaleMinMaxEnabled(true);
            }
        });
    }

    private void initCombinedChartLineDataset() {
        final LineDataSet linedataset1 = initLineDataSet(kData.getM5Entries(), "MA5", Color.parseColor("#5C3F7D"));
        final LineDataSet linedataset2 = initLineDataSet(kData.getM10Entries(), "MA10", Color.parseColor("#37445B"));
        final LineDataSet linedataset3 = initLineDataSet(kData.getM30Entries(), "MA30", Color.parseColor("#386D48"));
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>() {{
            add(linedataset1);
            add(linedataset2);
            add(linedataset3);
        }};
        LineData data = new LineData(kData.getxLabels(), dataSets);
        combinedData.setData(data);
    }

    private LineDataSet initLineDataSet(List<Entry> entries, String lable, int color) {

        LineDataSet lineDataSet = new LineDataSet(entries, lable);
        lineDataSet.setColor(color);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setHighLightColor(Color.parseColor("#9194A3"));
        lineDataSet.setHighlightLineWidth(0.5f);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return lineDataSet;
    }

    private void initCombinedChartLegend() {
        Legend legend = combinedChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        legend.setForm(Legend.LegendForm.SQUARE);// 样式
        legend.setFormSize(6f);// 字号
        legend.setTextColor(Color.WHITE);// 颜色
        legend.setTextSize(8f);
        legend.setEnabled(false);//决定显不显示标签

    }

    private void initCombinedChartStyle() {
        combinedChart.setBackgroundColor(Color.parseColor("#181B2A"));
        combinedChart.setNoDataTextDescription("暂无数据"); //无数据显示的提示
        combinedChart.setDescription("");
        combinedChart.setDrawBorders(false);//是否绘制边界线
        combinedChart.setTouchEnabled(true);//是否响应触摸事件
        combinedChart.setDragEnabled(true);// 是否可以拖拽
        combinedChart.setScaleEnabled(true);// 是否可以缩放
        combinedChart.setScaleYEnabled(false);//设置Y方向是否可以缩放
        combinedChart.setScaleXEnabled(true);//设置X轴方向是否可以缩放
        combinedChart.setDragDecelerationEnabled(false);
        combinedChart.setDragDecelerationFrictionCoef(0.2f);
    }

    private void initCombinedChartDataSet() {
        CandleDataSet candleDataSet = new CandleDataSet(kData.getCandleEntries(), "红涨/绿跌");
        candleDataSet.setIncreasingColor(kData.getColors().get(0));//增长的颜色
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);//增长矩形的样式
        candleDataSet.setDecreasingColor(kData.getColors().get(1));//下跌的颜色
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);//下跌矩形的样式
        candleDataSet.setNeutralColor(kData.getColors().get(0));//当天价格不涨不跌（一字线）颜色
        candleDataSet.setShadowColorSameAsCandle(true);//设置投影线颜色与对应矩形颜色相同
        candleDataSet.setShadowWidth(0.7f);//设置投影线的宽度
        candleDataSet.setHighlightEnabled(true); //设置是否可以高亮显示
        candleDataSet.setHighLightColor(Color.parseColor("#9194A3")); // 设置高亮线的颜色
        candleDataSet.setHighlightLineWidth(0.5f);//设置高亮线的宽度
        candleDataSet.setDrawValues(false);
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        CandleData data = new CandleData(kData.getxLabels(), candleDataSet);
        combinedData.setData(data);
    }

    private void initCombinedChartXAxis() {
        xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //X轴数据显示的位置
        xAxis.setDrawGridLines(true); // 是否绘制垂直X轴的表格线
        xAxis.setGridColor(Color.parseColor("#1F2943")); // 设置表格线的颜色
        xAxis.setDrawAxisLine(true);//是否绘制X轴线
        xAxis.setTextColor(Color.parseColor("#ffffff"));//设置文字颜色
        xAxis.setTextSize(8f);
    }

    private void initCombinedChartYAxis() {
        leftY = combinedChart.getAxisLeft();
        leftY.setEnabled(true);//设置是否可用
        leftY.setLabelCount(6, false);//设置y轴显示数据的近似数目
        leftY.setDrawGridLines(true);//是否绘制 垂直y轴的表格线
        leftY.setGridColor(Color.parseColor("#1F2943"));//设置表格线的颜色
        leftY.setDrawAxisLine(true);//是否绘制轴线
        leftY.setTextColor(Color.parseColor("#ffffff"));//社会自Y轴数据的颜色
        leftY.setTextSize(8f);//设置文字大小
        rightY = combinedChart.getAxisRight();
        rightY.setEnabled(false);
    }

    private void obtainData() {
        JSONObject obj = null;
        try {
            obj = new JSONObject(LocalData.KLINEURL);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        kData = new KData(obj);
    }

    private void setOffset() {
        float lineLeft = combinedChart.getViewPortHandler().offsetLeft();
        float barLeft = barChart.getViewPortHandler().offsetLeft();
        float lineRight = combinedChart.getViewPortHandler().offsetRight();
        float barRight = barChart.getViewPortHandler().offsetRight();
        float barBottom = barChart.getViewPortHandler().offsetBottom();
        float offsetLeft, offsetRight;
        float transLeft = 0, transRight = 0;
        if (barLeft < lineLeft) {
            transLeft = lineLeft;
        } else {
            offsetLeft = Utils.convertPixelsToDp(barLeft - lineLeft);
            combinedChart.setExtraLeftOffset(offsetLeft);
            transLeft = barLeft;
        }
        if (barRight < lineRight) {
            transRight = lineRight;
        } else {
            offsetRight = Utils.convertPixelsToDp(barRight);
            combinedChart.setExtraRightOffset(offsetRight);
            transRight = barRight;
        }
        barChart.setViewPortOffsets(transLeft, 15, transRight, barBottom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
