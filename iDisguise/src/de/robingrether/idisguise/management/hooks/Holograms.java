package de.robingrether.idisguise.management.hooks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;

public abstract class Holograms {
	
	private static Holograms instance;
	
	public static Holograms getInstance() {
		return instance;
	}
	
	public static boolean setup() {
		if(Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			instance = new ImplHolographicDisplays();
			return true;
		} else if(Bukkit.getPluginManager().isPluginEnabled("Holograms")) {
			instance = new ImplHolograms();
			return true;
		}
		return false;
	}
	
	public abstract void createHologram(Player player);
	
	public abstract void removeHologram(Player player);
	
	public abstract void updateHologram(Player player);
	
	private static class ImplHolographicDisplays extends Holograms {
		
		private Map<Player, com.gmail.filoghost.holographicdisplays.api.Hologram> holograms = new ConcurrentHashMap<Player, com.gmail.filoghost.holographicdisplays.api.Hologram>();
		
		public void createHologram(Player player) {
			com.gmail.filoghost.holographicdisplays.api.Hologram hologram = com.gmail.filoghost.holographicdisplays.api.HologramsAPI.createHologram(iDisguise.getInstance(), player.getEyeLocation());
			hologram.appendTextLine(player.getName());
			holograms.put(player, hologram);
		}
		
		public void removeHologram(Player player) {
			holograms.remove(player).delete();
		}
		
		public void updateHologram(Player player) {
			holograms.get(player).teleport(player.getEyeLocation());
		}
		
	}
	
	private static class ImplHolograms extends Holograms {
		
		private static int id = 0;
		
		private Map<Player, com.sainttx.holograms.api.Hologram> holograms = new ConcurrentHashMap<Player, com.sainttx.holograms.api.Hologram>();
		private com.sainttx.holograms.api.HologramManager hologramManager = ((com.sainttx.holograms.HologramPlugin)Bukkit.getPluginManager().getPlugin("Holograms")).getHologramManager();
		
		public void createHologram(Player player) {
			com.sainttx.holograms.api.Hologram hologram = new com.sainttx.holograms.api.Hologram("iDisguise" + id++, player.getEyeLocation());
			hologramManager.addActiveHologram(hologram);
			hologram.addLine(new com.sainttx.holograms.api.line.TextLine(hologram, player.getName()));
			holograms.put(player, hologram);
		}
		
		public void removeHologram(Player player) {
			hologramManager.deleteHologram(holograms.remove(player));
		}
		
		public void updateHologram(Player player) {
			holograms.get(player).teleport(player.getEyeLocation());
		}
		
	}
	
}