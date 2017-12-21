package com.spark.myapplication.entity;

import android.graphics.Color;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KData {

    private List<String> xLabels = new ArrayList<>();
    private List<Integer> colors = new ArrayList<Integer>(){{add(Color.parseColor("#AE4E54"));add(Color.parseColor("#589065"));}};
    private List<CandleEntry> candleEntries = new ArrayList<>();
    private List<Entry> m5Entries = new ArrayList<>();
    private List<Entry> m10Entries = new ArrayList<>();
    private List<Entry> m30Entries = new ArrayList<>();

    private List<BarEntry> barEntries = new ArrayList<>();
    private List<Integer> barColors = new ArrayList<Integer>() {{
        add(Color.parseColor("#AE4E54"));
    }};

    public KData(JSONObject obj) {
        try {
            parseJson(obj);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    private void parseJson(JSONObject obj) throws JSONException {
        JSONArray array = obj.getJSONObject("data").getJSONObject("sz002081").getJSONArray("day");
        for (int i = 0, len = array.length(); i < len; i++) {
            JSONArray data = array.getJSONArray(i);
            xLabels.add(data.getString(0));
            candleEntries.add(new CandleEntry(i, (float) data.getDouble(3), (float) data.getDouble(4), (float) data.getDouble(1), (float) data.getDouble(2)));
            barEntries.add(new BarEntry((float) data.getDouble(5), i));
            if (i > 0 && data.getDouble(2) - data.getDouble(1) > 0) barColors.add(Color.parseColor("#88DB1529"));
            else barColors.add(Color.parseColor("#8811B136"));
            if (i >= 4) m5Entries.add(new Entry(getAvg(array, 5, i - 4, i), i));
            if (i >= 9) m10Entries.add(new Entry(getAvg(array, 10, i - 9, i), i));
            if (i >= 29) m30Entries.add(new Entry(getAvg(array, 30, i - 29, i), i));
        }
    }

    public List<BarEntry> getBarEntries() {
        return barEntries;
    }


    public List<Integer> getBarColors() {
        return barColors;
    }


    private float getAvg(JSONArray array, int n, int start, int end) throws JSONException {
        float sum = 0;
        for (int i = start; i <= end; i++) {
            JSONArray data = array.getJSONArray(i);
            sum += (float) data.getDouble(2);
        }
        return sum / n;
    }

    public List<String> getxLabels() {
        return xLabels;
    }

    public List<Integer> getColors() {
        return colors;
    }


    public List<CandleEntry> getCandleEntries() {
        return candleEntries;
    }


    public List<Entry> getM5Entries() {
        return m5Entries;
    }


    public List<Entry> getM10Entries() {
        return m10Entries;
    }


    public List<Entry> getM30Entries() {
        return m30Entries;
    }

}
