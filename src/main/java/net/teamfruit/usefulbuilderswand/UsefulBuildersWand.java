package net.teamfruit.usefulbuilderswand;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new WandListener(this), this);
	}

	@Override
	public void onLoad() {
		super.onLoad();
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		return null;
	}
}
