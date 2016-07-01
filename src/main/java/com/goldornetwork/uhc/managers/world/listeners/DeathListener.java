package com.goldornetwork.uhc.managers.world.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.customevents.GameEndEvent;
import com.goldornetwork.uhc.managers.world.customevents.UHCDeathEvent;
import com.goldornetwork.uhc.managers.world.customevents.UHCKillEvent;
import com.goldornetwork.uhc.utils.MessageSender;


public class DeathListener implements Listener {


	private UHC plugin;
	private TeamManager teamM;
	private Random random = new Random();
	
	private Map<UUID, BukkitTask> afkCheck = new HashMap<UUID, BukkitTask>();
	private Set<UUID> toKill = new HashSet<UUID>();
	private int afkTimeTillKill;


	public DeathListener(UHC plugin, TeamManager teamM){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin=plugin;
		plugin.getConfig().addDefault("AFK-TIME-TILL-KILL", 5);
		plugin.saveConfig();
		this.teamM=teamM;
		this.afkTimeTillKill=plugin.getConfig().getInt("AFK-TIME-TILL-KILL");
	}

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
					String winner = teamM.getActiveTeams().iterator().next();
					plugin.getServer().getPluginManager().callEvent(new GameEndEvent(teamM.getPlayersOnATeam(winner)));
				}
				else if(teamM.getActiveTeams().size()==1){
					plugin.getServer().getPluginManager().callEvent(new GameEndEvent(null));
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
							playerDied(target, target.getPlayer().getLocation());
						}
					}
					DeathListener.this.afkCheck.remove(target.getUniqueId());
				}
			}.runTaskLater(plugin, (afkTimeTillKill * 60 * 20));

			this.afkCheck.put(target.getUniqueId(), afkCheck);
		}
	}

	@EventHandler
	public void on(PlayerDeathEvent e){
		Player p = e.getEntity();
		Location loc = p.getLocation();
		teamM.addPlayerToObservers(p);
		p.setHealth(20);
		
		if(teamM.isPlayerInGame(p.getUniqueId()) && p.getKiller() instanceof Player){
			Player killer = p.getKiller();
			if(teamM.isPlayerInGame(killer.getUniqueId())){
				plugin.getServer().getPluginManager().callEvent(new UHCKillEvent(killer));
			}
		}
		if(teamM.isPlayerInGame(p.getUniqueId())){
			if(State.getState().equals(State.INGAME) || State.getState().equals(State.SCATTER)){
				plugin.getServer().getPluginManager().callEvent(new UHCDeathEvent(p));
				e.setDeathMessage(ChatColor.GRAY + e.getDeathMessage());
				new BukkitRunnable() {
					@Override
					public void run() {
						playerDied(p, loc);
					}
				}.runTaskLater(plugin, 5L);
			}
			else{
				e.setDeathMessage(null);
			}
		}
		else{
			e.setDeathMessage(null);
		}
	}
}


