import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * Monobot Protocol Client (v2)
 *
 * Drives the full protocol handshake using all 15 PDU codes, then launches
 * the Monopoly game UI. The complete flow is:
 *
 *   Client sends 01 HELLO           -> Server sends 02 HELLO_ACK
 *   Server sends 04 AUTH_REQUESTED  -> Client sends AUTH <user> <pass>
 *   Server sends 05 AUTH_OK         -> (or 06 AUTH_FAIL, client retries)
 *   Client sends 09 LOBBY_LIST_REQ  -> Server sends 10 LOBBY_STATE
 *   Client sends 07 LOBBY_CREATE    -> Server sends 10 LOBBY_STATE
 *     (or)  08 LOBBY_JOIN <name>
 *   Client sends 11 GAME_START      -> Server sends 11 GAME_START
 *   Client sends 13 TURN_NOTIFY     -> Server sends 13 TURN_NOTIFY  (each turn)
 *   Client sends 12 GAME_OVER       -> Server sends 12 GAME_OVER
 *
 * Configuration: client.properties
 *   host=127.0.0.1
 *   port=9090
 *   username=admin
 *   password=password
 *
 * Usage: java Client [client.properties]
 */
public class Client {

    private static PrintWriter    serverOut;
    private static BufferedReader serverIn;
    private static Scanner        userInput = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        // Load config
        Properties config = new Properties();
        String configFile = (args.length > 0) ? args[0] : "client.properties";
        try (FileInputStream fis = new FileInputStream(configFile)) {
            config.load(fis);
        } catch (IOException e) {
            System.err.println("Warning: Could not load " + configFile + ", using defaults.");
        }

        String host     = config.getProperty("host",     "127.0.0.1");
        int    port     = Integer.parseInt(config.getProperty("port", "9090"));
        String username = config.getProperty("username");
        String password = config.getProperty("password");
        
