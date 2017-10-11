package net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi;

public class MethodNames {

	protected static String getTiledataMethodName() {
		final MinecraftVersion v = MinecraftVersion.getVersion();
		if (v==MinecraftVersion.MC1_8_R3)
			return "b";
		return "save";
	}

	protected static String getTypeMethodName() {
		final MinecraftVersion v = MinecraftVersion.getVersion();
		if (v==MinecraftVersion.MC1_8_R3)
			return "b";
		return "d";
	}

	protected static String getEntitynbtgetterMethodName() {
		// final MinecraftVersion v = MinecraftVersion.getVersion();
		return "b";
	}

	protected static String getEntitynbtsetterMethodName() {
		// final MinecraftVersion v = MinecraftVersion.getVersion();
		return "a";
	}

	protected static String getremoveMethodName() {
		final MinecraftVersion v = MinecraftVersion.getVersion();
		if (v==MinecraftVersion.MC1_8_R3)
			return "a";
		return "remove";
	}

}
