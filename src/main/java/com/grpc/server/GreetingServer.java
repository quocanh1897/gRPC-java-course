package com.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class GreetingServer {
    public static void main(String[] args) throws IOException, InterruptedException {

        // plaintext server
//        Server server = ServerBuilder.forPort(7777)
//                .addService(new GreetServiceImpl())
//                .build();


        // secure server
        Server server = ServerBuilder.forPort(7777)
                .addService(new GreetServiceImpl())
                .useTransportSecurity(
                        new File("ssl/server.crt"),
                        new File("ssl/server.pem")
                )
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
