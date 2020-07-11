package net.teamfruit.ubw;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class WandSessionManager {
    private final Map<UUID, SessionHolder> sessions = new HashMap<>();

    protected UUID getKey(Player key) {
        return key.getUniqueId();
    }

    @Nullable
    public synchronized WandSession getIfPresent(Player owner) {
        checkNotNull(owner);
        SessionHolder stored = sessions.get(getKey(owner));
        if (stored != null) {
            return stored.session;
        } else {
            return null;
        }
    }

    public synchronized WandSession get(Player owner) {
        checkNotNull(owner);

        WandSession session = getIfPresent(owner);

        // No session exists yet -- create one
        if (session == null) {
            session = new WandSession();

            // Remember the session regardless of if it's currently active or not.
            // And have the SessionTracker FLUSH inactive sessions.
            sessions.put(getKey(owner), new SessionHolder(session));
        }

        return session;
    }

    private static final class SessionHolder {
        private final WandSession session;

        private SessionHolder(WandSession session) {
            this.session = session;
        }
    }
}
