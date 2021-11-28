package com.yurwar.mc1.task3;

import mpi.MPI;
import mpi.Status;

import java.util.ArrayList;
import java.util.List;

public class DynamicNumberTransferExecutor {

    public void execute(String... args) {
        List<Long> longData = new ArrayList<>(220);
        Object[] longDataObj = { longData };
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
            MPI.COMM_WORLD.Send(longDataObj, 0, 1, MPI.OBJECT, 0, 100);
        } else {
            status = MPI.COMM_WORLD.Recv(longDataObj, 0, 1, MPI.OBJECT, 1, 100);
            int count = status.count;
            System.out.printf("Received %d elements\n", count);
        }
        MPI.Finalize();
    }
}
