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
