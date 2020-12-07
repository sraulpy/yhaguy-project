package com.yhaguy.gestion.rrhh;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.chart.AxisTitle;
import org.zkoss.chart.Charts;
import org.zkoss.chart.PaneBackground;
import org.zkoss.chart.Point;
import org.zkoss.chart.RadialGradient;
import org.zkoss.chart.Series;
import org.zkoss.chart.YAxis;
import org.zkoss.chart.plotOptions.GaugeDialPlotOptions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ClockComposer extends SelectorComposer<Window> {
    @Wire
    Charts chart;

    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        List<PaneBackground> backgrounds = new LinkedList<PaneBackground>();
        // Default background
        backgrounds.add(new PaneBackground());

        PaneBackground background = new PaneBackground();
        RadialGradient radialGradient = new RadialGradient(0.5, -0.4, 1.9);
        radialGradient.addStop(0.5, "rgba(255, 255, 255, 0.2)");
        radialGradient.addStop(0.5, "rgba(200, 200, 200, 0.2)");
        background.setBackgroundColor(radialGradient);
        backgrounds.add(background);

        chart.getPane().setBackground(backgrounds);

        // The value axis
        YAxis yAxis = chart.getYAxis();
        yAxis.getLabels().setDistance(-20);
        yAxis.setMin(0);
        yAxis.setMax(12);
        yAxis.setLineWidth(0);
        yAxis.setShowFirstLabel(false);

        yAxis.setMinorTickInterval("auto");
        yAxis.setMinorTickWidth(1);
        yAxis.setMinorTickLength(5);
        yAxis.setMinorTickPosition("inside");
        yAxis.setGridLineWidth(0);
        yAxis.setMinorTickColor("#666");

        yAxis.setTickInterval(1);
        yAxis.setTickWidth(2);
        yAxis.setTickPosition("inside");
        yAxis.setTickLength(10);
        yAxis.setTickColor("#666");
        AxisTitle title = yAxis.getTitle();
        title.setText("");
        title.setStyle("color: '#BBB'; fontWeight: 'normal'; fontSize: '8px'; lineHeight: '10px'");
        title.setY(10);

        chart.getTooltip().setFormat("{series.chart.tooltipText}");

        Series series = chart.getSeries();
        Double[] values = currentDateValue();
        Point hour = new Point("hour", values[0]);
        GaugeDialPlotOptions dial1 = hour.getDial();
        dial1.setRadius("60%");
        dial1.setBaseWidth(4);
        dial1.setBaseLength("95%");
        dial1.setRearLength("0");
        series.addPoint(hour);
        Point minute = new Point("minute", values[1]);
        GaugeDialPlotOptions dial2 = minute.getDial();
        dial2.setBaseLength("95%");
        dial2.setRearLength("0");
        series.addPoint(minute);
        Point second = new Point("second", values[2]);
        GaugeDialPlotOptions dial3 = second.getDial();
        dial3.setRadius("100%");
        dial3.setBaseWidth(1);
        dial3.setRearLength("20%");
        series.addPoint(second);

        series.getPlotOptions().setAnimation(false);
        series.getDataLabels().setEnabled(false);
    }

    // Move
    @Listen("onTimer = #timer")
    public void updateData() {
        Double[] value = currentDateValue();
        for (int i = 0; i < value.length; i ++) {
            chart.getSeries().getPoint(i).update(value[i]);
        }
    }
    
    private Double[] currentDateValue() {
        double hours = TimeUtil.getHour(),
                minutes = TimeUtil.getMinute(),
                seconds = TimeUtil.getSecond();
        Double[] result = new Double[3];
        result[0] = hours + minutes / 60;
        result[1] = minutes * 12 / 60 + seconds * 12 / 3600;
        result[2] = seconds * 12 / 60;
        return result;
    }
}