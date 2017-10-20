package net.teamfruit.usefulbuilderswand.meta;

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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.teamfruit.usefulbuilderswand.meta.ItemLore.ItemLoreDataFormat.FlagMeta;
import net.teamfruit.usefulbuilderswand.meta.ItemLore.ItemLoreDataFormat.FlagMeta.FlagMetaAccess;
import net.teamfruit.usefulbuilderswand.meta.ItemLore.ItemLoreDataFormat.NumberMeta;
import net.teamfruit.usefulbuilderswand.meta.ItemLore.ItemLoreDataFormat.TextMeta;

@Deprecated
public abstract class ItemLore {
	public static class ItemLoreRaw {
		private static final ItemLoreRaw instance = new ItemLoreRaw(ImmutableList.<String> of());

		public static ItemLoreRaw create() {
			return instance;
		}

		private final @Nonnull ImmutableList<String> lore;

		ItemLoreRaw(final @Nonnull ImmutableList<String> lore) {
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
					output.set(0, format.prefix+first);
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

	public static class ItemLoreRawMeta extends ItemLoreRaw {
		private static final ItemLoreRawMeta instance = new ItemLoreRawMeta(ImmutableList.<String> of());

		public static ItemLoreRawMeta create() {
			return instance;
		}

		ItemLoreRawMeta(final @Nonnull ImmutableList<String> lore) {
			super(lore);
		}

		@Override
		public ItemLoreRaw readItemStack(final ItemLoreDataFormat format, final ItemStack itemStack) {
			return super.readItemStack(format, itemStack);
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
				final String attr = meta.getAttributes(format, line);
				if (!StringUtils.isEmpty(attr))
					output.add(attr);
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

	public static abstract class ItemLoreMeta {

		public abstract ItemLoreMetaImmutable toImmutable();

		public abstract ItemLoreMetaEditable toEditable();

		public abstract Integer getNumber(final String key);

		public abstract Integer getNumber(final String key, final Integer defaultValue);

		public abstract String getText(final String key);

		public abstract String getText(final String key, final String defaultValue);

		public abstract Boolean getFlag(final String key);

		public abstract Boolean getFlag(final String key, final Boolean defaultValue);

		public String getAttributes(final ItemLoreDataFormat format, final String attributesbase) {
			final StringBuilder stb = new StringBuilder();
			String data = attributesbase;
			String current;
			while (true) {
				stb.append(NestedStringUtils.substringBeforeNested(data, "${", "$"));
				if (StringUtils.isEmpty(current = NestedStringUtils.substringNested(data, "${", "}", "$", null)))
					break;
				stb.append(getAttribute(format, current));
				data = NestedStringUtils.substringAfterNested(data, "${", "}", "$", null);
			}
			return stb.toString();
		}

		public String getAttribute(final ItemLoreDataFormat format, final String attributebase) {
			final StringBuilder stb = new StringBuilder();
			final String type = StringUtils.substringBefore(attributebase, ":");
			final String namevalue = StringUtils.substringAfter(attributebase, ":");
			final String name = StringUtils.substringBefore(namevalue, "=");
			final String value = StringUtils.substringAfter(namevalue, "=");
			if (StringUtils.equalsIgnoreCase(type, "B")) {
				final FlagMeta m;
				final boolean isMeta = StringUtils.equals(type, "B");
				if (isMeta)
					m = format.attributesFormat.typeFlag.get(name);
				else
					m = FlagMeta.Factory.create(value);
				if (m!=null) {
					final String s = m instanceof FlagMetaAccess ? ((FlagMetaAccess) m).compose(format, this, getFlag(name)) : m.compose(format, getFlag(name));
					if (s!=null)
						if (isMeta)
							stb.append(format.valueprefix).append(name).append(s).append(format.valuesuffix);
						else
							stb.append(s);
				}
			} else if (StringUtils.equalsIgnoreCase(type, "I")) {
				final NumberMeta m;
				final boolean isMeta = StringUtils.equals(type, "I");
				if (isMeta)
					m = format.attributesFormat.typeNumber.get(name);
				else
					m = NumberMeta.Factory.create(value);
				if (m!=null) {
					final String s = m.compose(format, getNumber(name));
					if (s!=null)
						if (isMeta)
							stb.append(format.valueprefix).append(name).append(s).append(format.valuesuffix);
						else
							stb.append(s);
				}
			} else if (StringUtils.equalsIgnoreCase(type, "S")) {
				final TextMeta m;
				final boolean isMeta = StringUtils.equals(type, "S");
				if (isMeta)
					m = format.attributesFormat.typeText.get(name);
				else
					m = TextMeta.Factory.create(value);
				if (m!=null) {
					final String s = m.compose(format, getText(name));
					if (s!=null)
						if (isMeta)
							stb.append(format.valueprefix).append(name).append(s).append(format.valuesuffix);
						else
							stb.append(s);
				}
			}
			return stb.toString();
		}

		public String get(final ItemLoreDataFormat format, final String key) {
			if (format.attributesFormat.typeFlag.containsKey(key)) {
				final FlagMeta m = format.attributesFormat.typeFlag.get(key);
				return m instanceof FlagMetaAccess ? ((FlagMetaAccess) m).compose(format, this, getFlag(key)) : m.compose(format, getFlag(key));
			} else if (format.attributesFormat.typeNumber.containsKey(key)) {
				final NumberMeta m = format.attributesFormat.typeNumber.get(key);
				return m.compose(format, getNumber(key));
			} else if (format.attributesFormat.typeText.containsKey(key)) {
				final TextMeta m = format.attributesFormat.typeText.get(key);
				return m.compose(format, getText(key));
			}
			return null;
		}

		public Object getRaw(final ItemLoreDataFormat format, final String key) {
			if (format.attributesFormat.typeFlag.containsKey(key))
				return getFlag(key);
			else if (format.attributesFormat.typeNumber.containsKey(key))
				return getNumber(key);
			else if (format.attributesFormat.typeText.containsKey(key))
				return getText(key);
			return null;
		}
	}

	public static class ItemLoreMetaImmutable extends ItemLoreMeta {
		private final ImmutableMap<String, Boolean> dataFlag;
		private final ImmutableMap<String, Integer> dataNumber;
		private final ImmutableMap<String, String> dataText;

		ItemLoreMetaImmutable(final ImmutableMap<String, Boolean> dataFlag, final ImmutableMap<String, Integer> dataNumber, final ImmutableMap<String, String> dataText) {
			this.dataFlag = dataFlag;
			this.dataNumber = dataNumber;
			this.dataText = dataText;
		}

		@Override
		public ItemLoreMetaImmutable toImmutable() {
			return this;
		}

		@Override
		public ItemLoreMetaEditable toEditable() {
			return new ItemLoreMetaEditable(Maps.newHashMap(this.dataFlag), Maps.newHashMap(this.dataNumber), Maps.newHashMap(this.dataText));
		}

		@Override
		public @Nullable Integer getNumber(final String key) {
			return this.dataNumber.get(key);
		}

		@Override
		public Integer getNumber(final String key, final Integer defaultValue) {
			final Integer value = getNumber(key);
			if (value!=null)
				return value;
			return defaultValue;
		}

		@Override
		public @Nullable String getText(final String key) {
			return this.dataText.get(key);
		}

		@Override
		public String getText(final String key, final String defaultValue) {
			final String value = getText(key);
			if (value!=null)
				return value;
			return defaultValue;
		}

		@Override
		public @Nullable Boolean getFlag(final String key) {
			return this.dataFlag.get(key);
		}

		@Override
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

	public static class ItemLoreMetaEditable extends ItemLoreMeta implements Cloneable {
		private final Map<String, Boolean> dataFlag;
		private final Map<String, Integer> dataNumber;
		private final Map<String, String> dataText;
		private int modCount;

		ItemLoreMetaEditable(final Map<String, Boolean> dataFlag, final Map<String, Integer> dataNumber, final Map<String, String> dataText) {
			this.dataFlag = dataFlag;
			this.dataNumber = dataNumber;
			this.dataText = dataText;
		}

		public ItemLoreMetaEditable() {
			this(Maps.<String, Boolean> newHashMap(), Maps.<String, Integer> newHashMap(), Maps.<String, String> newHashMap());
		}

		public int getModCount() {
			return this.modCount;
		}

		@Override
		public ItemLoreMetaImmutable toImmutable() {
			return new ItemLoreMetaImmutable(ImmutableMap.copyOf(this.dataFlag), ImmutableMap.copyOf(this.dataNumber), ImmutableMap.copyOf(this.dataText));
		}

		@Override
		public ItemLoreMetaEditable toEditable() {
			return this;
		}

		@Override
		public ItemLoreMetaEditable clone() {
			return new ItemLoreMetaEditable(Maps.newHashMap(this.dataFlag), Maps.newHashMap(this.dataNumber), Maps.newHashMap(this.dataText));
		}

		public ItemLoreMetaEditable fromContents(final ItemLoreDataFormat format, final ItemLoreContent contents) {
			// System.out.print("parsed");
			final List<String> input = contents.get();
			for (final ListIterator<String> itr = input.listIterator(); itr.hasNext();) {
				final String line = itr.next();
				addAttributes(format, line);
			}
			return this;
		}

		public ItemLoreMetaEditable addAttributes(final ItemLoreDataFormat format, final String attributes) {
			String data = attributes;
			String current;
			while (!StringUtils.isEmpty(current = NestedStringUtils.substringNested(data, format.valueprefix, format.valuesuffix))) {
				data = NestedStringUtils.substringAfterNested(data, format.valueprefix, format.valuesuffix);
				addAttribute(format, current);
			}
			return this;
		}

		public ItemLoreMetaEditable addAttribute(final ItemLoreDataFormat format, final String attribute) {
			for (final Entry<String, FlagMeta> entry : format.attributesFormat.typeFlag.entrySet()) {
				final String typeFlag = entry.getKey();
				if (StringUtils.startsWith(attribute, typeFlag)) {
					final String dataValue = StringUtils.substringAfter(attribute, typeFlag);

					setFlag(typeFlag, entry.getValue().parse(format, dataValue));
					return this;
				}
			}
			for (final Entry<String, NumberMeta> entry : format.attributesFormat.typeNumber.entrySet()) {
				final String typeNumber = entry.getKey();
				if (StringUtils.startsWith(attribute, typeNumber)) {
					final String dataValue = StringUtils.substringAfter(attribute, typeNumber);

					setNumber(typeNumber, entry.getValue().parse(format, dataValue));
					return this;
				}
			}
			for (final Entry<String, TextMeta> entry : format.attributesFormat.typeText.entrySet()) {
				final String typeText = entry.getKey();
				if (StringUtils.startsWith(attribute, typeText)) {
					final String dataValue = StringUtils.substringAfter(attribute, typeText);

					setText(typeText, entry.getValue().parse(format, dataValue));
					return this;
				}
			}
			return this;
		}

		public void set(final ItemLoreDataFormat format, final String key, final String value) {
			if (format.attributesFormat.typeFlag.containsKey(key))
				setFlag(key, format.attributesFormat.typeFlag.get(key).parse(format, value));
			else if (format.attributesFormat.typeNumber.containsKey(key))
				setNumber(key, format.attributesFormat.typeNumber.get(key).parse(format, value));
			else if (format.attributesFormat.typeText.containsKey(key))
				setText(key, format.attributesFormat.typeText.get(key).parse(format, value));
		}

		public void setRaw(final ItemLoreDataFormat format, final String key, final String value) {
			if (format.attributesFormat.typeFlag.containsKey(key))
				setFlag(key, BooleanUtils.toBoolean(value));
			else if (format.attributesFormat.typeNumber.containsKey(key))
				setNumber(key, NumberUtils.toInt(value));
			else if (format.attributesFormat.typeText.containsKey(key))
				setText(key, value);
		}

		@Override
		public @Nullable Integer getNumber(final String key) {
			return this.dataNumber.get(key);
		}

		@Override
		public Integer getNumber(final String key, final Integer defaultValue) {
			final Integer value = getNumber(key);
			if (value!=null)
				return value;
			return defaultValue;
		}

		public void setNumber(final String key, @Nullable final Integer value) {
			this.modCount++;
			if (value!=null)
				this.dataNumber.put(key, value);
			else
				this.dataNumber.remove(key);
		}

		@Override
		public @Nullable String getText(final String key) {
			return this.dataText.get(key);
		}

		@Override
		public String getText(final String key, final String defaultValue) {
			final String value = getText(key);
			if (value!=null)
				return value;
			return defaultValue;
		}

		public void setText(final String key, @Nullable final String value) {
			this.modCount++;
			if (value!=null)
				this.dataText.put(key, value);
			else
				this.dataText.remove(key);
		}

		@Override
		public @Nullable Boolean getFlag(final String key) {
			return this.dataFlag.get(key);
		}

		@Override
		public Boolean getFlag(final String key, final Boolean defaultValue) {
			final Boolean value = getFlag(key);
			if (value!=null)
				return value;
			return defaultValue;
		}

		public void setFlag(final String key, @Nullable final Boolean value) {
			this.modCount++;
			if (value!=null)
				this.dataFlag.put(key, value);
			else
				this.dataFlag.remove(key);
		}

		@Override
		public String toString() {
			return String.format("ItemLoreMetaEditable [dataFlag=%s, dataNumber=%s, dataText=%s, modcount=%s]", this.dataFlag, this.dataNumber, this.dataText, this.modCount);
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
		public final AttributesFormat attributesFormat;
		public final ImmutableList<String> metaFormat;

		protected ItemLoreDataFormat(final String prefix, final String valueprefix, final String valuesuffix, final List<String> metaFormat, final AttributesFormat attributesFormat) {
			this.prefix = prefix;
			this.valueprefix = valueprefix;
			this.valuesuffix = valuesuffix;
			this.metaFormat = ImmutableList.copyOf(metaFormat);
			this.attributesFormat = attributesFormat;
		}

		public ItemLoreDataFormat(final String prefix, final String valueprefix, final String valueend, final List<String> metaFormat) {
			this.prefix = prefix;
			this.valueprefix = valueprefix;
			this.valuesuffix = valueend;
			this.metaFormat = ImmutableList.copyOf(metaFormat);
			final AttributesFormat.Builder attrbuilder = new AttributesFormat.Builder();
			for (final String line : metaFormat)
				attrbuilder.addAttributes(line);
			this.attributesFormat = attrbuilder.build();
		}

		public ItemLoreDataFormat attributesInstance(final AttributesFormat moreAttributes) {
			return new ItemLoreDataFormat(this.prefix, this.valueprefix, this.valuesuffix, this.metaFormat, new AttributesFormat.Builder().addAttributes(this.attributesFormat).addAttributes(moreAttributes).build());
		}

		public static class AttributesFormat {
			public final ImmutableMap<String, FlagMeta> typeFlag;
			public final ImmutableMap<String, NumberMeta> typeNumber;
			public final ImmutableMap<String, TextMeta> typeText;

			private AttributesFormat(final ImmutableMap<String, FlagMeta> typeFlag, final ImmutableMap<String, NumberMeta> typeNumber, final ImmutableMap<String, TextMeta> typeText) {
				this.typeFlag = typeFlag;
				this.typeNumber = typeNumber;
				this.typeText = typeText;
			}

			@Override
			public String toString() {
				return String.format("AttributesFormat [typeFlag=%s, typeNumber=%s, typeText=%s]", this.typeFlag, this.typeNumber, this.typeText);
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime*result+(this.typeFlag==null ? 0 : this.typeFlag.hashCode());
				result = prime*result+(this.typeNumber==null ? 0 : this.typeNumber.hashCode());
				result = prime*result+(this.typeText==null ? 0 : this.typeText.hashCode());
				return result;
			}

			@Override
			public boolean equals(final Object obj) {
				if (this==obj)
					return true;
				if (obj==null)
					return false;
				if (!(obj instanceof AttributesFormat))
					return false;
				final AttributesFormat other = (AttributesFormat) obj;
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
				return true;
			}

			public static class Builder {
				public final Map<String, FlagMeta> typeFlag = Maps.newHashMap();
				public final Map<String, NumberMeta> typeNumber = Maps.newHashMap();
				public final Map<String, TextMeta> typeText = Maps.newHashMap();

				public Builder() {
				}

				public Builder addAttributes(final AttributesFormat attributes) {
					this.typeFlag.putAll(attributes.typeFlag);
					this.typeNumber.putAll(attributes.typeNumber);
					this.typeText.putAll(attributes.typeText);
					return this;
				}

				public Builder addAttributes(final String attributes) {
					String data = attributes;
					String current;
					while (!StringUtils.isEmpty(current = NestedStringUtils.substringNested(data, "${", "}", "$", null))) {
						addAttribute(current);
						data = NestedStringUtils.substringAfterNested(data, "${", "}", "$", null);
					}
					return this;
				}

				public Builder addAttribute(final String attribute) {
					final String type = StringUtils.substringBefore(attribute, ":");
					final String namevalue = StringUtils.substringAfter(attribute, ":");
					final String name = StringUtils.substringBefore(namevalue, "=");
					final String constant = StringUtils.substringAfter(namevalue, "=");
					addAttribute(type, name, constant);
					return this;
				}

				public Builder addAttribute(final String type, final String name, final String constant) {
					if (StringUtils.equals(type, "B"))
						this.typeFlag.put(name, FlagMeta.Factory.create(constant));
					else if (StringUtils.equals(type, "I"))
						this.typeNumber.put(name, NumberMeta.Factory.create(constant));
					else if (StringUtils.equals(type, "S"))
						this.typeText.put(name, TextMeta.Factory.create(constant));
					return this;
				}

				public AttributesFormat build() {
					return new AttributesFormat(ImmutableMap.<String, FlagMeta> copyOf(this.typeFlag), ImmutableMap.<String, NumberMeta> copyOf(this.typeNumber), ImmutableMap.<String, TextMeta> copyOf(this.typeText));
				}
			}
		}

		@Override
		public String toString() {
			return String.format("ItemLoreDataFormat [prefix=%s, valueprefix=%s, valuesuffix=%s, attributesFormat=%s, metaFormat=%s]", this.prefix, this.valueprefix, this.valuesuffix, this.attributesFormat, this.metaFormat);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime*result+(this.attributesFormat==null ? 0 : this.attributesFormat.hashCode());
			result = prime*result+(this.metaFormat==null ? 0 : this.metaFormat.hashCode());
			result = prime*result+(this.prefix==null ? 0 : this.prefix.hashCode());
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
			if (this.attributesFormat==null) {
				if (other.attributesFormat!=null)
					return false;
			} else if (!this.attributesFormat.equals(other.attributesFormat))
				return false;
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
			public static interface FlagMetaAccess extends FlagMeta {
				String compose(ItemLoreDataFormat format, ItemLoreMeta meta, Boolean data);
			}

			boolean parse(ItemLoreDataFormat format, String src);

			String compose(ItemLoreDataFormat format, Boolean data);

			@Deprecated
			public static class HiddenFlagMeta implements FlagMeta {
				private final Boolean defaultValue;

				public HiddenFlagMeta(final Boolean defaultValue) {
					this.defaultValue = defaultValue;
				}

				@Override
				public boolean parse(final ItemLoreDataFormat format, final String src) {
					return true;
				}

				@Override
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

			public static class TextFlagMeta implements FlagMetaAccess {
				private final Boolean defaultValue;

				private AttributesFormat attributesFormat;
				private final String strTrue;
				private final String strFalse;

				public TextFlagMeta(final Boolean defaultValue, final String strTrue, final String strFalse) {
					this.defaultValue = defaultValue;
					this.attributesFormat = new AttributesFormat.Builder()
							.addAttributes(strTrue)
							.addAttributes(strFalse)
							.build();
					this.strTrue = strTrue;
					this.strFalse = strFalse;
				}

				@Override
				public boolean parse(final ItemLoreDataFormat format, final String src) {
					return StringUtils.equalsIgnoreCase(src, strTrue);
				}

				@Override
				public @Nullable String compose(final ItemLoreDataFormat format, Boolean data) {
					if (data==null)
						if (defaultValue!=null)
							data = defaultValue;
						else
							data = false;
					return data ? strTrue : strFalse;
				}

				@Override
				public @Nullable String compose(final ItemLoreDataFormat format, final ItemLoreMeta meta, Boolean data) {
					if (data==null)
						if (defaultValue!=null)
							data = defaultValue;
						else
							data = false;
					return meta.getAttributes(format.attributesInstance(attributesFormat), data ? strTrue : strFalse);
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

				public class TestAccess {
					public String getTrue() {
						return strTrue;
					}

					public String getFalse() {
						return strFalse;
					}
				}
			}

			public static class Factory {
				public static FlagMeta create(final String constant) {
					if (!StringUtils.isEmpty(constant)) {
						final String defaultStr;
						final String trueOrFalse;
						if (StringUtils.contains(constant, "?")) {
							defaultStr = StringUtils.substringBefore(constant, "?");
							trueOrFalse = StringUtils.substringAfter(constant, "?");
						} else if (!StringUtils.contains(constant, ":")) {
							defaultStr = constant;
							trueOrFalse = "";
						} else {
							defaultStr = null;
							trueOrFalse = constant;
						}

						String current = trueOrFalse;
						String before = null;
						String after = null;
						final StringBuilder done = new StringBuilder();
						String b;
						String c;
						String a;
						if (!StringUtils.isEmpty(NestedStringUtils.substringNested(current, "${", "}", "$", null)))
							while (true) {
								c = NestedStringUtils.substringNested(current, "${", "}", "$", null);
								b = NestedStringUtils.substringBeforeNested(current, "${", "$");
								a = NestedStringUtils.substringAfterNested(current, "${", "}", "$", null);
								if (StringUtils.isEmpty(c)||StringUtils.contains(b, ":")||!StringUtils.contains(a, ":")) {
									before = done.toString()+StringUtils.substringBefore(b, ":");
									after = StringUtils.substringAfter(b, ":")+(c!=null ? "${"+c+"}" : "")+a;
									break;
								}
								current = a;
								done.append(b).append("${").append(c).append("}");
							}
						else if (StringUtils.contains(current, ":")) {
							before = StringUtils.substringBefore(current, ":");
							after = StringUtils.substringAfter(current, ":");
						}
						if (before!=null&&after!=null)
							return new TextFlagMeta(BooleanUtils.toBooleanObject(defaultStr), before, after);
						else
							// return new HiddenFlagMeta(BooleanUtils.toBooleanObject(defaultStr));
							return new TextFlagMeta(BooleanUtils.toBooleanObject(defaultStr), "§1", "§0");
					}
					// return new HiddenFlagMeta(false);
					return new TextFlagMeta(false, "§1", "§0");
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

				@Override
				public int parse(final ItemLoreDataFormat format, final String src) {
					final String numstr = StringUtils.replace(src, "\u00A7", "");
					final int num = NumberUtils.toInt(numstr, defaultValue!=null ? defaultValue : 0);
					return num;
				}

				@Override
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

				@Override
				public int parse(final ItemLoreDataFormat format, final String src) {
					final int num = NumberUtils.toInt(src, defaultValue!=null ? defaultValue : 0);
					return num;
				}

				@Override
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
				public static NumberMeta create(final String constant) {
					if (StringUtils.startsWith(constant, "\u00A7")) {
						final String numstr = StringUtils.substringAfter(constant, "\u00A7");
						return new HiddenNumberMeta(NumberUtils.isNumber(numstr) ? NumberUtils.toInt(numstr) : null);
					}
					return new TextNumberMeta(NumberUtils.isNumber(constant) ? NumberUtils.toInt(constant) : null);
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

				@Override
				public String parse(final ItemLoreDataFormat format, String src) {
					if (defaultValue!=null&&src==null)
						src = defaultValue;
					return src;
				}

				@Override
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
				public static TextMeta create(final String constant) {
					return new TextTextMeta(constant);
				}
			}
		}
	}
}
