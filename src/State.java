/**
 * Monobot Protocol - DFA State Machine
 *
 * Valid state transitions:
 *   CONNECTED    --[HELLO]-->   HANDSHAKED
 *   HANDSHAKED   --[AUTH]-->    AUTHENTICATED
 *   AUTHENTICATED--[JOIN]-->    IN_LOBBY
 *   IN_LOBBY     --[START]-->   IN_GAME
 *   IN_GAME      --[ACTION]--> IN_GAME
 *   IN_GAME      --[QUIT]-->   TERMINATED
 *
 * All other messages in any state throw an InvalidTransitionException.
 */
public enum State {
    CONNECTED,
    HANDSHAKED,
    AUTHENTICATED,
    IN_LOBBY,
    IN_GAME,
    TERMINATED;

    // Hardcoded credentials for v1 implementation (see protocol spec §5 Security)
    private static final String VALID_USER = "admin";
    private static final String VALID_PASS = "password";

    /**
     * Attempt a state transition given the raw message string from the client.
     * Expected message formats:
     *   HELLO
     *   AUTH <username> <password>
     *   JOIN
     *   START
     *   ACTION <payload>
     *   QUIT
     *
     * @param message raw message line received from client
     * @return the next State if transition is valid
     * @throws InvalidTransitionException if the message is not valid in this state
     */
    public State transition(String message) throws InvalidTransitionException {
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidTransitionException("Empty message received in state " + this);
        }

        String[] parts = message.trim().split("\\s+", 3);
        String command = parts[0].toUpperCase();

        switch (this) {
            case CONNECTED:
                if (command.equals("HELLO")) {
                    return HANDSHAKED;
                }
                break;

            case HANDSHAKED:
                if (command.equals("AUTH")) {
                    if (parts.length < 3) {
                        throw new InvalidTransitionException("AUTH requires username and password: AUTH <user> <pass>");
                    }
                    String user = parts[1];
                    String pass = parts[2];
                    if (user.equals(VALID_USER) && pass.equals(VALID_PASS)) {
                        return AUTHENTICATED;
                    } else {
                        throw new InvalidTransitionException("AUTH_FAIL: Invalid credentials");
                    }
                }
                break;

            case AUTHENTICATED:
                if (command.equals("JOIN")) {
                    return IN_LOBBY;
                }
                break;

            case IN_LOBBY:
                if (command.equals("START")) {
                    return IN_GAME;
                }
                break;

            case IN_GAME:
                if (command.equals("ACTION")) {
                    return IN_GAME; // stays in IN_GAME, payload handled by server
                }
                if (command.equals("QUIT")) {
                    return TERMINATED;
                }
                break;

            case TERMINATED:
                throw new InvalidTransitionException("Session is terminated. No further messages accepted.");

            default:
                break;
        }

        throw new InvalidTransitionException(
            "Invalid command '" + command + "' in state " + this
        );
    }

    /**
     * Returns true if this state can still accept messages.
     */
    public boolean isActive() {
        return this != TERMINATED;
    }

    /**
     * Custom exception for DFA violations — carries the rejection reason
     * so the server can send a meaningful rejection message back to the client.
     */
    public static class InvalidTransitionException extends Exception {
        public InvalidTransitionException(String reason) {
            super(reason);
        }
    }
}