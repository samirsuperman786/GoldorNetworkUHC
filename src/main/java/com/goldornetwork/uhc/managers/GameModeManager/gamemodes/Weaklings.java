package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;
import com.goldornetwork.uhc.utils.MessageSender;

import net.md_5.bungee.api.ChatColor;

public class Weaklings extends Gamemode implements Listener{


	private TeamManager teamM;
	private UHC plugin;

	public Weaklings(UHC plugin, TeamManager teamM) {
		super("Weaklings", "Weaklings", "Every 10 minutes, players with the lowest health receive one golden apple.");
		this.teamM=teamM;
		this.plugin=plugin;
	}

	@EventHandler
	public void on(GameStartEvent e){
		runChecker();
	}

	private void runChecker(){
		new BukkitRunnable() {
			double searchHealth = 0.5;
			double exactLowestHealth = 0.0;
			boolean foundLowest = false;
			@Override
			public void run() {
				while(foundLowest==false){

					checkLoop:
						for(UUID u : teamM.getPlayersInGame()){
							if(plugin.getServer().getOfflinePlayer(u).isOnline()){
								Player target = plugin.getServer().getPlayer(u);
								if(target.getHealth()==searchHealth){
									exactLowestHealth=searchHealth;
									foundLowest=true;
									break checkLoop;
								}
							}
						}

				if(foundLowest==false){
					searchHealth+=.5;
				}
				}
				if(foundLowest==true){
					MessageSender.broadcast(exactLowestHealth + "");
					distributeItems(findPlayers(exactLowestHealth));
				}
			}
		}.runTaskTimer(plugin, 0L, 100L); //TODO make 12000L 12000L
	}

	private Set<UUID> findPlayers(double health){
		Set<UUID> toReturn = new HashSet<UUID>();

		for(UUID u : teamM.getPlayersInGame()){
			if(plugin.getServer().getOfflinePlayer(u).isOnline()){
				Player target = plugin.getServer().getPlayer(u);
				if(target.getHealth()<=health){
					toReturn.add(u);
				}
			}
		}
		return toReturn;
	}

	private void distributeItems(Set<UUID> playersToSend){
		for(UUID u : playersToSend){
			if(plugin.getServer().getOfflinePlayer(u).isOnline()){
				Player target = plugin.getServer().getPlayer(u);
				target.getWorld().dropItem(target.getLocation(), new ItemStack(Material.GOLDEN_APPLE, 1));
				target.playSound(target.getLocation(), Sound.LEVEL_UP, 1f, 1f);;
			}
		}
		MessageSender.broadcast(ChatColor.GRAY + ""+ playersToSend.size() + ChatColor.GOLD + " players have received one golden apple.");
	}
}
