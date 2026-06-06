public enum State {
    CONNECTED,
    HANDSHAKED,
    AWAITING_AUTH,
    AUTHENTICATED,
    IN_LOBBY,
    IN_GAME,
    TERMINATED;

    public static final String HELLO          = "01";
    public static final String HELLO_ACK      = "02";
    public static final String HELLO_REJ      = "03";
    public static final String AUTH_REQUESTED = "04";
    public static final String AUTH_OK        = "05";
    public static final String AUTH_FAIL      = "06";
    public static final String LOBBY_CREATE   = "07";
    public static final String LOBBY_JOIN     = "08";
    public static final String LOBBY_LIST_REQ = "09";
    public static final String LOBBY_STATE    = "10";
    public static final String GAME_START     = "11";
    public static final String GAME_OVER      = "12";
    public static final String TURN_NOTIFY    = "13";
    public static final String DISCONNECT     = "14";
    public static final String ERROR_TERM     = "99";

    public static class TransitionResult {
        public final State nextState;
        public final boolean authFailed;
        public final String payload;

        public TransitionResult(State nextState, boolean authFailed, String payload) {
            this.nextState = nextState;
            this.authFailed = authFailed;
            this.payload = payload;
        }
    }

    public TransitionResult transition(String incomingLine, String validUser, String validPass) throws InvalidTransitionException {
        String[] tokens = incomingLine.trim().split("\\s+", 2);
        String code = tokens[0];
        String payload = (tokens.length > 1) ? tokens[1].trim() : "";

        if (DISCONNECT.equals(code)) {
            return new TransitionResult(TERMINATED, false, null);
        }

        switch (this) {
            case CONNECTED:
                if (HELLO.equals(code)) {
                    return new TransitionResult(HANDSHAKED, false, null);
                }
                break;

            case AWAITING_AUTH:
                if ("AUTH".equalsIgnoreCase(code)) {
                    String[] creds = payload.split("\\s+");
                    if (creds.length >= 2 && creds[0].equals(validUser) && creds[1].equals(validPass)) {
                        return new TransitionResult(AUTHENTICATED, false, creds[0]);
                    } else {
                        return new TransitionResult(AWAITING_AUTH, true, null);
                    }
                }
                break;

            case AUTHENTICATED:
                if (LOBBY_LIST_REQ.equals(code)) {
                    return new TransitionResult(AUTHENTICATED, false, null);
                }
                if (LOBBY_CREATE.equals(code)) {
                    if (payload.isEmpty()) throw new InvalidTransitionException("LOBBY_CREATE missing room name");
                    return new TransitionResult(IN_LOBBY, false, payload);
                }
                if (LOBBY_JOIN.equals(code)) {
                    if (payload.isEmpty()) throw new InvalidTransitionException("LOBBY_JOIN missing room name");
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
                    return new TransitionResult(IN_GAME, false, payload);
                }
                if (GAME_OVER.equals(code)) {
                    return new TransitionResult(TERMINATED, false, null);
                }
                break;

            case TERMINATED:
                throw new InvalidTransitionException("Session already closed");
        }

        throw new InvalidTransitionException("Invalid protocol sequence '" + code + "' during state " + this);
    }

    public boolean isActive() {
        return this != TERMINATED;
    }

    public static class InvalidTransitionException extends Exception {
        public InvalidTransitionException(String reason) {
            super(reason);
        }
    }
}