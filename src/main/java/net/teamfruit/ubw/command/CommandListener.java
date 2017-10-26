package net.teamfruit.ubw.command;

import static net.teamfruit.ubw.meta.WandMetaUtils.*;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import net.teamfruit.ubw.I18n;
import net.teamfruit.ubw.I18n.Locale;
import net.teamfruit.ubw.NativeMinecraft;
import net.teamfruit.ubw.WandData;
import net.teamfruit.ubw.WandItemStage;
import net.teamfruit.ubw.meta.WandFeature;
import net.teamfruit.ubw.meta.WandFeatureRegistry;

public class CommandListener implements CommandExecutor {
	private final Locale locale;
	private final NativeMinecraft nativemc;

	public CommandListener(final Locale locale, final NativeMinecraft nativemc) {
		this.locale = locale;
		this.nativemc = nativemc;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		CommandResult result;
		if (args.length<1)
			result = onCommand(sender, null, new String[0]);
		else
			result = onCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
		return sendResultMessage(result, this.locale, sender);
	}

	private @Nonnull CommandResult onCommand(final CommandSender sender, final String type, final String[] args) {
		if (StringUtils.equalsIgnoreCase(type, "set")||StringUtils.equalsIgnoreCase(type, "get")||StringUtils.equalsIgnoreCase(type, "remove")||StringUtils.equalsIgnoreCase(type, "create")) {
			if (!sender.hasPermission("ubw"))
				return CommandResult.error(I18n.format(this.locale, "ubw.command.error.permission", "ubw"));
			if (!(sender instanceof Player))
				return CommandResult.error(I18n.format(this.locale, "ubw.command.error.notplayer"));
			final Player player = (Player) sender;
			final WandItemStage stage = new WandItemStage();
			stage.setItem(this.nativemc.getItemInHand(player.getInventory()));
			if (!stage.isItem())
				return CommandResult.error(I18n.format(this.locale, "ubw.command.error.itemnotinhand"));
			try {
				if (StringUtils.equalsIgnoreCase(type, "create")) {
					if (!sender.hasPermission("ubw.create"))
						return CommandResult.error(I18n.format(this.locale, "ubw.command.error.permission", "ubw.create"));
					stage.getWandItem();
				} else {
					if (!stage.isWandItem())
						return CommandResult.error(I18n.format(this.locale, "ubw.command.error.itemnotwand"), I18n.format(this.locale, "ubw.command.error.itemnotwand.msg"));
					if (StringUtils.equalsIgnoreCase(type, "set")||StringUtils.equalsIgnoreCase(type, "remove")) {
						if (!sender.hasPermission("ubw.set"))
							return CommandResult.error(I18n.format(this.locale, "ubw.command.error.permission", "ubw.set"));
						if (args.length<1)
							if (StringUtils.equalsIgnoreCase(type, "remove"))
								return CommandResult.error(I18n.format(this.locale, "ubw.command.error.remove"));
							else
								return CommandResult.error(I18n.format(this.locale, "ubw.command.error.set"));
						final WandFeature<?> ft = WandFeatureRegistry.getFeatureKey(args[0]);
						if (ft==null)
							return CommandResult.error(I18n.format(this.locale, "ubw.command.error.invalidproperty"), I18n.format(this.locale, "ubw.command.error.invalidproperty.seehelp"));
						// if (!sender.hasPermission(ft.permission))
						//	return CommandResult.error(I18n.format(this.locale, "ubw.command.error.permission", ft.permission));
						Object value;
						if (StringUtils.equalsIgnoreCase(type, "remove")) {
							value = get(WandData.INSTANCE.configMeta(), ft);
							set(stage.meta(), ft, null);
						} else
							set(stage.meta(), ft, value = args.length<2 ? "" : args[1]);
						return CommandResult.success(I18n.format(this.locale, "ubw.command.success.set", ft.key, ft.type, value));
					} else if (StringUtils.equalsIgnoreCase(type, "get")) {
						if (!sender.hasPermission("ubw.get"))
							return CommandResult.error(I18n.format(this.locale, "ubw.command.error.permission", "ubw.get"));
						if (args.length<1) {
							final List<String> msgs = Lists.newArrayList();
							for (final WandFeature<?> ft : WandFeatureRegistry.getFeatures()) {
								final Object value = get(stage.meta(), ft);
								msgs.add(I18n.format(this.locale, "ubw.command.success.getall.sub", I18n.format(this.locale, "ubw.command.success.get", ft.key, ft.type, value)));
							}
							return CommandResult.success(I18n.format(this.locale, "ubw.command.success.getall.main"), msgs.toArray(new String[msgs.size()]));
						} else {
							final WandFeature<?> ft = WandFeatureRegistry.getFeatureKey(args[0]);
							if (ft==null)
								return CommandResult.error(I18n.format(this.locale, "ubw.command.error.invalidproperty"), I18n.format(this.locale, "ubw.command.error.invalidproperty.seehelp"));
							final Object value = get(stage.meta(), ft);
							return CommandResult.success(I18n.format(this.locale, "ubw.command.success.set", ft.key, ft.type, value));
						}
					}
				}
			} finally {
				if (stage.isWandItem()) {
					stage.updateItem();
					this.nativemc.setItemInHand(player.getInventory(), stage.getItem());
				}
			}
		} else if (StringUtils.equalsIgnoreCase(type, "help")) {
			if (!sender.hasPermission("ubw.help"))
				return CommandResult.error(I18n.format(this.locale, "ubw.command.error.permission", "ubw.help"));
			final List<String> msgs = Lists.newArrayList();
			if (args.length>=1&&StringUtils.equalsIgnoreCase(args[0], "defaults")) {
				if (!sender.hasPermission("ubw.help.defaults"))
					return CommandResult.error(I18n.format(this.locale, "ubw.command.error.permission", "ubw.help.defaults"));
				msgs.add(I18n.format(this.locale, "ubw.command.success.help.defaults.main"));
				for (final WandFeature<?> ft : WandFeatureRegistry.getFeatures()) {
					final Object value = get(WandData.INSTANCE.configMeta(), ft);
					msgs.add(I18n.format(this.locale, "ubw.command.success.help.defaults.sub", I18n.format(this.locale, "ubw.command.success.get", ft.key, ft.type, value)));
				}
			} else {
				msgs.add(I18n.format(this.locale, "ubw.command.success.help.sub.create"));
				msgs.add(I18n.format(this.locale, "ubw.command.success.help.sub.set"));
				msgs.add(I18n.format(this.locale, "ubw.command.success.help.sub.remove"));
				msgs.add(I18n.format(this.locale, "ubw.command.success.help.sub.getall"));
				msgs.add(I18n.format(this.locale, "ubw.command.success.help.sub.get"));
				msgs.add(I18n.format(this.locale, "ubw.command.success.help.sub.defaults"));
			}
			return CommandResult.success(I18n.format(this.locale, "ubw.command.success.help.main"), msgs.toArray(new String[msgs.size()]));
		} else if (type!=null)
			return CommandResult.error(I18n.format(this.locale, "ubw.command.error.unknown"));
		else
			return CommandResult.success(I18n.format(this.locale, "ubw.command.success.unknown"));
		return CommandResult.success();
	}

