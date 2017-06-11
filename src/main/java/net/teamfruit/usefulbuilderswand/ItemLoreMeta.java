package net.teamfruit.usefulbuilderswand;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.teamfruit.usefulbuilderswand.ItemLoreMeta.ItemLoreDataFormat.FlagMeta;
import net.teamfruit.usefulbuilderswand.ItemLoreMeta.ItemLoreDataFormat.NumberMeta;
import net.teamfruit.usefulbuilderswand.ItemLoreMeta.ItemLoreDataFormat.TextMeta;

public class ItemLoreMeta {
	private final Map<String, Integer> dataNumber = Maps.newHashMap();
	private final Map<String, String> dataText = Maps.newHashMap();
	private final Set<String> dataFlag = Sets.newHashSet();

	public ItemLoreMeta() {
	}

	public static List<String> contentsFromLore(final ItemLoreDataFormat format, final List<String> lore) {
		final List<String> lines = Lists.newArrayList();
		for (final ListIterator<String> itr = lore.listIterator(); itr.hasNext();) {
			final String line = itr.next();
			if (StringUtils.startsWith(line, format.prefix)) {
				final String data = StringUtils.substringAfter(line, format.prefix);
				lines.add(data);
			}
		}
		return lines;
	}

	public static void contentsToLore(final ItemLoreDataFormat format, final List<String> lore, final List<String> contents) {
		if (!lore.isEmpty()) {
			final int loresize = lore.size();
			int i = 0;
			boolean done = false;
			for (final ListIterator<String> itr = lore.listIterator(); itr.hasNext(); i++) {
				boolean flag = i+1==loresize;
				final String line = itr.next();
				if (StringUtils.startsWith(line, format.prefix)) {
					itr.remove();
					flag = true;
				}
				if (flag&&!done) {
					for (final String content : contents)
						itr.add(format.prefix+content);
					done = true;
				}
			}
		} else
			for (final String content : contents)
				lore.add(format.prefix+content);
	}

	public static void metaFromContents(final ItemLoreDataFormat format, final ItemLoreMeta meta, final List<String> contents) {
		for (final ListIterator<String> itr = contents.listIterator(); itr.hasNext();) {
			final String line = itr.next();
			String data = line;
			readvalue: while (!StringUtils.isEmpty(data = StringUtils.substringAfter(data, format.valueprefix))) {
				final String current = StringUtils.substringBefore(data, format.valuesuffix);
				for (final Entry<String, FlagMeta> entry : format.typeFlag.entrySet()) {
					final String typeFlag = entry.getKey();
					if (StringUtils.startsWith(current, typeFlag)) {
						final String dataValue = StringUtils.substringAfter(current, typeFlag);

						if (meta!=null)
							meta.setFlag(typeFlag, entry.getValue().parse(format, dataValue));
						continue readvalue;
					}
				}
				for (final Entry<String, NumberMeta> entry : format.typeNumber.entrySet()) {
					final String typeNumber = entry.getKey();
					if (StringUtils.startsWith(current, typeNumber)) {
						final String dataValue = StringUtils.substringAfter(current, typeNumber);

						if (meta!=null)
							meta.setNumber(typeNumber, entry.getValue().parse(format, dataValue));
						continue readvalue;
					}
				}
				for (final Entry<String, TextMeta> entry : format.typeText.entrySet()) {
					final String typeText = entry.getKey();
					if (StringUtils.startsWith(current, typeText)) {
						final String dataValue = StringUtils.substringAfter(current, typeText);

						if (meta!=null)
							meta.setText(typeText, entry.getValue().parse(format, dataValue));
						continue readvalue;
					}
				}
			}
		}
	}

	public static List<String> metaToContents(final ItemLoreDataFormat format, final ItemLoreMeta meta) {
		final List<String> contents = Lists.newArrayList();
		for (final String line : format.metaFormat) {
			final StringBuilder stb = new StringBuilder();
			String data = line;
			while (true) {
				stb.append(StringUtils.substringBefore(data, "$"));
				if ((data = StringUtils.substringAfter(data, "$")).isEmpty())
					break;
				if (StringUtils.startsWith(data, "$"))
					data = StringUtils.substringAfter(data, "$");
				else if (StringUtils.startsWith(data, "{")) {
					final String current = StringUtils.substringBetween(data, "{", "}");
					final String type = StringUtils.substringBefore(current, ":");
					final String namevalue = StringUtils.substringAfter(current, ":");
					final String name = StringUtils.substringBefore(namevalue, "=");
					if (StringUtils.equals(type, "B")) {
						final String s = format.typeFlag.get(name).compose(format, meta.getFlag(name));
						if (s!=null)
							stb.append(format.valueprefix).append(name).append(s).append(format.valuesuffix);
					} else if (StringUtils.equals(type, "I")) {
						final String s = format.typeNumber.get(name).compose(format, meta.getNumber(name));
						if (s!=null)
							stb.append(format.valueprefix).append(name).append(s).append(format.valuesuffix);
					} else if (StringUtils.equals(type, "S")) {
						final String s = format.typeText.get(name).compose(format, meta.getText(name));
						if (s!=null)
							stb.append(format.valueprefix).append(name).append(s).append(format.valuesuffix);
					}
					data = StringUtils.substringAfter(data, "}");
				}
			}
			contents.add(stb.toString());
		}
		return contents;
	}

