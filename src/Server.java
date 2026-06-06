import java.io.*;
import java.net.*;
import java.util.Properties;


//Sockets used for the Java server but I promise the python bridge actually transports the data over QUIC
//Main calls the config to connect to the client.java on the port after being run over QUIC
public class Server {
    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) { 
            prop.load(input); 
        }
        int port = Integer.parseInt(prop.getProperty("server.port"));
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Server] Plain TCP Server listening on " + port + " (Behind QUIC Bridge)");
            
            // Accepts client connections, right now it's just 1 but in a future version I plan to upscale to handle multiple
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    
                    System.out.println("[Server] Bridge connected. Initializing DFA...");
                    
                    // Initialize the DFA
                    State currentState = State.CONNECTED;
                    
                    String inputLine;
                    while (currentState.isActive() && (inputLine = in.readLine()) != null) {
                        System.out.println("[Server RCV] " + inputLine);
                        
                        try {
                            // Auth, this should be switched to the config properties, !!!!DONT FORGET!!!!
                            State.TransitionResult result = currentState.transition(inputLine, "admin", "password");
                            currentState = result.nextState;
                            
                            //DFA States frp, state.java and listed in P2 DFA
                            if (currentState == State.HANDSHAKED) {
                                out.println(State.HELLO_ACK);
                                out.println(State.AUTH_REQUESTED);
                                currentState = State.AWAITING_AUTH;
                            } 
                            else if (currentState == State.AWAITING_AUTH && result.authFailed) {
                                out.println(State.AUTH_FAIL);
                            } 
                            else if (currentState == State.AUTHENTICATED && inputLine.startsWith("AUTH")) {
                                out.println(State.AUTH_OK);
                            } 
                            else if (currentState == State.IN_LOBBY && inputLine.startsWith(State.LOBBY_CREATE)) {
                                out.println(State.LOBBY_STATE + " LobbyReady");
                            } 
                            else if (currentState == State.IN_GAME && inputLine.startsWith(State.GAME_START)) {
                                out.println(State.GAME_START + " Game is starting");
                            } 
                            else if (currentState == State.IN_GAME && inputLine.startsWith(State.TURN_NOTIFY)) {
                                
                                out.println(State.TURN_NOTIFY + " ACK"); 
                            } 
                            else if (currentState == State.TERMINATED) {
                                out.println(State.DISCONNECT);
                                break;
                            }
                        } catch (State.InvalidTransitionException e) {
                            System.err.println("[Server] Protocol Error: " + e.getMessage());
                            out.println(State.ERROR_TERM + " " + e.getMessage());
                            currentState = State.TERMINATED;
                        }
                    }
                    // Terminate the server, doesn't actually end the client, client also has to close the connection for UI to dissapear but data will not be passed and the connection is severed
                    // I Think its a SWING GUI error that keeps the GUI open post dc
                    System.out.println("[Server] Client session terminated.");
                } catch (IOException e) {
                    System.out.println("[Server] Connection dropped.");
                }
            }
        }
    }
}