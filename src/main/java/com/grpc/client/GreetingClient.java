package com.grpc.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
    ManagedChannel channel;
    private void run() {
        channel = ManagedChannelBuilder.forAddress("localhost", 7777)
                .usePlaintext()
                .build();

        // doUnaryCall(channel);
        // doServerStreamingCall(channel);
        doClientStreamingCall(channel);

        System.out.println("Shutting down the SERVER");
        channel.shutdown();
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        // create an async client stub
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        // create a protocol buffer greeting mess
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Nguyen")
                .setLastName("Anh")
                .build();
        Greeting greeting2 = Greeting.newBuilder()
                .setFirstName("Nguyen2")
                .setLastName("Anh1")
                .build();
        Greeting greeting3 = Greeting.newBuilder()
                .setFirstName("Nguyen3")
                .setLastName("Anh3")
                .build();

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestStreamObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                // get a response from SERVER
                System.out.println("Received a response from SERVER --->\n");
                System.out.println(value.getResult());
                // onNext will be called only once
            }

            @Override
            public void onError(Throwable t) {
                // get an error from SERVER
            }

            @Override
            public void onCompleted() {
                // the SERVER is done sending us data
                // onCompleted will be called right after onNext()
                System.out.println("<--- Server has completed sending data");

                latch.countDown();
            }
        });

        // send streaming messages
        System.out.println("Sending message #1");
        requestStreamObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(greeting)
                .build());
        System.out.println("Sending message #2");
        requestStreamObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(greeting2)
                .build());
        System.out.println("Sending message #3");
        requestStreamObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(greeting3)
                .build());

        // tell the server that client is done sending data
        requestStreamObserver.onCompleted();


        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        // create a greet service client blocking - sync
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // create a protocol buffer greeting mess
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Nguyen")
                .setLastName("Anh")
                .build();

        // SERVER STREAMING
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        greetClient.greetmanyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse ->
                        System.out.println(greetManyTimesResponse.getResult())
                );

    }

    private void doUnaryCall(ManagedChannel channel) {
        // create a greet service client blocking - sync
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // create a protocol buffer greeting mess
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Nguyen")
                .setLastName("Anh")
                .build();

        // create a protocol buffer for greetRequest
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        GreetResponse greetResponse = greetClient.greet(greetRequest);

        System.out.println(greetResponse.getResult());
    }

    public static void main(String[] args) {
        System.out.println("HELLO from client");

        GreetingClient main = new GreetingClient();
        main.run();

    }
}
