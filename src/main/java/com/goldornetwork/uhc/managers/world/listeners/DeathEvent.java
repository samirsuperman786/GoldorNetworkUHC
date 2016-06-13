package com.goldornetwork.uhc.managers.world.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.events.GameEndEvent;
import com.goldornetwork.uhc.managers.world.events.UHCDeathEvent;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;


public class DeathEvent implements Listener {

	//instances
	private UHC plugin;
	private TeamManager teamM;
	private Map<UUID, BukkitTask> afkCheck = new HashMap<UUID, BukkitTask>();
	private List<UUID> toKill = new ArrayList<UUID>();
	private int afkTimeTillKill;
	private Random random = new Random();

	public DeathEvent(UHC plugin, TeamManager teamM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin=plugin;
		plugin.getConfig().addDefault("AFK-TIME-TILL-KILL", 5);
		plugin.saveConfig();
		this.teamM=teamM;
		this.afkTimeTillKill=plugin.getConfig().getInt("AFK-TIME-TILL-KILL");
	}

	/**
	 * Handles the death of the player and removes them from an in-game state. 
	 * @param p - player who has just died
	 */
	public void playerDied(OfflinePlayer p, Location loc){
		String team = teamM.getTeamOfPlayer(p.getUniqueId());
		teamM.removePlayerFromTeam(p.getUniqueId());
		if(teamM.isTeamsEnabled()){
			if(teamM.isPlayerOwner(team, p.getUniqueId())){
				teamM.removePlayerFromOwner(team, p.getUniqueId());
				if(teamM.getPlayersOnATeam(team).isEmpty()==false){
					OfflinePlayer newOwner = Bukkit.getServer().getOfflinePlayer(teamM.getPlayersOnATeam(team).get(random.nextInt(teamM.getPlayersOnATeam(team).size())));
					teamM.addPlayerToOwner(team, newOwner.getUniqueId());
					if(newOwner.isOnline()){
						Player onlineNewOwner = (Player) newOwner;
						MessageSender.alertMessage(onlineNewOwner, "You have been granted ownership of team " + teamM.getColorOfTeam(team) + teamM.getTeamNameProper(team));
					}
				}
			}


			if(teamM.getPlayersOnATeam(team).isEmpty()){
				teamM.disbandTeam(team);
				if(teamM.getActiveTeams().size()==1){
					String winner = teamM.getActiveTeams().get(0);
					plugin.getServer().getPluginManager().callEvent(new GameEndEvent(teamM.getPlayersOnATeam(winner)));
				}
			}

		}
		loc.getWorld().strikeLightningEffect(loc);

	}

