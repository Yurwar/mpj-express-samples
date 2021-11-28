package com.yurwar.mc1.task4;

import mpi.MPI;

import java.util.Arrays;

public class StringCalculationExecutor {

    public void execute(String... args) {
        String[] surname = new String[1];
        String[] result;
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        if (rank == 0) {
            result = new String[3];
        } else {
            result = new String[1];
        }
        if (size != 4) {
            if (rank == 0) {
                System.out.printf("Only 4 tasks required instead of %d, abort\n", size);
            }
            MPI.COMM_WORLD.Barrier();
            MPI.COMM_WORLD.Abort(1);
            return;
        }

        if (rank == 0) {
            surname[0] = "Matora";
        }
        MPI.COMM_WORLD.Bcast(surname, 0, 1, MPI.OBJECT, 0);
        MPI.COMM_WORLD.Barrier();
        switch (rank) {
            case 0:
                MPI.COMM_WORLD.Recv(result, 0, 1, MPI.OBJECT, 1, 100);
                MPI.COMM_WORLD.Recv(result, 1, 1, MPI.OBJECT, 2, 100);
                MPI.COMM_WORLD.Recv(result, 2, 1, MPI.OBJECT, 3, 100);
                System.out.println(Arrays.toString(result));
                break;
            case 1:
                result[0] = "Yurii" + " " + surname[0];
                MPI.COMM_WORLD.Send(result, 0, 1, MPI.OBJECT, 0, 100);
                break;
            case 2:
                result[0] = String.valueOf(surname[0].length());
                MPI.COMM_WORLD.Send(result, 0, 1, MPI.OBJECT, 0, 100);
                break;
            case 3:
                result[0] = String.valueOf(surname[0].chars().sum() * Math.PI);
                MPI.COMM_WORLD.Send(result, 0, 1, MPI.OBJECT, 0, 100);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        MPI.Finalize();
    }
}
