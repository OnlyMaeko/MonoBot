import java.io.*;
import java.net.*;
import java.util.Properties;


/**
 * Monobot Protocol Server (v2)
 *
 * Implements the full server-side DFA with all 15 PDU codes:
 *   CONNECTED -> HANDSHAKED -> AWAITING_AUTH -> AUTHENTICATED
 *   -> IN_LOBBY -> IN_GAME -> TERMINATED
 *
 * Key behaviors:
 *   - After HELLO_ACK, server immediately sends AUTH_REQUESTED (PDU 04)
 *   - AUTH_FAIL keeps state in AWAITING_AUTH so client can retry
 *   - LOBBY_LIST_REQ returns current lobby list as LOBBY_STATE JSON
 *   - LOBBY_CREATE / LOBBY_JOIN advance to IN_LOBBY, return LOBBY_STATE
 *   - GAME_START launches the Monopoly UI on the client
 *   - TURN_NOTIFY receives full game state JSON, logs it, replies TURN_NOTIFY
 *   - GAME_OVER / DISCONNECT cleanly terminate the session
 *   - Any DFA violation sends ERROR_TERMINATION (PDU 15) and closes connection
 *
 * Configuration: server.properties  (port=9090)
 * Usage:         java Server [server.properties]
 */
public class Server {

    private static final LobbyManager lobbyManager = new LobbyManager();

    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        String configFile = (args.length > 0) ? args[0] : "server.properties";
        try (FileInputStream fis = new FileInputStream(configFile)) {
            config.load(fis);
        } catch (IOException e) {
            System.err.println("Warning: Could not load " + configFile + ", using defaults.");
        }
        int port = Integer.parseInt(config.getProperty("port", "9090"));
        String validUser = config.getProperty("username");
        String validPass = config.getProperty("password");
        
        if (validUser == null || validPass == null) {
            System.err.println("Error: username and password must be set in " + configFile);
            System.exit(1);
        }

        System.out.println("=== Monobot Protocol Server v2 ===");
        System.out.println("Listening on port " + port + "...");

