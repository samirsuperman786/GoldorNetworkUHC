package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.tools.DocumentationTool.Location;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.listeners.JoinEvent;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.GameModeManager.LocationListener;
import com.goldornetwork.uhc.utils.MessageSender;

public class SkyHigh {

	//instances
	private TeamManager teamM;
	private TimerManager timerM;
	private JoinEvent joinE;
	private LocationListener locationL;
	
	//storage
	private Map<UUID, BukkitTask> playersToDamage= new HashMap<UUID, BukkitTask>();
	private List<UUID> lateSkyHigh = new ArrayList<UUID>();
	UHC plugin;
	
	public SkyHigh(TeamManager teamM, TimerManager timerM, JoinEvent joinE, LocationListener locationL) {
		this.teamM=teamM;
		this.timerM=timerM;
		this.joinE=joinE;
		this.locationL=locationL;
	}
	
	public void setup(UHC plugin){
		playersToDamage.clear();
		lateSkyHigh.clear();
		this.plugin=plugin;
		timerM.enableSkyHigh(false);
		joinE.enableSkyHigh(false);
		locationL.enableSkyHigh(false);
	}
	public void enableSkyHigh(boolean val){
		timerM.enableSkyHigh(val);
		joinE.enableSkyHigh(val);
		locationL.enableSkyHigh(val);
	}

	public List<UUID> getLateSkyHigh(){
		return lateSkyHigh;
	}
	
	public void removePlayerFromLateSkyHigh(Player p){
		lateSkyHigh.remove(p.getUniqueId());
	}
	
	public void giveAPlayerSkyHighItems(Player p){
		p.getInventory().addItem(new ItemStack(Material.DIAMOND_SPADE, 1));
		p.getInventory().addItem(new ItemStack(Material.PUMPKIN, 10));
		p.getInventory().addItem(new ItemStack(Material.STAINED_CLAY, 64, (short) 13)); //green
		p.getInventory().addItem(new ItemStack(Material.STAINED_CLAY, 64, (short) 11));	//blue
		p.getInventory().addItem(new ItemStack(Material.SNOW_BLOCK, 64));
		p.getInventory().addItem(new ItemStack(Material.STRING, 2));
	}
	
	public void run(){
			//for(UUID u : teamM.getPlayersInGame()){
				for(Player p : Bukkit.getOnlinePlayers()){
					UUID u = p.getUniqueId();
				if(Bukkit.getServer().getPlayer(u).isOnline()){
				if(Bukkit.getServer().getPlayer(u).getLocation().getBlockY()<=100){
					if(playersToDamage.containsKey(u)==false){
						BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){
							@Override
							public void run() {
								Bukkit.getServer().getPlayer(u).damage(2);
								MessageSender.send(ChatColor.RED, Bukkit.getServer().getPlayer(u), "You are below y = 101!");
							}
							
						}, 0L, 600L);
						playersToDamage.put(u, task);
					}
					

				}
				else if(Bukkit.getServer().getPlayer(u).getLocation().getBlockY()>=101){
					if(playersToDamage.containsKey(u)){
						MessageSender.send(ChatColor.GREEN, Bukkit.getServer().getPlayer(u), "You are now above y =100!");
						playersToDamage.get(u).cancel();
						playersToDamage.remove(u);
					}
				}
			}
		}
		
	}
	
	public void distributeItems(){
		for(UUID u : teamM.getPlayersInGame()){
			if(Bukkit.getServer().getPlayer(u).isOnline()){
				Player p = Bukkit.getServer().getPlayer(u);
				giveAPlayerSkyHighItems(p);
			}
			else{
				lateSkyHigh.add(u);
			}
		}
	}
	
}
