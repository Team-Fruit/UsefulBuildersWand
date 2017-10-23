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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * bStats collects some data for plugin authors.
 *
 * Check out https://bStats.org/ to learn more about bStats!
 */
public class MetricsLite {

	// The version of this bStats class
	public static final int B_STATS_VERSION = 1;

	// The url to which the data is sent
	private static final String URL = "https://bStats.org/submitData/bukkit";

	// Should failed requests be logged?
	private static boolean logFailedRequests;

	// The uuid of the server
	private static String serverUUID;

	// The plugin
	private final JavaPlugin plugin;

	/**
	 * Class constructor.
	 *
	 * @param plugin The plugin which stats should be submitted.
	 */
	public MetricsLite(final JavaPlugin plugin) {
		if (plugin==null)
			throw new IllegalArgumentException("Plugin cannot be null!");
		this.plugin = plugin;

		// Get the config file
		final File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
		final File configFile = new File(bStatsFolder, "config.yml");
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

		// Check if the config file exists
		if (!config.isSet("serverUuid")) {

			// Add default values
			config.addDefault("enabled", true);
			// Every server gets it's unique random id.
			config.addDefault("serverUuid", UUID.randomUUID().toString());
			// Should failed request be logged?
			config.addDefault("logFailedRequests", false);

			// Inform the server owners about bStats
			config.options().header(
					"bStats collects some data for plugin authors like how many servers are using their plugins.\n"+
							"To honor their work, you should not disable it.\n"+
							"This has nearly no effect on the server performance!\n"+
							"Check out https://bStats.org/ to learn more :)")
					.copyDefaults(true);
			try {
				config.save(configFile);
			} catch (final IOException ignored) {
			}
		}

		// Load the data
		serverUUID = config.getString("serverUuid");
		logFailedRequests = config.getBoolean("logFailedRequests", false);
		if (config.getBoolean("enabled", true)) {
			boolean found = false;
			// Search for all other bStats Metrics classes to see if we are the first one
			for (final Class<?> service : Bukkit.getServicesManager().getKnownServices())
				try {
					service.getField("B_STATS_VERSION"); // Our identifier :)
					found = true; // We aren't the first
					break;
				} catch (final NoSuchFieldException ignored) {
				}
			// Register our service
			Bukkit.getServicesManager().register(MetricsLite.class, this, plugin, ServicePriority.Normal);
			if (!found)
				// We are the first!
				startSubmitting();
		}
	}

