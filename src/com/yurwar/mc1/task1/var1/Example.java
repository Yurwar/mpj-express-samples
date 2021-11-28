package com.yurwar.mc1.task1.var1;

import mpi.MPI;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Example {

    public void execute(String... args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (rank == 0) {
            System.out.printf("Amount of tasks=%d\n", size);
        }
        System.out.printf("My number in MPI_COMM_WORLD=%d\n", rank);

        MPI.COMM_WORLD.Barrier();
        if (rank == 0) {
            System.out.println("CommandLine for task 0:");
            String argsOut = IntStream.range(0, args.length)
                    .mapToObj(i -> String.format("%d: \"%s\"", i, args[i]))
                    .collect(Collectors.joining("\n"));
            System.out.println(argsOut);
        }
    }
}
