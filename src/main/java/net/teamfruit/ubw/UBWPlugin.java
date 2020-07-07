package net.teamfruit.ubw;

import net.teamfruit.ubw.I18n.Locale;
import net.teamfruit.ubw.I18n.Locale.LocaleBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UBWPlugin extends JavaPlugin {
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

        WandData.INSTANCE.initConfig(config);
        config.options().copyDefaults(true);
        saveConfig();

        final String langdef = (String) WandData.getInitDefault(WandData.SETTING_LANG);
        final String langcfg = config.getString(WandData.SETTING_LANG, langdef);
        final Locale locale = getLocale(langdef, langcfg);

        final NativeMinecraft nativemc = NativeMinecraft.NativeMinecraftFactory.create(this);

        final WandListener listener = new WandListener(this, locale, nativemc);
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private Locale getLocale(final String langdef, final String langcfg) {
        final LocaleBuilder lcb = new LocaleBuilder();
        final File langDir = new File(getDataFolder(), "lang");
        final File pluginFile = getFile();
        ZipFile pluginZip = null;
        try {
            pluginZip = new ZipFile(pluginFile);
            final File resdef = new File(langDir, langdef);
            final File res = new File(langDir, langcfg);
            final ZipEntry entrydef = pluginZip.getEntry("lang/" + langdef);
            final ZipEntry entry = pluginZip.getEntry("lang/" + langcfg);
            if (resdef.exists())
                try {
                    lcb.fromInputStream(new BufferedInputStream(new FileInputStream(resdef)));
                } catch (final IOException e) {
                }
            if (entrydef != null)
                try {
                    lcb.fromInputStream(pluginZip.getInputStream(entrydef));
                } catch (final IOException e) {
                }
            if (res.exists())
                try {
                    lcb.fromInputStream(new BufferedInputStream(new FileInputStream(res)));
                } catch (final IOException e) {
                }
            if (entry != null)
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
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (command.getName().equalsIgnoreCase("wand")) {
            if (!sender.hasPermission(WandData.PERMISSION_WAND_GRANT)) {
                sender.sendMessage("You don't have permission to do that");
                return true;
            }
            if (args.length < 2)
                return false;
            if ("on".equalsIgnoreCase(args[0])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        return null;
    }
}
