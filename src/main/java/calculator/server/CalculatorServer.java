package calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class CalculatorServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(7777)
                .addService(new CalculatorServiceImpl())
                .addService(ProtoReflectionService.newInstance())// reflection
                .build();

        server.start();
        System.out.println("Server is running ...");

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Server is shutting down ...");
            server.shutdown();
        }));

        server.awaitTermination();
    }
}
