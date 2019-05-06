package blog.client;

import com.proto.blog.Blog;
import com.proto.blog.BlogServiceGrpc;
import com.proto.blog.CreateBlogRequest;
import com.proto.blog.CreateBlogResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;


public class BlogClient {
    private void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 7778)
                .usePlaintext()
                .build();

        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

        Blog blog = Blog.newBuilder()
                .setTitle("New Blog !")
                .setAuthorId("Anh")
                .setContent("Hello, this is first blog!")
                .build();

        CreateBlogResponse createBlogResponse = blogClient.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(blog)
                .build());

    System.out.println("Received Create-blog response");
    System.out.println(createBlogResponse.toString());

        System.out.println("Shutting down the SERVER");
        channel.shutdown();
    }

    public static void main(String[] args) {
        System.out.println("==> This is BLOG - client <==");

        BlogClient main = new BlogClient();
        main.run();

    }

}
