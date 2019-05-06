package blog.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class BlogServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(7778)
                .addService(new BlogServiceImpl())
                .addService(ProtoReflectionService.newInstance())// reflection
                .build();

        server.start();
        System.out.println("Blog - Server is running ...");

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Blog - Server is shutting down ...");
            server.shutdown();
        }));

        server.awaitTermination();
    }
}