        ServerSocket serverSocket = new ServerSocket(port);
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: "
            + clientSocket.getInetAddress().getHostAddress());

        handleClient(clientSocket, validUser, validPass);
        serverSocket.close();
    }

    private static void handleClient(Socket clientSocket, String validUser, String validPass) {
        State  state       = State.CONNECTED;
        String activeLobby = null;
        String authUser    = null;

        System.out.println("[DFA] Initial state: " + state);

        try (
            BufferedReader in  = new BufferedReader(
                                     new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter    out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            while (state.isActive()) {
                String message = in.readLine();
                if (message == null) {
                    System.out.println("[Server] Client disconnected in state: " + state);
                    break;
                }
                System.out.println("[Server] Recv [" + state + "]: " + message);

                // CONNECTED: only HELLO is valid
                if (state == State.CONNECTED) {
                    try {
                        State.TransitionResult result = state.transition(message, validUser, validPass);
                        state = result.nextState;                        // HANDSHAKED
                        send(out, State.HELLO_ACK, "");
                        // Server immediately requests auth, move to AWAITING_AUTH
                        send(out, State.AUTH_REQUESTED, "Please authenticate");
                        state = State.AWAITING_AUTH;
                        System.out.println("[DFA] -> HANDSHAKED -> AWAITING_AUTH");
                    } catch (State.InvalidTransitionException e) {
                        send(out, State.HELLO_REJ, e.getMessage());
                        terminateWithError(out, e.getMessage());
                        break;
                    }
                    continue;
                }

                // All other states
                try {
                    State.TransitionResult result = state.transition(message, validUser, validPass);
                    String code = message.trim().split("\\s+")[0];

                    // DISCONNECT from any state
                    if (State.DISCONNECT.equals(code)) {
                        send(out, State.DISCONNECT, "Goodbye");
                        state = State.TERMINATED;
                        System.out.println("[DFA] -> TERMINATED (DISCONNECT)");
                        break;
                    }

                    // AUTH result
                    if (state == State.AWAITING_AUTH) {
                        if (result.authFailed) {
                            send(out, State.AUTH_FAIL, "Invalid credentials. Try again.");
                            System.out.println("[DFA] AUTH_FAIL - staying AWAITING_AUTH");
                        } else {
                            authUser = result.payload;
                            state    = result.nextState;                // AUTHENTICATED
                            send(out, State.AUTH_OK, "Welcome " + authUser);
                            System.out.println("[DFA] -> " + state + " user=" + authUser);
                        }
                        continue;
                    }

                    // LOBBY_LIST_REQ - no state change
                    if (State.LOBBY_LIST_REQ.equals(code)) {
                        String lobbyJson = lobbyManager.toJsonArray();
                        send(out, State.LOBBY_STATE, lobbyJson);
                        System.out.println("[Server] Sent lobby list: " + lobbyJson);
                        continue;
                    }

                    // LOBBY_CREATE
                    if (State.LOBBY_CREATE.equals(code)) {
                        String lobbyName = result.payload;
                        LobbyManager.Lobby lobby = lobbyManager.create(lobbyName, authUser);
                        if (lobby == null) {
                            send(out, State.ERROR_TERM,
                                "Lobby '" + lobbyName + "' already exists");
                            continue;
                        }
                        activeLobby = lobbyName;
                        state       = result.nextState;                 // IN_LOBBY
                        send(out, State.LOBBY_STATE, lobbyManager.toJsonArray());
                        System.out.println("[DFA] -> " + state + " created=" + lobbyName);
                        continue;
                    }

                    // LOBBY_JOIN
                    if (State.LOBBY_JOIN.equals(code)) {
                        String lobbyName = result.payload;
                        LobbyManager.Lobby lobby = lobbyManager.find(lobbyName);
                        if (lobby == null) {
                            send(out, State.ERROR_TERM,
                                "Lobby '" + lobbyName + "' not found");
                            continue;
                        }
                        if (lobby.status == LobbyManager.LobbyStatus.IN_PROGRESS) {
                            send(out, State.ERROR_TERM,
                                "Lobby '" + lobbyName + "' already in progress");
                            continue;
                        }
                        activeLobby = lobbyName;
                        state       = result.nextState;                 // IN_LOBBY
                        send(out, State.LOBBY_STATE, lobbyManager.toJsonArray());
                        System.out.println("[DFA] -> " + state + " joined=" + lobbyName);
                        continue;
                    }

                    // GAME_START
                    if (State.GAME_START.equals(code)) {
                        state = result.nextState;                       // IN_GAME
                        if (activeLobby != null) lobbyManager.markInProgress(activeLobby);
                        send(out, State.GAME_START, "Game starting");
                        System.out.println("[DFA] -> " + state + " lobby=" + activeLobby);
                        continue;
                    }

                    // TURN_NOTIFY - receive full game state JSON each turn
                    if (State.TURN_NOTIFY.equals(code)) {
                        logTurnState(result.payload);
                        send(out, State.TURN_NOTIFY, "");
                        continue;
                    }

                    // GAME_OVER
                    if (State.GAME_OVER.equals(code)) {
                        state = result.nextState;                       // TERMINATED
                        send(out, State.GAME_OVER, "Session closed");
                        System.out.println("[DFA] -> TERMINATED (GAME_OVER)");
                        break;
                    }

                } catch (State.InvalidTransitionException e) {
                    System.err.println("[DFA] Violation: " + e.getMessage());
                    terminateWithError(out, e.getMessage());
                    state = State.TERMINATED;
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("[Server] I/O error: " + e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
            System.out.println("[Server] Connection closed. Final state: " + state);
        }
    }

    /**
     * Sends a framed protocol message: "<code> <payload>" or just "<code>".
     */
    private static void send(PrintWriter out, String code, String payload) {
        String line = (payload == null || payload.isEmpty()) ? code : code + " " + payload;
        out.println(line);
        String preview = line.length() > 120 ? line.substring(0, 120) + "..." : line;
        System.out.println("[Server] Sent: " + preview);
    }

    /**
     * Sends ERROR_TERMINATION (PDU 15) with a reason and closes the session.
     */
    private static void terminateWithError(PrintWriter out, String reason) {
        send(out, State.ERROR_TERM, reason);
        System.out.println("[Server] ERROR_TERMINATION: " + reason);
    }

    /**
     * Logs a TURN_NOTIFY JSON payload — extracts key fields for a readable summary.
     */
    private static void logTurnState(String json) {
        String turn   = extractField(json, "turnCount");
        String player = extractField(json, "currentPlayerIndex");
        String event  = extractField(json, "lastEvent");
        System.out.println("[Server] TURN_NOTIFY turn=" + turn
            + " player=" + player + " event=" + event);
        System.out.println("[Server] Full state: " + json);
    }

    /**
     * Minimal JSON field extractor for logging purposes only.
     */
    private static String extractField(String json, String key) {
        if (json == null) return "?";
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return "?";
        int start = idx + search.length();
        if (start >= json.length()) return "?";
        char first = json.charAt(start);
        if (first == '"') {
            int end = json.indexOf('"', start + 1);
            return end == -1 ? "?" : json.substring(start + 1, end);
        } else {
            int end = start;
            while (end < json.length() && ",}]".indexOf(json.charAt(end)) == -1) end++;
            return json.substring(start, end).trim();
        }
    }
}