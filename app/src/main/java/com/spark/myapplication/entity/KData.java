package com.spark.myapplication.entity;

import android.graphics.Color;

import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/20.
 */

public class KData {
    private List<String> xLabels = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private List<CandleEntry> candleEntries = new ArrayList<>();
    private List<Entry> m5Entries = new ArrayList<>();
    private List<Entry> m10Entries = new ArrayList<>();
    private List<Entry> m30Entries = new ArrayList<>();

    public KData(JSONObject obj) {
        try {
            parseJson(obj);
            colors.add(Color.parseColor("#AE4E54"));
            colors.add(Color.parseColor("#589065"));
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
            if (i > 4) {
                m5Entries.add(new Entry(getAvg(data, 5, i - 4, i), i));
            }
            if (i >= 9) {
                m10Entries.add(new Entry(getAvg(data, 10, i - 9, i), i));
            }
            if (i >= 29) {
                m10Entries.add(new Entry(getAvg(data, 30, i - 29, i), i));
            }

        }
    }

    private float getAvg(JSONArray data, int n, int start, int end) throws JSONException {
        float sum = 0;
        for (int i = start; i < end; i++) sum += (float) data.getDouble(2);
        return sum / n;
    }

    public List<String> getxLabels() {
        return xLabels;
    }

    public void setxLabels(List<String> xLabels) {
        this.xLabels = xLabels;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public List<CandleEntry> getCandleEntries() {
        return candleEntries;
    }

    public void setCandleEntries(List<CandleEntry> candleEntries) {
        this.candleEntries = candleEntries;
    }

    public List<Entry> getM5Entries() {
        return m5Entries;
    }

    public void setM5Entries(List<Entry> m5Entries) {
        this.m5Entries = m5Entries;
    }

    public List<Entry> getM10Entries() {
        return m10Entries;
    }

    public void setM10Entries(List<Entry> m10Entries) {
        this.m10Entries = m10Entries;
    }

    public List<Entry> getM30Entries() {
        return m30Entries;
    }

    public void setM30Entries(List<Entry> m30Entries) {
        this.m30Entries = m30Entries;
    }
}
