package net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi;

import java.util.HashSet;
import java.util.Set;

public class NBTListCompound {

	private NBTList owner;
	private Object compound;

	protected NBTListCompound(final NBTList parent, final Object obj) {
		this.owner = parent;
		this.compound = obj;
	}

	public void setString(final String key, final String val) {
		if (val==null) {
			remove(key);
			return;
		}
		try {
			this.compound.getClass().getMethod("setString", String.class, String.class).invoke(this.compound, key, val);
			this.owner.save();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setInteger(final String key, final int val) {
		try {
			this.compound.getClass().getMethod("setInt", String.class, int.class).invoke(this.compound, key, val);
			this.owner.save();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public int getInteger(final String key) {
		try {
			return (Integer) this.compound.getClass().getMethod("getInt", String.class).invoke(this.compound, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public void setDouble(final String key, final double val) {
		try {
			this.compound.getClass().getMethod("setDouble", String.class, double.class).invoke(this.compound, key, val);
			this.owner.save();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public double getDouble(final String key) {
		try {
			return (Double) this.compound.getClass().getMethod("getDouble", String.class).invoke(this.compound, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public String getString(final String key) {
		try {
			return (String) this.compound.getClass().getMethod("getString", String.class).invoke(this.compound, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	public boolean hasKey(final String key) {
		try {
			return (Boolean) this.compound.getClass().getMethod("hasKey", String.class).invoke(this.compound, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public Set<String> getKeys() {
		try {
			@SuppressWarnings("unchecked")
			final Set<String> set = (Set<String>) this.compound.getClass().getMethod("c").invoke(this.compound);
			return set;
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return new HashSet<String>();
	}

	public void remove(final String key) {
		try {
			this.compound.getClass().getMethod("remove", String.class).invoke(this.compound, key);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

}
