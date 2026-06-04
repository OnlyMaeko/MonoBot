import java.net.*;

public class Server {
    public static void main(String[] args) throws Exception {
        int port = 9090;
        System.out.println("Server listening on port " + port);
        ServerSocket ss = new ServerSocket(port);
        Socket client = ss.accept();
        System.out.println("Client connected");

        // launch your java program here
        ProcessBuilder pb = new ProcessBuilder("java", "Interface");
        pb.inheritIO();
        pb.start();

        client.close();
        ss.close();
    }
}