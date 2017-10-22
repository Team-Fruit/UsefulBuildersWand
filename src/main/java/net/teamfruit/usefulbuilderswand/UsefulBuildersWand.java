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

	@Override
	public void onLoad() {
	}

	@Override
	public void onEnable() {
		Log.log = getLogger();
		final FileConfiguration config = getConfig();
		final WandData wanddata = new WandData();
		wanddata.initConfig(config);
		config.options().copyDefaults(true);
		saveConfig();

		final NativeMinecraft nativemc = NativeMinecraft.NativeMinecraftFactory.create(this);

		final WandListener listener = new WandListener(this, wanddata, nativemc);
		getServer().getPluginManager().registerEvents(listener, this);

		final CommandListener cmdlistener = new CommandListener(wanddata, nativemc);
		getCommand("usefulbuilderswand").setExecutor(cmdlistener);
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		return null;
	}
}
