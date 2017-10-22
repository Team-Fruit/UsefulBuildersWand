package net.teamfruit.usefulbuilderswand.meta;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

public class WandConfigMeta implements IWandMeta {
	private final FileConfiguration cfg;

	public WandConfigMeta(final FileConfiguration cfg) {
		this.cfg = cfg;
	}

	private @Nullable Object get(final String path) {
		if (this.cfg.isConfigurationSection(path)) {
			if (!StringUtils.endsWith(path, ".data"))
				return get(path+".data");
		} else
			return this.cfg.get(path);
		return null;
	}

	@Override
	public WandItemMetaType getType(final String key) {
		return WandMetaUtils.getType(get(key));
	}

	@Override
	public Integer getNumber(final String path) {
		return WandMetaUtils.toNumber(get(path));
	}

	@Override
	public String getText(final String path) {
		return WandMetaUtils.toText(get(path));
	}

	@Override
	public Boolean getFlag(final String path) {
		return WandMetaUtils.toFlag(get(path));
	}
}