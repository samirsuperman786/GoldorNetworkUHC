package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.GameStartEvent;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class KingsManager extends Gamemode implements Listener{

	//instances
	private TeamManager teamM;
	
	//storage
	private Map<String, UUID> listOfKings = new HashMap<String, UUID>();
	private List<UUID> lateKings = new ArrayList<UUID>();

	public KingsManager(TeamManager teamM) {
		super("Kings", "A random player on a team will receive special powers!");
		this.teamM=teamM;
	}
	@Override
	public void onEnable() {
		listOfKings.clear();
		lateKings.clear();
	}
	@Override
	public void onDisable() {}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(GameStartEvent e){
		distibruteItemsToTeams();
	}
	
	@EventHandler
	public void on(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(State.getState().equals(State.INGAME)){
			if(lateKings.contains(p.getUniqueId())){
				giveAPlayerKingItems(p);
				removePlayerFromLateKings(p);
			}
		}
	}
	
	private void giveAPlayerKingItems(Player King){
		King.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD,1));
		King.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		King.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		King.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		King.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
	}
	private void removePlayerFromLateKings(Player King){
		lateKings.remove(King.getUniqueId());
	}

	private void distibruteItemsToTeams() {
		for(String team : teamM.getListOfTeams()){
			Random random = new Random();
			//selecting a random king
			OfflinePlayer king = Bukkit.getServer().getOfflinePlayer(teamM.getPlayersOnATeam(team).get(random.nextInt(teamM.getPlayersOnATeam(team).size())));
			for(UUID u : teamM.getPlayersOnATeam(team)){
				if(Bukkit.getServer().getPlayer(u).isOnline()){
					MessageSender.alertMessage(Bukkit.getServer().getPlayer(u), ChatColor.GOLD, "Your king is " + teamM.getColorOfTeam(team) + king.getName());
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
