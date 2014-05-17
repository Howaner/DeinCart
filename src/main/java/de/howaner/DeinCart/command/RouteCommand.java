package de.howaner.DeinCart.command;

import de.howaner.DeinCart.CartManager;
import de.howaner.DeinCart.listener.DeinCartListener;
import de.howaner.DeinCart.listener.RailsChangerListener;
import de.howaner.DeinCart.util.Route;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RouteCommand implements CommandExecutor {
	private CartManager manager;
	
	public RouteCommand(CartManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Dieser Befehl ist nur für Spieler!");
			return true;
		}
		if (args.length == 0) return false;
		Player player = (Player) sender;
		
		if (args[0].equalsIgnoreCase("create")) {
			if (!player.isOp()) {
				player.sendMessage(ChatColor.RED + "Keine Rechte!");
				return true;
			}
			Route route = new Route();
			this.manager.addRoute(route);
			player.sendMessage(ChatColor.GREEN + "Route mit ID #" + (this.manager.getRoutes().size()-1) + " erstellt!");
			return true;
		}
		
		else if (args[0].equalsIgnoreCase("remove")) {
			if (!player.isOp()) {
				player.sendMessage(ChatColor.RED + "Keine Rechte!");
				return true;
			}
			if (args.length != 2) {
				player.sendMessage(ChatColor.GRAY + "Benutzung: /route remove <ID>");
				return true;
			}
			
			int id = 0;
			try {
				id = Integer.parseInt(args[1]);
			} catch (Exception e) {
				player.sendMessage(ChatColor.GRAY + args[1] + " ist keine gültige Zahl!");
				return true;
			}
			
			if (id >= this.manager.getRoutes().size() || id < 0) {
				player.sendMessage(ChatColor.GRAY + "Es existiert keine Route mit dieser ID!");
				return true;
			}
			
			Route route = this.manager.getRoutes().get(id);
			this.manager.removeRoute(route);
			player.sendMessage(ChatColor.GREEN + "Die Route wurde entfernt!");
			return true;
		}
		
		else if (args[0].equalsIgnoreCase("size")) {
			player.sendMessage(ChatColor.GREEN + "Es existieren " + this.manager.getRoutes().size() + " Routen!");
			return true;
		}
		
		else if (args[0].equalsIgnoreCase("detect")) {
			if (!player.isOp()) {
				player.sendMessage(ChatColor.RED + "Keine Rechte!");
				return true;
			}
			
			if (DeinCartListener.detectors.contains(player)) {
				DeinCartListener.detectors.remove(player);
				player.sendMessage(ChatColor.GOLD + "Detecten abgebrochen!");
			} else {
				DeinCartListener.detectors.add(player);
				player.sendMessage(ChatColor.GOLD + "Klicken sie jetzt bitte mit der blosen Hand auf eine Startschiene!");
			}
			return true;
		}
		
		else if (args[0].equalsIgnoreCase("setDirection")) {
			if (!player.isOp()) {
				player.sendMessage(ChatColor.RED + "Keine Rechte!");
				return true;
			}
			if (args.length != 2) {
				player.sendMessage(ChatColor.GRAY + "Benutzung: /route setDirection <ID>");
				return true;
			}
			
			int id = 0;
			try {
				id = Integer.parseInt(args[1]);
			} catch (Exception e) {
				player.sendMessage(ChatColor.GRAY + args[1] + " ist keine gültige Zahl!");
				return true;
			}
			
			if (id >= this.manager.getRoutes().size() || id < 0) {
				player.sendMessage(ChatColor.GRAY + "Es existiert keine Route mit dieser ID!");
				return true;
			}
			
			Route route = this.manager.getRoutes().get(id);
			route.setStartDirection(player.getLocation().getYaw());
			this.manager.saveRoutes();
			player.sendMessage(ChatColor.GREEN + "Direction gesetzt!");
			return true;
		}
		
		else if (args[0].equalsIgnoreCase("setStartSpawn")) {
			if (!player.isOp()) {
				player.sendMessage(ChatColor.RED + "Keine Rechte!");
				return true;
			}
			if (args.length != 2) {
				player.sendMessage(ChatColor.GRAY + "Benutzung: /route setStartSpawn <ID>");
				return true;
			}
			
			int id = 0;
			try {
				id = Integer.parseInt(args[1]);
			} catch (Exception e) {
				player.sendMessage(ChatColor.GRAY + args[1] + " ist keine gültige Zahl!");
				return true;
			}
			
			if (id >= this.manager.getRoutes().size() || id < 0) {
				player.sendMessage(ChatColor.GRAY + "Es existiert keine Route mit dieser ID!");
				return true;
			}
			
			Route route = this.manager.getRoutes().get(id);
			route.setStartSpawn(player.getLocation());
			this.manager.saveRoutes();
			player.sendMessage(ChatColor.GREEN + "Startspawn gesetzt!");
			return true;
		}
		
		else if (args[0].equalsIgnoreCase("setEndSpawn")) {
			if (!player.isOp()) {
				player.sendMessage(ChatColor.RED + "Keine Rechte!");
				return true;
			}
			if (args.length != 2) {
				player.sendMessage(ChatColor.GRAY + "Benutzung: /route setEndSpawn <ID>");
				return true;
			}
			
			int id = 0;
			try {
				id = Integer.parseInt(args[1]);
			} catch (Exception e) {
				player.sendMessage(ChatColor.GRAY + args[1] + " ist keine gültige Zahl!");
				return true;
			}
			
			if (id >= this.manager.getRoutes().size() || id < 0) {
				player.sendMessage(ChatColor.GRAY + "Es existiert keine Route mit dieser ID!");
				return true;
			}
			
			Route route = this.manager.getRoutes().get(id);
			route.setEndSpawn(player.getLocation());
			this.manager.saveRoutes();
			player.sendMessage(ChatColor.GREEN + "Endspawn gesetzt!");
			return true;
		}
		
		else if (args[0].equalsIgnoreCase("manageRails")) {
			if (!player.isOp()) {
				player.sendMessage(ChatColor.RED + "Keine Rechte!");
				return true;
			}
			if (args.length != 2) {
				player.sendMessage(ChatColor.GRAY + "Benutzung: /route manageRails <ID>");
				return true;
			}
			
			int id = 0;
			try {
				id = Integer.parseInt(args[1]);
			} catch (Exception e) {
				player.sendMessage(ChatColor.GRAY + args[1] + " ist keine gültige Zahl!");
				return true;
			}
			
			if (id >= this.manager.getRoutes().size() || id < 0) {
				player.sendMessage(ChatColor.GRAY + "Es existiert keine Route mit dieser ID!");
				return true;
			}
			
			Route route = this.manager.getRoutes().get(id);
			if (RailsChangerListener.players.containsKey(player)) {
				RailsChangerListener.players.remove(player);
				player.sendMessage(ChatColor.GREEN + "Railveränderung abgebrochen!");
			} else {
				RailsChangerListener.players.put(player, route);
				player.sendMessage(ChatColor.GREEN + "Railveränderung aktiviert!");
				player.sendMessage(ChatColor.GOLD + "Linksklick - Rail löschen");
				player.sendMessage(ChatColor.GOLD + "Rechtsklick - Rail hinzufügen");
			}
			return true;
		}
		
		else
			return false;
	}

}
