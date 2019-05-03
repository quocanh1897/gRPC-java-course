package calculator.client;

import com.grpc.client.GreetingClient;
import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class CalculatorClient {

    private void run(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 7777)
                .usePlaintext()
                .build();
//        doUnaryCall(channel);
//        doServerStreamingCall(channel);

        doClientStreamingCall(channel);

        System.out.println("SERVER is shutting down...");
        channel.shutdown();
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ComputeAverageRequest> requestStreamObserver = asyncClient.computeAverage(new StreamObserver<ComputeAverageResponse>() {
            @Override
            public void onNext(ComputeAverageResponse value) {
                System.out.println("Received a response from SERVER >->");
                System.out.println(value.getAverage());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("<-< Server has completed sending data");
                latch.countDown();

            }
        });

        requestStreamObserver.onNext(ComputeAverageRequest.newBuilder()
                .setNumber(1)
                .build());
        requestStreamObserver.onNext(ComputeAverageRequest.newBuilder()
                .setNumber(11)
                .build());
        requestStreamObserver.onNext(ComputeAverageRequest.newBuilder()
                .setNumber(111)
                .build());
        requestStreamObserver.onNext(ComputeAverageRequest.newBuilder()
                .setNumber(1111)
                .build());

        requestStreamObserver.onCompleted();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

        SumRequest request =  SumRequest.newBuilder()
                .setFirstNumber(122)
                .setSecondNumber(234)
                .build();

        Long number = 9798888788L;
        stub.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder()
                .setNumber(number)
                .build())
                .forEachRemaining(primeNumberDecompositionResponse ->
                        System.out.print(primeNumberDecompositionResponse.getPrimeFactor()+" x ")
                );
    }

    private void doUnaryCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

        SumRequest request =  SumRequest.newBuilder()
                .setFirstNumber(122)
                .setSecondNumber(234)
                .build();

        SumResponse response = stub.sum((request));

        System.out.println(request.getFirstNumber()+ " + " + request.getSecondNumber() + " = " + response.getSumResult());

    }

    public static void main(String[] args) {
        System.out.println("HELLO from client");

        CalculatorClient main = new CalculatorClient();
        main.run();

    }
}
