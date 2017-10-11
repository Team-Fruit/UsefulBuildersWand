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

import java.lang.reflect.Method;
import java.util.Set;
import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class NBTReflectionUtil {

	private static final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

	@SuppressWarnings("rawtypes")
	private static Class getCraftItemStack() {

		try {
			final Class c = Class.forName("org.bukkit.craftbukkit."+version+".inventory.CraftItemStack");
			return c;
		} catch (final Exception ex) {
			System.out.println("Error in ItemNBTAPI! (Outdated plugin?)");
			ex.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	private static Class getCraftEntity() {
		try {
			final Class c = Class.forName("org.bukkit.craftbukkit."+version+".entity.CraftEntity");
			return c;
		} catch (final Exception ex) {
			System.out.println("Error in ItemNBTAPI! (Outdated plugin?)");
			ex.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	protected static Class getNBTBase() {
		try {
			final Class c = Class.forName("net.minecraft.server."+version+".NBTBase");
			return c;
		} catch (final Exception ex) {
			System.out.println("Error in ItemNBTAPI! (Outdated plugin?)");
			ex.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	protected static Class getNBTTagString() {
		try {
			final Class c = Class.forName("net.minecraft.server."+version+".NBTTagString");
			return c;
		} catch (final Exception ex) {
			System.out.println("Error in ItemNBTAPI! (Outdated plugin?)");
			ex.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	protected static Class getNBTTagCompound() {
		try {
			final Class c = Class.forName("net.minecraft.server."+version+".NBTTagCompound");
			return c;
		} catch (final Exception ex) {
			System.out.println("Error in ItemNBTAPI! (Outdated plugin?)");
			ex.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	protected static Class getTileEntity() {
		try {
			final Class c = Class.forName("net.minecraft.server."+version+".TileEntity");
			return c;
		} catch (final Exception ex) {
			System.out.println("Error in ItemNBTAPI! (Outdated plugin?)");
			ex.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	protected static Class getCraftWorld() {
		try {
			final Class c = Class.forName("org.bukkit.craftbukkit."+version+".CraftWorld");
			return c;
		} catch (final Exception ex) {
			System.out.println("Error in ItemNBTAPI! (Outdated plugin?)");
			ex.printStackTrace();
			return null;
		}
	}

	private static Object getNewNBTTag() {
		final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		try {
			@SuppressWarnings("rawtypes")
			final Class c = Class.forName("net.minecraft.server."+version+".NBTTagCompound");
			return c.newInstance();
		} catch (final Exception ex) {
			System.out.println("Error in ItemNBTAPI! (Outdated plugin?)");
			ex.printStackTrace();
			return null;
		}
	}

	private static Object getnewBlockPosition(final int x, final int y, final int z) {
		final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		try {
			final Class<?> c = Class.forName("net.minecraft.server."+version+".BlockPosition");
			return c.getConstructor(int.class, int.class, int.class).newInstance(x, y, z);
		} catch (final Exception ex) {
			System.out.println("Error in ItemNBTAPI! (Outdated plugin?)");
			ex.printStackTrace();
			return null;
		}
	}

	public static Object setNBTTag(final Object NBTTag, final Object NMSItem) {
		try {
			java.lang.reflect.Method method;
			method = NMSItem.getClass().getMethod("setTag", NBTTag.getClass());
			method.invoke(NMSItem, NBTTag);
			return NMSItem;
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Object getNMSItemStack(final ItemStack item) {
		@SuppressWarnings("rawtypes")
		final Class cis = getCraftItemStack();
		java.lang.reflect.Method method;
		try {
			method = cis.getMethod("asNMSCopy", ItemStack.class);
			final Object answer = method.invoke(cis, item);
			return answer;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Object getNMSEntity(final Entity entity) {
		@SuppressWarnings("rawtypes")
		final Class cis = getCraftEntity();
		java.lang.reflect.Method method;
		try {
			method = cis.getMethod("getHandle");
			return method.invoke(getCraftEntity().cast(entity));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	public static ItemStack getBukkitItemStack(final Object item) {
		@SuppressWarnings("rawtypes")
		final Class cis = getCraftItemStack();
		java.lang.reflect.Method method;
		try {
			method = cis.getMethod("asCraftMirror", item.getClass());
			final Object answer = method.invoke(cis, item);
			return (ItemStack) answer;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	public static Object getItemRootNBTTagCompound(final Object nmsitem) {
		@SuppressWarnings("rawtypes")
		final Class c = nmsitem.getClass();
		java.lang.reflect.Method method;
		try {
			method = c.getMethod("getTag");
			final Object answer = method.invoke(nmsitem);
			return answer;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	public static Object getEntityNBTTagCompound(final Object nmsitem) {
		@SuppressWarnings("rawtypes")
		final Class c = nmsitem.getClass();
		java.lang.reflect.Method method;
		try {
			method = c.getMethod(MethodNames.getEntitynbtgetterMethodName(), getNBTTagCompound());
			final Object nbt = getNBTTagCompound().newInstance();
			Object answer = method.invoke(nmsitem, nbt);
			if (answer==null)
				answer = nbt;
			return answer;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object setEntityNBTTag(final Object NBTTag, final Object NMSItem) {
		try {
			java.lang.reflect.Method method;
			method = NMSItem.getClass().getMethod(MethodNames.getEntitynbtsetterMethodName(), getNBTTagCompound());
			method.invoke(NMSItem, NBTTag);
			return NMSItem;
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Object getTileEntityNBTTagCompound(final BlockState tile) {
		try {
			final Object pos = getnewBlockPosition(tile.getX(), tile.getY(), tile.getZ());
			final Object cworld = getCraftWorld().cast(tile.getWorld());
			final Object nmsworld = cworld.getClass().getMethod("getHandle").invoke(cworld);
			final Object o = nmsworld.getClass().getMethod("getTileEntity", pos.getClass()).invoke(nmsworld, pos);
			@SuppressWarnings("unchecked")
			final Method method = getTileEntity().getMethod(MethodNames.getTiledataMethodName(), getNBTTagCompound());
			final Object tag = getNBTTagCompound().newInstance();
			Object answer = method.invoke(o, tag);
			if (answer==null)
				answer = tag;
			return answer;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setTileEntityNBTTagCompound(final BlockState tile, final Object comp) {
		try {
			final Object pos = getnewBlockPosition(tile.getX(), tile.getY(), tile.getZ());
			final Object cworld = getCraftWorld().cast(tile.getWorld());
			final Object nmsworld = cworld.getClass().getMethod("getHandle").invoke(cworld);
			final Object o = nmsworld.getClass().getMethod("getTileEntity", pos.getClass()).invoke(nmsworld, pos);
			@SuppressWarnings("unchecked")
			final Method method = getTileEntity().getMethod("a", getNBTTagCompound());
			method.invoke(o, comp);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static Object getSubNBTTagCompound(final Object compound, final String name) {
		@SuppressWarnings("rawtypes")
		final Class c = compound.getClass();
		java.lang.reflect.Method method;
		try {
			method = c.getMethod("getCompound", String.class);
			final Object answer = method.invoke(compound, name);
			return answer;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addNBTTagCompound(final NBTCompound comp, final String name) {
		if (name==null) {
			remove(comp, name);
			return;
		}
		Object nbttag = comp.getCompound();
		if (nbttag==null)
			nbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return;
		final Object workingtag = gettoCompount(nbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("set", String.class, getNBTBase());
			method.invoke(workingtag, name, getNBTTagCompound().newInstance());
			comp.setCompound(nbttag);
			return;
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return;
	}

	public static Boolean valideCompound(final NBTCompound comp) {
		Object root = comp.getCompound();
		if (root==null)
			root = getNewNBTTag();
		return gettoCompount(root, comp)!=null;
	}

	private static Object gettoCompount(Object nbttag, NBTCompound comp) {
		final Stack<String> structure = new Stack<String>();
		while (comp.getParent()!=null) {
			structure.add(comp.getName());
			comp = comp.getParent();
		}
		while (!structure.isEmpty()) {
			nbttag = getSubNBTTagCompound(nbttag, structure.pop());
			if (nbttag==null)
				return null;
		}
		return nbttag;
	}

	public static void setString(final NBTCompound comp, final String key, final String text) {
		if (text==null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("setString", String.class, String.class);
			method.invoke(workingtag, key, text);
			comp.setCompound(rootnbttag);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getString(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("getString", String.class);
			return (String) method.invoke(workingtag, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String getContent(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("get", String.class);
			return method.invoke(workingtag, key).toString();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void setInt(final NBTCompound comp, final String key, final Integer i) {
		if (i==null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("setInt", String.class, int.class);
			method.invoke(workingtag, key, i);
			comp.setCompound(rootnbttag);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Integer getInt(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("getInt", String.class);
			return (Integer) method.invoke(workingtag, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void setByteArray(final NBTCompound comp, final String key, final byte[] b) {
		if (b==null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("setByteArray", String.class, byte[].class);
			method.invoke(workingtag, key, b);
			comp.setCompound(rootnbttag);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return;
	}

	public static byte[] getByteArray(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("getByteArray", String.class);
			return (byte[]) method.invoke(workingtag, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void setIntArray(final NBTCompound comp, final String key, final int[] i) {
		if (i==null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("setIntArray", String.class, int[].class);
			method.invoke(workingtag, key, i);
			comp.setCompound(rootnbttag);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static int[] getIntArray(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("getIntArray", String.class);
			return (int[]) method.invoke(workingtag, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void setFloat(final NBTCompound comp, final String key, final Float f) {
		if (f==null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("setFloat", String.class, float.class);
			method.invoke(workingtag, key, (float) f);
			comp.setCompound(rootnbttag);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Float getFloat(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("getFloat", String.class);
			return (Float) method.invoke(workingtag, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void setLong(final NBTCompound comp, final String key, final Long f) {
		if (f==null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("setLong", String.class, long.class);
			method.invoke(workingtag, key, (long) f);
			comp.setCompound(rootnbttag);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Long getLong(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("getLong", String.class);
			return (Long) method.invoke(workingtag, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void setShort(final NBTCompound comp, final String key, final Short f) {
		if (f==null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("setShort", String.class, short.class);
			method.invoke(workingtag, key, (short) f);
			comp.setCompound(rootnbttag);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Short getShort(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("getShort", String.class);
			return (Short) method.invoke(workingtag, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void setByte(final NBTCompound comp, final String key, final Byte f) {
		if (f==null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("setByte", String.class, byte.class);
			method.invoke(workingtag, key, (byte) f);
			comp.setCompound(rootnbttag);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Byte getByte(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("getByte", String.class);
			return (Byte) method.invoke(workingtag, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void setDouble(final NBTCompound comp, final String key, final Double d) {
		if (d==null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("setDouble", String.class, double.class);
			method.invoke(workingtag, key, d);
			comp.setCompound(rootnbttag);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Double getDouble(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("getDouble", String.class);
			return (Double) method.invoke(workingtag, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static byte getType(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return 0;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod(MethodNames.getTypeMethodName(), String.class);
			return (Byte) method.invoke(workingtag, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public static void setBoolean(final NBTCompound comp, final String key, final Boolean d) {
		if (d==null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("setBoolean", String.class, boolean.class);
			method.invoke(workingtag, key, d);
			comp.setCompound(rootnbttag);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Boolean getBoolean(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("getBoolean", String.class);
			return (Boolean) method.invoke(workingtag, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void set(final NBTCompound comp, final String key, final Object val) {
		if (val==null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp)) {
			new Throwable("InvalideCompound").printStackTrace();
			return;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("set", String.class, getNBTBase());
			method.invoke(workingtag, key, val);
			comp.setCompound(rootnbttag);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static NBTList getList(final NBTCompound comp, final String key, final NBTType type) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("getList", String.class, int.class);
			return new NBTList(comp, key, type, method.invoke(workingtag, key, type.getId()));
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void setObject(final NBTCompound comp, final String key, final Object value) {
		if (!MinecraftVersion.hasGson())
			return;
		try {
			final String json = GsonWrapper.getString(value);
			setString(comp, key, json);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static <T> T getObject(final NBTCompound comp, final String key, final Class<T> type) {
		if (!MinecraftVersion.hasGson())
			return null;
		final String json = getString(comp, key);
		if (json==null)
			return null;
		return GsonWrapper.deserializeJson(json, type);
	}

	public static void remove(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("remove", String.class);
			method.invoke(workingtag, key);
			comp.setCompound(rootnbttag);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Boolean hasKey(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("hasKey", String.class);
			return (Boolean) method.invoke(workingtag, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Set<String> getKeys(final NBTCompound comp) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag==null)
			rootnbttag = getNewNBTTag();
		if (!valideCompound(comp))
			return null;
		final Object workingtag = gettoCompount(rootnbttag, comp);
		java.lang.reflect.Method method;
		try {
			method = workingtag.getClass().getMethod("c");
			return (Set<String>) method.invoke(workingtag);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
