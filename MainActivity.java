package com.example.lab2;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.BarGraphSeries;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static int n = 10;
    private static int N = 256;
    private static double W = 1500;
    private double[] signal = new double[N];
    private double realKoef;
    private double realKoef1;
    private double realKoef2;
    private double notRealKoef;
    private double notRealKoef1;
    private double notRealKoef2;
    private Double[][] cosMemo = new Double[N][N];
    private Double[][] sinMemo = new Double[N][N];
    private double timeDFT;
    private double timeFFT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LineGraphSeries<DataPoint> line1 = new LineGraphSeries<>(generateRandomSignal(signal));

        LineGraphSeries<DataPoint> line2 = new LineGraphSeries<>(calculateFourie());
        line2.setColor(Color.GREEN);

        LineGraphSeries<DataPoint> line3 = new LineGraphSeries<>(calculateFastFourie());
        line3.setColor(Color.RED);

        BarGraphSeries<DataPoint> line4 =
                new BarGraphSeries<>(new DataPoint[] {new DataPoint(10, timeDFT)});
        line4.setColor(Color.GREEN);

        BarGraphSeries<DataPoint> line5 =
                new BarGraphSeries<>(new DataPoint[] {new DataPoint(20, timeFFT)});
        line5.setColor(Color.RED);

        GraphView graph = findViewById(R.id.graph1);
        setUpGraph(graph, line1, -5, 6);

        graph = findViewById(R.id.graph2);
        setUpGraph(graph, line2, 0, 80);

        graph = findViewById(R.id.graph3);
        setUpGraph(graph, line3, 0, 80);

        graph = findViewById(R.id.graph4);
        setUpTimeGraph(graph, line4, line5,0, (int)(timeDFT * 1.5));
    }

    private DataPoint[] generateRandomSignal(double[] res) {
        double phi;
        double A;
        double x;
        DataPoint[] points = new DataPoint[N];
        Random rnd = new Random();
        for (int i = 0; i < N; i++) {
            phi = rnd.nextDouble();
            A = rnd.nextDouble();
            x = 0;
            for (int j = 0; j < n; j++) {
                x += A * Math.sin(W / (j + 1) * i + phi);
            }
            res[i] = x;
            points[i] = new DataPoint(i, x);
        }
        return points;
    }

    private void setUpGraph(GraphView graph, LineGraphSeries line, int miny, int maxy) {
        graph.getViewport().setScrollable(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(30);
        graph.getViewport().setMaxY(maxy);
        graph.getViewport().setMinY(miny);
        graph.addSeries(line);
    }

    private void setUpTimeGraph(GraphView graph, BarGraphSeries line, BarGraphSeries line2, int miny, int maxy) {
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(30);
        graph.getViewport().setMaxY(maxy);
        graph.getViewport().setMinY(miny);
        graph.addSeries(line);
        graph.addSeries(line2);
    }

    private DataPoint[] calculateFourie() {
        long start = System.nanoTime();
        DataPoint[] points = new DataPoint[N - 1];
        for (int i = 0; i < N - 1; i++) {
            realKoef = 0;
            notRealKoef = 0;
            for (int j = 0; j < N - 1; j++) {
                if (cosMemo[i][j] == null) {
                    cosMemo[i][j] = Math.cos(2 * Math.PI * i * j / N);
                }
                if (sinMemo[i][j] == null) {
                    sinMemo[i][j] = Math.sin(2 * Math.PI * i * j / N);
                }
                realKoef += signal[j] * cosMemo[i][j];
                notRealKoef -= signal[j] * sinMemo[i][j];
            }
            points[i] = new DataPoint(i, Math.sqrt(Math.pow(realKoef, 2) + Math.pow(notRealKoef, 2)));
        }
        long end = System.nanoTime();
        timeDFT = end - start;
        return points;
    }

    private DataPoint[] calculateFastFourie() {
        long start = System.nanoTime();
        DataPoint[] points = new DataPoint[N];
        for (int p = 0; p < N; p++) {
            double temp = 4 * Math.PI * p / N;
            realKoef1 = 0;
            realKoef2 = 0;
            notRealKoef1 = 0;
            notRealKoef2 = 0;
            for (int k = 0; k < N / 2 - 1; k++) {
                double tmp = 4 * Math.PI * p * k / N;
                realKoef1 += signal[2*k] * Math.cos(tmp);
                notRealKoef1 += signal[2*k] * Math.sin(tmp);
                realKoef2 += signal[2*k+1] * Math.cos(tmp);
                notRealKoef2 += signal[2*k+1] * Math.sin(tmp);
            }
            if (p < N / 2) {
                points[p] = new DataPoint(p, Math.sqrt(Math.pow((realKoef2 + realKoef1 * Math.cos(temp)), 2)
                        + Math.pow((notRealKoef2 + notRealKoef1 * Math.sin(temp)), 2)));
            } else {
                points[p] = new DataPoint(p, Math.sqrt(Math.pow((realKoef2 - realKoef1 * Math.cos(temp)), 2)
                        + Math.pow((notRealKoef2 - notRealKoef1 * Math.sin(temp)), 2)));
            }
        }
        long end = System.nanoTime();
        timeFFT = end - start;
        return points;
    }

}
