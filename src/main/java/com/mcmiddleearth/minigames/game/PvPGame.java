package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.pvp.PvPLoadoutItem;
import com.mcmiddleearth.minigames.pvp.PvPLoadoutManager;
import com.mcmiddleearth.minigames.pvp.PvPLocationManager;
import com.mcmiddleearth.minigames.scoreboard.PvPGameScoreboard;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import com.mcmiddleearth.pluginutil.TitleUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.factory.SphereRegionFactory;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Planetology
 */
public class PvPGame extends AbstractGame implements Listener {

    @Getter private final PvPLocationManager locationManager;
    @Getter private final PvPLoadoutManager loadoutManager;
    @Getter private CuboidSelection cuboidSelection;
    @Getter private Region sphereRegion;
    @Getter private List<String> pvpers;
    @Getter private List<String> redTeam, blueTeam;

    @Getter private boolean ready, started, finished;
    @Getter @Setter boolean cuboidArena, loadout;
    @Getter private int readySeconds, gameSeconds;
    @Getter public int redKills, blueKills;

    @Getter private PvPGameScoreboard scoreboard;

    public PvPGame(Player manager, String name) {
        super(manager, name, GameType.PVP, new PvPGameScoreboard());
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGamesPlugin.getPluginInstance());

        setFlightAllowed(false);
        setGm3Allowed(false);
        setGm2Forced(true);

        locationManager = new PvPLocationManager(this);
        loadoutManager = new PvPLoadoutManager(this);

        pvpers = new ArrayList<>();
        redTeam = new ArrayList<>();
        blueTeam = new ArrayList<>();

        readySeconds = 16;
        redKills = 0;
        blueKills = 0;

