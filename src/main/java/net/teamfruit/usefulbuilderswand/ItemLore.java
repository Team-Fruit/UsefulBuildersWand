package net.teamfruit.usefulbuilderswand;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreDataFormat.FlagMeta;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreDataFormat.NumberMeta;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreDataFormat.TextMeta;

public abstract class ItemLore {
	public static class ItemLoreRaw {
		private List<String> lore;

		public @Nonnull List<String> get() {
			if (this.lore==null)
				this.lore = Lists.newArrayList();
			return this.lore;
		}

		public ItemLoreRaw read(final List<String> lore) {
			this.lore = lore;
			return this;
		}

		public ItemLoreRaw write(final List<String> lore) {
			lore.clear();
			lore.addAll(get());
			return this;
		}

		public ItemLoreRaw readItemStack(final ItemLoreDataFormat format, final ItemStack itemStack) {
			this.lore = itemStack.getItemMeta().getLore();
			return this;
		}

		public ItemLoreRaw writeItemStack(final ItemLoreDataFormat format, final ItemStack itemStack) {
			final ItemMeta meta = itemStack.getItemMeta();
			meta.setLore(get());
			itemStack.setItemMeta(meta);
			return this;
		}

		public boolean hasContent(final ItemLoreDataFormat format) {
			if (this.lore==null||this.lore.isEmpty())
				return false;
			for (final String line : this.lore)
				if (StringUtils.startsWith(line, format.prefix))
					return true;
			return false;
		}

		public ItemLoreRaw updateContents(final ItemLoreDataFormat format, final ItemLoreContent contents) {
			final List<String> output = get();
			final List<String> input = contents.get();
			if (!output.isEmpty()) {
				final int loresize = output.size();
				int i = 0;
				boolean done = false;
				for (final ListIterator<String> itr = output.listIterator(); itr.hasNext(); i++) {
					boolean flag = i+1==loresize;
					final String line = itr.next();
					if (StringUtils.startsWith(line, format.prefix)) {
						itr.remove();
						flag = true;
					}
					if (flag&&!done) {
						for (final String content : input)
							itr.add(format.prefix+content);
						done = true;
					}
				}
			} else
				for (final String content : input)
					output.add(format.prefix+content);
			return this;
		}
	}

	public static class ItemLoreContent {
		private List<String> contents;

		public @Nonnull List<String> get() {
			if (this.contents==null)
				this.contents = Lists.newArrayList();
			return this.contents;
		}

		public ItemLoreContent from(final List<String> contents) {
			this.contents = contents;
			return this;
		}

		public ItemLoreContent fromRaw(final ItemLoreDataFormat format, final ItemLoreRaw lore) {
			final List<String> output = get();
			final List<String> input = lore.get();
			for (final ListIterator<String> itr = input.listIterator(); itr.hasNext();) {
				final String line = itr.next();
				if (StringUtils.startsWith(line, format.prefix)) {
					final String data = StringUtils.substringAfter(line, format.prefix);
					output.add(data);
				}
			}
			return this;
		}

