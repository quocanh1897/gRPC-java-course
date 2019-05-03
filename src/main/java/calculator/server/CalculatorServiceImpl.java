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
}
