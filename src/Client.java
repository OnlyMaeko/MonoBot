import java.io.*;
import java.net.*;
import java.util.Properties;

public class Client {
    public static void main(String[] args) throws Exception {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) { 
            prop.load(input); 
        }
        String host = prop.getProperty("server.host");
        int port = Integer.parseInt(prop.getProperty("client.port"));

        System.out.println("[Client] Waiting 2 seconds for QUIC bridge to initialize...");
        Thread.sleep(2000);

        System.out.println("[Client] Connecting to bridge...");
        Socket socket = new Socket(host, port);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        // --- DFA HANDSHAKE SEQUENCE ---
        
        // 1. Send Hello
        out.println(State.HELLO);
        System.out.println("[Client] RCV: " + in.readLine()); // Should be 02 (HELLO_ACK)
        System.out.println("[Client] RCV: " + in.readLine()); // Should be 04 (AUTH_REQUESTED)
        
        // 2. Authenticate
        out.println("AUTH admin password");
        String authRes = in.readLine();
        System.out.println("[Client] RCV: " + authRes); // Should be 05 (AUTH_OK)
        
        if (State.AUTH_OK.equals(authRes)) {
            // 3. Create Lobby
            out.println(State.LOBBY_CREATE + " MonopolyRoom");
            System.out.println("[Client] RCV: " + in.readLine()); // Should be 10 (LOBBY_STATE)
            
            // 4. Start Game
            out.println(State.GAME_START);
            System.out.println("[Client] RCV: " + in.readLine()); // Should be 11 (GAME_START ACK)
            
            System.out.println("[Client] DFA Handshake Complete! Launching GUI...");
            
            // 5. Hand the active socket to the Game Interface
            Interface game = new Interface();
            game.setServerConnection(out, in); // This matches the method in your Interface.java!
            game.playGame();
            
            // Block the client thread until the game finishes
            game.waitForGameOver();
            
            // 6. Terminate Connection
            out.println(State.GAME_OVER);
            out.println(State.DISCONNECT);
        } else {
            System.out.println("[Client] Authentication failed. Exiting.");
        }
        
        socket.close();
    }
}