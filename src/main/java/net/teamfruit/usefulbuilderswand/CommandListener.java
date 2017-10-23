package net.teamfruit.usefulbuilderswand;

import static net.teamfruit.usefulbuilderswand.meta.WandMetaUtils.*;

import java.util.Arrays;
import java.util.List;

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
		final CommandResult result = onCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
		switch (result.getType()) {
			case UNKNOWN:
				return false;
			case ERROR: {
				final String message = result.getMessage();
				if (message!=null)
					sender.sendMessage(String.format("§c[UBW] %s", message));
				final String[] details = result.getDetails();
				for (final String detail : details)
					sender.sendMessage(String.format("§c        %s", detail));
				break;
			}
			default:
			case SUCCESS: {
				final String message = result.getMessage();
				if (message!=null)
					sender.sendMessage(String.format("§f[UBW] %s", message));
				final String[] details = result.getDetails();
				for (final String detail : details)
					sender.sendMessage(String.format("§f        %s", detail));
				break;
			}
		}
		return true;
	}

	private CommandResult onCommand(final CommandSender sender, final String type, final String[] args) {
		if (StringUtils.equalsIgnoreCase(type, "set")||StringUtils.equalsIgnoreCase(type, "get")||StringUtils.equalsIgnoreCase(type, "remove")) {
			if (!(sender instanceof Player))
				return CommandResult.error("you must be a player.");
			final Player player = (Player) sender;
			final ItemStack itemStack = this.nativemc.getItemInHand(player.getInventory());
			if (itemStack==null)
				return CommandResult.error("hold the item in your hand");
			final WandItem witem = new WandItem(itemStack);
			final WandItemMeta wmeta = witem.getMeta();
			if (wmeta==null)
				return CommandResult.error("this item is not a wand item", "type '/ubw create' to activate your item");
			if (StringUtils.equalsIgnoreCase(type, "set")||StringUtils.equalsIgnoreCase(type, "remove")) {
				final Features ft = Features.getFeatureKey(args[0]);
				if (ft==null)
					return CommandResult.error("invalid property", "see '/ubw help'");
				Object value;
				if (StringUtils.equalsIgnoreCase(type, "remove")) {
					final IWandMeta cfgmeta = this.wanddata.configMeta();
					value = get(cfgmeta, ft);
					set(wmeta, ft, null);
				} else
					set(wmeta, ft, value = args.length<2 ? "" : args[1]);
				this.wanddata.updateItem(witem);
				this.nativemc.setItemInHand(player.getInventory(), witem.getItem());
				return CommandResult.success(String.format("§7%s <=§f %s", args[0], value));
			} else if (StringUtils.equalsIgnoreCase(type, "get")) {
				final IWandMeta meta = this.wanddata.wrapMeta(wmeta);
				CommandResult result;
				if (args.length<1) {
					final List<String> msgs = Lists.newArrayList();
					for (final Features ft : Features.values()) {
						final Object value = get(meta, ft);
						msgs.add(String.format("§7%s [%s] =>§f %s", ft.key, ft.type, value));
					}
					result = CommandResult.success("your wand property:", msgs.toArray(new String[msgs.size()]));
				} else {
					final Features ft = Features.getFeatureKey(args[0]);
					if (ft==null)
						return CommandResult.error("invalid property", "see '/ubw help'");
					final Object value = get(meta, ft);
					this.wanddata.updateItem(witem);
					this.nativemc.setItemInHand(player.getInventory(), witem.getItem());
					result = CommandResult.success(String.format("§7%s [%s] =>§f %s", ft.key, ft.type, value));
				}
				this.wanddata.updateItem(witem);
				this.nativemc.setItemInHand(player.getInventory(), witem.getItem());
				return result;
			}
		} else if (StringUtils.equalsIgnoreCase(type, "help")) {
			final List<String> msgs = Lists.newArrayList();
			if (args.length>=1&&StringUtils.equalsIgnoreCase(args[0], "defaults")) {
				msgs.add("default properties:");
				final IWandMeta cfgmeta = this.wanddata.configMeta();
				for (final Features ft : Features.values()) {
					final Object value = get(cfgmeta, ft);
					msgs.add(String.format("§7%s [%s] =>§f %s", ft.key, ft.type, value));
				}
			} else {
				msgs.add("/ubw create §7the item in your hand will be a wand");
				msgs.add("/ubw set <property> <value> §7set property");
				msgs.add("/ubw remove <property> §7set property to default");
				msgs.add("/ubw get §7get property list");
				msgs.add("/ubw get <property> §7get property");
				msgs.add("/ubw help defaults §7get default properties");
			}
			return CommandResult.success("UsefulBuildersWand help", msgs.toArray(new String[msgs.size()]));
		} else if (StringUtils.equalsIgnoreCase(type, "create")) {
			if (sender instanceof Player) {
				final Player player = (Player) sender;
				final ItemStack itemStack = this.nativemc.getItemInHand(player.getInventory());
				if (itemStack==null||itemStack.getAmount()==0)
					return CommandResult.error("No Items");
				final WandItem witem = new WandItem(itemStack);
				witem.activate();
				this.wanddata.updateItem(witem);
				this.nativemc.setItemInHand(player.getInventory(), witem.getItem());
			}
		} else
			return CommandResult.unknown();
		return CommandResult.success();
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
