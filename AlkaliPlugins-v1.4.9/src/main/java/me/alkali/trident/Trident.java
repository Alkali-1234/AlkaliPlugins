package me.alkali.trident;

import java.util.*;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
// import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
//import org.bukkit.event.entity.EntityDamageEvent;
//import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
//import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
//import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import sun.tools.jconsole.JConsole;

public class Trident extends JavaPlugin implements Listener{

    Map<String, Long> cooldowns = new HashMap<String, Long>();
    Map<String, String> place = new HashMap<String, String>();

    public Inventory inv;

    public List<String> list = new ArrayList<String>();

    @Override
    public void onEnable(){
        // startup
        // reload
        // plugin reload
        createInv();
        this.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.addRecipe(getRecipe());
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
            if (!player.isOp()){
                player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }
            player.openInventory(inv);
            return true;
        }
        if(label.equalsIgnoreCase("putperm")) {
            if(!(sender instanceof Player)) {
                return true;
            }
            Player player = (Player) sender;
            if(player.isOp()) {
                return true;
            }
        }
        if(label.equalsIgnoreCase("balance") || label.equalsIgnoreCase("bal")){
            if(!(sender instanceof Player)) {
                return true;
            }
            Player player = (Player) sender;
            NamespacedKey namespacedkey = new NamespacedKey(this, "money");
            PersistentDataContainer data = player.getPersistentDataContainer();
            data.get(namespacedkey, PersistentDataType.DOUBLE);
            if(!data.has(namespacedkey, PersistentDataType.DOUBLE)) data.set(namespacedkey, PersistentDataType.DOUBLE, 0.0);
            double balance = data.get(namespacedkey, PersistentDataType.DOUBLE);
            player.sendMessage(ChatColor.GREEN + "You have" + balance + "!");
        }
        if(label.equalsIgnoreCase("hologram") || label.equalsIgnoreCase("gram")){
            if(!(sender instanceof Player)){
                return true;
            }
            Player player = (Player) sender;
            if(!player.isOp()){
                return true;
            }
            player.getInventory().addItem(testItem());
            ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
            // DEPRECATED
            //armorStand.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(ChatColor.GREEN + "Welcome!");
            armorStand.setGravity(false);

        }
        if(label.equalsIgnoreCase("shoot")){
            if(!(sender instanceof Player)){
                return true;
            }
            Player player = (Player) sender;
            ArmorStand balloon = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
            balloon.setGravity(false);
            balloon.setInvulnerable(true);
            balloon.getEquipment().setHelmet(getPlayerHead("Alkali1234"));
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
//            SkullMeta meta = (SkullMeta) skull.getItemMeta();
//            meta.setOwner("Alkali1234");

            balloon.setInvisible(true);
            balloon.setVelocity(balloon.getLocation().getDirection().multiply(2).setY(0));
//            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
//                @Override
//                public void run() {
//                    Location loc = balloon.getLocation();
//                    loc.setX(loc.getX() + 0.3);
//                    balloon.teleport(loc);
//                }
//            }, 1, 120);

        }
        if(label.equalsIgnoreCase("hyperion")) {
            if(!(sender instanceof Player)){
                return true;
            }
            Player player = (Player) sender;
            if(player.isOp()){
                if(player.getInventory().firstEmpty() != -1){
                    player.getInventory().addItem(hyperion());
                }else{
                    player.sendMessage(ChatColor.RED + "You don't have inventory space!");
                }
            }
        }
        return false;
    }

    public ItemStack hyperion(){
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GOLD + "Withered Hyperion");
        List<String> lore = new ArrayList<String>();
        lore.add("");
        lore.add(ChatColor.GREEN + "Item Ability: Wither Impact " + ChatColor.YELLOW + ChatColor.BOLD + "RIGHT CLICK");
        lore.add(ChatColor.GOLD + "Teleport 7 blocks and blast nearby enemies!");
        lore.add("");
        lore.add(ChatColor.GOLD + "Lightener " + ChatColor.YELLOW + ChatColor.BOLD + "LEFT CLICK");
        lore.add(ChatColor.GOLD + "Summon lightning to what your looking at.");
        lore.add("");
        lore.add(ChatColor.GOLD + "LEGENDARY");
        meta.setLore(lore);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onHypRightClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Withered Hyperion")){
            if(player.getInventory().getItemInMainHand().getItemMeta().hasLore()){
                if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
                    boolean blockIsEmpty = false;
                    if(player.getLocation().add(player.getLocation().getDirection().multiply(7)).getBlock().isEmpty()) {
                        player.teleport(player.getLocation().add(player.getLocation().getDirection().multiply(7)));
                        blockIsEmpty = true;
                    }
                    if(!blockIsEmpty){
                        player.sendMessage(ChatColor.RED + "You have blocks in front of you!");
                    }

                    int entities = 0;
                    double dmg = 0;
                    for(Entity e : player.getNearbyEntities(7, 7, 7)){
                        if(e instanceof Mob){
                            Mob mob = (Mob) e;
                            entities += 1;
                            dmg += 15.5;
                            mob.damage(15.5);
                            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                                @Override
                                public void run() {
                                    mob.damage(2);
                                }
                            }, 5, 200);
                        }
                    }
                    Location loc = player.getLocation();
                    if(entities > 0){
                        player.sendMessage("Your wither impact dealt " + ChatColor.RED + dmg + ChatColor.WHITE +" damage to" + ChatColor.RED + entities + ChatColor.WHITE + "entities.");
                    }

                    player.getWorld().playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 5, 4);
                    player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 3);
                }
                if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
                    Location loc = player.getLocation().getDirection().add(player.getLocation().getDirection().multiply(7)).toLocation(player.getWorld());
                    player.getWorld().spawnEntity(loc, EntityType.LIGHTNING);
                    BoundingBox box = new BoundingBox(2D, 2D, 2D, 2D, 2D, 2D);
                    for(Entity e : loc.getWorld().getNearbyEntities(box)){
                        if(e instanceof Mob){
                            Mob entity = (Mob) e;
                            entity.damage(10D);
                        }
                    }
                }
            }
        }
    }

