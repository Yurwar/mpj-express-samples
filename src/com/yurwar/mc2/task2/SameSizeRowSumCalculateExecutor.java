package com.yurwar.mc2.task2;

import mpi.MPI;

import java.util.Arrays;

public class SameSizeRowSumCalculateExecutor {
    private static final int N = 10000;

    public void execute(String... args) {
        int p;
        double[] x = new double[0];
        double[] a = new double[0];
        int myRank;
        int i, j;
        double q, an, nn;
        double c;
        double[] mul = {1.0};
        double[] sum = {0.0};
        double[] total = {0.0};
        double startTime = 0.0;
        double useTime;

        MPI.Init(args);
        p = MPI.COMM_WORLD.Size();
        myRank = MPI.COMM_WORLD.Rank();
        int[] n = new int[p];
        int[] offset = new int[p];

        if (myRank == 0) {
            n = new int[p];
            offset = new int[p];
            offset[0] = 0;
            n[p - 1] = N;
            int curN = N / p;

            for (i = 0; i < p - 1; i++) {
                n[i] = curN;
                n[p - 1] -= curN;
            }

            for (i = 1; i < p; i++)
                offset[i] = offset[i - 1] + n[i - 1];

            for (i = 0; i < p; i++)
                System.out.printf("process %d will be do %d multiples\n", i, n[i]);
        }
        MPI.COMM_WORLD.Scatter(n, 0, 1, MPI.INT, n, 0, 1, MPI.INT, 0);
        MPI.COMM_WORLD.Scatter(offset, 0, 1, MPI.INT, offset, 0, 1, MPI.INT, 0);

        System.out.printf("Rank: %d, array n: %s, array offset: %s%n", myRank, Arrays.toString(n), Arrays.toString(offset));

        if (myRank == 0) {
            x = new double[N];
            a = new double[N];

            for (i = 0; i < N; i++) {
                x[i] = (-1.073741824 + Math.random() * 1E-9) * 0.8;
                a[i] = (-1.073741824 + Math.random() * 1E-9) * 0.01;
            }
        }

        if (myRank != 0) {
            x = new double[n[0]];
            a = new double[n[0]];
        }

        if (myRank == 0) {
            startTime = System.currentTimeMillis();
        }

        MPI.COMM_WORLD.Scatterv(x, 0, n, offset, MPI.DOUBLE, x, 0, n[0], MPI.DOUBLE, 0);
        MPI.COMM_WORLD.Scatterv(a, 0, n, offset, MPI.DOUBLE, a, 0, n[0], MPI.DOUBLE, 0);

        for (i = 0; i < n[0]; i++) {
            for (j = 0; j < i + 1 + offset[0]; j++)
                mul[0] *= x[i];
            sum[0] += a[i] * mul[0];
            mul[0] = 1.0;
        }

        MPI.COMM_WORLD.Barrier();

        MPI.COMM_WORLD.Reduce(sum, 0, total, 0, 1, MPI.DOUBLE, MPI.SUM, 0);

        if (myRank == 0) {
            useTime = System.currentTimeMillis() - startTime;
            System.out.printf("Total = %.12e used %f millis%n", total[0], useTime);
        }
        MPI.Finalize();
    }
}
