package calculator.client;

import com.grpc.client.GreetingClient;
import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class CalculatorClient {

    private void run(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 7777)
                .usePlaintext()
                .build();
//        doUnaryCall(channel);
//        doServerStreamingCall(channel);
//        doClientStreamingCall(channel);
//        doBiDiStreamingCall (channel);
        doErrorCall(channel);

        System.out.println("SERVER is shutting down...");
        channel.shutdown();
    }

    private void doErrorCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub blockingStub = CalculatorServiceGrpc.newBlockingStub(channel);

        int number = -2134;

        try {
            blockingStub.squareRoot(SqrRequest.newBuilder()
                .setNumber(number)
                .build());
        }
        catch (StatusRuntimeException e){
            System.out.println("Got an exception ");
            e.printStackTrace();
        }

    }

    private void doBiDiStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<FindMaxRequest> requestObserver = asyncClient.findMaximum(new StreamObserver<FindMaxResponse>() {
            @Override
            public void onNext(FindMaxResponse value) {
                System.out.println("Got new max from SERVER >-> " + value.getMax());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();

            }

            @Override
            public void onCompleted() {
                System.out.println("SERVER is done sending data");

            }
        });

        Arrays.asList(3, 12, 3, 44, 55, 123, 444, 1, 23, 44, 5555).forEach(
                number -> {
                    System.out.println("Sending ... " + number);
                    requestObserver.onNext(FindMaxRequest.newBuilder()
                            .setNumber(number)
                            .build());

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );

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