//    private boolean getLookingAt(Player player, Mob mob)
//    {
//        Location eye = player.getEyeLocation();
//        Vector toEntity = mob.getEyeLocation().toVector().subtract(eye.toVector());
//        double dot = toEntity.normalize().dot(eye.getDirection());
//
//        return dot > 0.99D;
//    }

    public ShapedRecipe getRecipe(){
        NamespacedKey key = new NamespacedKey(this, "Withered Hyperion");
        ShapedRecipe recipe = new ShapedRecipe(key, hyperion());
        recipe.shape(" N ", " N ", " S ");
        recipe.setIngredient('N', Material.NETHER_STAR);
        recipe.setIngredient('S', Material.WITHER_SKELETON_SKULL);
        return recipe;
    }


    public ItemStack testItem(){
        // add the boots
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        // define the meta of the boots
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Item");
        List<String> lore = new ArrayList<String>();
        lore.add("");
        lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "RIGHT CLICK");
        lore.add("Summon tnt!");
        lore.add("");
        lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "LEFT CLICK");
        lore.add("Shoot explosives!");

        meta.setLore(lore);
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.MENDING , 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        // set the meta
        item.setItemMeta(meta);

        return item;
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
        lore.add("SHOOT!");
        lore.add("");

        meta.setLore(lore);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        // set the meta
        trident.setItemMeta(meta);

        return trident;

    }

