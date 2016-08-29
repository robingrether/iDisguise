package de.robingrether.idisguise.management.hooks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;

public class Holograms {
	
	private static Holograms instance;
	
	public static Holograms getInstance() {
		return instance;
	}
	
	public static boolean setup() {
		if(Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			instance = new Holograms();
			return true;
		}
		return false;
	}
	
	private Map<Player, com.gmail.filoghost.holographicdisplays.api.Hologram> holograms = new ConcurrentHashMap<Player, com.gmail.filoghost.holographicdisplays.api.Hologram>();
	
	public void createHologram(Player player) {
		com.gmail.filoghost.holographicdisplays.api.Hologram hologram = com.gmail.filoghost.holographicdisplays.api.HologramsAPI.createHologram(iDisguise.getInstance(), player.getEyeLocation());
		hologram.appendTextLine(player.getName());
		holograms.put(player, hologram);
	}
	
	public void removeHologram(Player player) {
		holograms.remove(player).delete();
	}
	
	public void showHologram(Player player, Player observer) {
		//TODO
	}
	
	public void updateHologram(Player player) {
		holograms.get(player).teleport(player.getEyeLocation());
	}
	
}