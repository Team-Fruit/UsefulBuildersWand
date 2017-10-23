package net.teamfruit.usefulbuilderswand;

import static net.teamfruit.usefulbuilderswand.meta.WandMetaUtils.*;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import net.teamfruit.usefulbuilderswand.I18n.Locale;
import net.teamfruit.usefulbuilderswand.meta.Features;
import net.teamfruit.usefulbuilderswand.meta.IWandMeta;
import net.teamfruit.usefulbuilderswand.meta.WandItem;
import net.teamfruit.usefulbuilderswand.meta.WandItemMeta;

public class CommandListener implements CommandExecutor {
	private final Locale locale;
	private final WandData wanddata;
	private final NativeMinecraft nativemc;

	public CommandListener(final Locale locale, final WandData wanddata, final NativeMinecraft nativemc) {
		this.locale = locale;
		this.wanddata = wanddata;
		this.nativemc = nativemc;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (args.length<1)
			return false;
		return sendResultMessage(onCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length)), this.locale, sender);
	}

	private @Nonnull CommandResult onCommand(final CommandSender sender, final String type, final String[] args) {
		if (StringUtils.equalsIgnoreCase(type, "set")||StringUtils.equalsIgnoreCase(type, "get")||StringUtils.equalsIgnoreCase(type, "remove")) {
			if (!(sender instanceof Player))
				return CommandResult.error(I18n.format(this.locale, "ubw.command.error.notplayer"));
			final Player player = (Player) sender;
			final ItemStack itemStack = this.nativemc.getItemInHand(player.getInventory());
			if (itemStack==null)
				return CommandResult.error(I18n.format(this.locale, "ubw.command.error.itemnotinhand"));
			final WandItem witem = new WandItem(itemStack);
			final WandItemMeta wmeta = witem.getMeta();
			if (wmeta==null)
				return CommandResult.error(I18n.format(this.locale, "ubw.command.error.itemnotwand"), I18n.format(this.locale, "ubw.command.error.itemnotwand.msg"));
			if (StringUtils.equalsIgnoreCase(type, "set")||StringUtils.equalsIgnoreCase(type, "remove")) {
				if (args.length<1)
					if (StringUtils.equalsIgnoreCase(type, "remove"))
						return CommandResult.error(I18n.format(this.locale, "ubw.command.error.remove"));
					else
						return CommandResult.error(I18n.format(this.locale, "ubw.command.error.set"));
				final Features ft = Features.getFeatureKey(args[0]);
				if (ft==null)
					return CommandResult.error(I18n.format(this.locale, "ubw.command.error.invalidproperty"), I18n.format(this.locale, "ubw.command.error.invalidproperty.seehelp"));
				Object value;
				if (StringUtils.equalsIgnoreCase(type, "remove")) {
					final IWandMeta cfgmeta = this.wanddata.configMeta();
					value = get(cfgmeta, ft);
					set(wmeta, ft, null);
				} else
					set(wmeta, ft, value = args.length<2 ? "" : args[1]);
				this.wanddata.updateItem(witem);
				this.nativemc.setItemInHand(player.getInventory(), witem.getItem());
				return CommandResult.success(I18n.format(this.locale, "ubw.command.success.set", ft.key, ft.type, value));
			} else if (StringUtils.equalsIgnoreCase(type, "get")) {
				final IWandMeta meta = this.wanddata.wrapMeta(wmeta);
				CommandResult result;
				if (args.length<1) {
					final List<String> msgs = Lists.newArrayList();
					for (final Features ft : Features.values()) {
						final Object value = get(meta, ft);
						msgs.add(I18n.format(this.locale, "ubw.command.success.getall.sub", I18n.format(this.locale, "ubw.command.success.get", ft.key, ft.type, value)));
					}
					result = CommandResult.success(I18n.format(this.locale, "ubw.command.success.getall.main"), msgs.toArray(new String[msgs.size()]));
				} else {
					final Features ft = Features.getFeatureKey(args[0]);
					if (ft==null)
						return CommandResult.error(I18n.format(this.locale, "ubw.command.error.invalidproperty"), I18n.format(this.locale, "ubw.command.error.invalidproperty.seehelp"));
					final Object value = get(meta, ft);
					this.wanddata.updateItem(witem);
					this.nativemc.setItemInHand(player.getInventory(), witem.getItem());
					return CommandResult.success(I18n.format(this.locale, "ubw.command.success.set", ft.key, ft.type, value));
				}
				this.wanddata.updateItem(witem);
				this.nativemc.setItemInHand(player.getInventory(), witem.getItem());
				return result;
			}
		} else if (StringUtils.equalsIgnoreCase(type, "help")) {
			final List<String> msgs = Lists.newArrayList();
			if (args.length>=1&&StringUtils.equalsIgnoreCase(args[0], "defaults")) {
				msgs.add(I18n.format(this.locale, "ubw.command.success.help.defaults.main"));
				final IWandMeta cfgmeta = this.wanddata.configMeta();
				for (final Features ft : Features.values()) {
					final Object value = get(cfgmeta, ft);
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
		} else if (StringUtils.equalsIgnoreCase(type, "create")) {
			if (sender instanceof Player) {
				final Player player = (Player) sender;
				final ItemStack itemStack = this.nativemc.getItemInHand(player.getInventory());
				if (itemStack==null||itemStack.getAmount()==0)
					return CommandResult.error(I18n.format(this.locale, "ubw.command.error.itemnotinhand"));
				final WandItem witem = new WandItem(itemStack);
				witem.activate();
				this.wanddata.updateItem(witem);
				this.nativemc.setItemInHand(player.getInventory(), witem.getItem());
			}
		} else
			return CommandResult.unknown();
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
