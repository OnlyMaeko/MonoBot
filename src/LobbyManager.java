import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * LobbyManager — server-side store of active game lobbies.
 *
 * A Lobby has:
 *   - name:   the human-readable lobby name (e.g. "MonopolyRoom1")
 *   - owner:  the username who created it
 *   - status: WAITING (accepting players) or IN_PROGRESS
 *
 * Thread-safe via synchronized methods (future-proofs for concurrent clients).
 */
public class LobbyManager {

    public enum LobbyStatus { WAITING, IN_PROGRESS }

    public static class Lobby {
        public final String name;
        public final String owner;
        public LobbyStatus status;

        public Lobby(String name, String owner) {
            this.name   = name;
            this.owner  = owner;
            this.status = LobbyStatus.WAITING;
        }

        /**
         * Serializes this lobby as a compact JSON object.
         * Example: {"name":"Room1","owner":"admin","status":"WAITING"}
         */
        public String toJson() {
            return "{\"name\":\"" + escape(name) + "\""
                 + ",\"owner\":\"" + escape(owner) + "\""
                 + ",\"status\":\"" + status.name() + "\"}";
        }

        private String escape(String s) {
            return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }

    private final List<Lobby> lobbies = new ArrayList<>();

    /**
     * Creates a new lobby. Returns null if a lobby with that name already exists.
     *
     * @param name  lobby name
     * @param owner username of the creator
     * @return the new Lobby, or null on duplicate name
     */
    public synchronized Lobby create(String name, String owner) {
        for (Lobby l : lobbies) {
            if (l.name.equalsIgnoreCase(name)) return null; // duplicate
        }
        Lobby lobby = new Lobby(name, owner);
        lobbies.add(lobby);
        return lobby;
    }

    /**
     * Finds an existing lobby by name.
     *
     * @param name the lobby name to look up
     * @return the Lobby, or null if not found
     */
    public synchronized Lobby find(String name) {
        for (Lobby l : lobbies) {
            if (l.name.equalsIgnoreCase(name)) return l;
        }
        return null;
    }

    /**
     * Returns a snapshot of all lobbies as a JSON array string.
     * Example: [{"name":"Room1","owner":"admin","status":"WAITING"}]
     */
    public synchronized String toJsonArray() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lobbies.size(); i++) {
            sb.append(lobbies.get(i).toJson());
            if (i < lobbies.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns true if there are no lobbies yet.
     */
    public synchronized boolean isEmpty() {
        return lobbies.isEmpty();
    }

    /**
     * Marks a lobby as IN_PROGRESS when the game starts.
     *
     * @param name lobby name
     */
    public synchronized void markInProgress(String name) {
        Lobby l = find(name);
        if (l != null) l.status = LobbyStatus.IN_PROGRESS;
    }
}