	@EventHandler
	public void on(PlayerJoinEvent e){
		Player target = e.getPlayer();
		if(afkCheck.containsKey(target.getUniqueId())){
			afkCheck.get(target.getUniqueId()).cancel();
			afkCheck.remove(target.getUniqueId());
		}
		if(toKill.contains(target.getUniqueId())){
			toKill.remove(target.getUniqueId());
			MessageSender.alertMessage(target, "You were gone for more than " + ChatColor.GRAY + afkTimeTillKill + ChatColor.GOLD + " minutes so you were removed.");
		}
	}
	@EventHandler
	public void on(PlayerQuitEvent e){
		OfflinePlayer target = e.getPlayer();
		if(teamM.isPlayerInGame(target.getUniqueId())){
			BukkitTask afkCheck = new BukkitRunnable() {

				@Override
				public void run() {
					if(target.isOnline()==false){
						if(teamM.isPlayerInGame(target.getUniqueId())){
							playerDied(target, target.getPlayer().getLocation());
							toKill.add(target.getUniqueId());
							for(Player online : Bukkit.getServer().getOnlinePlayers()){
								online.sendMessage(ChatColor.GRAY + target.getName() + " was removed from the game for not returning after 5 minutes.");
							}
							ItemStack[] items = target.getPlayer().getInventory().getContents();
							for(ItemStack toDrop : items){
								if(toDrop != null){
									target.getPlayer().getWorld().dropItemNaturally(target.getPlayer().getLocation(), toDrop);
								}
							}
							ItemStack[] armor = target.getPlayer().getInventory().getArmorContents();
							for(ItemStack toDrop : armor){
								if(toDrop != null && (toDrop.getType()!=Material.AIR)){
									target.getPlayer().getWorld().dropItemNaturally(target.getPlayer().getLocation(), toDrop);
								}
							}

						}

					}
					DeathEvent.this.afkCheck.remove(target.getUniqueId());

				}
			}.runTaskLater(plugin, (afkTimeTillKill * 60 * 20));
			this.afkCheck.put(target.getUniqueId(), afkCheck);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		Location loc = p.getLocation();
		p.setHealth(p.getMaxHealth());
		teamM.addPlayerToObservers(p);
		p.setHealth(20);
		if(teamM.isPlayerInGame(p.getUniqueId())){
			if(State.getState().equals(State.INGAME) || State.getState().equals(State.SCATTER)){
				plugin.getServer().getPluginManager().callEvent(new UHCDeathEvent(p));
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						playerDied(p, loc);
						
					}
				}.runTaskLater(plugin, 5L);
				
				e.setDeathMessage(ChatColor.GRAY + e.getDeathMessage());
			}
			else{
				e.setDeathMessage(null);
			}
		}
		else{
			e.setDeathMessage(null);
		}
	}

	private void deathMsgHandler(PlayerDeathEvent e){
		Player target = e.getEntity();
		Player killerEntity;
		String killer;
		ItemStack killingWeapon = null;
		boolean killerPlayer = false;
		EntityDamageEvent lastDamageCause = target.getLastDamageCause();
		DamageCause dc = target.getLastDamageCause().getCause();
		String colorOfPlayer = PlayerUtils.getPrefix(target) + teamM.getColorOfPlayer(target.getUniqueId());
		String cc = ChatColor.GRAY.toString();

		if(lastDamageCause instanceof EntityDamageByEntityEvent){
			Entity damager = ((EntityDamageByEntityEvent) lastDamageCause).getDamager();
			if (damager instanceof Player) { /// killer is player
				killerPlayer = true;
				killerEntity = (Player) damager;
				killer = teamM.getColorOfPlayer(killerEntity.getUniqueId()) + killerEntity.getName();
				killingWeapon = killerEntity.getItemInHand();
			}
			else if (damager instanceof Projectile) { /// we have some sort of projectile
				Projectile proj = (Projectile) damager;
				if (proj.getShooter() instanceof Player){ /// projectile was shot by a player
					killerEntity = (Player) proj.getShooter();
					killerPlayer =true;
					killer = teamM.getColorOfPlayer(killerEntity.getUniqueId()) + killerEntity.getName();
					killingWeapon = killerEntity.getItemInHand();
				} 
				else if (proj.getShooter() != null){ /// projectile shot by some mob, or other source
					killer = proj.getShooter().getClass().getSimpleName();
					killerPlayer =false;
				} else {
					killer = "unkown"; /// projectile was null?
					killerPlayer =false;
				}

			}
			else if (damager instanceof Tameable && ((Tameable) damager).isTamed()) {
				AnimalTamer at = ((Tameable) damager).getOwner();
				if (at != null){
					if (at instanceof Player){
						killerEntity = (Player) at;
					}
					killer = at.getName();
				} else {
					killer = damager.getType().getName();
				}
			} else { /// Killer is not a player
				killer = damager.getType().getName();
			}
		}
		else {
			if (lastDamageCause == null || lastDamageCause.getCause() == null){
				killer  = "unkown";
			}
				
			else{
				killer = lastDamageCause.getCause().name();
			}
				
		}

			e.setDeathMessage(null);
			//String deathMsg = colorOfPlayer + target.getName() + cc + " was killed by " + (killerPlayer ? killer + cc + " using a " + killingWeapon.getType().toString(): " a " + ChatColor.GRAY + killer);
			for(Player online : plugin.getServer().getOnlinePlayers()){
				
				//online.sendMessage(deathMsg);
			}
		}


	}


