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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreDataFormat.FlagMeta;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreDataFormat.NumberMeta;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreDataFormat.TextMeta;

public abstract class ItemLore {
	public static class ItemLoreRaw {
		private static final ItemLoreRaw instance = new ItemLoreRaw(ImmutableList.<String> of());

		public static final ItemLoreRaw create() {
			return instance;
		}

		private final @Nonnull ImmutableList<String> lore;

		private ItemLoreRaw(final @Nonnull ImmutableList<String> lore) {
			this.lore = lore;
		}

		public @Nonnull ImmutableList<String> get() {
			return this.lore;
		}

		public ItemLoreRaw read(final List<String> lore) {
			return new ItemLoreRaw(ImmutableList.copyOf(lore));
		}

		public ItemLoreRaw write(final List<String> lore) {
			lore.clear();
			lore.addAll(get());
			return this;
		}

		public ItemLoreRaw readItemStack(final ItemLoreDataFormat format, final ItemStack itemStack) {
			if (itemStack!=null) {
				final ItemMeta meta = itemStack.getItemMeta();
				if (meta!=null) {
					final ImmutableList.Builder<String> builder = ImmutableList.builder();
					if (!format.metaFormat.isEmpty()) {
						final String formatFirst = format.metaFormat.get(0);
						if (!StringUtils.isEmpty(formatFirst))
							builder.add(StringUtils.defaultString(meta.getDisplayName()));
					}
					final List<String> lore = meta.getLore();
					if (lore!=null)
						builder.addAll(lore);
					return new ItemLoreRaw(builder.build());
				}
			}
			return this;
		}

		public ItemLoreRaw writeItemStack(final ItemLoreDataFormat format, final ItemStack itemStack) {
			if (itemStack!=null) {
				final ItemMeta meta = itemStack.getItemMeta();
				if (meta!=null) {
					ImmutableList<String> lore = get();
					final int size = lore.size();
					if (!format.metaFormat.isEmpty()&&size>0) {
						meta.setDisplayName(lore.get(0));
						if (size>1)
							lore = lore.subList(1, size);
						else
							lore = ImmutableList.of();
					}
					meta.setLore(lore);
					itemStack.setItemMeta(meta);
				}
			}
			return this;
		}

		public boolean hasContent(final ItemLoreDataFormat format) {
			if (this.lore.isEmpty())
				return false;
			for (final String line : this.lore)
				if (StringUtils.startsWith(line, format.prefix))
					return true;
			return false;
		}

		public ItemLoreRaw updateContents(final ItemLoreDataFormat format, final ItemLoreContent contents) {
			final List<String> output = Lists.newArrayList(get());
			List<String> input = contents.get();

			if (!output.isEmpty()) {
				String first = null;
				if (!format.metaFormat.isEmpty()) {
					final String formatFirst = format.metaFormat.get(0);
					if (!StringUtils.isEmpty(formatFirst)&&!input.isEmpty()) {
						first = input.get(0);
						input = input.subList(1, input.size());
					}
				}
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
				if (first!=null)
					output.add(0, format.prefix+first);
			} else
				for (final String content : input)
					output.add(format.prefix+content);
			return new ItemLoreRaw(ImmutableList.copyOf(output));
		}

		@Override
		public String toString() {
			return String.format("ItemLoreRaw [lore=%s]", this.lore);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime*result+this.lore.hashCode();
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this==obj)
				return true;
			if (obj==null)
				return false;
			if (!(obj instanceof ItemLoreRaw))
				return false;
			final ItemLoreRaw other = (ItemLoreRaw) obj;
			if (!this.lore.equals(other.lore))
				return false;
			return true;
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
						if (StringUtils.equalsIgnoreCase(type, "B")) {
							final String s = format.typeFlag.get(name).compose(format, meta.getFlag(name));
							if (s!=null)
								stb.append(format.valueprefix).append(name).append(s).append(format.valuesuffix);
						} else if (StringUtils.equalsIgnoreCase(type, "I")) {
							final String s = format.typeNumber.get(name).compose(format, meta.getNumber(name));
							if (s!=null)
								stb.append(format.valueprefix).append(name).append(s).append(format.valuesuffix);
						} else if (StringUtils.equalsIgnoreCase(type, "S")) {
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

		@Override
		public String toString() {
			return String.format("ItemLoreContent [contents=%s]", this.contents);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime*result+(this.contents==null ? 0 : this.contents.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this==obj)
				return true;
			if (obj==null)
				return false;
			if (!(obj instanceof ItemLoreContent))
				return false;
			final ItemLoreContent other = (ItemLoreContent) obj;
			if (this.contents==null||this.contents.isEmpty()) {
				if (other.contents!=null&&other.contents.isEmpty())
					return false;
			} else if (!this.contents.equals(other.contents))
				return false;
			return true;
		}
	}

	public interface ItemLoreMeta {

		ItemLoreMetaImmutable toImmutable();

		ItemLoreMetaEditable toEditable();

		Integer getNumber(final String key);

