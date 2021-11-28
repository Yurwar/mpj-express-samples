package com.yurwar.mc2.task1;

import mpi.MPI;
import mpi.Status;

import java.util.concurrent.ThreadLocalRandom;

public class RowSinSumCalculateExecutor {
    public static final int N = 240000;
    public static final int K = 500;

    public void execute(String... args) {
        int[] m = new int[1];
        int p;
        double[] x = new double[N];
        double[] a = new double[N];
        int myrank;
        int i;
        double[] sum = {0.0};
        double[] total = {0.0};
        double startTime = 0.0;
        double useTime;

        MPI.Init(args);
        p = MPI.COMM_WORLD.Size();
        myrank = MPI.COMM_WORLD.Rank();


        if (myrank == 0) {
            for (i = 0; i < N; i++) {
                x[i] = 0.0001 * i;
                a[i] = ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
            }
            m[0] = N / p;
        }
        if (myrank == 0) {
            startTime = MPI.Wtime();
        }

        MPI.COMM_WORLD.Bcast(m, 0, 1, MPI.INT, 0);
        MPI.COMM_WORLD.Scatter(x, 0, m[0], MPI.DOUBLE, x, 0, m[0], MPI.DOUBLE, 0);
        MPI.COMM_WORLD.Scatter(a, 0, m[0], MPI.DOUBLE, a, 0, m[0], MPI.DOUBLE, 0);

        for (i = 0; i < m[0]; i++) {
            sum[0] += a[i] * calculateSin(x[i]);
        }

        MPI.COMM_WORLD.Barrier();

        MPI.COMM_WORLD.Reduce(sum, 0, total, 0, 1, MPI.DOUBLE, MPI.SUM, 0);

        if (myrank == 0) {
            useTime = MPI.Wtime() - startTime;
            System.out.printf("t=%f sec.%n", useTime);
            System.out.printf("Sum in %d procs is %.5f%n", p, total[0]);
        }
        MPI.Finalize();
    }

    private double calculateSin(double x) {
        double y = x;
        double s = y;
        for (int k = 1; k <= K; k += 2) {
            y = -(((x * x) / ((k + 1) * (k + 2))) * y);
            s += y;
        }
        return s;
    }
}
