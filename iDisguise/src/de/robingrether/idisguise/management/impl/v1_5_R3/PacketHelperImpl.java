package de.robingrether.idisguise.management.impl.v1_5_R3;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.disguise.AgeableDisguise;
import de.robingrether.idisguise.disguise.CreeperDisguise;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.EndermanDisguise;
import de.robingrether.idisguise.disguise.FallingBlockDisguise;
import de.robingrether.idisguise.disguise.ItemDisguise;
import de.robingrether.idisguise.disguise.MinecartDisguise;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.ObjectDisguise;
import de.robingrether.idisguise.disguise.OcelotDisguise;
import de.robingrether.idisguise.disguise.PigDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.disguise.SheepDisguise;
import de.robingrether.idisguise.disguise.SizedDisguise;
import de.robingrether.idisguise.disguise.SkeletonDisguise;
import de.robingrether.idisguise.disguise.VillagerDisguise;
import de.robingrether.idisguise.disguise.WolfDisguise;
import de.robingrether.idisguise.disguise.ZombieDisguise;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.PacketHelper;
import de.robingrether.idisguise.management.VersionHelper;
import net.minecraft.server.v1_5_R3.Block;
import net.minecraft.server.v1_5_R3.Entity;
import net.minecraft.server.v1_5_R3.EntityAgeable;
import net.minecraft.server.v1_5_R3.EntityBat;
import net.minecraft.server.v1_5_R3.EntityCreeper;
import net.minecraft.server.v1_5_R3.EntityEnderman;
import net.minecraft.server.v1_5_R3.EntityFallingBlock;
import net.minecraft.server.v1_5_R3.EntityItem;
import net.minecraft.server.v1_5_R3.EntityLiving;
import net.minecraft.server.v1_5_R3.EntityMinecartRideable;
import net.minecraft.server.v1_5_R3.EntityOcelot;
import net.minecraft.server.v1_5_R3.EntityPig;
import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.EntitySheep;
import net.minecraft.server.v1_5_R3.EntitySkeleton;
import net.minecraft.server.v1_5_R3.EntitySlime;
import net.minecraft.server.v1_5_R3.EntityVillager;
import net.minecraft.server.v1_5_R3.EntityWolf;
import net.minecraft.server.v1_5_R3.EntityZombie;
import net.minecraft.server.v1_5_R3.Item;
import net.minecraft.server.v1_5_R3.ItemStack;
import net.minecraft.server.v1_5_R3.Packet;
import net.minecraft.server.v1_5_R3.Packet20NamedEntitySpawn;
import net.minecraft.server.v1_5_R3.Packet23VehicleSpawn;
import net.minecraft.server.v1_5_R3.Packet24MobSpawn;
import net.minecraft.server.v1_5_R3.Packet40EntityMetadata;
import net.minecraft.server.v1_5_R3.World;

public class PacketHelperImpl extends PacketHelper {
	