	public void fromItemStack(final ItemLoreDataFormat format, final ItemStack itemStack) {
		fromLore(format, itemStack.getItemMeta().getLore());
	}

	public void toItemStack(final ItemLoreDataFormat format, final ItemStack itemStack) {
		final ItemMeta meta = itemStack.getItemMeta();
		final List<String> lore = meta.getLore();
		toLore(format, lore);
		meta.setLore(lore);
		itemStack.setItemMeta(meta);
	}

	public void fromLore(final ItemLoreDataFormat format, final List<String> lore) {
		final List<String> contents = contentsFromLore(format, lore);
		metaFromContents(format, this, contents);
	}

	public void toLore(final ItemLoreDataFormat format, final List<String> lore) {
		final List<String> contents = metaToContents(format, this);
		contentsToLore(format, lore, contents);
	}

	public @Nullable Integer getNumber(final String key) {
		return this.dataNumber.get(key);
	}

	public void setNumber(final String key, @Nullable final Integer value) {
		if (key!=null)
			this.dataNumber.put(key, value);
		else
			this.dataNumber.remove(key);
	}

	public @Nullable String getText(final String key) {
		return this.dataText.get(key);
	}

	public void setText(final String key, @Nullable final String value) {
		if (key!=null)
			this.dataText.put(key, value);
		else
			this.dataText.remove(key);
	}

	public boolean getFlag(final String key) {
		return this.dataFlag.contains(key);
	}

	public void setFlag(final String key, final boolean value) {
		if (value)
			this.dataFlag.add(key);
		else
			this.dataFlag.remove(key);
	}

	@Override
	public String toString() {
		return String.format("ItemLoreMeta [dataNumber=%s, dataText=%s, dataFlag=%s]", this.dataNumber, this.dataText, this.dataFlag);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime*result+(this.dataFlag==null ? 0 : this.dataFlag.hashCode());
		result = prime*result+(this.dataNumber==null ? 0 : this.dataNumber.hashCode());
		result = prime*result+(this.dataText==null ? 0 : this.dataText.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this==obj)
			return true;
		if (obj==null)
			return false;
		if (!(obj instanceof ItemLoreMeta))
			return false;
		final ItemLoreMeta other = (ItemLoreMeta) obj;
		if (this.dataFlag==null) {
			if (other.dataFlag!=null)
				return false;
		} else if (!this.dataFlag.equals(other.dataFlag))
			return false;
		if (this.dataNumber==null) {
			if (other.dataNumber!=null)
				return false;
		} else if (!this.dataNumber.equals(other.dataNumber))
			return false;
		if (this.dataText==null) {
			if (other.dataText!=null)
				return false;
		} else if (!this.dataText.equals(other.dataText))
			return false;
		return true;
	}

	public static class ItemLoreDataFormat {
		public final String prefix;
		public final String valueprefix;
		public final String valuesuffix;
		public final Map<String, NumberMeta> typeNumber;
		public final Map<String, TextMeta> typeText;
		public final Map<String, FlagMeta> typeFlag;
		public final List<String> metaFormat;

		public ItemLoreDataFormat(final String prefix, final String valueprefix, final String valuesuffix, final Map<String, NumberMeta> typeNumber, final Map<String, TextMeta> typeText, final Map<String, FlagMeta> typeFlag, final List<String> metaFormat) {
			this.prefix = prefix;
			this.valueprefix = valueprefix;
			this.valuesuffix = valuesuffix;
			this.typeNumber = typeNumber;
			this.typeText = typeText;
			this.typeFlag = typeFlag;
			this.metaFormat = metaFormat;
		}

