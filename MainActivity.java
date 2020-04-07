package com.example.lab2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LineGraphSeries<DataPoint> line1 = new LineGraphSeries<>(generateRandomSignal(signal));

        LineGraphSeries<DataPoint> line2 = new LineGraphSeries<>(calculateFourie());

        LineGraphSeries<DataPoint> line3 = new LineGraphSeries<>(calculateFastFourie());

        GraphView graph = findViewById(R.id.graph1);
        setUpGraph(graph, line1, -5, 6);

        graph = findViewById(R.id.graph2);
        setUpGraph(graph, line2, 0, 80);

        graph = findViewById(R.id.graph3);
        setUpGraph(graph, line3, 0, 70);
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

    private DataPoint[] calculateFourie() {
        DataPoint[] points = new DataPoint[N - 1];
        for (int i = 0; i < N - 1; i++) {
            realKoef = 0;
            notRealKoef = 0;
            for (int j = 0; j < N - 1; j++) {
                realKoef += signal[j] * Math.cos(2 * Math.PI * i * j / N);
                notRealKoef -= signal[j] * Math.sin(2 * Math.PI * i * j / N);
            }
            points[i] = new DataPoint(i, Math.sqrt(Math.pow(realKoef, 2) + Math.pow(notRealKoef, 2)));
        }
        return points;
    }

    private DataPoint[] calculateFastFourie() {
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
        return points;
    }

}