//	public ItemStack netheriteAxe() {
//		ItemStack axe = new ItemStack
//	}


    @EventHandler
    public void onClickBlaze(PlayerInteractEvent event){
        Player player = (Player) event.getPlayer();
        if(player.getInventory().getItemInMainHand().equals(Material.BLAZE_ROD))
            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Item"))
                if(player.getInventory().getItemInMainHand().getItemMeta().hasLore()){
                    player.sendMessage(ChatColor.GREEN + "Summoning new armorstand...");
                    try{
                        ArmorStand balloon = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
                        balloon.setGravity(false);
                        balloon.setInvulnerable(true);
                        balloon.getEquipment().setHelmet(getPlayerHead("CybermanAC"));
                        balloon.setVelocity(balloon.getLocation().getDirection().multiply(2));
                        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                            @Override
                            public void run() {
                                balloon.setHealth(0.0);
                            }
                        }, 40);
                    }catch (Exception e){
                        player.sendMessage("ERROR");
                    }
                }
    }

    @SuppressWarnings("deprecation")
    public ItemStack getPlayerHead(String player){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        assert meta != null;
        meta.setOwner(player);
        head.setItemMeta(meta);
        return head;
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

                                player.sendMessage(ChatColor.RED + "Ability on cooldown! " + (timeleft + 1) + " seconds left!");
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
        if(!event.getCurrentItem().getItemMeta().getDisplayName().contains("Click to Claim!")) return;
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


    //TODO Inventories
    public void createInv() {
        inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Claim!");

        ItemStack item = new ItemStack (Material.TRIDENT);


        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(ChatColor.GREEN + "Ancient Trident");
        List<String> lore = new ArrayList<String>();
        lore.add("");
        lore.add("Claim!");

        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(4, item);
    }

    public ItemStack getStart() {
        // add the boots
        ItemStack start = new ItemStack(Material.EMERALD);
        // define the meta of the boots
        ItemMeta meta = start.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Start");
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        // set the meta
        start.setItemMeta(meta);

        return start;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = (Player) event.getPlayer();
        player.getInventory().clear();
        player.getInventory().setItem(4, getStart());
        player.teleport(player.getWorld().getSpawnLocation());
        place.putIfAbsent(player.getName(), "spawn");
        NamespacedKey namespacedkey = new NamespacedKey(this, "money");
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.get(namespacedkey, PersistentDataType.DOUBLE);
        if(!data.has(namespacedkey, PersistentDataType.DOUBLE)) data.set(namespacedkey, PersistentDataType.DOUBLE, 0.0);
        double balance = data.get(namespacedkey, PersistentDataType.DOUBLE);
        player.sendMessage(ChatColor.GREEN + "You have" + balance + "!");


        NamespacedKey namespacedkey2 = new NamespacedKey(this, "lvl1beatens");
        PersistentDataContainer data2 = player.getPersistentDataContainer();
        data2.get(namespacedkey2, PersistentDataType.INTEGER);
        if(!data2.has(namespacedkey2, PersistentDataType.INTEGER)) data2.set(namespacedkey2, PersistentDataType.INTEGER, 0);

    }

    public Integer getLvl1Beatens(Player player){
        NamespacedKey namespacedkey2 = new NamespacedKey(this, "lvl1beatens");
        PersistentDataContainer data2 = player.getPersistentDataContainer();
        data2.get(namespacedkey2, PersistentDataType.INTEGER);
        if(!data2.has(namespacedkey2, PersistentDataType.INTEGER)) data2.set(namespacedkey2, PersistentDataType.INTEGER, 0);
        return data2.get(namespacedkey2, PersistentDataType.INTEGER);
    }




    public Double getMoney(Player player) {
        NamespacedKey namespacedkey = new NamespacedKey(this, "money");
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.get(namespacedkey, PersistentDataType.DOUBLE);
        if(!data.has(namespacedkey, PersistentDataType.DOUBLE)) data.set(namespacedkey, PersistentDataType.DOUBLE, 0.0);
        return data.get(namespacedkey, PersistentDataType.DOUBLE);
    }

    //TODO Inventory Click
    @EventHandler
    public void onPlayerStartClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null) return;
        if(event.getCurrentItem().getItemMeta() == null) return;
        if(!event.getCurrentItem().getType().equals(Material.EMERALD)) return;
        if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Start")) {
            player.setItemOnCursor(null);
            player.updateInventory();
            event.setCancelled(true);
        }
        if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Lvl 1")){
            Location loc = player.getLocation();
            loc.setY(71);
            loc.setX(-700);
            loc.setZ(72);
            loc.setPitch(0);
            player.teleport(loc);
            loc.setZ(81);
            player.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);
            player.getInventory().clear();
            place.replace(player.getName(), "arena");
            player.getInventory().addItem(new ItemStack(Material.NETHERITE_AXE));
            player.closeInventory();
        }
        if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Lvl 2")){
            Location loc = player.getLocation();
            if(getLvl1Beatens(player) < 20){
                player.sendMessage(ChatColor.RED + "You need to beat Level 1 20 times to unlock this level!");
                player.closeInventory();
            }
            loc.setY(71);
            loc.setX(-700);
            loc.setZ(72);
            loc.setPitch(0);
            player.teleport(loc);
            loc.setZ(81);
            player.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);
            player.getInventory().clear();
            place.replace(player.getName(), "arena");
            player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
            player.closeInventory();
        }

    }


    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if(event.getPlayer().hasPermission("item.drop")) {
            return;
        }
        event.getPlayer().sendMessage(ChatColor.RED + "You cannot drop this item!");
        event.setCancelled(true);
    }

    //TODO Break block event
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if(!event.getPlayer().hasPermission("block.break")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Sorry you can't break that there!");
            event.setDropItems(false);
        }
    }

    //TODO Interact
    @EventHandler
    public void onStart(PlayerInteractEvent event) {
        Player player = (Player) event.getPlayer();

        inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Level");
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GREEN + "Lvl 1");
        List<String> lore = new ArrayList<String>();
        lore.add("");
        lore.add(ChatColor.WHITE + "Click to go to lvl 1!");
        lore.add("Times beaten: " + getLvl1Beatens(player));
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        inv.setItem(0, item);

        ItemStack item2 = new ItemStack(Material.EMERALD);
        ItemMeta meta2 = item2.getItemMeta();
        assert meta2 != null;
        meta2.setDisplayName(ChatColor.GREEN + "Lvl 2");
        List<String> lore2 = new ArrayList<String>();
        lore2.add("");
        lore2.add(ChatColor.WHITE + "Click to go to lvl 2!");
        meta2.setLore(lore2);
        meta2.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item2.setItemMeta(meta2);
        inv.setItem(1, item2);

        if(player.getInventory().getItemInMainHand().getType() == Material.EMERALD)
            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Start")) {
                player.openInventory(inv);
            }
    }


    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(event.getEntity().getType().equals(EntityType.WITHER_SKELETON)) {
            event.setDroppedExp(0);
            event.getDrops().clear();
            if(event.getEntity().getKiller() == null) return;
            if (!(event.getEntity().getKiller() instanceof Player)) {
                return;
            }
            Player player = (Player) event.getEntity().getKiller();
            NamespacedKey namespacedkey = new NamespacedKey(this, "money");
            PersistentDataContainer data = player.getPersistentDataContainer();
            data.get(namespacedkey, PersistentDataType.DOUBLE);
            if(!data.has(namespacedkey, PersistentDataType.DOUBLE)) data.set(namespacedkey, PersistentDataType.DOUBLE, 0.0);
            double balance = data.get(namespacedkey, PersistentDataType.DOUBLE);
            data.set(namespacedkey, PersistentDataType.DOUBLE, balance + 9.5);
            NamespacedKey namespacedkey2 = new NamespacedKey(this, "lvl1beatens");
            PersistentDataContainer data2 = player.getPersistentDataContainer();
            data2.get(namespacedkey2, PersistentDataType.INTEGER);
            data.set(namespacedkey2, PersistentDataType.INTEGER, getLvl1Beatens(player) + 1);
            player.sendMessage(ChatColor.GREEN + "You successfully beat wither skeleton and got 9.5 coins!");
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    player.getInventory().clear();
                    player.getInventory().setChestplate(null);
                    player.getInventory().setItem(4, getStart());
                    player.teleport(player.getWorld().getSpawnLocation());
                    player.setHealth(20);
                    player.getActivePotionEffects().clear();
                    place.replace(player.getName(), "spawn");
                }

            }, 40);
        }
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = (Player) event.getPlayer();
        if(place.get(player.getName()).equals("arena")) {
            player.getInventory().clear();
            player.getInventory().setItem(4, getStart());
            player.teleport(player.getWorld().getSpawnLocation());
            player.sendMessage(ChatColor.RED + "You died!");
            place.replace(player.getName(), "spawn");
        }
    }

    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = (Player) event.getEntity();
        if(place.get(player.getName()).equals("arena")){
            if(!Objects.requireNonNull(player.getKiller()).isDead()) {
                player.getKiller().setHealth(0);
            }
        }
    }



    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(!place.get(event.getPlayer().getName()).isEmpty()) {
            place.remove(event.getPlayer().getName());
        }
    }



}
