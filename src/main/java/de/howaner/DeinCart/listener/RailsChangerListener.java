package de.howaner.DeinCart.listener;

import de.howaner.DeinCart.DeinCartPlugin;
import de.howaner.DeinCart.util.Route;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RailsChangerListener implements Listener {
	public static final Map<Player, Route> players = new HashMap<Player, Route>();
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		Route route = players.get(player);
		if (route == null) return;
		if (!block.getType().name().contains("RAIL")) return;
		
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (route.isStartRail(block)) {
				event.setCancelled(true);
				route.removeStartRail(block);
				DeinCartPlugin.getManager().saveRoutes();
				player.sendMessage(ChatColor.GREEN + "Startrail gelöscht!");
			}
		} else {
			if (!route.isStartRail(block)) {
				event.setCancelled(true);
				
				if (DeinCartPlugin.getManager().getRouteFromRail(block) != null) {
					player.sendMessage(ChatColor.RED + "Diese Schiene ist bereits in einer anderen Route definiert!");
					return;
				}
				
				route.addStartRail(block);
				DeinCartPlugin.getManager().saveRoutes();
				player.sendMessage(ChatColor.GOLD + "Startrail hinzugefügt!");
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		players.remove(event.getPlayer());
	}

}
