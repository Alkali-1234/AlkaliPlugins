package me.Alkali.AncientTrident;

import org.bukkit.command.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
//import org.bukkit.event.entity.EntityDamageEvent;
//import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
//import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	Map<String, Long> cooldowns = new HashMap<String, Long>();
	
	public Inventory inv;
	
	public List<String> list = new ArrayList<String>();
	
	@Override
	public void onEnable(){
		// startup
		// reload
		// plugin reload
		createInv();
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable(){
		// close
		// reloads
		// plugin reloads
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// the actual thing
		if(label.equalsIgnoreCase("trident")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("no");
				return true;
			}
			Player player = (Player) sender;
			player.openInventory(inv);
			return true;
		}
		
		return false;
	}
	
	public ItemStack getItem() {
		// add the boots
		ItemStack trident = new ItemStack(Material.TRIDENT);
		// define the meta of the boots
		ItemMeta meta = trident.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Ancient Trident");
		List<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "RIGHT CLICK");
		lore.add("Summon tnt!");
		lore.add("");
		lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "LEFT CLICK");
		lore.add("Shoot explosives!");
		
		meta.setLore(lore);
		meta.setUnbreakable(true);
		meta.addEnchant(Enchantment.LOYALTY , 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		
		// set the meta
		trident.setItemMeta(meta);
		
		return trident;
	
	}
	
	@EventHandler()
	public void onClick (PlayerInteractEvent event) {
		if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.TRIDENT))
			if(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasLore())
				if(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Ancient Trident")) {
					Player player = (Player) event.getPlayer();
					// Right Click
					if(event.getAction() == Action.RIGHT_CLICK_AIR){
						
						if(cooldowns.containsKey(player.getName())) {
							// player is inside hashmap
							if(cooldowns.get(player.getName()) > System.currentTimeMillis()) {
								long timeleft = (cooldowns.get(player.getName()) - System.currentTimeMillis()) / 1000;
								
								player.sendMessage(ChatColor.RED + "Ability on cooldown! " + timeleft + " seconds left!");
								event.setCancelled(true);
								return;
							}
						}
						
						if (!list.contains(player.getName()))
							list.add(player.getName());
						cooldowns.put(player.getName(), System.currentTimeMillis() + (5 * 1000));
						
						return;
					}
					
					// Left click
					if(event.getAction() == Action.LEFT_CLICK_AIR) {
						player.launchProjectile(Fireball.class);
					}
				}
			if(list.contains(event.getPlayer().getName())) {
				list.remove(event.getPlayer().getName());
			}
			
		
	}
	
	@EventHandler
	public void onLand (ProjectileHitEvent event) {
		if(event.getEntityType() == EntityType.TRIDENT) {
			if(event.getEntity().getShooter() instanceof Player) {
				Player player = (Player) event.getEntity().getShooter();
				if(list.contains(player.getName())) {
					Location loc = event.getEntity().getLocation();
					loc.setY(loc.getY() + 1);
					
					loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
					
					loc.setY(loc.getY() + 1);
					
					loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
					
					loc.setY(loc.getY() + 1);
					
					loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
					loc.setY(loc.getY() + 1);
					
					loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
					
				}
			}
		}
	}
	
	@EventHandler()
	public void onClick (InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if(!event.getInventory().equals(inv)) {
			return;
		}
		if(event.getCurrentItem() == null) return;
		if(event.getCurrentItem().getItemMeta() == null) return;
		if(event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
		if(player.getInventory().firstEmpty() == -1) {
			// Inventory full
			Location loc = player.getLocation();
			World world = player.getWorld();
			world.dropItemNaturally(loc, getItem());
			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "WATERRRR");
			player.closeInventory();
		}
		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "WATERRRR");
		player.getInventory().addItem(getItem());
		player.closeInventory();
	}
	
	public void createInv() {
		inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Claim!");
		
		ItemStack item = new ItemStack (Material.TRIDENT);
		
		
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(ChatColor.GREEN + "Ancient Trident");
		List<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add("Claim!");
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(4, item);
	}
	
	
	
	
}

