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

package net.teamfruit.ubw.lib.de.tr7zw.itemnbtapi;

public enum NBTType {
	NBTTagEnd(0),
	NBTTagByte(1),
	NBTTagShort(2),
	NBTTagInt(3),
	NBTTagLong(4),
	NBTTagFloat(5),
	NBTTagDouble(6),
	NBTTagByteArray(7),
	NBTTagIntArray(11),
	NBTTagString(8),
	NBTTagList(9),
	NBTTagCompound(10);

	NBTType(final int i) {
		this.id = i;
	}

	private final int id;

	public int getId() {
		return this.id;
	}

	public static NBTType valueOf(final int id) {
		for (final NBTType t : values())
			if (t.getId()==id)
				return t;
		return NBTType.NBTTagEnd;
	}

}
