package de.howaner.DeinCart.listener;

import de.howaner.DeinCart.CartManager;
import de.howaner.DeinCart.util.Route;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

public class DeinCartListener implements Listener {
	private final CartManager manager;
	public static List<Player> detectors = new ArrayList<Player>();
	
	public DeinCartListener(CartManager manager) {
		this.manager = manager;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if ((event.getAction() != Action.RIGHT_CLICK_BLOCK) || event.isCancelled()) return;
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (!block.getType().name().contains("RAIL")) return;
		
		if ((event.getItem() == null) || (event.getItem().getType() == Material.AIR)) {
			Route route = this.manager.getRouteFromRail(block);
			if (route == null) return;
			event.setCancelled(true);
			
			if (detectors.contains(player)) {
				int id = 0;
				for (Route r : this.manager.getRoutes()) {
					if (r == route) break;
					id++;
				}
				player.sendMessage(ChatColor.GOLD + "Dies ist die Route #" + id + "!");
				detectors.remove(player);
				return;
			}
			
			if (!route.isComplete()) {
				player.sendMessage(ChatColor.GRAY + "Mit dieser Bahn kannst du zurzeit nicht fahren!");
				return;
			}
			
			this.manager.giveMinecart(player);
		}
		
		else if (this.manager.isDeincartItem(event.getItem())) {
			event.setCancelled(true);
			Route route = this.manager.getRouteFromRail(block);
			if ((route == null) || this.manager.isRoutePlayer(player)) {
				player.updateInventory();
				return;
			}
			
			if (!route.isComplete()) {
				player.sendMessage(ChatColor.GRAY + "Mit dieser Bahn kannst du zurzeit nicht fahren!");
				player.updateInventory();
				return;
			}
			
			if (this.manager.isDeincartInNear(block.getLocation())) {
				player.sendMessage(ChatColor.GRAY + "Es steht bereits ein Minecart auf der Bahn!");
				player.updateInventory();
				return;
			}
			
			this.manager.startRoute(player, route, block.getLocation());
		}
	}
	
	@EventHandler
	public void onPlayerCancelRoute(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (this.manager.isRouteCancelItem(item) && this.manager.isRoutePlayer(player)) {
			this.manager.stopRoute(player, false);
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(event.getPreviousSlot());
		if (this.manager.isDeincartItem(item)) {
			player.getInventory().setItem(event.getPreviousSlot(), null);
			player.updateInventory();
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (this.manager.isDeincartItem(event.getItemDrop().getItemStack())) {
			event.getItemDrop().remove();
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.isCancelled() || (event.getCurrentItem() == null) || !(event.getWhoClicked() instanceof Player)) return;
		if (this.manager.isDeincartItem(event.getCurrentItem())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) {
		if (event.getExited() != null && event.getExited() instanceof Player) {
			Player player = (Player) event.getExited();
			if (this.manager.isRoutePlayer(player)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (this.manager.isRoutePlayer(player)) {
			this.manager.stopRoute(player, false);
		}
		detectors.remove(player);
	}

}
