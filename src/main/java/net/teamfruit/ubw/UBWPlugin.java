package net.teamfruit.ubw;

import net.teamfruit.ubw.I18n.Locale;
import net.teamfruit.ubw.I18n.Locale.LocaleBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UBWPlugin extends JavaPlugin {
    public static NativeMinecraft nativemc;

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

        nativemc = NativeMinecraft.NativeMinecraftFactory.create(this);

        final WandListener listener = new WandListener(this, locale);
        getServer().getPluginManager().registerEvents(listener, this);
        getCommand("wand").setExecutor(listener);
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
}
