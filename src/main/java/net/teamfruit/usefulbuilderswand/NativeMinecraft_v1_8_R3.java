package net.teamfruit.usefulbuilderswand;

import static net.teamfruit.usefulbuilderswand.ReflectionUtil.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class NativeMinecraft_v1_8_R3 implements NativeMinecraft {
	private String internalversion;

	private final Class<?> c$CraftItemStack;
	private final Class<?> c$CraftBlock;
	private final Class<?> c$ItemStack;
	private final Class<?> c$Item;
	private final Class<?> c$Block;
	private final Class<?> c$Material;
	private final Class<?> c$CraftChunk;
	private final Class<?> c$Chunk;
	private final Class<?> c$BlockPosition;
	private final Class<?> c$World;
	private final Class<?> c$StepSound;
	private final Class<?> c$CraftPlayer;
	private final Class<?> c$EntityHuman;
	private final Class<?> c$IBlockData;
	private final Class<?> c$EnumDirection;
	private final Class<?> c$Vec3D;
	private final Class<?> c$MovingObjectPosition;
	private final Class<?> c$CraftWorld;
	private final Class<?> c$PacketPlayOutWorldParticles;
	private final Class<?> c$EnumParticle;
	private final Class<?> c$EntityPlayer;
	private final Class<?> c$PlayerConnection;
	private final Class<?> c$Packet;

	private final Method m$CraftItemStack$asNMSCopy;
	private final Method m$ItemStack$getItem;
	private final Method m$Item$k;
	private final Method m$CraftBlock$getNMSBlock;
	private final Method m$Material$isReplaceable;
	private final Method m$CraftChunk$getHandle;
	private final Method m$Chunk$getWorld;
	private final Method m$StepSound$getPlaceSound;
	private final Method m$CraftPlayer$getHandle;
	private final Method m$ItemStack$placeItem;
	private final Method m$Block$getDropData;
	private final Method m$Item$getItemOf;
	private final Method m$CraftItemStack$asNewCraftStack;
	private final Method m$EnumDirection$valueOf;
	private final Method m$Vec3D$e;
	private final Method m$World$rayTrace;
	private final Method m$World$getType;
	private final Method m$MovingObjectPosition$a;
	private final Method m$BlockPosition$getX;
	private final Method m$BlockPosition$getY;
	private final Method m$BlockPosition$getZ;
	private final Method m$PlayerConnection$sendPacket;
	private final Method m$PlayerInventory$getItemInHand;
	private final Method m$PlayerInventory$setItemInHand;

	private final Field f$Block$material;
	private final Field f$CraftBlock$chunk;
	private final Field f$Block$stepSound;

	private final Field f$CraftWorld$world;
	private final Field f$MovingObjectPosition$direction;
	private final Field f$EntityPlayer$playerConnection;
	private final Field f$EnumParticle$REDSTONE;

	private final Constructor<?> n$BlockPosition;
	private final Constructor<?> n$Vec3D;
	private final Constructor<?> n$PacketPlayOutWorldParticles;

	private final Map<Material, Boolean> cacheitem = new HashMap<Material, Boolean>();
	private final Map<Material, Boolean> cacheblock = new HashMap<Material, Boolean>();
	private final Map<Material, String> cacheblocksound = new HashMap<Material, String>();

	public NativeMinecraft_v1_8_R3(final String internalversion) throws Exception {
		this.internalversion = internalversion;

		this.c$CraftItemStack = $class("org.bukkit.craftbukkit.%version%.inventory.CraftItemStack");
		this.c$CraftBlock = $class("org.bukkit.craftbukkit.%version%.block.CraftBlock");
		this.c$ItemStack = $class("net.minecraft.server.%version%.ItemStack");
		this.c$Item = $class("net.minecraft.server.%version%.Item");
		this.c$Block = $class("net.minecraft.server.%version%.Block");
		this.c$Material = $class("net.minecraft.server.%version%.Material");
		this.c$CraftChunk = $class("org.bukkit.craftbukkit.%version%.CraftChunk");
		this.c$Chunk = $class("net.minecraft.server.%version%.Chunk");
		this.c$BlockPosition = $class("net.minecraft.server.%version%.BlockPosition");
		this.c$World = $class("net.minecraft.server.%version%.World");
		this.c$StepSound = $class("net.minecraft.server.%version%.Block$StepSound");
		this.c$CraftPlayer = $class("org.bukkit.craftbukkit.%version%.entity.CraftPlayer");
		this.c$EntityHuman = $class("net.minecraft.server.%version%.EntityHuman");
		this.c$IBlockData = $class("net.minecraft.server.%version%.IBlockData");
		this.c$EnumDirection = $class("net.minecraft.server.%version%.EnumDirection");
		this.c$Vec3D = $class("net.minecraft.server.%version%.Vec3D");
		this.c$MovingObjectPosition = $class("net.minecraft.server.%version%.MovingObjectPosition");
		this.c$CraftWorld = $class("org.bukkit.craftbukkit.%version%.CraftWorld");
		this.c$PacketPlayOutWorldParticles = $class("net.minecraft.server.%version%.PacketPlayOutWorldParticles");
		this.c$EnumParticle = $class("net.minecraft.server.%version%.EnumParticle");
		this.c$EntityPlayer = $class("net.minecraft.server.%version%.EntityPlayer");
		this.c$PlayerConnection = $class("net.minecraft.server.%version%.PlayerConnection");
		this.c$Packet = $class("net.minecraft.server.%version%.Packet");

		this.m$CraftItemStack$asNMSCopy = $method(this.c$CraftItemStack, "asNMSCopy", ItemStack.class);
		this.m$ItemStack$getItem = $method(this.c$ItemStack, "getItem");
		this.m$Item$k = $method(this.c$Item, "k");
		this.m$CraftBlock$getNMSBlock = $pmethod(this.c$CraftBlock, "getNMSBlock");
		this.m$Material$isReplaceable = $method(this.c$Material, "isReplaceable");
		this.m$CraftChunk$getHandle = $method(this.c$CraftChunk, "getHandle");
		this.m$Chunk$getWorld = $method(this.c$Chunk, "getWorld");
		this.m$StepSound$getPlaceSound = $method(this.c$StepSound, "getPlaceSound");
		this.m$CraftPlayer$getHandle = $method(this.c$CraftPlayer, "getHandle");
		this.m$ItemStack$placeItem = $method(this.c$ItemStack, "placeItem", this.c$EntityHuman, this.c$World, this.c$BlockPosition, /*this.c$EnumHand, */this.c$EnumDirection, float.class, float.class, float.class);
		this.m$Block$getDropData = $method(this.c$Block, "getDropData", this.c$IBlockData);
		this.m$Item$getItemOf = $method(this.c$Item, "getItemOf", this.c$Block);
		this.m$CraftItemStack$asNewCraftStack = $method(this.c$CraftItemStack, "asNewCraftStack", this.c$Item);
		this.m$EnumDirection$valueOf = $method(this.c$EnumDirection, "valueOf", String.class);
		this.m$Vec3D$e = $method(this.c$Vec3D, "e", this.c$Vec3D);
		this.m$World$rayTrace = $method(this.c$World, "rayTrace", this.c$Vec3D, this.c$Vec3D, boolean.class, boolean.class, boolean.class);
		this.m$World$getType = $method(this.c$World, "getType", this.c$BlockPosition);
		this.m$MovingObjectPosition$a = $method(this.c$MovingObjectPosition, "a");
		this.m$BlockPosition$getX = $method(this.c$BlockPosition, "getX");
		this.m$BlockPosition$getY = $method(this.c$BlockPosition, "getY");
		this.m$BlockPosition$getZ = $method(this.c$BlockPosition, "getZ");
		this.m$PlayerConnection$sendPacket = $method(this.c$PlayerConnection, "sendPacket", this.c$Packet);
		this.m$PlayerInventory$getItemInHand = $method(PlayerInventory.class, "getItemInHand");
		this.m$PlayerInventory$setItemInHand = $method(PlayerInventory.class, "setItemInHand", ItemStack.class);

		this.f$Block$material = $pfield(this.c$Block, "material");
		this.f$CraftBlock$chunk = $pfield(this.c$CraftBlock, "chunk");
		this.f$Block$stepSound = $field(this.c$Block, "stepSound");
		this.f$CraftWorld$world = $pfield(this.c$CraftWorld, "world");
		this.f$MovingObjectPosition$direction = $field(this.c$MovingObjectPosition, "direction");
		this.f$EntityPlayer$playerConnection = $field(this.c$EntityPlayer, "playerConnection");
		this.f$EnumParticle$REDSTONE = $field(this.c$EnumParticle, "REDSTONE");

		this.n$BlockPosition = $new(this.c$BlockPosition, int.class, int.class, int.class);
		this.n$Vec3D = $new(this.c$Vec3D, double.class, double.class, double.class);
		this.n$PacketPlayOutWorldParticles = $new(this.c$PacketPlayOutWorldParticles, this.c$EnumParticle, boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);
	}

	Class<?> $class(final String _class) throws Exception {
		return Class.forName(_class.replace("%version%", this.internalversion));
	}

	@Nullable
	Class<?> $$class(final String _class) {
		try {
			return $class(_class);
		} catch (final Exception e) {
		}
		return null;
	}

	public boolean hasSubType(final ItemStack itemStack) {
		if (itemStack!=null) {
			final Material type = itemStack.getType();
			final Boolean cached = this.cacheitem.get(type);
			if (cached!=null)
				return cached;
			else
				try {
					final Object nItemStack = this.m$CraftItemStack$asNMSCopy.invoke(null, itemStack);
					final Object nItem = this.m$ItemStack$getItem.invoke(nItemStack);
					final boolean hasSubType = (Boolean) this.m$Item$k.invoke(nItem);
					this.cacheitem.put(type, hasSubType);
					return hasSubType;
				} catch (final Exception e) {
				}
		}
		return true;
	}

	public boolean canReplace(final Block block) {
		if (block!=null) {
			final Material type = block.getType();
			final Boolean cached = this.cacheblock.get(type);
			if (cached!=null)
				return cached;
			else
				try {
					final Object nBlock = this.m$CraftBlock$getNMSBlock.invoke(block);
					final Object nMaterial = this.f$Block$material.get(nBlock);
					final boolean canReplace = (Boolean) this.m$Material$isReplaceable.invoke(nMaterial);
					this.cacheblock.put(type, canReplace);
					return canReplace;
				} catch (final Exception e) {
				}
		}
		return false;
	}

	public boolean canPlace(final Block block) {
		return block.isEmpty()||canReplace(block);
	}

	public void playSound(final Player player, final Location location, final Block block, final float volume, final float pitch) {
		if (block!=null) {
			final Material type = block.getType();
			final String cached = this.cacheblocksound.get(type);
			if (cached!=null)
				player.playSound(location, cached, volume, pitch);
			else
				try {
					final Object nBlock = this.m$CraftBlock$getNMSBlock.invoke(block);
					final Object stepSound = this.f$Block$stepSound.get(nBlock);
					final String keysound = (String) this.m$StepSound$getPlaceSound.invoke(stepSound);
					this.cacheblocksound.put(type, keysound);
					player.playSound(location, keysound, volume, pitch);
				} catch (final Exception e) {
					try {
						player.playSound(location, Sound.valueOf("DIG_STONE"), volume, pitch);
					} catch (final IllegalArgumentException ex) {
					}
				}
		}
	}

	// public EnumInteractionResult placeItem(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2)
	public boolean placeItem(final Player player, final Block block, final ItemStackHolder handItemStack, final ItemStack placeItemStack, final EquipmentSlot hand, final BlockFace face, final Location eyeLocation) {
		if (block!=null)
			if (this.c$CraftBlock!=null)
				try {
					final Object nItemStack = this.m$CraftItemStack$asNMSCopy.invoke(null, placeItemStack);
					final Object nPlayer = this.m$CraftPlayer$getHandle.invoke(player);

					final Object nChunk = this.f$CraftBlock$chunk.get(block);
					final Object nChunkHandle = this.m$CraftChunk$getHandle.invoke(nChunk);
					final Object nWorld = this.m$Chunk$getWorld.invoke(nChunkHandle);

					final Location location = block.getLocation();
					final Object nBlockPosition = this.n$BlockPosition.newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());

					// final Object nHand = hand==EquipmentSlot.OFF_HAND ? this.f$EnumHand$OFF_HAND.get(null) : this.f$EnumHand$MAIN_HAND.get(null);
					final Object nDirection = this.m$EnumDirection$valueOf.invoke(null, face.name());

					final Object nResult = this.m$ItemStack$placeItem.invoke(nItemStack, nPlayer, nWorld, nBlockPosition, /*nHand, */nDirection, (float) eyeLocation.getX(), (float) eyeLocation.getY(), (float) eyeLocation.getZ());

					return (Boolean) nResult;
				} catch (final Exception e) {
					e.printStackTrace();
				}
		return false;
	}

	public ItemStack getItemFromBlock(final Block block) {
		try {
			final Object nBlock = this.m$CraftBlock$getNMSBlock.invoke(block);
			final Object nItem = this.m$Item$getItemOf.invoke(null, nBlock);

			return (ItemStack) this.m$CraftItemStack$asNewCraftStack.invoke(null, nItem);
		} catch (final Exception e) {
		}
		return block.getState().getData().toItemStack();
	}

	public int getDropData(final Block block) {
		try {
			final Object nBlock = this.m$CraftBlock$getNMSBlock.invoke(block);

			final Object nChunk = this.f$CraftBlock$chunk.get(block);
			final Object nChunkHandle = this.m$CraftChunk$getHandle.invoke(nChunk);
			final Object nWorld = this.m$Chunk$getWorld.invoke(nChunkHandle);

			final Location location = block.getLocation();
			final Object nBlockPosition = this.n$BlockPosition.newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());

			final Object nIBlockState = this.m$World$getType.invoke(nWorld, nBlockPosition);

			return (Integer) this.m$Block$getDropData.invoke(nBlock, nIBlockState);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private Object getLook(final Location loc, final double scale) throws Exception {
		final float rotationYaw = loc.getYaw();
		final float rotationPitch = loc.getPitch();

		final double f1 = Math.cos(-rotationYaw*0.017453292F-(float) Math.PI);
		final double f2 = Math.sin(-rotationYaw*0.017453292F-(float) Math.PI);
		final double f3 = -Math.cos(-rotationPitch*0.017453292F);
		final double f4 = Math.sin(-rotationPitch*0.017453292F);

		return this.n$Vec3D.newInstance(f2*f3*scale, f4*scale, f1*f3*scale);
	}

	public RayTraceResult rayTrace(final Player player) {
		try {
			final Object nWorld = this.f$CraftWorld$world.get(player.getWorld());
			final Location pLoc = player.getEyeLocation();
			final Object nEyelocvec = this.n$Vec3D.newInstance(pLoc.getX(), pLoc.getY(), pLoc.getZ());
			final Object nSeelocvec0 = getLook(pLoc, player.getGameMode()==GameMode.CREATIVE ? 5 : 4.5);
			final Object nSeelocvec = this.m$Vec3D$e.invoke(nSeelocvec0, nEyelocvec);
			final Object nPos = this.m$World$rayTrace.invoke(nWorld, nEyelocvec, nSeelocvec, false, false, true);
			if (nPos!=null) {
				final Object nPos1 = this.m$MovingObjectPosition$a.invoke(nPos);
				final int nPosX = (Integer) this.m$BlockPosition$getX.invoke(nPos1);
				final int nPosY = (Integer) this.m$BlockPosition$getY.invoke(nPos1);
				final int nPosZ = (Integer) this.m$BlockPosition$getZ.invoke(nPos1);
				final Location loc = new Location(player.getWorld(), nPosX, nPosY, nPosZ);
				final Enum<?> nDirection = (Enum<?>) this.f$MovingObjectPosition$direction.get(nPos);
				final BlockFace direction = BlockFace.valueOf(nDirection.name());
				return new RayTraceResult(loc, direction);
			}
		} catch (final Exception e) {
		}
		return null;
	}

	public void spawnParticles(final Player player, final Location loc, final float r, final float g, final float b) {
		try {
			final Object nPacket = this.n$PacketPlayOutWorldParticles.newInstance(this.f$EnumParticle$REDSTONE.get(null), true, loc.getBlockX()+.5f, loc.getBlockY()+.5f, loc.getBlockZ()+.5f, r+Float.MIN_VALUE, g, b, 1f, 0, new int[] {});
			final Object nPlayer = this.m$CraftPlayer$getHandle.invoke(player);
			final Object nConnection = this.f$EntityPlayer$playerConnection.get(nPlayer);
			this.m$PlayerConnection$sendPacket.invoke(nConnection, nPacket);
		} catch (final Exception e) {
		}
	}

	public boolean isMainHand(final PlayerInteractEvent event) {
		return true;
	}

	public ItemStack getItemInHand(final PlayerInventory inventory) {
		try {
			return (ItemStack) this.m$PlayerInventory$getItemInHand.invoke(inventory);
		} catch (final Exception e) {
		}
		return null;
	}

	public void setItemInHand(final PlayerInventory inventory, final ItemStack itemStack) {
		try {
			this.m$PlayerInventory$setItemInHand.invoke(inventory, itemStack);
		} catch (final Exception e) {
		}
	}
}
