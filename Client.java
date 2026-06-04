import java.net.*;

public class Client {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = 9090;

        System.out.println("Connecting to " + host + ":" + port);
        Socket socket = new Socket(host, port);
        System.out.println("Connected");
        socket.close();
    }
}