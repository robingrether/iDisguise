package de.robingrether.idisguise.management.hooks;

import org.bukkit.entity.Player;

public abstract class Holograms {
	
	private static Holograms instance;
	
	public static Holograms getInstance() {
		return instance;
	}
	
	public static boolean setup() {
		//TODO: setup hook
		return false;
	}
	
	public abstract void createHologram(Player player);
	
	public abstract void removeHologram(Player player);
	
	public abstract void updateHologram(Player player);
	
	private class ImplHolographicDisplays extends Holograms {
		
		public void createHologram(Player player) {
			//TODO
		}
		
		public void removeHologram(Player player) {
			//TODO
		}
		
		public void updateHologram(Player player) {
			//TODO
		}
		
	}
	
	private class ImplHolograms extends Holograms {
		
		public void createHologram(Player player) {
			//TODO
		}
		
		public void removeHologram(Player player) {
			//TODO
		}
		
		public void updateHologram(Player player) {
			//TODO
		}
		
	}
	
}