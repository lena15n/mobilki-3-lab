package com.lena.timetracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShowChartActivity extends AppCompatActivity {
    PieChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chart);

        savedInstanceState = getIntent().getExtras();
        String jsonData = savedInstanceState.getString(getString(R.string.data_for_chart));
        String descriptionForChart = savedInstanceState.getString(getString(R.string.desc_for_chart));

        mChart = (PieChart) findViewById(R.id.chart);
        mChart.setDescription(descriptionForChart);
        mChart.setRotationEnabled(true);
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (e == null)
                    return;

                String[] temp = ShowRecordActivity.timePeriodToString((long)e.getVal()).split(":");
                Toast.makeText(getApplicationContext(),
                        "Days: " + temp[0] + ", hours: " + temp[1] + ", minutes: " + temp[2],
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });

        setDataForPieChart(decodeData(jsonData), descriptionForChart);
    }

    private LinkedHashMap<String, Long> decodeData(String json) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<LinkedHashMap<String, Long>>() {}.getType();
        return gson.fromJson(json, collectionType);
    }

    public void setDataForPieChart(LinkedHashMap<String, Long> categories, String descriptionForChart) {
        ArrayList<Entry> yValues = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Long> category : categories.entrySet()) {
            yValues.add(new Entry(category.getValue(), i));
            i++;
        }

        ArrayList<String> xValues = new ArrayList<>();
        i = 0;
        for (Map.Entry<String, Long> category : categories.entrySet()) {
            xValues.add(category.getKey());
            i++;
        }
        // create pieDataSet
        PieDataSet dataSet = new PieDataSet(yValues, descriptionForChart);//""
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(10);//5
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        PieData data = new PieData(xValues, dataSet);
        data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(13f);
        data.setValueTextColor(R.color.colorOfLabels);

        mChart.setData(data);
        // undo all highlights
        mChart.highlightValues(null);
        mChart.invalidate();
        mChart.animateXY(800, 800);
        // Legends to show on bottom of the graph
        Legend legend = mChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setXEntrySpace(7);
        legend.setYEntrySpace(5);
    }

    public class MyValueFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return "";//empty for values (would be very big labels)
        }
    }
}