		public ItemLoreDataFormat(final String prefix, final String valueprefix, final String valueend, final List<String> metaFormat) {
			this.prefix = prefix;
			this.valueprefix = valueprefix;
			this.valuesuffix = valueend;
			this.typeNumber = Maps.newHashMap();
			this.typeText = Maps.newHashMap();
			this.typeFlag = Maps.newHashMap();
			for (final String line : metaFormat) {
				String data = line;
				while (!(data = StringUtils.substringAfter(data, "$")).isEmpty())
					if (StringUtils.startsWith(data, "$"))
						data = StringUtils.substringAfter(data, "$");
					else if (StringUtils.startsWith(data, "{")) {
						final String current = StringUtils.substringBetween(data, "{", "}");
						final String type = StringUtils.substringBefore(current, ":");
						final String namevalue = StringUtils.substringAfter(current, ":");
						final String name = StringUtils.substringBefore(namevalue, "=");
						final String constant = StringUtils.substringAfter(namevalue, "=");
						if (StringUtils.equals(type, "B"))
							this.typeFlag.put(name, FlagMeta.Factory.create(constant));
						else if (StringUtils.equals(type, "I"))
							this.typeNumber.put(name, NumberMeta.Factory.create(constant));
						else if (StringUtils.equals(type, "S"))
							this.typeText.put(name, TextMeta.Factory.create(constant));
						data = StringUtils.substringAfter(data, "}");
					}
			}
			this.metaFormat = metaFormat;
		}

		@Override
		public String toString() {
			return String.format("ItemLoreDataFormat [prefix=%s, valueprefix=%s, valuesuffix=%s, typeNumber=%s, typeText=%s, typeFlag=%s, metaFormat=%s]", this.prefix, this.valueprefix, this.valuesuffix, this.typeNumber, this.typeText, this.typeFlag, this.metaFormat);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime*result+(this.metaFormat==null ? 0 : this.metaFormat.hashCode());
			result = prime*result+(this.prefix==null ? 0 : this.prefix.hashCode());
			result = prime*result+(this.typeFlag==null ? 0 : this.typeFlag.hashCode());
			result = prime*result+(this.typeNumber==null ? 0 : this.typeNumber.hashCode());
			result = prime*result+(this.typeText==null ? 0 : this.typeText.hashCode());
			result = prime*result+(this.valueprefix==null ? 0 : this.valueprefix.hashCode());
			result = prime*result+(this.valuesuffix==null ? 0 : this.valuesuffix.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this==obj)
				return true;
			if (obj==null)
				return false;
			if (!(obj instanceof ItemLoreDataFormat))
				return false;
			final ItemLoreDataFormat other = (ItemLoreDataFormat) obj;
			if (this.metaFormat==null) {
				if (other.metaFormat!=null)
					return false;
			} else if (!this.metaFormat.equals(other.metaFormat))
				return false;
			if (this.prefix==null) {
				if (other.prefix!=null)
					return false;
			} else if (!this.prefix.equals(other.prefix))
				return false;
			if (this.typeFlag==null) {
				if (other.typeFlag!=null)
					return false;
			} else if (!this.typeFlag.equals(other.typeFlag))
				return false;
			if (this.typeNumber==null) {
				if (other.typeNumber!=null)
					return false;
			} else if (!this.typeNumber.equals(other.typeNumber))
				return false;
			if (this.typeText==null) {
				if (other.typeText!=null)
					return false;
			} else if (!this.typeText.equals(other.typeText))
				return false;
			if (this.valueprefix==null) {
				if (other.valueprefix!=null)
					return false;
			} else if (!this.valueprefix.equals(other.valueprefix))
				return false;
			if (this.valuesuffix==null) {
				if (other.valuesuffix!=null)
					return false;
			} else if (!this.valuesuffix.equals(other.valuesuffix))
				return false;
			return true;
		}

		public static interface FlagMeta {
			boolean parse(ItemLoreDataFormat format, String src);

			String compose(ItemLoreDataFormat format, boolean data);

			public static class HiddenFlagMeta implements FlagMeta {
				public HiddenFlagMeta() {
				}

				public boolean parse(final ItemLoreDataFormat format, final String src) {
					return true;
				}

				public @Nullable String compose(final ItemLoreDataFormat format, final boolean data) {
					return data ? "" : null;
				}

				@Override
				public String toString() {
					return String.format("HiddenFlagMeta []");
				}

				@Override
				public int hashCode() {
					return 0;
				}

				@Override
				public boolean equals(final Object obj) {
					return obj instanceof HiddenFlagMeta;
				}
			}

			public static class TextFlagMeta implements FlagMeta {
				private final String strTrue;
				private final String strFalse;

				public TextFlagMeta(final String strTrue, final String strFalse) {
					this.strTrue = strTrue;
					this.strFalse = strFalse;
				}

				public boolean parse(final ItemLoreDataFormat format, final String src) {
					return StringUtils.equalsIgnoreCase(src, strTrue);
				}

				public @Nullable String compose(final ItemLoreDataFormat format, final boolean data) {
					return data ? strTrue : strFalse;
				}

