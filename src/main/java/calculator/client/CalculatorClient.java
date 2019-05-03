package calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 7777)
                .usePlaintext()
                .build();
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

        SumRequest request =  SumRequest.newBuilder()
                .setFirstNumber(122)
                .setSecondNumber(234)
                .build();

        // UNARY
//        SumResponse response = stub.sum((request));
//
//        System.out.println(request.getFirstNumber()+ " + " + request.getSecondNumber() + " = " + response.getSumResult());


        // SERVER STREAMING
        Long number = 9798888788L;
        stub.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder()
                .setNumber(number)
                .build())
                .forEachRemaining(primeNumberDecompositionResponse ->
                    System.out.print(primeNumberDecompositionResponse.getPrimeFactor()+" x ")
                );
        channel.shutdown();
    }
}
