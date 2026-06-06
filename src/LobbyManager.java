import java.util.ArrayList;
import java.util.List;

// This is the lobby manager that attempts to implement a lobby, due to the single machine implementation
// It is not fully implemented to control the logic of entering and creating a user name and password as those are passed by the client and server config files.
// In a future version of the game the 
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

        // Serializes this lobby as a compact JSON object. Example: {"name":"Room1","owner":"admin","status":"WAITING"}
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

    public synchronized Lobby create(String name, String owner) {
        for (Lobby l : lobbies) {
            if (l.name.equalsIgnoreCase(name)) return null; 
        }
        Lobby lobby = new Lobby(name, owner);
        lobbies.add(lobby);
        return lobby;
    }

    public synchronized Lobby find(String name) {
        for (Lobby l : lobbies) {
            if (l.name.equalsIgnoreCase(name)) return l;
        }
        return null;
    }

    //
    public synchronized String toJsonArray() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lobbies.size(); i++) {
            sb.append(lobbies.get(i).toJson());
            if (i < lobbies.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    //
    public synchronized boolean isEmpty() {
        return lobbies.isEmpty();
    }

    //
    public synchronized void markInProgress(String name) {
        Lobby l = find(name);
        if (l != null) l.status = LobbyStatus.IN_PROGRESS;
    }
}