        scoreboard = ((PvPGameScoreboard) getBoard());
        scoreboard.init(this);
    }

    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);

        forceTeleport(player, getWarp());
        pvpers.add(player.getName());
    }

    @Override
    public void removePlayer(OfflinePlayer player) {
        super.removePlayer(player);

        pvpers.remove(player.getName());
        if (PlayerUtil.isSame(player, getManager())) ((Player) player).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    @Override
    public boolean joinAllowed() {
        return super.joinAllowed() && !ready;
    }

    @Override
    public String getGameChatTag(Player player) {
        if(PluginData.isManager(player)) {
            return ChatColor.DARK_AQUA + "<Manager ";
        } else if (redTeam.contains(player.getName())) {
            return ChatColor.RED + "<Warrior: ";
        } else {
            return ChatColor.BLUE + "<Warrior: ";
        }
    }

    @Override
    public void end(Player sender) {
        super.end(sender);

        // Cleanup
        this.finished = false;
        this.redTeam.clear();
        this.blueTeam.clear();
        this.pvpers.clear();
    }

    public void ready(int seconds) {
        ready = true;
        gameSeconds = seconds;

        createTeams();

        for (String name : pvpers) {
            Player player = Bukkit.getPlayer(name);

            if (redTeam.contains(player.getName())) PluginData.getMessageUtil().sendInfoMessage(player, "You joined team " + ChatColor.RED + "red"
                    + ChatColor.AQUA + ", battle will begin in 15 seconds...");
            if (blueTeam.contains(player.getName())) PluginData.getMessageUtil().sendInfoMessage(player, "You joined team " + ChatColor.BLUE + "blue"
                    + ChatColor.AQUA + ", battle will begin in 15 seconds...");

            giveLoadout(player);

            player.setHealth(20);
            player.setFoodLevel(20);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (readySeconds > 0) {
                    readySeconds--;

                    for (String name : pvpers) {
                        Player player = Bukkit.getPlayer(name);

                        player.setLevel(readySeconds);

                        if (readySeconds <= 3) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1.0f, .8f);

                            switch (readySeconds) {
                                case 3:
                                    TitleUtil.showTitle(player, ChatColor.GOLD + "3", "", 0, 30, 60);
                                    break;
                                case 2:
                                    TitleUtil.showTitle(player, ChatColor.GOLD + "2", "", 0, 30, 60);
                                    break;
                                case 1:
                                    TitleUtil.showTitle(player, ChatColor.GOLD + "1", "", 0, 30, 60);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    if (readySeconds == 0) {
                        cancel();

                        for (String name : pvpers) {
                            Player player = Bukkit.getPlayer(name);

                            TitleUtil.showTitle(player, ChatColor.GOLD + "Fight!", ChatColor.GRAY + "The battle has begun", 0, 30, 60);
                            PluginData.getMessageUtil().sendInfoMessage(player, "Started! Kill as many players of the other team, before the time runs out...");

                            player.playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL, 1.0f, .85f);
                        }

                        start();
                    }
                }
            }
        }.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 0, 20);
    }

    private void start() {
        if (locationManager.getArenaMax() != null && locationManager.getArenaMin() != null) {
            cuboidSelection = new CuboidSelection(locationManager.getArenaMax().getLocation().getWorld(),
                    locationManager.getArenaMax().getLocation(),
                    locationManager.getArenaMin().getLocation());
        } else if (locationManager.getArenaCenter() != null) {
            Location center = locationManager.getArenaCenter().getLocation();

            sphereRegion = new SphereRegionFactory().createCenteredAt(new BlockVector(center.getX(), center.getY(), center.getZ()), locationManager.getArenaRadius());
        }

        started = true;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameSeconds > 0) {
                    gameSeconds --;
                    ((PvPGameScoreboard) getBoard()).showKills();

                    if (gameSeconds == 0) {
                        cancel();

                        ((PvPGameScoreboard) getBoard()).showGameOver();

                        for (String name : pvpers) {
                            Player player = Bukkit.getPlayer(name);

                            if (redKills > blueKills) {
                                PluginData.getMessageUtil().sendInfoMessage(player, "The battle has ended, team " + ChatColor.RED + "red " + ChatColor.AQUA + "has won!");
                            } else if (blueKills > redKills) {
                                PluginData.getMessageUtil().sendInfoMessage(player, "The battle has ended, team " + ChatColor.BLUE + "blue " + ChatColor.AQUA + "has won!");
                            } else if (redKills == blueKills) {
                                PluginData.getMessageUtil().sendInfoMessage(player, "The battle has ended in a tie!");
                            }

                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, .85f);
                        }

                        finish();
                    }
                }
            }
        }.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 0, 20);
    }

    private void finish() {
        finished = true;

        for (final String name : pvpers) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getPlayer(name).teleport(getWarp());
                    Bukkit.getPlayer(name).getInventory().clear();
                    end(Bukkit.getPlayer(name));
                }
            }.runTaskLater(MiniGamesPlugin.getPluginInstance(), 20 * 10);
        }
    }

    private void giveLoadout(Player player) {
        if (!loadout) return;

        player.getInventory().clear();
        Color color = null;

        if (redTeam.contains(player.getName())) color = Color.fromRGB(255, 0, 0);
        if (blueTeam.contains(player.getName())) color = Color.fromRGB(0, 0, 255);

        ItemStack helmet = PvPLoadoutItem.fromJson(loadoutManager.getArmor().get(3)).toItemStack(),
                chestplate = PvPLoadoutItem.fromJson(loadoutManager.getArmor().get(2)).toItemStack(),
                leggings = PvPLoadoutItem.fromJson(loadoutManager.getArmor().get(1)).toItemStack(),
                boots = PvPLoadoutItem.fromJson(loadoutManager.getArmor().get(0)).toItemStack();

        if (helmet.getType().equals(Material.LEATHER_HELMET)) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) helmet.getItemMeta();
            leatherArmorMeta.setColor(color);
            helmet.setItemMeta(leatherArmorMeta);
        } else if (chestplate.getType().equals(Material.LEATHER_CHESTPLATE)) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) chestplate.getItemMeta();
            leatherArmorMeta.setColor(color);
            chestplate.setItemMeta(leatherArmorMeta);
        } else if (leggings.getType().equals(Material.LEATHER_LEGGINGS)) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) leggings.getItemMeta();
            leatherArmorMeta.setColor(color);
            leggings.setItemMeta(leatherArmorMeta);
        } else if (boots.getType().equals(Material.LEATHER_BOOTS)) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) boots.getItemMeta();
            leatherArmorMeta.setColor(color);
            boots.setItemMeta(leatherArmorMeta);
        }

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        if (loadoutManager.getShield() != null) player.getInventory().setItemInOffHand(PvPLoadoutItem.fromJson(loadoutManager.getShield()).toItemStack());

        for (Object object : loadoutManager.getHotbar()) {
            player.getInventory().addItem(PvPLoadoutItem.fromJson(object).toItemStack());
        }
    }

    private void createTeams() {
        for (String name : pvpers) {
            Player player = Bukkit.getPlayer(name);

            if (blueTeam.size() > redTeam.size()) {
                redTeam.add(player.getName());
                player.teleport(locationManager.getRedSpawn().getLocation());
            } else if (redTeam.size() > blueTeam.size()) {
                blueTeam.add(player.getName());
                player.teleport(locationManager.getBlueSpawn().getLocation());
            } else {
                Random random = new Random();

                if(random.nextBoolean()) {
                    redTeam.add(player.getName());
                    player.teleport(locationManager.getRedSpawn().getLocation());
                } else {
                    blueTeam.add(player.getName());
                    player.teleport(locationManager.getBlueSpawn().getLocation());
                }
            }
        }
    }

    public String getClock() {
        int minutes = gameSeconds / 60;
        int seconds = gameSeconds % 60;

        return ChatColor.GOLD + "" + minutes + ChatColor.GRAY + ":" + ChatColor.GOLD +  ((seconds > 9) ? seconds : "0" + seconds);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if(PluginData.isInGame(player)) {
                if (!started || this.finished) event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();

            if(PluginData.isInGame(damager) && (PluginData.isInGame(damaged))) {
                if (started) {
                    if (redTeam.contains(damager.getName()) && redTeam.contains(damaged.getName())) event.setCancelled(true);
                    if (blueTeam.contains(damager.getName()) && blueTeam.contains(damaged.getName())) event.setCancelled(true);
                }

                if (!started || this.finished) event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom(), to = event.getTo();

        if (PluginData.isInGame(event.getPlayer())) {
            if (ready && !started) {
                if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                    event.getPlayer().teleport(from.setDirection(to.getDirection()));
                }
            } else if (started) {
                if (cuboidArena) {
                    if (cuboidSelection.contains(from) && !cuboidSelection.contains(to)) {
                        event.getPlayer().teleport(from.setDirection(to.getDirection()));
                    }
                } else {
                    BlockVector fromVector = new BlockVector(from.getX(), from.getY(), from.getZ());
                    BlockVector toVector = new BlockVector(to.getX(), to.getY(), to.getZ());

                    if (sphereRegion.contains(fromVector) && !sphereRegion.contains(toVector)) {
                        event.getPlayer().teleport(from.setDirection(to.getDirection()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (PluginData.isInGame(player) && started) {
            event.setDeathMessage(null);
            event.getDrops().clear();

            for (String name : pvpers) {
                Bukkit.getPlayer(name).playSound(Bukkit.getPlayer(name).getLocation(), Sound.ENTITY_VILLAGER_DEATH, 1.0f, .7f);
            }

            if (redTeam.contains(player.getName())) {
                blueKills ++;
                ((PvPGameScoreboard) getBoard()).addKill("blue");

                if (player.getKiller() != null) {
                    for (String name : pvpers) {
                        PluginData.getMessageUtil().sendInfoMessage(Bukkit.getPlayer(name), ChatColor.RED + player.getName()
                                + ChatColor.AQUA  + " got killed by " + ChatColor.BLUE + player.getKiller().getName());
                    }
                }

                player.spigot().respawn();
            }

            if (blueTeam.contains(player.getName())) {
                redKills ++;
                ((PvPGameScoreboard) getBoard()).addKill("red");

                if (player.getKiller() != null) {
                    for (String name : pvpers) {
                        PluginData.getMessageUtil().sendInfoMessage(Bukkit.getPlayer(name), ChatColor.BLUE + player.getName()
                                + ChatColor.AQUA  + " got killed by " + ChatColor.RED + player.getKiller().getName());
                    }
                }

                player.spigot().respawn();
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        if(PluginData.isInGame(player) && started) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    giveLoadout(player);

                    if (redTeam.contains(player.getName())) {
                        player.teleport(locationManager.getRedSpawn().getLocation());
                    } else if (blueTeam.contains(player.getName())) {
                        player.teleport(locationManager.getBlueSpawn().getLocation());
                    }
                }
            }.runTaskLater(MiniGamesPlugin.getPluginInstance(), 1);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if(PluginData.isInGame(event.getPlayer()) && ready) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(PluginData.isInGame((Player) event.getWhoClicked()) && ready) {
            if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (PluginData.isInGame((Player) event.getEntity()) && ready) event.setCancelled(true);
    }

    public boolean hasRedSpawn() { return locationManager.getRedSpawn() != null; }

    public boolean hasBlueSpawn() { return locationManager.getBlueSpawn() != null; }

    public boolean hasArenaMax() { return locationManager.getArenaMax() != null; }

    public boolean hasArenaMin() { return locationManager.getArenaMin() != null; }

    public boolean hasArenaCenter() { return locationManager.getArenaCenter() != null; }
}
