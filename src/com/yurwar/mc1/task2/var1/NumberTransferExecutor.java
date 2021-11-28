package com.yurwar.mc1.task2.var1;

import mpi.MPI;
import mpi.Status;

public class NumberTransferExecutor {

    public void execute(String... args) {
        long longData[] = new long[220];
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
        if (rank == 1) {
            MPI.COMM_WORLD.Send(longData, 0, 22, MPI.LONG, 0, 100);
        } else {
            status = MPI.COMM_WORLD.Recv(longData, 0, 22, MPI.LONG, 1, 100);
            int count = status.count;
            System.out.printf("Received %d elements\n", count);
        }
        MPI.Finalize();
    }
}
