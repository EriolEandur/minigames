package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.golf.GolfLocationManager;
import com.mcmiddleearth.minigames.golf.GolfPlayer;
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
    @Getter private int readySeconds, hole, par, round, shots;

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
        par = 3;
        round = 1;
        shots = 0;

        ((GolfGameScoreboard) getBoard()).init(this);
    }

    public void ready() {
        ready = true;

        for (GolfPlayer golfPlayer : golfers) {
            Player player = golfPlayer.getGolfer();

            player.teleport(locationManager.getTeeStart().getLocation());
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();
            PluginData.getMessageUtil().sendInfoMessage(player, "Golf course starts in " + ChatColor.GREEN + "15 " + ChatColor.AQUA + "seconds...");
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
                            TitleUtil.showTitle(golfPlayer.getGolfer(), "", ChatColor.GOLD + "Golf course has started!", 0, 30, 60);
                        }

                        start();
                    }
                }
            }
        }.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 0, 20);
    }

    private void start() {
        started = true;

        for (GolfPlayer golfPlayer : golfers) {
            Player player = golfPlayer.getGolfer();

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

            player.setFoodLevel(20);
            player.setHealth(20);
            player.setFireTicks(0);
            player.getInventory().setItem(0, bow);
            player.getInventory().setItem(1, arrow);

            PluginData.getMessageUtil().sendInfoMessage(player, ChatColor.GREEN + golfers.get(0).getGolfer().getName() + ChatColor.AQUA + " starts with the first hole.");
        }

        setGolfing(golfers.get(0).getGolfer());
        ((GolfGameScoreboard)getBoard()).showHole();
    }

    private void nextGolfer() {
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

                        if (notFinished.isEmpty()) {
                            nextHole();
                        } else {
                            setGolfing(notFinished.get(new Random().nextInt(notFinished.size())));
                        }
                    }
                }
            }
        }.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 0, 20);
    }

    private void setGolfing(Player player) {
        for (GolfPlayer golfPlayer : golfers) {
            golfPlayer.setShot(false);

            if (golfPlayer.getGolfer().getName().equalsIgnoreCase(player.getName())) {
                PluginData.getMessageUtil().sendInfoMessage(golfPlayer.getGolfer(),
                        ChatColor.GREEN + golfPlayer.getGolfer().getName() + ChatColor.AQUA + " is golfing.");

                golfPlayer.getGolfer().setGameMode(GameMode.ADVENTURE);
                ((GolfGameScoreboard) getBoard()).showGolfer(golfPlayer);

                if (notFinished.contains(golfPlayer.getGolfer())) {
                    if (round == 1) {
                        if (golfPlayer.getArrowLocation() == null) {
                            if (hole == 1) {
                                golfPlayer.getGolfer().teleport(locationManager.getTeeStart().getLocation());
                            } else if (hole == 9 || hole ==  18|| hole == 27 || hole == 14 || hole == 20) {
                                golfPlayer.getGolfer().teleport(locationManager.getTeeEnd().getLocation());
                            } else {
                                golfPlayer.getGolfer().teleport(locationManager.getTees().get(hole - 2).getLocation());
                            }
                        } else {
                            golfPlayer.getGolfer().teleport(golfPlayer.getArrowLocation());
                        }
                    } else if (round > 1) {
                        if (golfPlayer.getArrowLocation() != null) {
                            golfPlayer.getGolfer().teleport(golfPlayer.getArrowLocation());
                        } else {
                            if (hole == 1) {
                                golfPlayer.getGolfer().teleport(locationManager.getTeeStart().getLocation());
                            } else if (hole == 9 || hole ==  18|| hole == 27 || hole == 14 || hole == 20) {
                                golfPlayer.getGolfer().teleport(locationManager.getTeeEnd().getLocation());
                            } else {
                                golfPlayer.getGolfer().teleport(locationManager.getTees().get(hole - 2).getLocation());
                            }
                        }
                    }
                }
            } else {
                golfPlayer.getGolfer().setGameMode(GameMode.SPECTATOR);
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
            hole++;

            for (GolfPlayer golfPlayer : golfers) {
                showScore(golfPlayer);
                ((GolfGameScoreboard) getBoard()).addScoresPlayer(golfPlayer.getGolfer().getName(), golfPlayer.getShots());

                golfPlayer.setShots(0);
                ((GolfGameScoreboard) getBoard()).resetGolferScore(golfPlayer);
            }
        } else {
            for (final GolfPlayer golfPlayer : golfers) {
                golfPlayer.getGolfer().playSound( golfPlayer.getGolfer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, .6f);
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
                }.runTaskLater(MiniGamesPlugin.getPluginInstance(), 20 * 15);
            }

            return;
        }

        ((GolfGameScoreboard) getBoard()).showScores();

        for (GolfPlayer golfPlayer : golfers) {
            Player player = golfPlayer.getGolfer();

            player.setGameMode(GameMode.SPECTATOR);

            if (hole == 9 || hole ==  18|| hole == 27 || hole == 14 || hole == 20) {
                player.teleport(locationManager.getTeeEnd().getLocation());
            } else {
                player.teleport(locationManager.getTees().get(hole - 2).getLocation());
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

                        setGolfing(getWinner());
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

                if(PluginData.isInGame(shooter) && started) {
                    shots++;
                    arrow.remove();

                    for (GolfPlayer golfPlayer : golfers) {
                        if (golfPlayer.getGolfer().getName().equalsIgnoreCase(shooter.getName())) {
                            if (golfPlayer.isShot()) {
                                PluginData.getMessageUtil().sendErrorMessage(shooter, "You already used your golfclub ones, wait till your next turn.");

                                return;
                            } else {
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
                                        PluginData.getMessageUtil().sendInfoMessage(golfPlayer.getGolfer(), ChatColor.GREEN + shooter.getName() + ChatColor.AQUA + " reached the hole.");

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
                                        || hitBlock.getType().equals(Material.SPRUCE_LEAVES)) {
                                    PluginData.getMessageUtil().sendErrorMessage(shooter, "Out of bound, try again.");
                                    setGolfing(shooter);
                                    return;
                                } else {
                                    Block blockAbove = hitBlock.getRelative(BlockFace.UP);

                                    if (golfPlayer.getGolfer().getName().equalsIgnoreCase(shooter.getName())) {
                                        if (golfPlayer.getArrowBlockMaterial() != null && golfPlayer.getArrowLocation() != null) {
                                            golfPlayer.getArrowLocation().getBlock().setType(golfPlayer.getArrowBlockMaterial());
                                        }

                                        golfPlayer.setArrowLocation(blockAbove.getLocation());
                                        golfPlayer.setArrowBlockMaterial(blockAbove.getType());
                                    }

                                    blockAbove.setType(Material.PLAYER_HEAD);

                                    Skull skull = (Skull) blockAbove.getState();

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
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(PluginData.isInGame((Player) event.getEntity()) && ready) event.setCancelled(true);
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
        String scoreType = "";
        int difference = -par + golfPlayer.getShots();

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

        TitleUtil.showTitle(golfPlayer.getGolfer(), ChatColor.GOLD + scoreType, ChatColor.AQUA + "You finished this hole!", 0, 30, 60);
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

    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);
        forceTeleport(player, getWarp());
        ((GolfGameScoreboard) getBoard()).addPlayer(player.getName());

        golfers.add(new GolfPlayer(player));
        notFinished.add(player);
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
