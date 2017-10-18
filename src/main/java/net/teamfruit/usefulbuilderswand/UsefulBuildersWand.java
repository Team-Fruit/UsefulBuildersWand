package net.teamfruit.usefulbuilderswand;

import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class UsefulBuildersWand extends JavaPlugin {
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		return false;
	}

	@Override
	public void onDisable() {

	}

	private WandData data = new WandData();
	private WandListener listener;

	private WandListener getWandListener() {
		if (this.listener==null)
			this.listener = new WandListener(this, this.data);
		return this.listener;
	}

	public @Nonnull UsefulBuildersWandAPI getAPI() {
		return getWandListener();
	}

	@Override
	public void onLoad() {
	}

	@Override
	public void onEnable() {
		Log.log = getLogger();
		final FileConfiguration config = getConfig();
		this.data.initConfig(config);
		config.options().copyDefaults(true);
		saveConfig();

		final WandListener listener = getWandListener();
		getServer().getPluginManager().registerEvents(listener, this);
		getCommand("usefulbuilderswand").setExecutor(listener);
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		return null;
	}
}
