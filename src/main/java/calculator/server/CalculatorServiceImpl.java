package calculator.server;

import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        SumResponse sumResponse = SumResponse.newBuilder()
                .setSumResult(request.getFirstNumber()+request.getSecondNumber())
                .build();

        responseObserver.onNext(sumResponse);

        responseObserver.onCompleted();
    }

    @Override
    public void primeNumberDecomposition(PrimeNumberDecompositionRequest request, StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
        Long number = request.getNumber();
        Long divisor =2L ;

        while(number >1){
            if (number % divisor == 0){
                number /= divisor;
                responseObserver.onNext(PrimeNumberDecompositionResponse.newBuilder()
                        .setPrimeFactor(divisor)
                        .build());
            }else{
                divisor += 1;
            }
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {
        StreamObserver<ComputeAverageRequest> requestStreamObserver = new StreamObserver<ComputeAverageRequest>() {

            int sum = 0;
            int count = 0;

            @Override
            public void onNext(ComputeAverageRequest value) {

                sum += value.getNumber();

                count += 1;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                double average = (double) sum / count;
                responseObserver.onNext(
                        ComputeAverageResponse.newBuilder()
                                .setAverage(average)
                                .build()
                );
                responseObserver.onCompleted();

            }
        };

        return requestStreamObserver;
    }

    @Override
    public StreamObserver<FindMaxRequest> findMaximum(StreamObserver<FindMaxResponse> responseObserver) {
        return new StreamObserver<FindMaxRequest>() {
            int currMax = 0;

            @Override
            public void onNext(FindMaxRequest value) {
                int currNumber = value.getNumber();

                if (currNumber > currMax){
                    currMax = currNumber;
                    responseObserver.onNext(
                            FindMaxResponse.newBuilder()
                                    .setMax(currNumber)
                                    .build()
                    );
                }

            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onCompleted();

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(
                        FindMaxResponse.newBuilder()

                                .setMax(currMax)
                                .build()
                );
                responseObserver.onCompleted();

            }
        };
    }
}
