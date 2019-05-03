package com.grpc.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("HELLO from client");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 7777)
                .usePlaintext()
                .build();

        System.out.println("Creating stub");

        // create a greet service client blocking - sync
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // create a protocol buffer greeting mess
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Nguyen")
                .setLastName("Anh")
                .build();

        // UNARY
//        // create a protocol buffer for greetRequest
//        GreetRequest greetRequest = GreetRequest.newBuilder()
//                .setGreeting(greeting)
//                .build();
//
//        GreetResponse greetResponse = greetClient.greet(greetRequest);
//
//        System.out.println(greetResponse.getResult());

        // SERVER STREAMING
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        greetClient.greetmanyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse ->
                    System.out.println(greetManyTimesResponse.getResult())
                );


        channel.shutdown();

    }
}