				@Override
				public String toString() {
					return String.format("TextFlagMeta [strTrue=%s, strFalse=%s]", strTrue, strFalse);
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime*result+(strFalse==null ? 0 : strFalse.hashCode());
					result = prime*result+(strTrue==null ? 0 : strTrue.hashCode());
					return result;
				}

				@Override
				public boolean equals(final Object obj) {
					if (this==obj)
						return true;
					if (obj==null)
						return false;
					if (!(obj instanceof TextFlagMeta))
						return false;
					final TextFlagMeta other = (TextFlagMeta) obj;
					if (strFalse==null) {
						if (other.strFalse!=null)
							return false;
					} else if (!strFalse.equals(other.strFalse))
						return false;
					if (strTrue==null) {
						if (other.strTrue!=null)
							return false;
					} else if (!strTrue.equals(other.strTrue))
						return false;
					return true;
				}
			}

			public static class Factory {
				public static FlagMeta create(final String format) {
					if (!StringUtils.isEmpty(format)) {
						final String[] selectable = StringUtils.split(format, ":");
						if (selectable.length>=2)
							return new TextFlagMeta(selectable[0], selectable[1]);
					}
					return new HiddenFlagMeta();
				}
			}
		}

		public static interface NumberMeta {
			int parse(ItemLoreDataFormat format, String src);

			String compose(ItemLoreDataFormat format, Integer data);

			public static class HiddenNumberMeta implements NumberMeta {
				private final int defaultValue;

				public HiddenNumberMeta(final int defaultValue) {
					this.defaultValue = defaultValue;
				}

				public int parse(final ItemLoreDataFormat format, final String src) {
					final String numstr = StringUtils.replace(src, "\u00A7", "");
					final int num = NumberUtils.toInt(numstr, defaultValue);
					return num;
				}

				public @Nullable String compose(final ItemLoreDataFormat format, final Integer data) {
					if (data==null)
						return null;
					final String numstr = String.valueOf(data);
					final StringBuilder stb = new StringBuilder();
					for (final char str : numstr.toCharArray())
						stb.append("\u00A7").append(str);
					return stb.toString();
				}

				@Override
				public String toString() {
					return String.format("HiddenNumberMeta [defaultValue=%s]", defaultValue);
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime*result+defaultValue;
					return result;
				}

				@Override
				public boolean equals(final Object obj) {
					if (this==obj)
						return true;
					if (obj==null)
						return false;
					if (!(obj instanceof HiddenNumberMeta))
						return false;
					final HiddenNumberMeta other = (HiddenNumberMeta) obj;
					if (defaultValue!=other.defaultValue)
						return false;
					return true;
				}
			}

			public static class TextNumberMeta implements NumberMeta {
				private final int defaultValue;

				public TextNumberMeta(final int defaultValue) {
					this.defaultValue = defaultValue;
				}

				public int parse(final ItemLoreDataFormat format, final String src) {
					final int num = NumberUtils.toInt(src, defaultValue);
					return num;
				}

				public @Nullable String compose(final ItemLoreDataFormat format, final Integer data) {
					if (data==null)
						return null;
					final String numstr = String.valueOf(data);
					return numstr;
				}

				@Override
				public String toString() {
					return String.format("TextNumberMeta [defaultValue=%s]", defaultValue);
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime*result+defaultValue;
					return result;
				}

				@Override
				public boolean equals(final Object obj) {
					if (this==obj)
						return true;
					if (obj==null)
						return false;
					if (!(obj instanceof TextNumberMeta))
						return false;
					final TextNumberMeta other = (TextNumberMeta) obj;
					if (defaultValue!=other.defaultValue)
						return false;
					return true;
				}
			}

			public static class Factory {
				public static NumberMeta create(final String format) {
					if (StringUtils.startsWith(format, "\u00A7")) {
						final String numstr = StringUtils.substringAfter(format, "\u00A7");
						return new HiddenNumberMeta(NumberUtils.toInt(numstr));
					}
					return new TextNumberMeta(NumberUtils.toInt(format));
				}
			}
		}

		public static interface TextMeta {
			String parse(ItemLoreDataFormat format, String src);

			String compose(ItemLoreDataFormat format, String data);

			public static class TextTextMeta implements TextMeta {
				public TextTextMeta() {
				}

				public String parse(final ItemLoreDataFormat format, final String src) {
					return src;
				}

				public @Nullable String compose(final ItemLoreDataFormat format, final String data) {
					return data;
				}

				@Override
				public String toString() {
					return String.format("TextTextMeta []");
				}

				@Override
				public int hashCode() {
					return 0;
				}

				@Override
				public boolean equals(final Object obj) {
					return obj instanceof TextTextMeta;
				}
			}

			public static class Factory {
				public static TextMeta create(final String format) {
					return new TextTextMeta();
				}
			}
		}
	}
}
