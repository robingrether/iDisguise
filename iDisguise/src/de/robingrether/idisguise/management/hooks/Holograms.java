package de.robingrether.idisguise.management.hooks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;

public class Holograms {
	
	private static Holograms instance;
	private static boolean enabled = false;
	
	public static Holograms getInstance() {
		return instance;
	}
	
	public static boolean enable() {
		if(!enabled && Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			instance = new Holograms();
			enabled = true;
			return true;
		}
		return false;
	}
	
	public static boolean disable() {
		if(enabled) {
			enabled = false;
			return true;
		}
		return false;
	}
	
	public static boolean isEnabled() {
		return enabled;
	}
	
	private Map<Player, com.gmail.filoghost.holographicdisplays.api.Hologram> holograms = new ConcurrentHashMap<Player, com.gmail.filoghost.holographicdisplays.api.Hologram>();
	
	private void createHologram(Player player) {
		com.gmail.filoghost.holographicdisplays.api.Hologram hologram = com.gmail.filoghost.holographicdisplays.api.HologramsAPI.createHologram(iDisguise.getInstance(), player.getEyeLocation());
		hologram.getVisibilityManager().setVisibleByDefault(false);
		hologram.appendTextLine(player.getName());
		holograms.put(player, hologram);
	}
	
	private void removeHologram(Player player) {
		holograms.remove(player).delete();
	}
	
	public boolean hasHologram(Player player) {
		return holograms.containsKey(player);
	}
	
	public void showHologram(Player player, Player observer) {
		if(!hasHologram(player)) {
			createHologram(player);
		}
		holograms.get(player).getVisibilityManager().showTo(observer);
	}
	
	public void hideHologram(Player player, Player observer) {
		holograms.get(player).getVisibilityManager().hideTo(observer);
		//TODO
	}
	
	public void updateHologram(Player player) {
		holograms.get(player).teleport(player.getEyeLocation());
	}
	
}