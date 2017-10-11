/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 tr7zw
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi;

import java.util.Set;

import org.bukkit.inventory.ItemStack;

public class NBTCompound {

	private String compundname;
	private NBTCompound parent;

	protected NBTCompound(final NBTCompound owner, final String name) {
		this.compundname = name;
		this.parent = owner;
	}

	public String getName() {
		return this.compundname;
	}

	protected Object getCompound() {
		return this.parent.getCompound();
	}

	protected void setCompound(final Object comp) {
		this.parent.setCompound(comp);
	}

	public NBTCompound getParent() {
		return this.parent;
	}

	protected void setItem(final ItemStack item) {
		this.parent.setItem(item);
	}

	public void setString(final String key, final String value) {
		NBTReflectionUtil.setString(this, key, value);
	}

	public String getString(final String key) {
		return NBTReflectionUtil.getString(this, key);
	}

	protected String getContent(final String key) {
		return NBTReflectionUtil.getContent(this, key);
	}

	public void setInteger(final String key, final Integer value) {
		NBTReflectionUtil.setInt(this, key, value);
	}

	public Integer getInteger(final String key) {
		return NBTReflectionUtil.getInt(this, key);
	}

	public void setDouble(final String key, final Double value) {
		NBTReflectionUtil.setDouble(this, key, value);
	}

	public Double getDouble(final String key) {
		return NBTReflectionUtil.getDouble(this, key);
	}

	public void setByte(final String key, final Byte value) {
		NBTReflectionUtil.setByte(this, key, value);
	}

	public Byte getByte(final String key) {
		return NBTReflectionUtil.getByte(this, key);
	}

	public void setShort(final String key, final Short value) {
		NBTReflectionUtil.setShort(this, key, value);
	}

	public Short getShort(final String key) {
		return NBTReflectionUtil.getShort(this, key);
	}

	public void setLong(final String key, final Long value) {
		NBTReflectionUtil.setLong(this, key, value);
	}

	public Long getLong(final String key) {
		return NBTReflectionUtil.getLong(this, key);
	}

	public void setFloat(final String key, final Float value) {
		NBTReflectionUtil.setFloat(this, key, value);
	}

	public Float getFloat(final String key) {
		return NBTReflectionUtil.getFloat(this, key);
	}

	public void setByteArray(final String key, final byte[] value) {
		NBTReflectionUtil.setByteArray(this, key, value);
	}

	public byte[] getByteArray(final String key) {
		return NBTReflectionUtil.getByteArray(this, key);
	}

	public void setIntArray(final String key, final int[] value) {
		NBTReflectionUtil.setIntArray(this, key, value);
	}

	public int[] getIntArray(final String key) {
		return NBTReflectionUtil.getIntArray(this, key);
	}

	public void setBoolean(final String key, final Boolean value) {
		NBTReflectionUtil.setBoolean(this, key, value);
	}

	protected void set(final String key, final Object val) {
		NBTReflectionUtil.set(this, key, val);
	}

	public Boolean getBoolean(final String key) {
		return NBTReflectionUtil.getBoolean(this, key);
	}

	public void setObject(final String key, final Object value) {
		NBTReflectionUtil.setObject(this, key, value);
	}

	public <T> T getObject(final String key, final Class<T> type) {
		return NBTReflectionUtil.getObject(this, key, type);
	}

	public Boolean hasKey(final String key) {
		return NBTReflectionUtil.hasKey(this, key);
	}

	public void removeKey(final String key) {
		NBTReflectionUtil.remove(this, key);
	}

	public Set<String> getKeys() {
		return NBTReflectionUtil.getKeys(this);
	}

	public NBTCompound addCompound(final String name) {
		NBTReflectionUtil.addNBTTagCompound(this, name);
		return getCompound(name);
	}

	public NBTCompound getCompound(final String name) {
		final NBTCompound next = new NBTCompound(this, name);
		if (NBTReflectionUtil.valideCompound(next))
			return next;
		return null;
	}

	public NBTList getList(final String name, final NBTType type) {
		return NBTReflectionUtil.getList(this, name, type);
	}

	public NBTType getType(final String name) {
		if (MinecraftVersion.getVersion()==MinecraftVersion.MC1_7_R4)
			return NBTType.NBTTagEnd;
		return NBTType.valueOf(NBTReflectionUtil.getType(this, name));
	}

	@Override
	public String toString() {
		String str = "";
		for (final String k : getKeys())
			str += toString(k);
		return str;
	}

	public String toString(final String key) {
		String s = "";
		NBTCompound c = this;
		while (c.getParent()!=null) {
			s += "   ";
			c = c.getParent();
		}
		if (getType(key)==NBTType.NBTTagCompound)
			return this.getCompound(key).toString();
		else
			return s+"-"+key+": "+getContent(key)+System.lineSeparator();
	}

}
