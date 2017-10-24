package net.teamfruit.ubw;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.teamfruit.ubw.I18n.Locale;
import net.teamfruit.ubw.I18n.Locale.LocaleBuilder;
import net.teamfruit.ubw.api.UsefulBuildersWandAPI;
import net.teamfruit.ubw.api.WandItemAPI;
import net.teamfruit.ubw.api.impl.WandItemAPIImpl;

public class UBWPlugin extends JavaPlugin implements UsefulBuildersWandAPI {
	private WandItemAPIImpl wandItemAPI;

	@Override
	public WandItemAPI itemAPI() throws IllegalStateException {
		if (this.wandItemAPI==null)
			throw new IllegalStateException("Item API is not initialized. call after initialized.");
		return this.wandItemAPI;
	}

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

		this.wandItemAPI = new WandItemAPIImpl(wanddata);

		final Locale locale = getLocale(wanddata.getConfig());

		final NativeMinecraft nativemc = NativeMinecraft.NativeMinecraftFactory.create(this);

		final WandListener listener = new WandListener(this, locale, wanddata, nativemc);
		getServer().getPluginManager().registerEvents(listener, this);

		final CommandListener cmdlistener = new CommandListener(locale, wanddata, nativemc);
		getCommand("ubw").setExecutor(cmdlistener);
	}

	private Locale getLocale(final FileConfiguration cfg) {
		final LocaleBuilder lcb = new LocaleBuilder();
		final String langdef = (String) WandData.it.get(WandData.SETTING_LANG);
		final String lang = cfg.getString(WandData.SETTING_EFFECT_RANGE, langdef);
		final File langDir = new File(getDataFolder(), "lang");
		final File pluginFile = getFile();
		ZipFile pluginZip = null;
		try {
			pluginZip = new ZipFile(pluginFile);
			final File resdef = new File(langDir, langdef);
			final File res = new File(langDir, lang);
			final ZipEntry entrydef = pluginZip.getEntry("lang/"+langdef);
			final ZipEntry entry = pluginZip.getEntry("lang/"+lang);
			if (resdef.exists())
				try {
					lcb.fromInputStream(new BufferedInputStream(new FileInputStream(resdef)));
				} catch (final IOException e) {
				}
			if (entrydef!=null)
				try {
					lcb.fromInputStream(pluginZip.getInputStream(entrydef));
				} catch (final IOException e) {
				}
			if (res.exists())
				try {
					lcb.fromInputStream(new BufferedInputStream(new FileInputStream(res)));
				} catch (final IOException e) {
				}
			if (entry!=null)
				try {
					lcb.fromInputStream(pluginZip.getInputStream(entry));
				} catch (final IOException e) {
				}
		} catch (final IOException e1) {
		} finally {
			IOUtils.closeQuietly(pluginZip);
		}
		return lcb.build();
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		return null;
	}
}
