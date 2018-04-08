package de.robingrether.idisguise.management.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;

public class ScoreboardHooks {
	
	public static boolean nametagEdit = false;
//	public static boolean coloredTags = false;
	
	public static void setup() {
		nametagEdit = Bukkit.getPluginManager().getPlugin("NametagEdit") != null;
	}
	
	public static void updatePlayer(final Player player) {
		if(nametagEdit) {
			final com.nametagedit.plugin.NametagEdit plugin = (com.nametagedit.plugin.NametagEdit)Bukkit.getPluginManager().getPlugin("NametagEdit");
			plugin.getHandler().getNametagManager().reset(player.getName());
			Bukkit.getScheduler().runTaskLaterAsynchronously(iDisguise.getInstance(), new Runnable() {
				
				public void run() {
					plugin.getHandler().applyTagToPlayer(player, false);
				}
				
			}, 5L);
		}
		// TODO
//		if(coloredTags) {
//			com.gmail.filoghost.coloredtags.ColoredTags.updateNametag(player);
//			com.gmail.filoghost.coloredtags.ColoredTags.updateTab(player);
//		}
	}
	
}