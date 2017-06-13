package net.teamfruit.usefulbuilderswand;

import java.util.List;

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

	@Override
	public void onLoad() {
	}

	@Override
	public void onEnable() {
		final FileConfiguration config = getConfig();
		this.data.initConfig(config);
		config.options().copyDefaults(true);
		saveConfig();

		final WandListener listener = new WandListener(this, this.data);
		getServer().getPluginManager().registerEvents(listener, this);
		getCommand("usefulbuilderswand").setExecutor(listener);
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		return null;
	}
}
