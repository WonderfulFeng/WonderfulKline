package com.spark.myapplication.activity.kline;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.spark.myapplication.R;
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

    private XAxis xAxis;
    private YAxis leftY;
    private YAxis rightY;

    private CombinedData combinedData;

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
        initChart();
    }

    private void initChart() {
        combinedData = new CombinedData(kData.getxLabels());
        initChartStyle();
        initXAxis();
        initYAxis();
        initLegend();
        initDataSet();
        initLineDataset();
        combinedChart.setData(combinedData);
        combinedChart.animateX(1500);
    }

    private void initLineDataset() {
        final LineDataSet linedataset1 = initLineDataSet(kData.getM5Entries(), "MA5", Color.parseColor("#5C3F7D"));
        final LineDataSet linedataset2 = initLineDataSet(kData.getM10Entries(), "MA10", Color.parseColor("#37445B"));
        final LineDataSet linedataset3 = initLineDataSet(kData.getM30Entries(), "MA30", Color.parseColor("#386D48"));
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>(){{add(linedataset1);add(linedataset2);add(linedataset3);}};
        LineData data = new LineData(kData.getxLabels(),dataSets);
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
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return lineDataSet;
    }

    private void initLegend() {
        Legend legend = combinedChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        legend.setForm(Legend.LegendForm.SQUARE);// 样式
        legend.setFormSize(6f);// 字号
        legend.setTextColor(Color.WHITE);// 颜色
        legend.setTextSize(8f);
        List<String> labels = new ArrayList<>();
        labels.add("红涨");
        labels.add("绿跌");
        List<Integer> colors = new ArrayList<>();
        colors.add(kData.getColors().get(0));
        colors.add(kData.getColors().get(1));
        legend.setExtra(colors, labels);//设置标注的颜色及内容，设置的效果如下图
        legend.setEnabled(true);//决定显不显示标签

    }

    private void initChartStyle() {
        combinedChart.setBackgroundColor(Color.parseColor("#181B2A"));
        combinedChart.setNoDataTextDescription("暂无数据"); //无数据显示的提示
        combinedChart.setDrawBorders(false);//是否绘制边界线
        combinedChart.setTouchEnabled(true);//是否响应触摸事件
        combinedChart.setDragEnabled(true);// 是否可以拖拽
        combinedChart.setScaleEnabled(true);// 是否可以缩放
        combinedChart.setScaleYEnabled(false);//设置Y方向是否可以缩放
        combinedChart.setScaleXEnabled(true);//设置X轴方向是否可以缩放
    }

    private void initDataSet() {
        CandleDataSet candleDataSet = new CandleDataSet(kData.getCandleEntries(), "数据");
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
        CandleData data = new CandleData(kData.getxLabels(), candleDataSet);
        combinedData.setData(data);
    }

    private void initXAxis() {
        xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //X轴数据显示的位置
        xAxis.setDrawGridLines(true); // 是否绘制垂直X轴的表格线
        xAxis.setGridColor(Color.parseColor("#1F2943")); // 设置表格线的颜色
        xAxis.setDrawAxisLine(true);//是否绘制X轴线
        xAxis.setTextColor(Color.parseColor("#ffffff"));//设置文字颜色
        xAxis.setTextSize(8f);
    }

    private void initYAxis() {
        leftY = combinedChart.getAxisLeft();
        leftY.setEnabled(true);//设置是否可用
        leftY.setLabelCount(10, false);//设置y轴显示数据的近似数目
        leftY.setDrawGridLines(true);//是否绘制 垂直y轴的表格线
        leftY.setGridColor(Color.parseColor("#1F2943"));//设置表格线的颜色
        leftY.setDrawAxisLine(true);//是否绘制轴线
        leftY.setTextColor(Color.parseColor("#ffffff"));//社会自Y轴数据的颜色
        leftY.setTextSize(6f);//设置文字大小
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
