package com.goldornetwork.uhc.managers.ModifierManager.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class SkyHigh {

	private static SkyHigh instance = new SkyHigh();
	private TeamManager teamM = TeamManager.getInstance();
	private MessageSender ms = new MessageSender();
	
	private Map<UUID, BukkitTask> playersToDamage= new HashMap<UUID, BukkitTask>();
	private List<UUID> lateSkyHigh = new ArrayList<UUID>();
	UHC plugin;
	
	public static SkyHigh getInstance(){
		return instance;
	}
	
	public void setup(UHC plugin){
		this.plugin=plugin;
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
			for(UUID u : teamM.getPlayersInGame()){
				if(Bukkit.getServer().getPlayer(u).isOnline()){
				if(Bukkit.getServer().getPlayer(u).getLocation().getBlockY()<=100){
					if(playersToDamage.containsKey(u)==false){
						BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){
							@Override
							public void run() {
								Bukkit.getServer().getPlayer(u).damage(2);
								ms.send(ChatColor.RED, Bukkit.getServer().getPlayer(u), "You are below y = 101!");
							}
							
						}, 0L, 600L);
						playersToDamage.put(u, task);
					}
					

				}
				else if(Bukkit.getServer().getPlayer(u).getLocation().getBlockY()>=101){
					if(playersToDamage.containsKey(u)){
						ms.send(ChatColor.GREEN, Bukkit.getServer().getPlayer(u), "You are now above y =100!");
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
