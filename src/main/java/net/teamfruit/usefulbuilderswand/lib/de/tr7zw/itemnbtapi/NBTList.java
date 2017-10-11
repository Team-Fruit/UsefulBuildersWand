package net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi;

import java.lang.reflect.Method;

public class NBTList {

	private String listname;
	private NBTCompound parent;
	private NBTType type;
	private Object listobject;

	protected NBTList(final NBTCompound owner, final String name, final NBTType type, final Object list) {
		this.parent = owner;
		this.listname = name;
		this.type = type;
		this.listobject = list;
		if (!(type==NBTType.NBTTagString||type==NBTType.NBTTagCompound))
			System.err.println("List types != String/Compound are currently not implemented!");
	}

	protected void save() {
		this.parent.set(this.listname, this.listobject);
	}

	public NBTListCompound addCompound() {
		if (this.type!=NBTType.NBTTagCompound) {
			new Throwable("Using Compound method on a non Compound list!").printStackTrace();
			return null;
		}
		try {
			final Method m = this.listobject.getClass().getMethod("add", NBTReflectionUtil.getNBTBase());
			final Object comp = NBTReflectionUtil.getNBTTagCompound().newInstance();
			m.invoke(this.listobject, comp);
			return new NBTListCompound(this, comp);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public NBTListCompound getCompound(final int id) {
		if (this.type!=NBTType.NBTTagCompound) {
			new Throwable("Using Compound method on a non Compound list!").printStackTrace();
			return null;
		}
		try {
			final Method m = this.listobject.getClass().getMethod("get", int.class);
			final Object comp = m.invoke(this.listobject, id);
			return new NBTListCompound(this, comp);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public String getString(final int i) {
		if (this.type!=NBTType.NBTTagString) {
			new Throwable("Using String method on a non String list!").printStackTrace();
			return null;
		}
		try {
			final Method m = this.listobject.getClass().getMethod("getString", int.class);
			return (String) m.invoke(this.listobject, i);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void addString(final String s) {
		if (this.type!=NBTType.NBTTagString) {
			new Throwable("Using String method on a non String list!").printStackTrace();
			return;
		}
		try {
			final Method m = this.listobject.getClass().getMethod("add", NBTReflectionUtil.getNBTBase());
			m.invoke(this.listobject, NBTReflectionUtil.getNBTTagString().getConstructor(String.class).newInstance(s));
			save();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void setString(final int i, final String s) {
		if (this.type!=NBTType.NBTTagString) {
			new Throwable("Using String method on a non String list!").printStackTrace();
			return;
		}
		try {
			final Method m = this.listobject.getClass().getMethod("a", int.class, NBTReflectionUtil.getNBTBase());
			m.invoke(this.listobject, i, NBTReflectionUtil.getNBTTagString().getConstructor(String.class).newInstance(s));
			save();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public void remove(final int i) {
		try {
			final Method m = this.listobject.getClass().getMethod(MethodNames.getremoveMethodName(), int.class);
			m.invoke(this.listobject, i);
			save();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public int size() {
		try {
			final Method m = this.listobject.getClass().getMethod("size");
			return (Integer) m.invoke(this.listobject);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	public NBTType getType() {
		return this.type;
	}

}