	public boolean sendResultMessage(final CommandResult result, final Locale locale, final CommandSender sender) {
		switch (result.getType()) {
			case UNKNOWN:
				return false;
			case ERROR: {
				final String message = result.getMessage();
				if (message!=null)
					sender.sendMessage(I18n.format(locale, "ubw.command.format.error.main", message));
				final String[] details = result.getDetails();
				for (final String detail : details)
					sender.sendMessage(I18n.format(locale, "ubw.command.format.error.sub", detail));
				return true;
			}
			default:
			case SUCCESS: {
				final String message = result.getMessage();
				if (message!=null)
					sender.sendMessage(I18n.format(locale, "ubw.command.format.success.main", message));
				final String[] details = result.getDetails();
				for (final String detail : details)
					sender.sendMessage(I18n.format(locale, "ubw.command.format.success.sub", detail));
				return true;
			}
		}
	}

	public static class CommandResult {
		private ResultType type;
		private String message;
		private String[] details;

		private CommandResult(final ResultType type, final String message, final String... details) {
			this.type = type;
			this.message = message;
			this.details = details;
		}

		public ResultType getType() {
			return this.type;
		}

		public String getMessage() {
			return this.message;
		}

		public String[] getDetails() {
			return this.details;
		}

		public static CommandResult success(final String message, final String... details) {
			return new CommandResult(ResultType.SUCCESS, message, details);
		}

		public static CommandResult success() {
			return new CommandResult(ResultType.SUCCESS, null);
		}

		public static CommandResult error(final String message, final String... details) {
			return new CommandResult(ResultType.ERROR, message, details);
		}

		public static CommandResult unknown() {
			return new CommandResult(ResultType.UNKNOWN, null);
		}

		public enum ResultType {
			SUCCESS,
			ERROR,
			UNKNOWN,
		}
	}
}