	public Packet[] getPackets(Player player) {
		Disguise disguise = DisguiseManager.instance.getDisguise(player);
		if(disguise == null) {
			return null;
		}
		EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
		DisguiseType type = disguise.getType();
		List<Packet> packets = new ArrayList<Packet>();
		if(disguise instanceof MobDisguise) {
			MobDisguise mobDisguise = (MobDisguise)disguise;
			EntityLiving entity;
			try {
				entity = (EntityLiving)type.getClass(VersionHelper.getNMSPackage()).getConstructor(World.class).newInstance(entityPlayer.world);
			} catch(Exception e) {
				entity = null;
			}
			if(mobDisguise.getCustomName() != null && !mobDisguise.getCustomName().isEmpty()) {
				entity.setCustomName(mobDisguise.getCustomName());
				entity.setCustomNameVisible(true);
			}
			if(entity instanceof EntityAgeable) {
				if(mobDisguise instanceof AgeableDisguise && !((AgeableDisguise)mobDisguise).isAdult()) {
					((EntityAgeable)entity).setAge(-24000);
				}
			}
			Location location = player.getLocation();
			entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
			entity.id = entityPlayer.id;
			if(mobDisguise instanceof SheepDisguise) {
				if(entity instanceof EntitySheep) {
					((EntitySheep)entity).setColor(((SheepDisguise)mobDisguise).getColor().getData());
				}
			} else if(mobDisguise instanceof WolfDisguise) {
				if(entity instanceof EntityWolf) {
					WolfDisguise wolfDisguise = (WolfDisguise)mobDisguise;
					EntityWolf wolf = (EntityWolf)entity;
					wolf.setCollarColor(wolfDisguise.getCollarColor().getData());
					wolf.setTamed(wolfDisguise.isTamed());
					wolf.setAngry(wolfDisguise.isAngry());
				}
			} else if(mobDisguise instanceof CreeperDisguise) {
				if(entity instanceof EntityCreeper) {
					((EntityCreeper)entity).setPowered(((CreeperDisguise)mobDisguise).isPowered());
				}
			} else if(mobDisguise instanceof EndermanDisguise) {
				if(entity instanceof EntityEnderman) {
					EndermanDisguise endermanDisguise = (EndermanDisguise)mobDisguise;
					EntityEnderman enderman = (EntityEnderman)entity;
					enderman.setCarriedId(endermanDisguise.getBlockInHand().getId());
					enderman.setCarriedData(endermanDisguise.getBlockInHandData());
				}
			} else if(mobDisguise instanceof OcelotDisguise) {
				if(entity instanceof EntityOcelot) {
					((EntityOcelot)entity).setCatType(((OcelotDisguise)mobDisguise).getCatType().getId());
				}
			} else if(mobDisguise instanceof PigDisguise) {
				if(entity instanceof EntityPig) {
					((EntityPig)entity).setSaddle(((PigDisguise)mobDisguise).isSaddled());
				}
			} else if(mobDisguise instanceof SizedDisguise) {
				if(entity instanceof EntitySlime) {
					((EntitySlime)entity).setSize(((SizedDisguise)mobDisguise).getSize());
				}
			} else if(mobDisguise instanceof SkeletonDisguise) {
				if(entity instanceof EntitySkeleton) {
					((EntitySkeleton)entity).setSkeletonType(((SkeletonDisguise)mobDisguise).getSkeletonType().getId());
				}
			} else if(mobDisguise instanceof VillagerDisguise) {
				if(entity instanceof EntityVillager) {
					((EntityVillager)entity).setProfession(((VillagerDisguise)mobDisguise).getProfession().getId());
				}
			} else if(mobDisguise instanceof ZombieDisguise) {
				if(entity instanceof EntityZombie) {
					ZombieDisguise zombieDisguise = (ZombieDisguise)mobDisguise;
					EntityZombie zombie = (EntityZombie)entity;
					zombie.setBaby(!zombieDisguise.isAdult());
					zombie.setVillager(zombieDisguise.isVillager());
				}
			}
			if(entity instanceof EntityBat) {
				((EntityBat)entity).a(false);
			}
			if(attributes[0]) {
				entity.setCustomName(player.getName());
				entity.setCustomNameVisible(true);
			}
			packets.add(new Packet24MobSpawn(entity));
		} else if(disguise instanceof PlayerDisguise) {
			packets.add(new Packet20NamedEntitySpawn(((CraftPlayer)player).getHandle()));
			((Packet20NamedEntitySpawn)packets.get(0)).b = ((PlayerDisguise)disguise).getName();
		} else if(disguise instanceof ObjectDisguise) {
			ObjectDisguise objectDisguise = (ObjectDisguise)disguise;
			Entity entity;
			try {
				entity = (Entity)type.getClass(VersionHelper.getNMSPackage()).getConstructor(World.class).newInstance(entityPlayer.world);
			} catch(Exception e) {
				entity = null;
			}
			Location location = player.getLocation();
			entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
			entity.id = entityPlayer.id;
			if(entity instanceof EntityFallingBlock) {
				packets.add(new Packet23VehicleSpawn(entity, objectDisguise.getTypeId(), objectDisguise instanceof FallingBlockDisguise ? ((FallingBlockDisguise)objectDisguise).getMaterial().getId() : 1));
			} else if(entity instanceof EntityItem) {
				if(objectDisguise instanceof ItemDisguise) {
					ItemDisguise itemDisguise = (ItemDisguise)objectDisguise;
					if(itemDisguise.getItemStack().getType().isBlock()) {
						((EntityItem)entity).setItemStack(new ItemStack(Block.byId[itemDisguise.getItemStack().getTypeId()], itemDisguise.getItemStack().getAmount(), itemDisguise.getItemStack().getDurability()));
					} else {
						((EntityItem)entity).setItemStack(new ItemStack(Item.byId[itemDisguise.getItemStack().getTypeId()], itemDisguise.getItemStack().getAmount(), itemDisguise.getItemStack().getDurability()));
					}
				}
				packets.add(new Packet23VehicleSpawn(entity, objectDisguise.getTypeId()));
				packets.add(new Packet40EntityMetadata(entity.id, entity.getDataWatcher(), true));
			} else if(entity instanceof EntityMinecartRideable) {
				if(objectDisguise instanceof MinecartDisguise) {
					MinecartDisguise minecartDisguise = (MinecartDisguise)objectDisguise;
					((EntityMinecartRideable)entity).k(minecartDisguise.getDisplayedBlock().getId());
					((EntityMinecartRideable)entity).l(minecartDisguise.getDisplayedBlockData());
				}
				packets.add(new Packet23VehicleSpawn(entity, objectDisguise.getTypeId()));
				packets.add(new Packet40EntityMetadata(entity.id, entity.getDataWatcher(), true));
			} else {
				packets.add(new Packet23VehicleSpawn(entity, objectDisguise.getTypeId()));
			}
		}
		return packets.toArray(new Packet[0]);
	}
	
	public String getPlayerInfo(OfflinePlayer offlinePlayer, Object context, int ping, Object gamemode) {
		Disguise disguise = DisguiseManager.instance.getDisguise(offlinePlayer);
		if(disguise == null) {
			return offlinePlayer.isOnline() ? offlinePlayer.getPlayer().getPlayerListName() : offlinePlayer.getName();
		} else if(disguise instanceof PlayerDisguise) {
			return attributes[1] ? ((PlayerDisguise)disguise).getName() : offlinePlayer.isOnline() ? offlinePlayer.getPlayer().getPlayerListName() : offlinePlayer.getName();
		} else if(!attributes[1]) {
			return offlinePlayer.isOnline() ? offlinePlayer.getPlayer().getPlayerListName() : offlinePlayer.getName();
		}
		return null;
	}
	
}