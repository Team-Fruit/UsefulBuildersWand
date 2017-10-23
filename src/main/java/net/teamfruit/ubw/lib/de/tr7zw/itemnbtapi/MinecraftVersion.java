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

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

public enum MinecraftVersion {
	Unknown(0, null),
	MC1_7_R4(174, "v1_7"),
	MC1_8_R3(183, "v1_8"),
	MC1_9_R1(191, "v1_9_R1"),
	MC1_9_R2(192, "v1_9"),
	MC1_10_R1(1101, "v1_10"),
	MC1_11_R1(1111, "v1_11"),
	MC1_12_R1(1121, "v1_12");

	MinecraftVersion(final int id, final String name) {
		this.id = id;
		this.name = name;
	}

	private static MinecraftVersion version;
	private final int id;
	private final String name;

	public int getId() {
		return this.id;
	}

	public static MinecraftVersion getVersion() {
		if (version!=null)
			return version;

		final String NBTAPI_PREF = "[UsefulBuildersWand-NBTAPI] ";

		final String ver = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		System.out.println(NBTAPI_PREF+"Found Spigot: "+ver+"! Trying to find NMS support");
		try {
			version = MinecraftVersion.valueOf(ver.replace("v", "MC"));
		} catch (final IllegalArgumentException ex) {
			for (final MinecraftVersion mcver : values())
				if (StringUtils.startsWith(ver, mcver.name)) {
					System.out.println(NBTAPI_PREF+"NMS '"+ver+"' is not found but '"+mcver.name().replace("MC", "v")+"' found.");
					version = mcver;
					break;
				}
			version = MinecraftVersion.Unknown;
		}
		if (version!=Unknown)
			System.out.println(NBTAPI_PREF+"NMS support '"+version.name()+"' loaded!");
		else
			System.out.println(NBTAPI_PREF+"Wasn't able to find NMS Support! Some functions will not work!");
		return version;
	}

	private static Boolean cache = null;

	public static boolean hasGson() {
		if (cache!=null)
			return cache;
		cache = false;
		try {
			System.out.println("Found Gson: "+Class.forName("com.google.gson.Gson"));
			cache = true;
			return cache;
		} catch (final Exception ex) {
			return cache;
		}
	}

}
