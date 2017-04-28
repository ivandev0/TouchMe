package source;

import java.util.Arrays;

public class Spline {

    //массивы хранения значений x, y
    private float[] xx;
    private float[] yy;

    //массивы хранения коэф перед кубическим полиномом
    private float[] a;
    private float[] b;
    private float[] c;
    private float[] d;

    //начальная инициализаця
    public Spline(float[] xx, float[] yy) {
        setValues(xx, yy);
    }

    public void setValues(float[] xx, float[] yy) {
        this.xx = xx;
        this.yy = yy;
        if (xx.length > 1) {
            calculateCoefficients();
        }
    }

    //получить значения по заданному x
    public float getValue(float x) {
        if (xx.length == 0) {
            return Float.NaN;
        }

        if (xx.length == 1) {
            if (xx[0] == x) {
                return yy[0];
            } else {
                return Float.NaN;
            }
        }

        int index = Arrays.binarySearch(xx, x);
        if (index > 0) {
            return yy[index];
        }

        index = - (index + 1) - 1;

        if (index < 0) {
            return yy[0];
        }

        return (float) (a[index]
                        + b[index] * (x - xx[index])
                        + c[index] * Math.pow(x - xx[index], 2)
                        + d[index] * Math.pow(x - xx[index], 3));
    }

    //вычисление коэфициентов полинома и заполение массива
    private void calculateCoefficients() {
        int N = yy.length;
        a = new float[N];
        b = new float[N];
        c = new float[N];
        d = new float[N];

        if (N == 2) {
            a[0] = yy[0];
            b[0] = yy[1] - yy[0];
            return;
        }

        float[] h = new float[N - 1];
        for (int i = 0; i < N - 1; i++) {
            a[i] = yy[i];
            h[i] = xx[i + 1] - xx[i];
            // h[i] is used for division later, avoid a NaN
            if (h[i] == 0.0) {
                h[i] = 0.01f;
            }
        }
        a[N - 1] = yy[N - 1];

        float[][] A = new float[N - 2][N - 2];
        float[] y = new float[N - 2];
        for (int i = 0; i < N - 2; i++) {
            y[i] = 3 * ((yy[i + 2] - yy[i + 1]) / h[i + 1] - (yy[i + 1] - yy[i]) / h[i]);

            A[i][i] = 2 * (h[i] + h[i + 1]);

            if (i > 0) {
                A[i][i - 1] = h[i];
            }

            if (i < N - 3) {
                A[i][i + 1] = h[i + 1];
            }
        }
        solve(A, y);

        for (int i = 0; i < N - 2; i++) {
            c[i + 1] = y[i];
            b[i] = (a[i + 1] - a[i]) / h[i] - (2 * c[i] + c[i + 1]) / 3 * h[i];
            d[i] = (c[i + 1] - c[i]) / (3 * h[i]);
        }
        b[N - 2] = (a[N - 1] - a[N - 2]) / h[N - 2] - (2 * c[N - 2] + c[N - 1]) / 3 * h[N - 2];
        d[N - 2] = (c[N - 1] - c[N - 2]) / (3 * h[N - 2]);
    }

    //решает систему Ax = b
    public void solve(float[][] A, float[] b) {
        int n = b.length;
        for (int i = 1; i < n; i++) {
            A[i][i - 1] = A[i][i - 1] / A[i - 1][i - 1];
            A[i][i] = A[i][i] - A[i - 1][i] * A[i][i - 1];
            b[i] = b[i] - A[i][i - 1] * b[i - 1];
        }

        b[n - 1] = b[n - 1] / A[n - 1][n - 1];
        for (int i = b.length - 2; i >= 0; i--) {
            b[i] = (b[i] - A[i][i + 1] * b[i + 1]) / A[i][i];
        }
    }
}

