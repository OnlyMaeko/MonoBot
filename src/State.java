/**
 * Monobot Protocol — DFA State Machine (v2)
 *
 * States and valid transitions:
 *
 *   CONNECTED     --[01 HELLO]----------->  HANDSHAKED
 *   HANDSHAKED    (server sends 04 AUTH_REQUESTED automatically)
 *   HANDSHAKED    --[AUTH user pass]------>  AWAITING_AUTH  (internal — server sends AUTH_REQUESTED first)
 *   AWAITING_AUTH --[AUTH user pass]------>  AUTHENTICATED  (on good creds → AUTH_OK)
 *   AWAITING_AUTH --[AUTH user pass]------>  AWAITING_AUTH  (on bad creds  → AUTH_FAIL, retry allowed)
 *   AUTHENTICATED --[09 LOBBY_LIST_REQ]--> AUTHENTICATED   (server replies 10 LOBBY_STATE)
 *   AUTHENTICATED --[07 LOBBY_CREATE]----> IN_LOBBY        (server replies 10 LOBBY_STATE)
 *   AUTHENTICATED --[08 LOBBY_JOIN]------> IN_LOBBY        (server replies 10 LOBBY_STATE)
 *   IN_LOBBY      --[11 GAME_START]------> IN_GAME
 *   IN_GAME       --[13 TURN_NOTIFY]-----> IN_GAME
 *   IN_GAME       --[12 GAME_OVER]-------> TERMINATED
 *   ANY           --[14 DISCONNECT]------> TERMINATED
 *
 * PDU codes match the protocol spec table:
 *   01 HELLO            02 HELLO_ACK        03 HELLO_REJ
 *   04 AUTH_REQUESTED   05 AUTH_OK          06 AUTH_FAIL
 *   07 LOBBY_CREATE     08 LOBBY_JOIN       09 LOBBY_LIST_REQ
 *   10 LOBBY_STATE      11 GAME_START       12 GAME_OVER
 *   13 TURN_NOTIFY      14 DISCONNECT       15 ERROR_TERMINATION
 */
public enum State {
    CONNECTED,
    HANDSHAKED,
    AWAITING_AUTH,
    AUTHENTICATED,
    IN_LOBBY,
    IN_GAME,
    TERMINATED;

    // ── PDU code constants ────────────────────────────────────────────────────
    public static final String HELLO           = "01";
    public static final String HELLO_ACK       = "02";
    public static final String HELLO_REJ       = "03";
    public static final String AUTH_REQUESTED  = "04";
    public static final String AUTH_OK         = "05";
    public static final String AUTH_FAIL       = "06";
    public static final String LOBBY_CREATE    = "07";
    public static final String LOBBY_JOIN      = "08";
    public static final String LOBBY_LIST_REQ  = "09";
    public static final String LOBBY_STATE     = "10";
    public static final String GAME_START      = "11";
    public static final String GAME_OVER       = "12";
    public static final String TURN_NOTIFY     = "13";
    public static final String DISCONNECT      = "14";
    public static final String ERROR_TERM      = "15";

    /**
     * Result of a transition attempt. Carries the next state plus whether
     * authentication specifically failed (so the server can send AUTH_FAIL
     * without advancing state).
     */
    public static class TransitionResult {
        public final State     nextState;
        public final boolean   authFailed;   // true → send AUTH_FAIL, stay in AWAITING_AUTH
        public final String    payload;       // optional extra data (e.g. lobby name, username)

        public TransitionResult(State nextState, boolean authFailed, String payload) {
            this.nextState  = nextState;
            this.authFailed = authFailed;
            this.payload    = payload;
        }
    }