		public ItemLoreContent fromMeta(final ItemLoreDataFormat format, final ItemLoreMeta meta) {
			final List<String> output = get();
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
				output.add(stb.toString());
			}
			return this;
		}
	}

	public static class ItemLoreMeta {
		private final Map<String, Boolean> dataFlag = Maps.newHashMap();
		private final Map<String, Integer> dataNumber = Maps.newHashMap();
		private final Map<String, String> dataText = Maps.newHashMap();
		private int modcount;

		public int getModCount() {
			return this.modcount;
		}

		public ItemLoreMeta fromContents(final ItemLoreDataFormat format, final ItemLoreContent contents) {
			this.modcount++;
			final List<String> input = contents.get();
			for (final ListIterator<String> itr = input.listIterator(); itr.hasNext();) {
				final String line = itr.next();
				String data = line;
				readvalue: while (!StringUtils.isEmpty(data = StringUtils.substringAfter(data, format.valueprefix))) {
					final String current = StringUtils.substringBefore(data, format.valuesuffix);
					for (final Entry<String, FlagMeta> entry : format.typeFlag.entrySet()) {
						final String typeFlag = entry.getKey();
						if (StringUtils.startsWith(current, typeFlag)) {
							final String dataValue = StringUtils.substringAfter(current, typeFlag);

							setFlag(typeFlag, entry.getValue().parse(format, dataValue));
							continue readvalue;
						}
					}
					for (final Entry<String, NumberMeta> entry : format.typeNumber.entrySet()) {
						final String typeNumber = entry.getKey();
						if (StringUtils.startsWith(current, typeNumber)) {
							final String dataValue = StringUtils.substringAfter(current, typeNumber);

							setNumber(typeNumber, entry.getValue().parse(format, dataValue));
							continue readvalue;
						}
					}
					for (final Entry<String, TextMeta> entry : format.typeText.entrySet()) {
						final String typeText = entry.getKey();
						if (StringUtils.startsWith(current, typeText)) {
							final String dataValue = StringUtils.substringAfter(current, typeText);

							setText(typeText, entry.getValue().parse(format, dataValue));
							continue readvalue;
						}
					}
				}
			}
			return this;
		}

		public @Nullable Integer getNumber(final String key) {
			return this.dataNumber.get(key);
		}

		public Integer getNumber(final String key, final Integer defaultValue) {
			final Integer value = getNumber(key);
			if (value!=null)
				return value;
			return defaultValue;
		}

		public void setNumber(final String key, @Nullable final Integer value) {
			this.modcount++;
			if (value!=null)
				this.dataNumber.put(key, value);
			else
				this.dataNumber.remove(key);
		}

		public @Nullable String getText(final String key) {
			return this.dataText.get(key);
		}

		public String getText(final String key, final String defaultValue) {
			final String value = getText(key);
			if (value!=null)
				return value;
			return defaultValue;
		}

		public void setText(final String key, @Nullable final String value) {
			this.modcount++;
			if (value!=null)
				this.dataText.put(key, value);
			else
				this.dataText.remove(key);
		}

		public @Nullable Boolean getFlag(final String key) {
			return this.dataFlag.get(key);
		}

		public Boolean getFlag(final String key, final Boolean defaultValue) {
			final Boolean value = getFlag(key);
			if (value!=null)
				return value;
			return defaultValue;
		}

		public void setFlag(final String key, @Nullable final Boolean value) {
			this.modcount++;
			if (value!=null)
				this.dataFlag.put(key, value);
			else
				this.dataFlag.remove(key);
		}

		public void set(final ItemLoreDataFormat format, final String key, final String value) {
			if (format.typeFlag.containsKey(key))
				setFlag(key, format.typeFlag.get(key).parse(format, value));
			else if (format.typeNumber.containsKey(key))
				setNumber(key, format.typeNumber.get(key).parse(format, value));
			else if (format.typeText.containsKey(key))
				setText(key, format.typeText.get(key).parse(format, value));
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

	}

	public static class ItemLoreDataFormat {
		public final String prefix;
		public final String valueprefix;
		public final String valuesuffix;
		public final Map<String, FlagMeta> typeFlag;
		public final Map<String, NumberMeta> typeNumber;
		public final Map<String, TextMeta> typeText;
		public final List<String> metaFormat;

		public ItemLoreDataFormat(final String prefix, final String valueprefix, final String valuesuffix, final Map<String, FlagMeta> typeFlag, final Map<String, NumberMeta> typeNumber, final Map<String, TextMeta> typeText, final List<String> metaFormat) {
			this.prefix = prefix;
			this.valueprefix = valueprefix;
			this.valuesuffix = valuesuffix;
			this.typeFlag = typeFlag;
			this.typeNumber = typeNumber;
			this.typeText = typeText;
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

			String compose(ItemLoreDataFormat format, Boolean data);

			public static class HiddenFlagMeta implements FlagMeta {
				private final Boolean defaultValue;

				public HiddenFlagMeta(final Boolean defaultValue) {
					this.defaultValue = defaultValue;
				}

				public boolean parse(final ItemLoreDataFormat format, final String src) {
					return true;
				}

				public @Nullable String compose(final ItemLoreDataFormat format, Boolean data) {
					if (data==null)
						if (defaultValue!=null)
							data = defaultValue;
						else
							data = false;
					return data ? "" : null;
				}

				@Override
				public String toString() {
					return String.format("HiddenFlagMeta [defaultValue=%s]", defaultValue);
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime*result+(defaultValue==null ? 0 : defaultValue.hashCode());
					return result;
				}

				@Override
				public boolean equals(final Object obj) {
					if (this==obj)
						return true;
					if (obj==null)
						return false;
					if (!(obj instanceof HiddenFlagMeta))
						return false;
					final HiddenFlagMeta other = (HiddenFlagMeta) obj;
					if (defaultValue==null) {
						if (other.defaultValue!=null)
							return false;
					} else if (!defaultValue.equals(other.defaultValue))
						return false;
					return true;
				}
			}

			public static class TextFlagMeta implements FlagMeta {
				private final Boolean defaultValue;

				private final String strTrue;
				private final String strFalse;

				public TextFlagMeta(final Boolean defaultValue, final String strTrue, final String strFalse) {
					this.defaultValue = defaultValue;
					this.strTrue = strTrue;
					this.strFalse = strFalse;
				}

				public boolean parse(final ItemLoreDataFormat format, final String src) {
					return StringUtils.equalsIgnoreCase(src, strTrue);
				}

				public @Nullable String compose(final ItemLoreDataFormat format, Boolean data) {
					if (data==null)
						if (defaultValue!=null)
							data = defaultValue;
						else
							data = false;
					return data ? strTrue : strFalse;
				}

				@Override
				public String toString() {
					return String.format("TextFlagMeta [defaultValue=%s, strTrue=%s, strFalse=%s]", defaultValue, strTrue, strFalse);
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime*result+(defaultValue==null ? 0 : defaultValue.hashCode());
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
					if (defaultValue==null) {
						if (other.defaultValue!=null)
							return false;
					} else if (!defaultValue.equals(other.defaultValue))
						return false;
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
						final String defaultStr;
						final String format1;
						if (StringUtils.contains(format, "?")) {
							defaultStr = StringUtils.substringBefore(format, "?");
							format1 = StringUtils.substringAfter(format, "?");
						} else {
							defaultStr = null;
							format1 = format;
						}
						if (StringUtils.contains(format, ":")) {
							final String trueStr = StringUtils.substringBefore(format1, ":");
							final String falseStr = StringUtils.substringAfter(format1, ":");
							return new TextFlagMeta(BooleanUtils.toBooleanObject(defaultStr), trueStr, falseStr);
						} else
							return new HiddenFlagMeta(BooleanUtils.toBooleanObject(defaultStr));
					}
					return new HiddenFlagMeta(false);
				}
			}
		}

		public static interface NumberMeta {
			int parse(ItemLoreDataFormat format, String src);

			String compose(ItemLoreDataFormat format, Integer data);

			public static class HiddenNumberMeta implements NumberMeta {
				private final @Nullable Integer defaultValue;

				public HiddenNumberMeta(final @Nullable Integer defaultValue) {
					this.defaultValue = defaultValue;
				}

				public int parse(final ItemLoreDataFormat format, final String src) {
					final String numstr = StringUtils.replace(src, "\u00A7", "");
					final int num = NumberUtils.toInt(numstr, defaultValue!=null ? defaultValue : 0);
					return num;
				}

				public @Nullable String compose(final ItemLoreDataFormat format, Integer data) {
					if (data==null)
						if (defaultValue!=null)
							data = defaultValue;
						else
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
					result = prime*result+(defaultValue!=null ? defaultValue.hashCode() : 0);
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
					if (defaultValue!=null) {
						if (!defaultValue.equals(other.defaultValue))
							return false;
					} else if (other.defaultValue!=null)
						return false;
					return true;
				}
			}

			public static class TextNumberMeta implements NumberMeta {
				private final @Nullable Integer defaultValue;

				public TextNumberMeta(final @Nullable Integer defaultValue) {
					this.defaultValue = defaultValue;
				}

				public int parse(final ItemLoreDataFormat format, final String src) {
					final int num = NumberUtils.toInt(src, defaultValue!=null ? defaultValue : 0);
					return num;
				}

				public @Nullable String compose(final ItemLoreDataFormat format, Integer data) {
					if (data==null)
						if (defaultValue!=null)
							data = defaultValue;
						else
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
					result = prime*result+(defaultValue!=null ? defaultValue.hashCode() : 0);
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
					if (defaultValue!=null) {
						if (!defaultValue.equals(other.defaultValue))
							return false;
					} else if (other.defaultValue!=null)
						return false;
					return true;
				}
			}

			public static class Factory {
				public static NumberMeta create(final String format) {
					if (StringUtils.startsWith(format, "\u00A7")) {
						final String numstr = StringUtils.substringAfter(format, "\u00A7");
						return new HiddenNumberMeta(NumberUtils.isNumber(numstr) ? NumberUtils.toInt(numstr) : null);
					}
					return new TextNumberMeta(NumberUtils.isNumber(format) ? NumberUtils.toInt(format) : null);
				}
			}
		}

		public static interface TextMeta {
			String parse(ItemLoreDataFormat format, String src);

			String compose(ItemLoreDataFormat format, String data);

			public static class TextTextMeta implements TextMeta {
				private final @Nullable String defaultValue;

				public TextTextMeta(final @Nullable String defaultValue) {
					this.defaultValue = defaultValue;
				}

				public String parse(final ItemLoreDataFormat format, String src) {
					if (defaultValue!=null&&src==null)
						src = defaultValue;
					return src;
				}

				public @Nullable String compose(final ItemLoreDataFormat format, String data) {
					if (defaultValue!=null&&data==null)
						data = defaultValue;
					return data;
				}

				@Override
				public String toString() {
					return String.format("TextTextMeta [defaultValue=%s]", defaultValue);
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime*result+(defaultValue!=null ? defaultValue.hashCode() : 0);
					return result;
				}

				@Override
				public boolean equals(final Object obj) {
					if (this==obj)
						return true;
					if (obj==null)
						return false;
					if (!(obj instanceof TextTextMeta))
						return false;
					final TextTextMeta other = (TextTextMeta) obj;
					if (defaultValue!=null) {
						if (!defaultValue.equals(other.defaultValue))
							return false;
					} else if (other.defaultValue!=null)
						return false;
					return true;
				}
			}

			public static class Factory {
				public static TextMeta create(final String format) {
					return new TextTextMeta(format);
				}
			}
		}
	}
}
