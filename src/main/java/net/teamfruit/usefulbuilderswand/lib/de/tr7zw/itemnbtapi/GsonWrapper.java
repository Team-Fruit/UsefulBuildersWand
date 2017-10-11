package net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi;

import com.google.gson.Gson;

public class GsonWrapper {

	private static final Gson gson = new Gson();

	public static String getString(final Object obj) {
		return gson.toJson(obj);
	}

	public static <T> T deserializeJson(final String json, final Class<T> type) {
		try {
			if (json==null)
				return null;

			final T obj = gson.fromJson(json, type);
			return type.cast(obj);
		} catch (final Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
