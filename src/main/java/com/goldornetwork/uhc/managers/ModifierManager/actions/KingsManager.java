package com.goldornetwork.uhc.managers.ModifierManager.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.listeners.JoinEvent;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class KingsManager {

	//instances
	private static KingsManager instance = new KingsManager();
	private TeamManager teamM = TeamManager.getInstance();
	private MessageSender ms = new MessageSender();
	private JoinEvent joinE = JoinEvent.getInstance();
	private TimerManager timerM = TimerManager.getInstance();
	
	//storage
	private Map<String, UUID> listOfKings = new HashMap<String, UUID>();
	private List<UUID> lateKings = new ArrayList<UUID>();
	private boolean enableKings;
	
	
	public static KingsManager getInstance(){
		return instance;
	}
	
	public void setup(){
		enableKings=false;
		listOfKings.clear();
		lateKings.clear();
	}
	
	public List<UUID> getLateKings(){
		return lateKings;
	}
	public void giveAPlayerKingItems(Player King){
		King.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD,1));
		King.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		King.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		King.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		King.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
	}
	public void removePlayerFromLateKings(Player King){
		lateKings.remove(King.getUniqueId());
	}
	
	public void enableKings(boolean val){
		joinE.enableKings(val);
		this.enableKings = val;
		if(enableKings){
			if(teamM.isFFAEnabled()){
				ms.sendToOPS("Kings could not be loaded becuase FFA is enabled!");
				enableKings = false;
			}
			else if(teamM.isTeamsEnabled()){
				timerM.enableKings(val);
			}
		}
		
	}

	public void distibruteItemsToTeams() {
		
		for(String team : teamM.getListOfTeams()){
			Random random = new Random();
			//selecting a random king
			OfflinePlayer king = Bukkit.getServer().getOfflinePlayer(teamM.getPlayersOnATeam(team).get(random.nextInt(teamM.getPlayersOnATeam(team).size())));
			for(UUID u : teamM.getPlayersOnATeam(team)){
				if(Bukkit.getServer().getPlayer(u).isOnline()){
					ms.alertMessage(Bukkit.getServer().getPlayer(u), ChatColor.GOLD, "Your king is " + teamM.getColorOfTeam(team) + king.getName());
				}
			}
			if(king.isOnline()){
				Player King = (Player) king;
				giveAPlayerKingItems(King);
			}
			else if(king.isOnline()==false){
				lateKings.add(king.getUniqueId());
			}
		}
		
	}
	
	
	
	
	
}
