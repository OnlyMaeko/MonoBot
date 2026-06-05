import java.io.*;
import java.net.*;
import java.util.Properties;

/**
 * Monobot Protocol Server (v1)
 *
 * Implements the full server-side DFA:
 *   CONNECTED -> HANDSHAKED -> AUTHENTICATED -> IN_LOBBY -> IN_GAME -> TERMINATED
 *
 * Configuration: server.properties
 *   port=9090
 *
 * Usage: java Server [server.properties]
 */
public class Server {

    // Protocol response codes
    private static final String HELLO_ACK   = "HELLO_ACK";
    private static final String HELLO_REJ   = "HELLO_REJ";
    private static final String AUTH_ACK    = "AUTH_ACK";
    private static final String AUTH_REJ    = "AUTH_REJ";
    private static final String JOIN_ACK    = "JOIN_ACK";
    private static final String JOIN_REJ    = "JOIN_REJ";
    private static final String START_ACK   = "START_ACK";
    private static final String START_REJ   = "START_REJ";
    private static final String ACTION_ACK  = "ACTION_ACK";
    private static final String ACTION_REJ  = "ACTION_REJ";
    private static final String QUIT_ACK    = "QUIT_ACK";
    private static final String ERROR       = "ERROR";

    public static void main(String[] args) throws Exception {
        // Load configuration
        Properties config = new Properties();
        String configFile = (args.length > 0) ? args[0] : "server.properties";
        try (FileInputStream fis = new FileInputStream(configFile)) {
            config.load(fis);
        } catch (IOException e) {
            System.err.println("Warning: Could not load " + configFile + ", using defaults.");
        }
        int port = Integer.parseInt(config.getProperty("port", "9090"));

        System.out.println("=== Monobot Protocol Server v1 ===");
        System.out.println("Listening on port " + port + "...");

        ServerSocket serverSocket = new ServerSocket(port);

        // Accept one client connection (single-client model)
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

        handleClient(clientSocket);

        serverSocket.close();
    }

    /**
     * Drives the full DFA for one connected client session.
     * Reads messages line-by-line, attempts state transitions,
     * sends ACK or REJ responses accordingly.
     */
    private static void handleClient(Socket clientSocket) {
        State state = State.CONNECTED;
        System.out.println("[DFA] Initial state: " + state);

        try (
            BufferedReader in  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter    out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            while (state.isActive()) {
                // Read the next message from the client
                String message = in.readLine();
                if (message == null) {
                    // Client closed the connection unexpectedly
                    System.out.println("[Server] Client disconnected unexpectedly in state: " + state);
                    break;
                }

                System.out.println("[Server] Received: '" + message + "' (state=" + state + ")");

                // Attempt the DFA transition
                try {
                    State nextState = state.transition(message);
                    state = nextState;
                    System.out.println("[DFA] Transitioned to: " + state);

                    // Send the appropriate ACK and handle any side effects
                    switch (state) {
                        case HANDSHAKED:
                            out.println(HELLO_ACK);
                            System.out.println("[Server] Sent: " + HELLO_ACK);
                            break;

                        case AUTHENTICATED:
                            out.println(AUTH_ACK);
                            System.out.println("[Server] Sent: " + AUTH_ACK);
                            break;

                        case IN_LOBBY:
                            out.println(JOIN_ACK);
                            System.out.println("[Server] Sent: " + JOIN_ACK);
                            System.out.println("[Server] Player is in the lobby.");
                            break;

                        case IN_GAME:
                            // Distinguish between START (entering game) and ACTION (in-game move)
                            String command = message.trim().split("\\s+")[0].toUpperCase();
                            if (command.equals("START")) {
                                out.println(START_ACK);
                                System.out.println("[Server] Sent: " + START_ACK);
                                System.out.println("[Server] Game starting — launching Interface...");
                                // Launch the Monopoly game UI
                                Interface game = new Interface();
                                game.playGame();
                            } else if (command.equals("ACTION")) {
                                // Extract the action payload (everything after "ACTION ")
                                String payload = message.trim().length() > 7
                                    ? message.trim().substring(7).trim()
                                    : "";
                                System.out.println("[Server] Processing ACTION: " + payload);
                                // Phase 1: echo back — game runs locally on Interface
                                out.println(ACTION_ACK + " " + payload);
                                System.out.println("[Server] Sent: " + ACTION_ACK + " " + payload);
                            }
                            break;

                        case TERMINATED:
                            out.println(QUIT_ACK);
                            System.out.println("[Server] Sent: " + QUIT_ACK);
                            System.out.println("[Server] Session terminated cleanly.");
                            break;

                        default:
                            break;
                    }

                } catch (State.InvalidTransitionException e) {
                    // DFA rejected the message — send appropriate rejection
                    String rejection = buildRejectionResponse(state, message, e.getMessage());
                    out.println(rejection);
                    System.out.println("[DFA] Rejected: " + e.getMessage());
                    System.out.println("[Server] Sent: " + rejection);
                    // State does NOT change on rejection
                }
            }

        } catch (IOException e) {
            System.err.println("[Server] I/O error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // ignore
            }
            System.out.println("[Server] Client socket closed. Final state: " + state);
        }
    }

    /**
     * Builds a specific rejection response based on the current state and command attempted.
     * This ensures the client always knows WHY the message was rejected.
     */
    private static String buildRejectionResponse(State currentState, String message, String reason) {
        String command = message.trim().split("\\s+")[0].toUpperCase();
        switch (command) {
            case "HELLO":  return HELLO_REJ  + " " + reason;
            case "AUTH":   return AUTH_REJ   + " " + reason;
            case "JOIN":   return JOIN_REJ   + " " + reason;
            case "START":  return START_REJ  + " " + reason;
            case "ACTION": return ACTION_REJ + " " + reason;
            default:       return ERROR + " Unknown command '" + command + "' in state " + currentState;
        }
    }
}