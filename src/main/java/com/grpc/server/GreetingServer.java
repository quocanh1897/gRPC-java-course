package com.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(7777)
                .addService(new GreetServiceImpl())
                .build();

        server.start();
        System.out.println("SERVER is running ...");

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Receive shutdown request");
            server.shutdown();
        }));

        server.awaitTermination();
    }
}
