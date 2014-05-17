package de.howaner.DeinCart.util;

import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RoutePlayer {
	private final Player player;
	private final Minecart minecart;
	private final Route route;
	private ItemStack[] inventory;
	
	public RoutePlayer(Player player, Minecart minecart, Route route) {
		this.player = player;
		this.minecart = minecart;
		this.route = route;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public Minecart getMinecart() {
		return this.minecart;
	}
	
	public Route getRoute() {
		return this.route;
	}
	
	public ItemStack[] getInventory() {
		return this.inventory;
	}
	
	public void setInventory(ItemStack[] inventory) {
		this.inventory = inventory;
	}

}