	/**
	 * Starts the Scheduler which submits our data every 30 minutes.
	 */
	private void startSubmitting() {
		final Timer timer = new Timer(true); // We use a timer cause the Bukkit scheduler is affected by server lags
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (!MetricsLite.this.plugin.isEnabled()) { // Plugin was disabled
					timer.cancel();
					return;
				}
				// Nevertheless we want our code to run in the Bukkit main thread, so we have to use the Bukkit scheduler
				// Don't be afraid! The connection to the bStats server is still async, only the stats collection is sync ;)
				Bukkit.getScheduler().runTask(MetricsLite.this.plugin, new Runnable() {
					@Override
					public void run() {
						submitData();
					}
				});
			}
		}, 1000*60*5, 1000*60*30);
		// Submit the data every 30 minutes, first time after 5 minutes to give other plugins enough time to start
		// WARNING: Changing the frequency has no effect but your plugin WILL be blocked/deleted!
		// WARNING: Just don't do it!
	}

	/**
	 * Gets the plugin specific data.
	 * This method is called using Reflection.
	 *
	 * @return The plugin specific data.
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getPluginData() {
		final JSONObject data = new JSONObject();

		final String pluginName = this.plugin.getDescription().getName();
		final String pluginVersion = this.plugin.getDescription().getVersion();

		data.put("pluginName", pluginName); // Append the name of the plugin
		data.put("pluginVersion", pluginVersion); // Append the version of the plugin
		final JSONArray customCharts = new JSONArray();
		data.put("customCharts", customCharts);

		return data;
	}

	/**
	 * Gets the server specific data.
	 *
	 * @return The server specific data.
	 */
	@SuppressWarnings("unchecked")
	private JSONObject getServerData() {
		// Minecraft specific data
		final int playerAmount = Bukkit.getOnlinePlayers().size();
		final int onlineMode = Bukkit.getOnlineMode() ? 1 : 0;
		String bukkitVersion = Bukkit.getVersion();
		bukkitVersion = bukkitVersion.substring(bukkitVersion.indexOf("MC: ")+4, bukkitVersion.length()-1);

		// OS/Java specific data
		final String javaVersion = System.getProperty("java.version");
		final String osName = System.getProperty("os.name");
		final String osArch = System.getProperty("os.arch");
		final String osVersion = System.getProperty("os.version");
		final int coreCount = Runtime.getRuntime().availableProcessors();

		final JSONObject data = new JSONObject();

		data.put("serverUUID", serverUUID);

		data.put("playerAmount", playerAmount);
		data.put("onlineMode", onlineMode);
		data.put("bukkitVersion", bukkitVersion);

		data.put("javaVersion", javaVersion);
		data.put("osName", osName);
		data.put("osArch", osArch);
		data.put("osVersion", osVersion);
		data.put("coreCount", coreCount);

		return data;
	}

	/**
	 * Collects the data and sends it afterwards.
	 */
	@SuppressWarnings("unchecked")
	private void submitData() {
		final JSONObject data = getServerData();

		final JSONArray pluginData = new JSONArray();
		// Search for all other bStats Metrics classes to get their plugin data
		for (final Class<?> service : Bukkit.getServicesManager().getKnownServices()) {
			try {
				service.getField("B_STATS_VERSION"); // Our identifier :)
			} catch (final NoSuchFieldException ignored) {
				continue; // Continue "searching"
			}
			// Found one!
			try {
				pluginData.add(service.getMethod("getPluginData").invoke(Bukkit.getServicesManager().load(service)));
			} catch (final NoSuchMethodException ignored) {
			} catch (final IllegalAccessException ignored) {
			} catch (final InvocationTargetException ignored) {
			}
		}

		data.put("plugins", pluginData);

		// Create a new thread for the connection to the bStats server
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// Send the data
					sendData(data);
				} catch (final Exception e) {
					// Something went wrong! :(
					if (logFailedRequests)
						MetricsLite.this.plugin.getLogger().log(Level.WARNING, "Could not submit plugin stats of "+MetricsLite.this.plugin.getName(), e);
				}
			}
		}).start();
	}

	/**
	 * Sends the data to the bStats server.
	 *
	 * @param data The data to send.
	 * @throws Exception If the request failed.
	 */
	private static void sendData(final JSONObject data) throws Exception {
		if (data==null)
			throw new IllegalArgumentException("Data cannot be null!");
		if (Bukkit.isPrimaryThread())
			throw new IllegalAccessException("This method must not be called from the main thread!");
		final HttpsURLConnection connection = (HttpsURLConnection) new URL(URL).openConnection();

		// Compress the data to save bandwidth
		final byte[] compressedData = compress(data.toString());

		// Add headers
		connection.setRequestMethod("POST");
		connection.addRequestProperty("Accept", "application/json");
		connection.addRequestProperty("Connection", "close");
		connection.addRequestProperty("Content-Encoding", "gzip"); // We gzip our request
		connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
		connection.setRequestProperty("Content-Type", "application/json"); // We send our data in JSON format
		connection.setRequestProperty("User-Agent", "MC-Server/"+B_STATS_VERSION);

		// Send data
		connection.setDoOutput(true);
		final DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
		outputStream.write(compressedData);
		outputStream.flush();
		outputStream.close();

		connection.getInputStream().close(); // We don't care about the response - Just send our data :)
	}

	/**
	 * Gzips the given String.
	 *
	 * @param str The string to gzip.
	 * @return The gzipped String.
	 * @throws IOException If the compression failed.
	 */
	private static byte[] compress(final String str) throws IOException {
		if (str==null)
			return null;
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
		gzip.write(str.getBytes("UTF-8"));
		gzip.close();
		return outputStream.toByteArray();
	}

}
