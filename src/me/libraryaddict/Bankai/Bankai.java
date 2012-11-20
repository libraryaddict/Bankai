package me.libraryaddict.Bankai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Bankai extends JavaPlugin implements Listener {
  List<Ichigo> bankaiUsers = new ArrayList<Ichigo>();
  Bankai main;

  public void onEnable() {
    main = this;
    Bukkit.getPluginManager().registerEvents(this, this);
    Bukkit.getServer().getScheduler()
        .scheduleSyncRepeatingTask(this, new Runnable() {
          public void run() {
            List<Ichigo> remove = new ArrayList<Ichigo>();
            for (Ichigo ichy : bankaiUsers) {
              Player p = Bukkit.getPlayerExact(ichy.getPlayer());
              if (p == null) {
                remove.add(ichy);
                continue;
              }
              ichy.decreaseBankai();
              ichy.decreaseHallow();
              ichy.decreasePowerwave();
              if (ichy.getHallowTime() <= 0 && ichy.getHallow()) {
                Bukkit.broadcastMessage(ChatColor.DARK_RED + p.getName()
                    + "'s mask breaks away..");
                ichy.setHallow(false);
              }
              if ((ichy.getHallow() || ichy.getBankaiTime() % 1 == 0)
                  && p.getHealth() < 20)
                p.setHealth(p.getHealth() + 1);
              if (ichy.getPowerwave() == 0)
                p.sendMessage(ChatColor.DARK_RED
                    + "Your Getsuga Tenshou is ready!");
              if (ichy.getHallowTime() <= 0 && ichy.getHallow()) {
                Bukkit.broadcastMessage(ChatColor.DARK_RED + p.getName()
                    + "'s mask breaks away..");
                ichy.setHallow(false);
              }
              if (ichy.getBankaiTime() <= 0) {
                remove.add(ichy);
                p.getWorld().strikeLightningEffect(p.getLocation());
                Bukkit.broadcastMessage(ChatColor.DARK_RED
                    + "The dark forces surrounding " + ichy.getPlayer()
                    + " disappear..");
                continue;
              } else {
                if (new Random().nextInt(3) == 1)
                  p.getWorld().playEffect(p.getEyeLocation(),
                      Effect.MOBSPAWNER_FLAMES, 0);
                if (!ichy.getHallow())
                  p.addPotionEffect(new PotionEffect(
                      PotionEffectType.REGENERATION, 10, 0), true);
                else
                  p.addPotionEffect(new PotionEffect(
                      PotionEffectType.REGENERATION, 10, 5), true);
                p.addPotionEffect(new PotionEffect(
                    PotionEffectType.FIRE_RESISTANCE, 10, 0), true);
                if (!ichy.getHallow())
                  p.addPotionEffect(new PotionEffect(
                      PotionEffectType.INCREASE_DAMAGE, 10, 2), true);
                else
                  p.addPotionEffect(new PotionEffect(
                      PotionEffectType.INCREASE_DAMAGE, 10, 5), true);
                if (ichy.getHallow())
                  p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10,
                      0), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10,
                    2), true);
                if (ichy.getBankaiTime() % 1 == 0) {
                  Location loc = p.getLocation();
                  int z = -2;
                  int x = -2;
                  for (int i = 0; i < 25; i++) {
                    Location newLoc = new Location(loc.getWorld(), loc.getX()
                        + x, loc.getY(), loc.getZ() + z);
                    for (int direction = 0; direction < 8; direction++) {
                      loc.getWorld()
                          .playEffect(newLoc, Effect.SMOKE, direction);
                    }
                    if (x == 2) {
                      x = 0;
                      z++;
                    }
                    x++;
                  }
                }
              }
            }
            bankaiUsers.removeAll(remove);
          }
        }, 0, 1L);
  }

  Ichigo getIchigo(String name) {
    for (Ichigo ichy : bankaiUsers) {
      if (ichy.getPlayer().equals(name))
        return ichy;
    }
    return null;
  }

  @EventHandler
  public void onDamage(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player) {
      Player p = (Player) event.getEntity();
      Ichigo ichy = getIchigo(p.getName());
      if (ichy != null)
        event.setDamage(Math.round((event.getDamage() / 3) * 2));
    }
  }

  @EventHandler
  public void playerInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR)
      return;
    Player p = event.getPlayer();
    Ichigo ichy = getIchigo(p.getName());
    if (ichy == null)
      return;
    if (ichy.getPowerwave() > 0)
      return;
    if (p.getItemInHand().getType() != Material.DIAMOND_SWORD)
      return;
    Bukkit.broadcastMessage(ChatColor.DARK_RED + ichy.getPlayer()
        + " screams Getsuga Tenshou!");
    final Vector direction = p.getEyeLocation().getDirection().multiply(2);
    final Fireball fireball = p.getWorld().spawn(
        p.getEyeLocation().add(direction.getX(), direction.getY(),
            direction.getZ()), Fireball.class);
    fireball.setShooter(p);
    fireball.setIsIncendiary(true);
    if (ichy.getHallow())
      fireball.setYield(20);
    else
      fireball.setYield(10);
    ichy.setPowerwave(17);
  }

  @EventHandler
  public void onDeath(EntityDeathEvent event) {
    if (event.getEntity() instanceof Player) {
      Player p = (Player) event.getEntity();
      Ichigo ichy = getIchigo(p.getName());
      if (ichy != null)
        bankaiUsers.remove(ichy);
    }
  }

  public boolean onCommand(CommandSender sender, Command cmd,
      String commandLabel, String[] args) {
    Player player = Bukkit.getPlayerExact(sender.getName());
    if (player == null)
      return true;
    if (cmd.getName().equalsIgnoreCase("bankai")) {
      if (player.isOp() || player.hasPermission("libraryaddict.bankai")) {
        if (args.length == 1
            && (player.isOp() || player
                .hasPermission("libraryaddict.bankaiothers"))) {
          player = Bukkit.getPlayer(args[0]);
          if (player == null) {
            sender
                .sendMessage(ChatColor.RED + "That player is not in the game");
            return true;
          } else
            player.sendMessage(ChatColor.RED + sender.getName()
                + " just helped you achieve bankai!");
        }
        final Player p = player;
        Ichigo ichy = getIchigo(player.getName());
        if (ichy == null) {
          player.addPotionEffect(
              new PotionEffect(PotionEffectType.SLOW, 60, 2), true);
          Bukkit.broadcastMessage("<" + p.getDisplayName() + ChatColor.RESET
              + "> " + ChatColor.DARK_RED + "Ban-");
          Bukkit.getServer().getScheduler()
              .scheduleSyncDelayedTask(this, new Runnable() {
                public void run() {
                  Bukkit.broadcastMessage(ChatColor.DARK_RED + "* "
                      + ChatColor.stripColor(p.getDisplayName())
                      + ChatColor.RESET + ChatColor.DARK_RED + " grunts");
                }
              }, 20L);
          Bukkit.getServer().getScheduler()
              .scheduleSyncDelayedTask(this, new Runnable() {
                public void run() {
                  Bukkit.broadcastMessage("<" + p.getDisplayName()
                      + ChatColor.RESET + "> " + ChatColor.DARK_RED + "-kai!");
                  if (new Random().nextInt(5) != 1) {
                    Bukkit.broadcastMessage(ChatColor.DARK_RED
                        + "Dark forces envelope " + p.getName() + "!");
                    p.setHealth(20);
                    p.setFoodLevel(20);
                    for (int n = 0; n <= 3; n++) {
                      Bukkit.getServer().getScheduler()
                          .scheduleSyncDelayedTask(main, new Runnable() {
                            public void run() {
                              p.getWorld().strikeLightningEffect(
                                  p.getLocation());
                            }
                          }, n * 2L);
                    }
                    Ichigo ichy = new Ichigo(p.getName());
                    bankaiUsers.add(ichy);
                  } else {
                    Bukkit.broadcastMessage(ChatColor.DARK_RED
                        + "Dark forces envelope " + p.getName() + "!");
                    Bukkit
                        .broadcastMessage(ChatColor.DARK_RED
                            + "The dark forces suddenly reacts violently and attacks "
                            + p.getName() + "!");
                    p.damage(1000000);
                  }
                }
              }, 40L);
        } else
          sender.sendMessage(ChatColor.RED + "Already bankai bro!");
      }
    }
    if (cmd.getName().equalsIgnoreCase("hallow")) {
      final Ichigo ichy = getIchigo(sender.getName());
      if (ichy == null) {
        sender.sendMessage(ChatColor.RED + "You are not in bankai state");
        return true;
      }
      if (ichy.getHallow()) {
        sender.sendMessage(ChatColor.RED + "Already doing hallow state!");
        return true;
      }
      Bukkit.broadcastMessage(ChatColor.DARK_RED + sender.getName()
          + " drags his hand across his face! A hallow mask appears!");
      player.getWorld().strikeLightning(player.getLocation());
      if (new Random().nextInt(3) == 1 || ichy.getBankaiTime() < 10
          || ichy.hasHallowed()) {
        final Player p = player;
        Bukkit.getServer().getScheduler()
            .scheduleSyncDelayedTask(main, new Runnable() {
              public void run() {
                Bukkit.broadcastMessage(ChatColor.DARK_RED
                    + p.getName()
                    + " suddenly loses control of his mask and it tears his face off!");
                bankaiUsers.remove(ichy);
                p.damage(999999);
              }
            }, 20L);
        return true;
      } else {
        ichy.setHallowTime(10);
        ichy.setHallow(true);
      }
    }
    return true;
  }
}
