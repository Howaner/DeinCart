package de.howaner.DeinCart;

import de.howaner.DeinCart.command.RouteCommand;
import de.howaner.DeinCart.listener.DeinCartListener;
import de.howaner.DeinCart.listener.RailsChangerListener;
import de.howaner.DeinCart.util.FakeMinecart;
import de.howaner.DeinCart.util.Route;
import de.howaner.DeinCart.util.RoutePlayer;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.server.v1_7_R3.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class CartManager {
	private final DeinCartPlugin plugin;
	private List<Route> routes = new ArrayList<Route>();
	private final Map<Player, RoutePlayer> players = new HashMap<Player, RoutePlayer>();
	public final File routesFile = new File("plugins/DeinCart/routes.yml");
	
	public CartManager(DeinCartPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void onEnable() {
		this.registerCustomEntity();
		
		if (!routesFile.exists())
			this.saveRoutes();
		this.loadRoutes();
		
		Bukkit.getPluginManager().registerEvents(new DeinCartListener(this), plugin);
		Bukkit.getPluginManager().registerEvents(new RailsChangerListener(), plugin);
		
		plugin.getCommand("route").setExecutor(new RouteCommand(this));
	}
	
	public void onDisable() {
		for (RoutePlayer player : this.getRoutePlayers())
			this.stopRoute(player.getPlayer(), false);
		
		DeinCartListener.detectors.clear();
		RailsChangerListener.players.clear();
		
		this.saveRoutes();
	}
	
	public List<Route> getRoutes() {
		return this.routes;
	}
	
	public void addRoute(Route route) {
		this.routes.add(route);
		this.saveRoutes();
	}
	
	public void removeRoute(Route route) {
		this.routes.remove(route);
		this.saveRoutes();
	}
	
	public List<RoutePlayer> getRoutePlayers() {
		List<RoutePlayer> playerList = new ArrayList<RoutePlayer>();
		playerList.addAll(this.players.values());
		return playerList;
	}
	
	public Map<Player, RoutePlayer> getRoutePlayersMap() {
		return this.players;
	}
	
	public RoutePlayer getRoutePlayer(Player player) {
		return this.players.get(player);
	}
	
	public boolean isRoutePlayer(Player player) {
		return this.players.containsKey(player);
	}
	
	public Route getRouteFromRail(Block rail) {
		for (Route route : this.routes) {
			if (route.isStartRail(rail)) {
				return route;
			}
		}
		return null;
	}
	
	public RoutePlayer getPlayerFromMinecart(Minecart minecart) {
		for (RoutePlayer player : this.getRoutePlayers()) {
			if (player.getMinecart() == minecart) {
				return player;
			}
		}
		return null;
	}
	
	public RoutePlayer startRoute(final Player player, Route route, Location loc) {
		if (!route.isComplete() || this.isRoutePlayer(player)) return null;
		
		if (this.isDeincartItem(player.getItemInHand())) {
			player.setItemInHand(null);
			player.updateInventory();
		}
		
		FakeMinecart entity = new FakeMinecart(((CraftWorld)loc.getWorld()).getHandle(), loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D);
		((CraftWorld)loc.getWorld()).getHandle().addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
		
		RideableMinecart minecart = (RideableMinecart) entity.getBukkitEntity();
		minecart.setPassenger(player);
		
		RoutePlayer user = new RoutePlayer(player, minecart, route);
		user.setInventory(player.getInventory().getContents());
		this.players.put(player, user);
		
		player.getInventory().clear();
		
		ItemStack stack = new ItemStack(Material.WOOD_DOOR);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§6Fahrt abbrechen.");
		stack.setItemMeta(meta);
		
		player.getInventory().setItem(8, stack);
		player.updateInventory();
		return user;
	}
	
	public void stopRoute(Player player, boolean finished) {
		RoutePlayer user = this.getRoutePlayer(player);
		if (user == null) return;
		
		user.getMinecart().setPassenger(null);
		user.getMinecart().remove();
		
		if (finished) {
			player.teleport(user.getRoute().getEndSpawn());
		} else {
			player.teleport(user.getRoute().getStartSpawn());
		}
		
		player.getInventory().setContents(user.getInventory());
		player.updateInventory();
		this.players.remove(player);
	}
	
	public boolean isDeincartItem(ItemStack item) {
		return (
			(item != null) &&
			(item.getType() == Material.MINECART) &&
			item.hasItemMeta() &&
			(item.getItemMeta().getDisplayName() != null) &&
			item.getItemMeta().getDisplayName().equals("§6DeinCart") &&
			!item.getItemMeta().getLore().isEmpty()
		);
	}
	
	public boolean isRouteCancelItem(ItemStack item) {
		return (
			(item != null) &&
			(item.getType() == Material.WOOD_DOOR) &&
			item.hasItemMeta() &&
			(item.getItemMeta().getDisplayName() != null) &&
			item.getItemMeta().getDisplayName().equals("§6Fahrt abbrechen.")
		);
	}
	
	public void giveMinecart(Player player) {
		if (this.isRoutePlayer(player)) return;
		
		ItemStack item = new ItemStack(Material.MINECART);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6DeinCart");
		
		List<String> lore = new ArrayList<String>();
		lore.add("Setze es auf eine Schiene, ");
		lore.add("um loszufahren!");
		lore.add("§7Dieses Item ist virtuell!");
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		player.setItemInHand(item);
	}
	
	public boolean isDeincartInNear(Location loc) {
		for (RoutePlayer player : this.getRoutePlayers()) {
			Minecart minecart = player.getMinecart();
			Location loc1 = minecart.getLocation();
			
			if (
				(Math.max(loc.getBlockX(), loc1.getBlockX()) - Math.min(loc.getBlockX(), loc1.getBlockX()) < 6) &&
				(Math.max(loc.getBlockY(), loc1.getBlockY()) - Math.min(loc.getBlockY(), loc1.getBlockY()) < 4) &&
				(Math.max(loc.getBlockZ(), loc1.getBlockZ()) - Math.min(loc.getBlockZ(), loc1.getBlockZ()) < 6)
			) {
				return true;
			}
		}
		return false;
	}
	
	public void registerCustomEntity() {
		try {
			Field fieldC = EntityTypes.class.getDeclaredField("c");
			Field fieldD = EntityTypes.class.getDeclaredField("d");
			Field fieldF = EntityTypes.class.getDeclaredField("f");
			Field fieldG = EntityTypes.class.getDeclaredField("g");
			
			fieldC.setAccessible(true);
			fieldD.setAccessible(true);
			fieldF.setAccessible(true);
			fieldG.setAccessible(true);
			
			Map c = (Map) fieldC.get(null);
			Map d = (Map) fieldD.get(null);
			Map f = (Map) fieldF.get(null);
			Map g = (Map) fieldG.get(null);
			
			c.put("DeinCart", FakeMinecart.class);
			d.put(FakeMinecart.class, "DeinCart");
			f.put(FakeMinecart.class, 42);
			g.put("DeinCart", 42);
			
			fieldC.set(null, c);
			fieldD.set(null, d);
			fieldF.set(null, f);
			fieldG.set(null, g);
			
			DeinCartPlugin.log.info("Loaded custom entity!");
		} catch (Exception e) {
			DeinCartPlugin.log.log(Level.SEVERE, "Can't load custom minecart!", e);
		}
	}
	
	public void loadRoutes() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(routesFile);
		for (String key : config.getKeys(false)) {
			ConfigurationSection section = config.getConfigurationSection(key);
			Location startSpawn = null;
			Location endSpawn = null;
			List<Block> startRails = new ArrayList<Block>();
			float startDirection = (float)section.getDouble("StartDirection");
			
			if (section.contains("StartSpawn")) {
				startSpawn = new Location(
					Bukkit.getWorld(section.getString("StartSpawn.World")),
					section.getDouble("StartSpawn.X"),
					section.getDouble("StartSpawn.Y"),
					section.getDouble("StartSpawn.Z"),
					(float)section.getDouble("StartSpawn.Yaw"),
					(float)section.getDouble("StartSpawn.Pitch")
				);
			}
			if (section.contains("EndSpawn")) {
				endSpawn = new Location(
					Bukkit.getWorld(section.getString("EndSpawn.World")),
					section.getDouble("EndSpawn.X"),
					section.getDouble("EndSpawn.Y"),
					section.getDouble("EndSpawn.Z"),
					(float)section.getDouble("EndSpawn.Yaw"),
					(float)section.getDouble("EndSpawn.Pitch")
				);
			}
			
			if (section.contains("Rails")) {
				for (String rail : section.getStringList("Rails")) {
					String[] split = rail.split(";");
					Location loc = new Location(
						Bukkit.getWorld(split[0]),
						Integer.parseInt(split[1]),
						Integer.parseInt(split[2]),
						Integer.parseInt(split[3])
					);
					startRails.add(loc.getBlock());
				}
			}
			
			Route route = new Route();
			route.setStartSpawn(startSpawn);
			route.setEndSpawn(endSpawn);
			route.setStartDirection(startDirection);
			route.setStartRails(startRails);
			
			this.routes.add(route);
		}
	}
	
	public void saveRoutes() {
		YamlConfiguration config = new YamlConfiguration();
		for (int i = 0; i < this.routes.size(); i++) {
			Route route = this.routes.get(i);
			ConfigurationSection section = config.createSection("Route" + i);
			
			section.set("StartDirection", route.getStartDirection());
			
			if (route.getStartSpawn() != null) {
				section.set("StartSpawn.World", route.getStartSpawn().getWorld().getName());
				section.set("StartSpawn.X", route.getStartSpawn().getX());
				section.set("StartSpawn.Y", route.getStartSpawn().getY());
				section.set("StartSpawn.Z", route.getStartSpawn().getZ());
				section.set("StartSpawn.Yaw", route.getStartSpawn().getYaw());
				section.set("StartSpawn.Pitch", route.getStartSpawn().getPitch());
			}
			
			if (route.getEndSpawn() != null) {
				section.set("EndSpawn.World", route.getEndSpawn().getWorld().getName());
				section.set("EndSpawn.X", route.getEndSpawn().getX());
				section.set("EndSpawn.Y", route.getEndSpawn().getY());
				section.set("EndSpawn.Z", route.getEndSpawn().getZ());
				section.set("EndSpawn.Yaw", route.getEndSpawn().getYaw());
				section.set("EndSpawn.Pitch", route.getEndSpawn().getPitch());
			}
			
			if (!route.getStartRails().isEmpty()) {
				List<String> railsList = new ArrayList<String>();
				
				for (Block block : route.getStartRails()) {
					railsList.add(String.format("%s;%d;%d;%d", block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
				}
				
				section.set("Rails", railsList);
			}
		}
		
		try {
			config.save(routesFile);
		} catch (Exception e) {
			DeinCartPlugin.log.log(Level.SEVERE, "Can't save routes!", e);
		}
	}

}