        // Command-line overrides
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("-host"))      host = args[i+1];
            if (args[i].equals("-port"))      port = Integer.parseInt(args[i+1]);
            if (args[i].equals("-username"))  username = args[i+1];
            if (args[i].equals("-password"))  password = args[i+1];
        }
        
        if (username == null || password == null) {
            System.err.println("Error: username and password must be set in client.properties or via -username/-password flags");
            System.exit(1);
        }

        System.out.println("=== Monobot Protocol Client v2 ===");
        System.out.println("Connecting to " + host + ":" + port + "...");

        try (Socket socket = new Socket(host, port)) {
            serverOut = new PrintWriter(socket.getOutputStream(), true);
            serverIn  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected.\n");

            // ── 01 HELLO ─────────────────────────────────────────────────────
            send(State.HELLO, "");
            String r1 = serverIn.readLine();
            System.out.println("[Client] Recv: " + r1);
            if (r1 == null || !r1.startsWith(State.HELLO_ACK)) {
                System.err.println("[Client] Handshake failed: " + r1);
                return;
            }
            System.out.println("[Client] Handshake OK");

            // ── 04 AUTH_REQUESTED (server-initiated) ─────────────────────────
            String r2 = serverIn.readLine();
            System.out.println("[Client] Recv: " + r2);
            if (r2 == null || !r2.startsWith(State.AUTH_REQUESTED)) {
                System.err.println("[Client] Expected AUTH_REQUESTED, got: " + r2);
                return;
            }

            // ── AUTH (retry loop on AUTH_FAIL) ────────────────────────────────
            boolean authenticated = false;
            int     authAttempts  = 0;
            while (!authenticated && authAttempts < 3) {
                authAttempts++;
                send("AUTH", username + " " + password);
                String authResp = serverIn.readLine();
                System.out.println("[Client] Recv: " + authResp);
                if (authResp != null && authResp.startsWith(State.AUTH_OK)) {
                    System.out.println("[Client] Authenticated as: " + username);
                    authenticated = true;
                } else if (authResp != null && authResp.startsWith(State.AUTH_FAIL)) {
                    System.err.println("[Client] Auth failed (attempt " + authAttempts + ")");
                    if (authAttempts >= 3) {
                        System.err.println("[Client] Too many failed attempts. Exiting.");
                        return;
                    }
                } else {
                    System.err.println("[Client] Unexpected auth response: " + authResp);
                    return;
                }
            }

            // ── 09 LOBBY_LIST_REQ ─────────────────────────────────────────────
            send(State.LOBBY_LIST_REQ, "");
            String lobbyListResp = serverIn.readLine();
            System.out.println("[Client] Recv: " + lobbyListResp);
            String lobbyListJson = extractPayload(lobbyListResp, State.LOBBY_STATE);
            displayLobbies(lobbyListJson);

            // ── Prompt user: CREATE or JOIN ───────────────────────────────────
            String lobbyName = promptLobbyChoice(lobbyListJson);

            // ── 07 LOBBY_CREATE or 08 LOBBY_JOIN ─────────────────────────────
            String lobbyResp;
            if (lobbyListJson.equals("[]") || promptIsCreate()) {
                send(State.LOBBY_CREATE, lobbyName);
                lobbyResp = serverIn.readLine();
                System.out.println("[Client] Recv: " + lobbyResp);
                if (lobbyResp == null || !lobbyResp.startsWith(State.LOBBY_STATE)) {
                    System.err.println("[Client] Lobby create failed: " + lobbyResp);
                    return;
                }
                System.out.println("[Client] Lobby '" + lobbyName + "' created.");
            } else {
                send(State.LOBBY_JOIN, lobbyName);
                lobbyResp = serverIn.readLine();
                System.out.println("[Client] Recv: " + lobbyResp);
                if (lobbyResp == null || !lobbyResp.startsWith(State.LOBBY_STATE)) {
                    System.err.println("[Client] Lobby join failed: " + lobbyResp);
                    return;
                }
                System.out.println("[Client] Joined lobby '" + lobbyName + "'.");
            }

            // ── 11 GAME_START ─────────────────────────────────────────────────
            System.out.println("\nPress ENTER to start the game...");
            userInput.nextLine();
            send(State.GAME_START, "");
            String startResp = serverIn.readLine();
            System.out.println("[Client] Recv: " + startResp);
            if (startResp == null || !startResp.startsWith(State.GAME_START)) {
                System.err.println("[Client] Game start failed: " + startResp);
                return;
            }
            System.out.println("[Client] Game started!\n");

            // ── Launch game UI, wire in socket for TURN_NOTIFY ────────────────
            Interface game = new Interface();
            game.setServerConnection(serverOut, serverIn);
            game.playGame();
            game.waitForGameOver();

            // ── 12 GAME_OVER ──────────────────────────────────────────────────
            send(State.GAME_OVER, "");
            String overResp = serverIn.readLine();
            System.out.println("[Client] Recv: " + overResp);
            System.out.println("[Client] Session ended. Goodbye.");

        } catch (ConnectException e) {
            System.err.println("[Client] Cannot connect to " + host + ":" + port
                + " — is the server running?");
        } catch (IOException e) {
            System.err.println("[Client] Connection error: " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Sends a framed PDU: "<code> <payload>" or just "<code>" if payload is empty.
     */
    private static void send(String code, String payload) {
        String line = (payload == null || payload.isEmpty()) ? code : code + " " + payload;
        serverOut.println(line);
        String preview = line.length() > 120 ? line.substring(0, 120) + "..." : line;
        System.out.println("[Client] Sent: " + preview);
    }

    /**
     * Extracts the payload (everything after the code prefix) from a server response.
     * Returns empty string if the response doesn't start with the expected code.
     */
    private static String extractPayload(String response, String expectedCode) {
        if (response == null) return "";
        if (response.startsWith(expectedCode + " ")) {
            return response.substring(expectedCode.length() + 1).trim();
        }
        return response.startsWith(expectedCode) ? "" : "";
    }

    /**
     * Parses and prints the lobby list JSON for the user.
     * Format: [{"name":"...","owner":"...","status":"..."},...]
     */
    private static void displayLobbies(String json) {
        System.out.println("\n--- Available Lobbies ---");
        if (json == null || json.equals("[]") || json.isEmpty()) {
            System.out.println("  (none — you'll need to create one)");
        } else {
            // Simple display: find each "name" value in the array
            int idx = 0;
            int count = 1;
            while ((idx = json.indexOf("\"name\":\"", idx)) != -1) {
                int end = json.indexOf('"', idx + 8);
                if (end == -1) break;
                String name = json.substring(idx + 8, end);

                // Also extract status
                int statusIdx = json.indexOf("\"status\":\"", idx);
                String status = "?";
                if (statusIdx != -1 && statusIdx < json.indexOf('}', idx)) {
                    int statusEnd = json.indexOf('"', statusIdx + 10);
                    if (statusEnd != -1) status = json.substring(statusIdx + 10, statusEnd);
                }
                System.out.println("  " + count++ + ". " + name + " [" + status + "]");
                idx = end;
            }
        }
        System.out.println();
    }

    /**
     * Prompts the user to enter a lobby name (create scenario or explicit entry).
     */
    private static String promptLobbyChoice(String lobbyJson) {
        System.out.print("Enter lobby name: ");
        return userInput.nextLine().trim();
    }

    /**
     * Asks the user whether to create or join when lobbies exist.
     * Returns true = create, false = join.
     */
    private static boolean promptIsCreate() {
        System.out.print("(C)reate new lobby or (J)oin existing? [C/J]: ");
        String choice = userInput.nextLine().trim().toUpperCase();
        return !choice.equals("J");
    }
}