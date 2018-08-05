package de.robingrether.idisguise.management.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.VersionHelper;

import static de.robingrether.idisguise.management.Reflection.*;

public abstract class VanillaTargetParser {
	
	private static VanillaTargetParser instance;
	
	public static VanillaTargetParser getInstance() {
		return instance;
	}
	
	public static void setInstance(VanillaTargetParser instance) {
		VanillaTargetParser.instance = instance;
	}
	
	public Collection<LivingEntity> parseVanillaTargets(String argument, CommandSender sender) {
		try {
			Collection<? extends Object> nmsTargets = parseTargets(argument, sender);
			Collection<LivingEntity> bukkitTargets = new HashSet<LivingEntity>();
			for(Object target : nmsTargets) {
				Object bukkitEntity = Entity_getBukkitEntity.invoke(target);
				if(bukkitEntity instanceof LivingEntity) bukkitTargets.add((LivingEntity)bukkitEntity);
			}
			return bukkitTargets;
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.WARNING, "Cannot parse vanilla target.", e);
			}
		}
		return Collections.emptySet();
	}
	
	protected abstract Collection<? extends Object> parseTargets(String argument, CommandSender sender) throws Exception;
	
}