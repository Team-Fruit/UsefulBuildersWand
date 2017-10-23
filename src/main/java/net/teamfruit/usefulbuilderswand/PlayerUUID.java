package net.teamfruit.usefulbuilderswand;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.util.UUIDTypeAdapter;

public class PlayerUUID {
	private static final LoadingCache<String, Optional<String>> uuid_to_name = CacheBuilder.newBuilder().build(new CacheLoader<String, Optional<String>>() {
		@Override
		public Optional<String> load(final String uuidstr) throws Exception {
			try {
				final UUID uuid = UUIDTypeAdapter.fromString(uuidstr);
				final OfflinePlayer offplayer = Bukkit.getServer().getOfflinePlayer(uuid);
				if (offplayer!=null)
					return Optional.ofNullable(offplayer.getName());
			} catch (final IllegalArgumentException e) {
			}
			return Optional.empty();
		}
	});

	public static Optional<String> getName(final String uuid) {
		try {
			return uuid_to_name.get(uuid);
		} catch (final ExecutionException e) {
		}
		return Optional.empty();
	}
}
