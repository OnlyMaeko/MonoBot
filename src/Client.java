import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * Monobot Protocol Client (v1)
 *
 * Implements the full client-side DFA handshake, then launches the Monopoly game UI.
 * The protocol sequence is:
 *   1. HELLO        -> expects HELLO_ACK
 *   2. AUTH u p     -> expects AUTH_ACK
 *   3. JOIN         -> expects JOIN_ACK
 *   4. START        -> expects START_ACK  (game launches here)
 *   5. QUIT         -> expects QUIT_ACK   (sent when game ends)
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

    // Client-side DFA state (mirrors server)
    private static State clientState = State.CONNECTED;

    public static void main(String[] args) throws Exception {
        // Load configuration
        Properties config = new Properties();
        String configFile = (args.length > 0) ? args[0] : "client.properties";
        try (FileInputStream fis = new FileInputStream(configFile)) {
            config.load(fis);
        } catch (IOException e) {
            System.err.println("Warning: Could not load " + configFile + ", using defaults.");
        }

        String host     = config.getProperty("host",     "127.0.0.1");
        int    port     = Integer.parseInt(config.getProperty("port",     "9090"));
        String username = config.getProperty("username", "admin");
        String password = config.getProperty("password", "password");

        System.out.println("=== Monobot Protocol Client v1 ===");
        System.out.println("Connecting to " + host + ":" + port + "...");

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected.");

            PrintWriter    out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // ── Step 1: HELLO handshake ──────────────────────────────────────
            if (!sendAndExpect(out, in, "HELLO", "HELLO_ACK")) {
                System.err.println("[Client] Handshake failed. Exiting.");
                return;
            }
            clientState = State.HANDSHAKED;

            // ── Step 2: AUTH ─────────────────────────────────────────────────
            if (!sendAndExpect(out, in, "AUTH " + username + " " + password, "AUTH_ACK")) {
                System.err.println("[Client] Authentication failed. Exiting.");
                return;
            }
            clientState = State.AUTHENTICATED;

            // ── Step 3: JOIN lobby ───────────────────────────────────────────
            if (!sendAndExpect(out, in, "JOIN", "JOIN_ACK")) {
                System.err.println("[Client] Failed to join lobby. Exiting.");
                return;
            }
            clientState = State.IN_LOBBY;
            System.out.println("[Client] In lobby. Ready to start.");

            // ── Step 4: START game ───────────────────────────────────────────
            if (!sendAndExpect(out, in, "START", "START_ACK")) {
                System.err.println("[Client] Failed to start game. Exiting.");
                return;
            }
            clientState = State.IN_GAME;
            System.out.println("[Client] Game started!");

            // ── Step 5: Launch the game UI ───────────────────────────────────
            // The game runs on the client side. When it ends, we send QUIT.
            Interface game = new Interface();
            game.playGame();

            // Wait for the Swing UI to be done (game.playGame() is async via SwingUtilities)
            // The game window's close/end will trigger cleanup. For now, we wait on user input
            // before sending QUIT so the server knows the session is ending.
            System.out.println("\n[Client] Press ENTER when the game is finished to disconnect...");
            new Scanner(System.in).nextLine();

            // ── Step 6: QUIT ─────────────────────────────────────────────────
            if (!sendAndExpect(out, in, "QUIT", "QUIT_ACK")) {
                System.err.println("[Client] QUIT was not acknowledged.");
            }
            clientState = State.TERMINATED;
            System.out.println("[Client] Session ended cleanly. Goodbye.");

        } catch (ConnectException e) {
            System.err.println("[Client] Could not connect to " + host + ":" + port
                + " — is the server running?");
        } catch (IOException e) {
            System.err.println("[Client] Connection error: " + e.getMessage());
        }
    }

    /**
     * Sends a protocol message and checks that the server's response starts with the
     * expected ACK token. Prints both sides of the exchange to stdout.
     *
     * @param out      PrintWriter to the server
     * @param in       BufferedReader from the server
     * @param message  the message to send
     * @param expectAck the ACK token we expect back (e.g. "HELLO_ACK")
     * @return true if the server responded with the expected ACK, false otherwise
     */
    private static boolean sendAndExpect(PrintWriter out, BufferedReader in,
                                          String message, String expectAck) throws IOException {
        System.out.println("[Client] Sending:  " + message);
        out.println(message);

        String response = in.readLine();
        System.out.println("[Client] Received: " + response);

        if (response == null) {
            System.err.println("[Client] Server closed connection unexpectedly.");
            return false;
        }

        if (response.startsWith(expectAck)) {
            System.out.println("[Client] ✓ " + expectAck);
            return true;
        } else {
            System.err.println("[Client] ✗ Expected " + expectAck + " but got: " + response);
            return false;
        }
    }
}