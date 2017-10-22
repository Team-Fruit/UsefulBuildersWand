package net.teamfruit.usefulbuilderswand;

import static net.teamfruit.usefulbuilderswand.meta.Features.*;
import static net.teamfruit.usefulbuilderswand.meta.WandMetaUtils.*;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.teamfruit.usefulbuilderswand.meta.Features;
import net.teamfruit.usefulbuilderswand.meta.IWandMeta;
import net.teamfruit.usefulbuilderswand.meta.WandItem;
import net.teamfruit.usefulbuilderswand.meta.WandItemMeta;

public class CommandListener implements CommandExecutor {
	private final WandData wanddata;
	private NativeMinecraft nativemc;

	public CommandListener(final WandData wanddata, final NativeMinecraft nativemc) {
		this.wanddata = wanddata;
		this.nativemc = nativemc;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (args.length>=1)
			if (StringUtils.equalsIgnoreCase(args[0], "set")) {
				if (args.length>=3)
					if (sender instanceof Player) {
						final Player player = (Player) sender;
						final ItemStack itemStack = this.nativemc.getItemInHand(player.getInventory());
						if (itemStack!=null) {
							final WandItem witem = new WandItem(itemStack);
							final WandItemMeta wmeta = witem.getMeta();
							if (wmeta!=null) {
								final Features ft = getFt(args[1]);
								if (ft!=null)
									set(wmeta, ft, args[2]);
								this.wanddata.updateItem(witem);
								this.nativemc.setItemInHand(player.getInventory(), witem.getItem());
								return true;
							}
						}
					}
			} else if (StringUtils.equalsIgnoreCase(args[0], "get")) {
				if (args.length>=2)
					if (sender instanceof Player) {
						final Player player = (Player) sender;
						final ItemStack itemStack = this.nativemc.getItemInHand(player.getInventory());
						if (itemStack!=null) {
							final WandItem witem = new WandItem(itemStack);
							final WandItemMeta wmeta = witem.getMeta();
							final IWandMeta meta = this.wanddata.wrapMeta(wmeta);
							if (wmeta!=null) {
								final Features ft = getFt(args[1]);
								if (ft!=null) {
									final Object value = get(meta, ft);
									if (value!=null)
										sender.sendMessage(String.valueOf(value));
								}
								this.wanddata.updateItem(witem);
								this.nativemc.setItemInHand(player.getInventory(), witem.getItem());
								return true;
							}
						}
					}
			} else if (StringUtils.equalsIgnoreCase(args[0], "create"))
				if (sender instanceof Player) {
					final Player player = (Player) sender;
					final ItemStack itemStack = this.nativemc.getItemInHand(player.getInventory());
					if (itemStack==null||itemStack.getAmount()==0)
						return true;
					final WandItem witem = new WandItem(itemStack);
					witem.activate();
					this.wanddata.updateItem(witem);
					this.nativemc.setItemInHand(player.getInventory(), witem.getItem());
					return true;
				}

		return false;
	}

	private Features getFt(final String key) {
		Features key1 = getFeature(WandData.FEATURE_META+"."+key);
		if (key1==null)
			key1 = getFeature(key);
		if (key1==null)
			key1 = getFeature(WandData.FEATURE_META+"."+key+".data");
		if (key1==null)
			key1 = getFeature(key+".data");
		return key1;
	}

	public static class CommandMathUtils {
		public static int parseInt(final String p_71526_1_) {
			try {
				return Integer.parseInt(p_71526_1_);
			} catch (final NumberFormatException numberformatexception) {
				throw new CommandException("commands.generic.num.invalid"/*, new Object[] {p_71526_1_}*/);
			}
		}

		public static int parseIntWithMin(final String p_71528_1_, final int p_71528_2_) {
			return parseIntBounded(p_71528_1_, p_71528_2_, Integer.MAX_VALUE);
		}

		public static int parseIntBounded(final String p_71532_1_, final int p_71532_2_, final int p_71532_3_) {
			final int k = parseInt(p_71532_1_);

			if (k<p_71532_2_)
				throw new CommandException("commands.generic.num.tooSmall"/*, new Object[] {Integer.valueOf(k), Integer.valueOf(p_71532_2_)}*/);
			else if (k>p_71532_3_)
				throw new CommandException("commands.generic.num.tooBig"/*, new Object[] {Integer.valueOf(k), Integer.valueOf(p_71532_3_)}*/);
			else
				return k;
		}
	}
}