    /**
     * Attempt a DFA transition given a raw message line from the client.
     *
     * Message format:  <code> [payload...]
     * Examples:
     *   "01"                      → HELLO
     *   "AUTH admin password"     → AUTH (code implicit in AWAITING_AUTH state)
     *   "09"                      → LOBBY_LIST_REQ
     *   "07 MyRoom"               → LOBBY_CREATE with name "MyRoom"
     *   "08 MyRoom"               → LOBBY_JOIN with name "MyRoom"
     *   "11"                      → GAME_START
     *   "13 {json...}"            → TURN_NOTIFY with JSON payload
     *   "12"                      → GAME_OVER
     *   "14"                      → DISCONNECT
     *
     * @param message raw message line from client
     * @return TransitionResult with nextState and optional payload
     * @throws InvalidTransitionException if the message is invalid in this state
     */
    public TransitionResult transition(String message, String validUser, String validPass) throws InvalidTransitionException {
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidTransitionException("Empty message in state " + this);
        }

        // DISCONNECT is valid from any non-terminated state
        String code = message.trim().split("\\s+")[0];
        if (DISCONNECT.equals(code)) {
            return new TransitionResult(TERMINATED, false, null);
        }

        String[] parts   = message.trim().split("\\s+", 2);
        String   payload = parts.length > 1 ? parts[1].trim() : "";

        switch (this) {
            case CONNECTED:
                if (HELLO.equals(code)) {
                    return new TransitionResult(HANDSHAKED, false, null);
                }
                break;

            // HANDSHAKED is a transient state — server immediately sends AUTH_REQUESTED
            // then moves its own internal tracking to AWAITING_AUTH before the client responds.
            // We model this by treating HANDSHAKED as immediately transitioning to AWAITING_AUTH
            // on the server side (no client message needed for this hop).

            case AWAITING_AUTH:
                // Client sends: AUTH <username> <password>
                if ("AUTH".equalsIgnoreCase(code)) {
                    String[] creds = payload.split("\\s+", 2);
                    if (creds.length < 2) {
                        throw new InvalidTransitionException("AUTH requires: AUTH <username> <password>");
                    }
                    String user = creds[0];
                    String pass = creds[1];
                    if (validUser.equals(user) && validPass.equals(pass)) {
                        return new TransitionResult(AUTHENTICATED, false, user);
                    } else {
                        // Stay in AWAITING_AUTH — client may retry
                        return new TransitionResult(AWAITING_AUTH, true, null);
                    }
                }
                break;

            case AUTHENTICATED:
                if (LOBBY_LIST_REQ.equals(code)) {
                    return new TransitionResult(AUTHENTICATED, false, null); // no state change
                }
                if (LOBBY_CREATE.equals(code)) {
                    if (payload.isEmpty()) {
                        throw new InvalidTransitionException("LOBBY_CREATE requires a lobby name");
                    }
                    return new TransitionResult(IN_LOBBY, false, payload);
                }
                if (LOBBY_JOIN.equals(code)) {
                    if (payload.isEmpty()) {
                        throw new InvalidTransitionException("LOBBY_JOIN requires a lobby name");
                    }
                    return new TransitionResult(IN_LOBBY, false, payload);
                }
                break;

            case IN_LOBBY:
                if (GAME_START.equals(code)) {
                    return new TransitionResult(IN_GAME, false, null);
                }
                break;

            case IN_GAME:
                if (TURN_NOTIFY.equals(code)) {
                    return new TransitionResult(IN_GAME, false, payload); // JSON payload
                }
                if (GAME_OVER.equals(code)) {
                    return new TransitionResult(TERMINATED, false, null);
                }
                break;

            case TERMINATED:
                throw new InvalidTransitionException("Session already terminated");

            default:
                break;
        }

        throw new InvalidTransitionException(
            "Invalid PDU code '" + code + "' in state " + this
        );
    }

    /** Returns true if this state can still accept messages. */
    public boolean isActive() {
        return this != TERMINATED;
    }

    // ── Exception ─────────────────────────────────────────────────────────────

    public static class InvalidTransitionException extends Exception {
        public InvalidTransitionException(String reason) {
            super(reason);
        }
    }
}