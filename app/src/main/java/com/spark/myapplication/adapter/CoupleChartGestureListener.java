package com.spark.myapplication.adapter;

import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.List;

/**
 * Created by Administrator on 2017/12/21.
 */

public class CoupleChartGestureListener implements OnChartGestureListener {
    private Chart srcChart;
    private List<Chart> charts;

    public CoupleChartGestureListener(Chart srcChart, List<Chart> charts) {
        this.srcChart = srcChart;
        this.charts = charts;
    }

    private void syncCharts() {
        Matrix srcMatrix = null;
        float[] srcVals = new float[9];
        Matrix targetMatrix = null;
        float[] targetVals = new float[9];
        srcMatrix = srcChart.getViewPortHandler().getMatrixTouch();
        srcMatrix.getValues(srcVals);
        for (Chart chart : charts) {
            if (chart.getVisibility() != View.VISIBLE) return;
            targetMatrix = chart.getViewPortHandler().getMatrixTouch();
//            targetMatrix.getValues(targetVals);
//            targetVals[Matrix.MSCALE_X] = srcVals[Matrix.MSCALE_X];
//            targetVals[Matrix.MSKEW_X] = srcVals[Matrix.MSKEW_X];
//            targetVals[Matrix.MTRANS_X] = srcVals[Matrix.MTRANS_X];
//            targetVals[Matrix.MSKEW_Y] = srcVals[Matrix.MSKEW_Y];
//            targetVals[Matrix.MSCALE_Y] = srcVals[Matrix.MSCALE_Y];
//            targetVals[Matrix.MTRANS_Y] = srcVals[Matrix.MTRANS_Y];
//            targetVals[Matrix.MPERSP_0] = srcVals[Matrix.MPERSP_0];
//            targetVals[Matrix.MPERSP_1] = srcVals[Matrix.MPERSP_1];
//            targetVals[Matrix.MPERSP_2] = srcVals[Matrix.MPERSP_2];
//            targetMatrix.setValues(targetVals);
            targetMatrix.setValues(srcVals);
            chart.getViewPortHandler().refresh(targetMatrix, chart, true);
        }
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        syncCharts();
    }


    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        syncCharts();
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        syncCharts();
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        syncCharts();
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        syncCharts();
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        syncCharts();
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        syncCharts();
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        syncCharts();
    }
}