		Integer getNumber(final String key, final Integer defaultValue);

		String getText(final String key);

		String getText(final String key, final String defaultValue);

		Boolean getFlag(final String key);

		Boolean getFlag(final String key, final Boolean defaultValue);

	}

	public static class ItemLoreMetaImmutable implements ItemLoreMeta {
		private final ImmutableMap<String, Boolean> dataFlag;
		private final ImmutableMap<String, Integer> dataNumber;
		private final ImmutableMap<String, String> dataText;

		ItemLoreMetaImmutable(final ImmutableMap<String, Boolean> dataFlag, final ImmutableMap<String, Integer> dataNumber, final ImmutableMap<String, String> dataText) {
			this.dataFlag = dataFlag;
			this.dataNumber = dataNumber;
			this.dataText = dataText;
		}

		public ItemLoreMetaImmutable toImmutable() {
			return this;
		}

		public ItemLoreMetaEditable toEditable() {
			return new ItemLoreMetaEditable(Maps.newHashMap(this.dataFlag), Maps.newHashMap(this.dataNumber), Maps.newHashMap(this.dataText));
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

		public @Nullable String getText(final String key) {
			return this.dataText.get(key);
		}

		public String getText(final String key, final String defaultValue) {
			final String value = getText(key);
			if (value!=null)
				return value;
			return defaultValue;
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

		@Override
		public String toString() {
			return String.format("ItemLoreMetaImmutable [dataFlag=%s, dataNumber=%s, dataText=%s]", this.dataFlag, this.dataNumber, this.dataText);
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
			if (!(obj instanceof ItemLoreMetaImmutable))
				return false;
			final ItemLoreMetaImmutable other = (ItemLoreMetaImmutable) obj;
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

	public static class ItemLoreMetaEditable implements ItemLoreMeta, Cloneable {
		private final Map<String, Boolean> dataFlag;
		private final Map<String, Integer> dataNumber;
		private final Map<String, String> dataText;
		private int modcount;

		ItemLoreMetaEditable(final Map<String, Boolean> dataFlag, final Map<String, Integer> dataNumber, final Map<String, String> dataText) {
			this.dataFlag = dataFlag;
			this.dataNumber = dataNumber;
			this.dataText = dataText;
		}

		public ItemLoreMetaEditable() {
			this(Maps.<String, Boolean> newHashMap(), Maps.<String, Integer> newHashMap(), Maps.<String, String> newHashMap());
		}

		public int getModCount() {
			return this.modcount;
		}

		public ItemLoreMetaImmutable toImmutable() {
			return new ItemLoreMetaImmutable(ImmutableMap.copyOf(this.dataFlag), ImmutableMap.copyOf(this.dataNumber), ImmutableMap.copyOf(this.dataText));
		}

		public ItemLoreMetaEditable toEditable() {
			return this;
		}

		@Override
		public ItemLoreMetaEditable clone() {
			return new ItemLoreMetaEditable(Maps.newHashMap(this.dataFlag), Maps.newHashMap(this.dataNumber), Maps.newHashMap(this.dataText));
		}

		public ItemLoreMetaEditable fromContents(final ItemLoreDataFormat format, final ItemLoreContent contents) {
			System.out.print("parsed");
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
			return String.format("ItemLoreMetaMutable [dataFlag=%s, dataNumber=%s, dataText=%s, modcount=%s]", this.dataFlag, this.dataNumber, this.dataText, this.modcount);
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
			if (!(obj instanceof ItemLoreMetaEditable))
				return false;
			final ItemLoreMetaEditable other = (ItemLoreMetaEditable) obj;
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
		public final ImmutableMap<String, FlagMeta> typeFlag;
		public final ImmutableMap<String, NumberMeta> typeNumber;
		public final ImmutableMap<String, TextMeta> typeText;
		public final ImmutableList<String> metaFormat;

		public ItemLoreDataFormat(final String prefix, final String valueprefix, final String valueend, final List<String> metaFormat) {
			this.prefix = prefix;
			this.valueprefix = valueprefix;
			this.valuesuffix = valueend;
			final Builder<String, FlagMeta> typeFlagBuilder = ImmutableMap.builder();
			final Builder<String, NumberMeta> typeNumberBuilder = ImmutableMap.builder();
			final Builder<String, TextMeta> typeTextBuilder = ImmutableMap.builder();
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
							typeFlagBuilder.put(name, FlagMeta.Factory.create(constant));
						else if (StringUtils.equals(type, "I"))
							typeNumberBuilder.put(name, NumberMeta.Factory.create(constant));
						else if (StringUtils.equals(type, "S"))
							typeTextBuilder.put(name, TextMeta.Factory.create(constant));
						data = StringUtils.substringAfter(data, "}");
					}
			}
			this.typeNumber = typeNumberBuilder.build();
			this.typeText = typeTextBuilder.build();
			this.typeFlag = typeFlagBuilder.build();
			this.metaFormat = ImmutableList.copyOf(metaFormat);
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
