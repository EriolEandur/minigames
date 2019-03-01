package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.golf.GolfHoleLocation;
import com.mcmiddleearth.minigames.golf.GolfLocationManager;
import com.mcmiddleearth.minigames.golf.GolfPlayer;
import com.mcmiddleearth.minigames.golf.GolfTeeLocation;
import com.mcmiddleearth.minigames.scoreboard.GolfGameScoreboard;
import com.mcmiddleearth.pluginutil.TitleUtil;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * @author Planetology
 */
public class GolfGame extends AbstractGame implements Listener {

    @Getter private final GolfLocationManager locationManager;
    @Getter private final List<GolfPlayer> golfers;
    @Getter private final List<Player> notFinished;

    @Getter private boolean ready, started;
    @Getter private int readySeconds, hole, round, shots;
    @Getter private GolfPlayer golfer;

    public GolfGame(Player manager, String name) {
        super(manager, name, GameType.GOLF, new GolfGameScoreboard());
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGamesPlugin.getPluginInstance());

        setFlightAllowed(false);
        setGm3Allowed(true);
        setGm2Forced(false);

        locationManager = new GolfLocationManager(this);
        golfers = new ArrayList<>();
        notFinished = new ArrayList<>();

        readySeconds = 16;
        hole = 1;
        round = 1;
        shots = 0;
    }

    public void ready() {
        ready = true;

        ((GolfGameScoreboard) getBoard()).init(this);

        for (GolfTeeLocation location : locationManager.getGameTees()) {
            location.getLocation().getBlock().setType(Material.STONE_BRICK_SLAB);
        }

        for (GolfHoleLocation location : locationManager.getGameHoles()) {
            if (location.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) location.getLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.IRON_BARS);
            location.getLocation().getBlock().setType(Material.ORANGE_WOOL);
        }

        for (GolfPlayer golfPlayer : golfers) {
            Player player = golfPlayer.getGolfer();

            player.teleport(locationManager.getTeeStart().getLocation());
            player.getInventory().clear();
            PluginData.getMessageUtil().sendInfoMessage(player, "Golf game starts in " + ChatColor.GREEN + "15 " + ChatColor.AQUA + "seconds...");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (readySeconds > 0) {
                    readySeconds--;

                    for (GolfPlayer golfPlayer : golfers) {
                        golfPlayer.getGolfer().setLevel(readySeconds);
                    }

                    if (readySeconds == 0) {
                        cancel();

                        for (GolfPlayer golfPlayer : golfers) {
                            TitleUtil.showTitle(golfPlayer.getGolfer(), "", ChatColor.GOLD + "Golf game has started!", 0, 30, 60);
                        }

                        start();
                    }
                }
            }
        }.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 0, 20);
    }

    private void start() {
        started = true;

        GolfPlayer golfer = golfers.get(new Random().nextInt(golfers.size()));

        for (GolfPlayer golfPlayer : golfers) {
            Player player = golfPlayer.getGolfer();

            ((GolfGameScoreboard) getBoard()).addPlayer(player.getName());

            ItemStack bow = new ItemStack(Material.BOW);
            ItemMeta bowMeta = bow.getItemMeta();
            bowMeta.setDisplayName(ChatColor.GOLD + "Golfclub");
            bowMeta.addEnchant(Enchantment.DURABILITY, 3, true);
            bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            bow.setItemMeta(bowMeta);

            ItemStack arrow = new ItemStack(Material.ARROW);
            ItemMeta arrowMeta = arrow.getItemMeta();
            arrowMeta.setDisplayName(ChatColor.GOLD + "Golfball");
            arrow.setItemMeta(arrowMeta);

            ItemStack potato = new ItemStack(Material.POTATO, 5);
            ItemMeta potatoMeta = arrow.getItemMeta();
            potatoMeta.setDisplayName(ChatColor.GOLD + "Golf Snacks");
            potato.setItemMeta(potatoMeta);

            player.setFoodLevel(20);
            player.setHealth(20);
            player.setFireTicks(0);
            player.getInventory().setItem(0, bow);
            player.getInventory().setItem(1, arrow);
            player.getInventory().setItem(8, potato);

            PluginData.getMessageUtil().sendInfoMessage(player, ChatColor.GREEN + golfer.getGolfer().getName() + ChatColor.AQUA + " starts with the first hole.");
        }

        setGolfing(golfer.getGolfer());
        ((GolfGameScoreboard) getBoard()).showHole();
    }

    private void nextGolfer() {
        new BukkitRunnable() {
            int seconds = 5;

            @Override
            public void run() {
                if (seconds > 0) {
                    seconds --;

                    for (GolfPlayer golfPlayer : golfers) {
                        golfPlayer.getGolfer().setLevel(seconds);
                    }

                    if (seconds == 0) {
                        cancel();

                        if (notFinished.isEmpty()) {
                            nextHole();
                        } else {
                            int random = new Random().nextInt(notFinished.size());
                            Player nextGolfer = notFinished.get(random);

                            if (nextGolfer.getName().equals(golfer.getGolfer().getName())) {
                                if (notFinished.size() > 1) {
                                    if (notFinished.size() > (random + 1)) {
                                        nextGolfer = notFinished.get(random + 1);
                                    } else if (notFinished.size() < (random - 1)) {
                                        nextGolfer = notFinished.get(random - 1);
                                    }
                                }
                            }

                            setGolfing(nextGolfer);
                        }
                    }
                }
            }
        }.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 0, 20);
    }

    private void setGolfing(Player player) {
        golfer = getGolfPlayer(player);

        for (GolfPlayer golfPlayer : golfers) {
            golfPlayer.setShot(false);

            if (golfPlayer.getGolfer().getName().equalsIgnoreCase(player.getName())) {
                PluginData.getMessageUtil().sendInfoMessage(player, ChatColor.AQUA + "You are the one golfing.");

                golfPlayer.getGolfer().setGameMode(GameMode.ADVENTURE);
                ((GolfGameScoreboard) getBoard()).showGolfer(golfPlayer);

                if (notFinished.contains(golfPlayer.getGolfer())) {
                    if (round == 1) {
                        if (golfPlayer.getArrowLocation() == null) {
                            if (hole == 1) {
                                golfPlayer.getGolfer().teleport(locationManager.getGameTees().getFirst().getLocation());
                            } else if (hole == 9 || hole ==  18|| hole == 27 || hole == 14 || hole == 20) {
                                golfPlayer.getGolfer().teleport(locationManager.getGameTees().getLast().getLocation());
                            } else {
                                golfPlayer.getGolfer().teleport(locationManager.getGameTees().get(hole - 1).getLocation());
                            }
                        } else {
                            golfPlayer.getGolfer().teleport(golfPlayer.getArrowLocation());
                        }
                    } else if (round > 1) {
                        if (golfPlayer.getArrowLocation() != null) {
                            golfPlayer.getGolfer().teleport(golfPlayer.getArrowLocation());
                        } else {
                            if (hole == 1) {
                                golfPlayer.getGolfer().teleport(locationManager.getGameTees().getFirst().getLocation());
                            } else if (hole == 9 || hole ==  18|| hole == 27 || hole == 14 || hole == 20) {
                                golfPlayer.getGolfer().teleport(locationManager.getGameTees().getLast().getLocation());
                            } else {
                                golfPlayer.getGolfer().teleport(locationManager.getGameTees().get(hole - 1).getLocation());
                            }
                        }
                    }
                }
            } else {
                golfPlayer.getGolfer().setGameMode(GameMode.SPECTATOR);
                PluginData.getMessageUtil().sendInfoMessage(golfPlayer.getGolfer(), ChatColor.GREEN + player.getName() + ChatColor.AQUA + " is golfing.");
            }
        }
    }

    private void nextHole() {
        round = 1;
        shots = 0;
        notFinished.clear();

        for (GolfPlayer golfPlayer : golfers) {
            notFinished.add(golfPlayer.getGolfer());
            golfPlayer.setArrowLocation(null);
            golfPlayer.setArrowBlockMaterial(null);
        }

        if (!(hole == 9 || hole == 18 || hole == 27 || hole == 14 || hole == 20)) {
            for (GolfPlayer golfPlayer : golfers) {
                showScore(golfPlayer);
                ((GolfGameScoreboard) getBoard()).addScoresPlayer(golfPlayer.getGolfer().getName(), golfPlayer.getShots());

                golfPlayer.setShots(0);
                ((GolfGameScoreboard) getBoard()).resetGolferScore(golfPlayer);
            }
        } else {
            for (GolfPlayer golfPlayer : golfers) {
                golfPlayer.getGolfer().playSound( golfPlayer.getGolfer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, .6f);
                PluginData.getMessageUtil().sendInfoMessage(golfPlayer.getGolfer(), ChatColor.GOLD + getWinner().getName() + ChatColor.AQUA + " is the winner!");
                TitleUtil.showTitle(golfPlayer.getGolfer(), ChatColor.GOLD + getWinner().getName(), ChatColor.AQUA + "is the winner!", 0, 20 * 5, 20 * 3);

                if (golfPlayer.getGolfer().getName().equalsIgnoreCase(getWinner().getName())) {
                    ((GolfGameScoreboard) getBoard()).addFinishedPlayer(ChatColor.GOLD + golfPlayer.getGolfer().getName(), golfPlayer.getPoints());
                } else {
                    ((GolfGameScoreboard) getBoard()).addFinishedPlayer(golfPlayer.getGolfer().getName(), golfPlayer.getPoints());
                }

                ((GolfGameScoreboard) getBoard()).showFinished();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        end(golfPlayer.getGolfer());
                    }
                }.runTaskLater(MiniGamesPlugin.getPluginInstance(), 20 * 3);
            }

            return;
        }

        ((GolfGameScoreboard) getBoard()).showScores();
        hole ++;

        for (GolfPlayer golfPlayer : golfers) {
            Player player = golfPlayer.getGolfer();

            player.setGameMode(GameMode.SPECTATOR);

            if (hole == 9 || hole ==  18|| hole == 27 || hole == 14 || hole == 20) {
                player.teleport(locationManager.getGameTees().getLast().getLocation());
            } else {
                player.teleport(locationManager.getGameTees().get(hole - 1).getLocation());
            }

            PluginData.getMessageUtil().sendInfoMessage(player,
                    "All golfers reached the hole, hole " + ChatColor.GREEN + hole + ChatColor.AQUA + " starts in " + ChatColor.GREEN + "5 " + ChatColor.AQUA + "seconds...");
        }

        new BukkitRunnable() {
            int seconds = 6;

            @Override
            public void run() {
                if (seconds > 0) {
                    seconds--;

                    for (GolfPlayer golfPlayer : golfers) {
                        golfPlayer.getGolfer().setLevel(seconds);
                    }

                    if (seconds == 0) {
                        cancel();

                        for (GolfPlayer golfPlayer : golfers) {
                            if (hole != 1) {
                                if (hole == 9 || hole ==  18|| hole == 27 || hole == 14 || hole == 20) {
                                    PluginData.getMessageUtil().sendInfoMessage(golfPlayer.getGolfer(), ChatColor.GREEN + golfers.get(0).getGolfer().getName() + ChatColor.AQUA + " starts with the last hole.");
                                } else {
                                    PluginData.getMessageUtil().sendInfoMessage(golfPlayer.getGolfer(), ChatColor.GREEN + golfers.get(0).getGolfer().getName() + ChatColor.AQUA + " starts with hole " + ChatColor.GREEN + hole);
                                }
                            }
                        }

                        int random = new Random().nextInt(notFinished.size());
                        Player nextGolfer = notFinished.get(random);

                        setGolfing(nextGolfer);
                        ((GolfGameScoreboard) getBoard()).showHole();
                    }
                }
            }
        }.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 0, 20);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Block hitBlock = event.getHitBlock();

        if (event.getEntityType().equals(EntityType.ARROW)) {
            Arrow arrow = (Arrow) event.getEntity();
            ProjectileSource projectileSource = arrow.getShooter();

            if (projectileSource instanceof Player) {
                Player shooter = (Player) projectileSource;

                if (PluginData.isInGame(shooter) && started) {
                    shots++;
                    arrow.remove();

                    GolfPlayer golfPlayer = getGolfPlayer(shooter);

                    if (golfPlayer.isShot()) {
                        PluginData.getMessageUtil().sendErrorMessage(shooter, "You already used your golfclub ones, wait till your next turn.");
                    } else {
                        if (!golfer.getGolfer().getName().equals(golfPlayer.getGolfer().getName())) return;

                        golfPlayer.setShots(golfPlayer.getShots() + 1);
                        golfPlayer.setShot(true);

                        ((GolfGameScoreboard) getBoard()).showShots(golfPlayer);
                        ((GolfGameScoreboard) getBoard()).removeGolfer(golfPlayer);

                        if (hitBlock.getType().equals(Material.ORANGE_WOOL)) {
                            if (!notFinished.contains(shooter)) {
                                PluginData.getMessageUtil().sendErrorMessage(shooter, "You already reached the hole.");

                                return;
                            } else {
                                golfPlayer.getGolfer().playSound(golfPlayer.getGolfer().getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
                                golfPlayer.getGolfer().spawnParticle(Particle.VILLAGER_HAPPY, hitBlock.getLocation(), 10, 1, 1, 1);

                                for (GolfPlayer golfPlayer1 : golfers) {
                                    PluginData.getMessageUtil().sendInfoMessage(golfPlayer1.getGolfer(), ChatColor.GREEN + shooter.getName() + ChatColor.AQUA + " reached the hole.");
                                }

                                notFinished.remove(shooter);

                                if (golfPlayer.getGolfer().getName().equalsIgnoreCase(shooter.getName())) {
                                    if (golfPlayer.getArrowBlockMaterial() != null && golfPlayer.getArrowLocation() != null) {
                                        golfPlayer.getArrowLocation().getBlock().setType(golfPlayer.getArrowBlockMaterial());
                                    }
                                }
                            }
                        } else if (hitBlock.getType().equals(Material.WATER)
                                || hitBlock.getType().equals(Material.ACACIA_LEAVES)
                                || hitBlock.getType().equals(Material.BIRCH_LEAVES)
                                || hitBlock.getType().equals(Material.DARK_OAK_LEAVES)
                                || hitBlock.getType().equals(Material.JUNGLE_LEAVES)
                                || hitBlock.getType().equals(Material.OAK_LEAVES)
                                || hitBlock.getType().equals(Material.SPRUCE_LEAVES)
                                || hitBlock.getType().equals(Material.IRON_BARS)
                                || hitBlock.getRelative(BlockFace.UP).getType().equals(Material.IRON_BARS)) {

                            PluginData.getMessageUtil().sendErrorMessage(shooter, "Out of bound, try again.");
                            setGolfing(shooter);
                            return;
                        } else {
                            Block blockAbove = hitBlock.getRelative(BlockFace.UP);

                            if (golfPlayer.getArrowBlockMaterial() != null && golfPlayer.getArrowLocation() != null) {
                                golfPlayer.getArrowLocation().getBlock().setType(golfPlayer.getArrowBlockMaterial());
                            }

                            golfPlayer.setArrowLocation(blockAbove.getLocation());
                            golfPlayer.setArrowBlockMaterial(blockAbove.getType());

                            blockAbove.setType(Material.PLAYER_HEAD);

                            Skull skull = (Skull) blockAbove.getState();

                            // Using deprecated 'setOwner' method here, will probably be changed in the future
                            skull.setOwner(shooter.getName());
                            skull.update();
                        }

                        if (shots == golfers.size()) {
                            round++;
                        }

                        nextGolfer();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if(PluginData.isInGame(player) && ready) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (PluginData.isInGame((Player) event.getEntity()) && ready) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if(PluginData.isInGame(event.getPlayer()) && ready) event.setCancelled(true);
    }

    private void showScore(GolfPlayer golfPlayer) {
        String scoreType;
        int difference = -locationManager.getGameHoles().get(hole - 1).getPar() + golfPlayer.getShots();

        golfPlayer.setPoints(golfPlayer.getPoints() + difference);

        switch (difference) {
            case 3:
                scoreType = "Triple Bogey";

                break;
            case 2:
                scoreType = "Double Bogey";

                break;
            case 1:
                scoreType = "Bogey";

                break;
            case 0:
                scoreType = "Par";

                break;
            case -1:
                scoreType = "Birdie";

                break;
            case -2:
                scoreType = "Eagle";

                break;
            case -3:
                scoreType = "Albatross";

                break;
            case -4:
                scoreType = "Condor";

                break;
            default:
                scoreType = "" + difference;

                break;
        }

        TitleUtil.showTitle(golfPlayer.getGolfer(), ChatColor.GOLD + scoreType, ChatColor.AQUA + "Current points: " + golfPlayer.getPoints(), 0, 30, 60);
    }

    private Player getWinner() {
        int lowest = 0;

        for (GolfPlayer golfPlayer : golfers) {
            int points = golfPlayer.getPoints();

            if (points < lowest) lowest = points;
        }

        for (GolfPlayer golfPlayer : golfers) {
            if (golfPlayer.getPoints() == lowest) return golfPlayer.getGolfer();
        }

        return null;
    }

    private GolfPlayer getGolfPlayer(Player player) {
        for (GolfPlayer golfPlayer : getGolfers()) {
            if (golfPlayer.getGolfer().getName().equals(player.getName())) {
                return golfPlayer;
            }
        }

        return null;
    }

    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);

        forceTeleport(player, getWarp());
        player.setGameMode(GameMode.ADVENTURE);

        golfers.add(new GolfPlayer(player));
        notFinished.add(player);
    }

    @Override
    public void end(Player sender) {
        for (GolfPlayer golfPlayer : getGolfers()) {
            golfPlayer.getGolfer().teleport(locationManager.getGameTees().getFirst().getLocation());
            golfPlayer.getGolfer().getInventory().clear();
            golfPlayer.getGolfer().setGameMode(GameMode.SURVIVAL);
            golfPlayer.setShot(false);
        }

        for (GolfTeeLocation location : locationManager.getGameTees()) {
            location.getLocation().getBlock().setType(Material.AIR);
        }

        for (GolfHoleLocation location : locationManager.getGameHoles()) {
            if (location.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.IRON_BARS)) location.getLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.AIR);
            location.getLocation().getBlock().setType(Material.AIR);
        }

        notFinished.clear();
        golfers.clear();

        super.end(sender);
    }

    @Override
    public void removePlayer(OfflinePlayer player) {
        super.removePlayer(player);
        ((GolfGameScoreboard) getBoard()).removePlayer(player.getName());

        golfers.remove(new GolfPlayer((Player) player));
    }

    @Override
    public boolean joinAllowed() {
        return super.joinAllowed() && !ready && !(getPlayers().size() >= 6);
    }

    @Override
    public String getGameChatTag(Player player) {
        if(PluginData.isManager(player)) {
            return ChatColor.DARK_AQUA + "<Manager ";
        }
        else {
            return ChatColor.BLUE + "<Golfer: ";
        }
    }

    public boolean hasTeeStart() { return locationManager.getTeeStart() != null; }

    public boolean hasTeeEnd() { return locationManager.getTeeEnd() != null; }

    public boolean hasHoleStart() { return locationManager.getHoleStart() != null; }

    public boolean hasHoleEnd() { return locationManager.getHoleEnd() != null; }

    public boolean hasEnoughTees() {
        int teeCount = locationManager.getTeeCount();

        return teeCount == 9 || teeCount ==  18|| teeCount == 27 || teeCount == 14 || teeCount == 20;
    }

    public boolean hasEnoughHoles() {
        int holeCount = locationManager.getHoleCount();

        return holeCount == 9 || holeCount ==  18|| holeCount == 27 || holeCount == 14 || holeCount == 20;
    }
}
