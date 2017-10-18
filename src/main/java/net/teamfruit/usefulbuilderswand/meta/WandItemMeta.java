package net.teamfruit.usefulbuilderswand.meta;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

import net.teamfruit.usefulbuilderswand.ItemStackHolder;
import net.teamfruit.usefulbuilderswand.WandData;
import net.teamfruit.usefulbuilderswand.WandData.AbstractData;
import net.teamfruit.usefulbuilderswand.WandData.AbstractSettings;
import net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi.NBTItem;

public class WandItemMeta implements ItemStackHolder {
	private NBTItem nbtItem;
	private WandItemMetaData data;

	public WandItemMeta(final ItemStack item) {
		super();
		this.nbtItem = new NBTItem(item);
	}

	public void activate() {
		if (!hasContent()) {
			this.nbtItem.addCompound(WandData.USEFUL_BUILDERS_WAND_NBT);
			init();
		}
	}

	public boolean hasContent() {
		return this.nbtItem.hasKey(WandData.USEFUL_BUILDERS_WAND_NBT);
	}

	public boolean init() {
		if (this.data==null)
			this.data = new WandItemMetaData(this.nbtItem.getCompound(WandData.USEFUL_BUILDERS_WAND_NBT));
		return this.data!=null;
	}

	public void updateItem(final WandData wanddata) {
		if (init()) {
			final ItemStack item = this.nbtItem.getItem();
			final ItemMeta meta = item.getItemMeta();
			final FileConfiguration cfg = wanddata.getConfig();
			final AbstractData data = new AbstractData.ItemData(this.data.nbt);
			final AbstractSettings settings = new AbstractSettings.ConfigSettings(cfg);

			Object itemtitle = cfg.get(WandData.ITEM_TITLE);
			if (itemtitle==null)
				itemtitle = WandData.it.get(WandData.ITEM_TITLE);
			if (itemtitle instanceof String)
				meta.setDisplayName(settings.resolve(data, (String) itemtitle));

			Object itemlore = cfg.get(WandData.ITEM_LORE);
			if (itemlore==null)
				itemlore = WandData.it.get(WandData.ITEM_LORE);
			if (itemlore instanceof List<?>) {
				final List<String> newlore = Lists.newArrayList();
				for (final Object obj : (List<?>) itemlore)
					if (obj instanceof String) {
						final String res = settings.resolve(data, (String) obj);
						if (!StringUtils.isEmpty(res))
							newlore.add(res);
					}
				meta.setLore(newlore);
			}
			item.setItemMeta(meta);
		}
	}

	@Override
	public ItemStack getItem() {
		return this.nbtItem.getItem();
	}

	@Override
	public void setItem(final ItemStack itemStack) {
		this.nbtItem = new NBTItem(itemStack);
		this.data = null;
	}

	public @Nullable Integer getNumber(final String key) {
		if (init())
			return this.data.getNumber(key);
		return null;
	}

	public Integer getNumber(final String key, final Integer defaultValue) {
		if (init())
			return this.data.getNumber(key, defaultValue);
		return null;
	}

	public void setNumber(final String key, @Nullable final Integer value) {
		if (init())
			this.data.setNumber(key, value);
	}

	public @Nullable String getText(final String key) {
		if (init())
			return this.data.getText(key);
		return null;
	}

	public String getText(final String key, final String defaultValue) {
		if (init())
			return this.data.getText(key, defaultValue);
		return null;
	}

	public void setText(final String key, @Nullable final String value) {
		if (init())
			this.data.setText(key, value);
	}

	public @Nullable Boolean getFlag(final String key) {
		if (init())
			return this.data.getFlag(key);
		return null;
	}

	public Boolean getFlag(final String key, final Boolean defaultValue) {
		if (init())
			return this.data.getFlag(key, defaultValue);
		return null;
	}

	public void setFlag(final String key, @Nullable final Boolean value) {
		if (init())
			this.data.setFlag(key, value);
	}

	public Object get(final Features ft) {
		if (init())
			return this.data.get(ft);
		return null;
	}

	public Object get(final Features ft, final Object defaultValue) {
		if (init())
			return this.data.get(ft, defaultValue);
		return null;
	}

	public void set(final Features ft, @Nullable final Object value) {
		if (init())
			this.data.set(ft, value);
	}
}