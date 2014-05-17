package de.howaner.DeinCart;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class DeinCartPlugin extends JavaPlugin {
	public static Logger log;
	private static DeinCartPlugin instance;
	private static CartManager manager;
	
	@Override
	public void onEnable() {
		instance = this;
		log = this.getLogger();
		manager = new CartManager(this);
		manager.onEnable();
		
		log.info("Plugin aktiviert!");
	}
	
	@Override
	public void onDisable() {
		manager.onDisable();
		log.info("Plugin deaktiviert!");
	}
	
	public static DeinCartPlugin getPlugin() {
		return instance;
	}
	
	public static CartManager getManager() {
		return manager;
	}

}
