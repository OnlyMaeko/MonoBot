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

        System.out.println("[Client] Waiting for QUIC bridge to initialize");
        Thread.sleep(2000);

        System.out.println("[Client] Connecting to bridge");
        Socket socket = new Socket(host, port);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        // DFA States as presented in my submission for P2 and the PDUs associated
        
        // 1 ACK, AUTH_REQUESTED
        out.println(State.HELLO);
        System.out.println("[Client] RCV: " + in.readLine()); 
        System.out.println("[Client] RCV: " + in.readLine()); 
        
        //2 Authenticate player, AUTH_OK
        out.println("AUTH admin password");
        String authRes = in.readLine();
        System.out.println("[Client] RCV: " + authRes);
        
        //If players clears auth
        if (State.AUTH_OK.equals(authRes)) {
            // 3  Create Lobby LOBBY_STATE
            out.println(State.LOBBY_CREATE + " MonopolyRoom");
            System.out.println("[Client] RCV: " + in.readLine()); 
            
            // 4  Start Game, GAME_START ACK
            out.println(State.GAME_START);
            System.out.println("[Client] RCV: " + in.readLine());
            
            System.out.println("[Client] Handshake Complete! Launching GUI");
            
            // 5 Start interface
            Interface game = new Interface();
            game.setServerConnection(out, in);
            game.playGame();
            
            game.waitForGameOver();
            // 6 Terminate Connection, 99 (was 15 in the P2 PDU list), not fully implemented as there is no terminate command in the Interface but just closing the window should trigger it by order of disconnectig ending
            out.println(State.GAME_OVER);
            out.println(State.DISCONNECT);
        } else {
            System.out.println("[Client] Authentication failed. Exiting.");
        }
        
        socket.close();
    }
}