package blog.client;

import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;


public class BlogClient {
    public static String blogID;

    private void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 7778)
                .usePlaintext()
                .build();

        doCreateBlog(channel);
        doReadBlog(channel);
        doUpdateBlog(channel);
        doDeleteBlog(channel);
        doListBlog(channel);

        System.out.println("Shutting down the SERVER");
        channel.shutdown();
    }

    private void doListBlog(ManagedChannel channel) {
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

    blogClient
        .listBlog(ListBlogRequest.newBuilder().build())
        .forEachRemaining(listBlogResponse -> System.out.println(listBlogResponse.getBlog().toString()));

        System.out.println("Received List-blog response");
    }

    private void doDeleteBlog(ManagedChannel channel) {
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

        String blogId = blogID;

        DeleteBlogResponse deleteBlogResponse = blogClient.deleteBlog(DeleteBlogRequest.newBuilder()
                .setBlogId(blogId)
                .build());

        System.out.println("Received Delete-blog response");
        System.out.println(deleteBlogResponse.toString());
    }

    private void doUpdateBlog(ManagedChannel channel) {
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

        String blogId = blogID;

        Blog newBlog = Blog.newBuilder()
                .setId(blogId)
                .setAuthorId("Changed id")
                .setTitle("Changed TILTLE")
                .setContent("CHanged content")
                .build();

        UpdateBlogResponse updateBlogResponse = blogClient.updateBlog(UpdateBlogRequest.newBuilder()
                .setBlog(newBlog)
                .build());

        System.out.println("Received Update-blog response");
        System.out.println(updateBlogResponse.toString());
    }

    private void doReadBlog(ManagedChannel channel) {
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

        String blogId = blogID;

        ReadBlogResponse readBlogResponse = blogClient.readBlog(ReadBlogRequest.newBuilder()
                .setBlogId(blogId)
                .build());

        System.out.println("Received Read-blog response");
        System.out.println(readBlogResponse.toString());
    }

    private void doCreateBlog(ManagedChannel channel){
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

        Blog blog = Blog.newBuilder()
                .setTitle("New Blog !")
                .setAuthorId("Anh")
                .setContent("Hello, this is 3rd blog!")
                .build();

        CreateBlogResponse createBlogResponse = blogClient.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(blog)
                .build());

        System.out.println("Received Create-blog response");
        blogID = createBlogResponse.getBlog().getId();
        System.out.println(createBlogResponse.toString());
    }

    public static void main(String[] args) {
        System.out.println("==> This is BLOG - client <==");

        BlogClient main = new BlogClient();
        main.run();

    }

}
