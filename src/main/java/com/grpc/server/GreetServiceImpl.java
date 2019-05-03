package com.grpc.server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        //extract fields we need
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();
        String lastName = greeting.getLastName();
        String result = "HELLO " + firstName + " " + lastName;

        //create the response
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build();

        //send the response
        responseObserver.onNext(response);

        //comple rpc call
        responseObserver.onCompleted();

    }

    @Override
    public void greetmanyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();

        try {
            for (int i = 0; i < 10; i++) {
                String result = "HELLO " + firstName + ", response number: " + i;
                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                        .setResult(result)
                        .build();

                responseObserver.onNext(response);
                Thread.sleep(1000);

            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            responseObserver.onCompleted();
        }


    }
}
