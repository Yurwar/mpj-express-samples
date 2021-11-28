package com.yurwar.mc1.task2.var1;

import mpi.MPI;
import mpi.Status;

public class Example {

    public void execute(String... args) {
        double doubleData[] = new double[20];
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        Status status;
        if (size != 2) {
            if (rank == 0) {
                System.out.printf("Only 2 tasks required instead of %d, abort\n", size);
            }
            MPI.COMM_WORLD.Barrier();
            MPI.COMM_WORLD.Abort(1);
            return;
        }
        if (rank == 0) {
            MPI.COMM_WORLD.Send(doubleData, 0, 5, MPI.DOUBLE, 1, 100);
        } else {
            status = MPI.COMM_WORLD.Recv(doubleData, 0, 5, MPI.DOUBLE, 0, 100);
            int count = status.count;
            System.out.printf("Received %d elements\n", count);
        }
        MPI.Finalize();
    }
}
