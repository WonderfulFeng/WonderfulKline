package com.spark.myapplication.activity.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.spark.myapplication.R;
import com.spark.myapplication.activity.kline.KlineActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.cvCandleStickChart)
    CardView cvCandleStickChart;
    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        setViews();
    }

    private void setViews() {
        cvCandleStickChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KlineActivity.actionStart(MainActivity.this);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
