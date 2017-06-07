package de.robingrether.idisguise.management.disguise;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.DisguiseManager;

public class DisguiseManager18 extends DisguiseManager {
	
	protected void showPlayer(final Player player) {
		Bukkit.getScheduler().runTaskLater(iDisguise.getInstance(), new Runnable() {
			
			public void run() {
				DisguiseManager18.super.showPlayer(player);
			}
			
		}, 10L);
	}
	
}