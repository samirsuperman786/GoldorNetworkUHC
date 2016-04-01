package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.goldornetwork.uhc.listeners.JoinEvent;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class TheHobbitManager implements Listener{

	//instances
	private TimerManager timerM;
	private TeamManager teamM;
	private JoinEvent joinE;
	
	//storage
	private boolean enableTheHobbit;
	private List<UUID> lateHobbits = new ArrayList<UUID>();
	
	public TheHobbitManager(TimerManager timerM, TeamManager teamM, JoinEvent joinE) {
		this.timerM=timerM;
		this.teamM=teamM;
		this.joinE=joinE;
	}
	
	public void setup(){
		lateHobbits.clear();
		enableTheHobbit=false;
		joinE.enableTheHobbit(false);
		timerM.enableTheHobbit(false);
	}
	public void enableTheHobbit(boolean val){
		this.enableTheHobbit=val;
		joinE.enableTheHobbit(val);
		timerM.enableTheHobbit(val);
	}
	
	public void giveAPlayerHobbitItems(Player p){
		ItemStack given = new ItemStack(Material.GOLD_NUGGET,1);
		ItemMeta im = given.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "The Magic Ring of Invisibility");
		given.setItemMeta(im);
		p.getInventory().addItem(given);
	}
	public List<UUID> getLateHobbits(){
		return lateHobbits;
	}
	public void removePlayerFromLateHobbits(Player p){
		lateHobbits.remove(p.getUniqueId());
	}
	public void distributeItems(){
		for(UUID u : teamM.getPlayersInGame()){
			if(Bukkit.getServer().getPlayer(u).isOnline()){
				Player p = Bukkit.getServer().getPlayer(u);
				giveAPlayerHobbitItems(p);
			}
			else{
				lateHobbits.add(u);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onUse(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(enableTheHobbit){
			if(timerM.hasCountDownEnded()){
				if(teamM.isPlayerInGame(p)){
					if(p.getItemInHand().equals(Material.GOLD_NUGGET)){
						if(p.getItemInHand().getItemMeta().getDisplayName().equals("The Magic Ring of Invisibility")){
									p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30*20, 1));
									p.getInventory().remove(p.getItemInHand());
									MessageSender.send(ChatColor.GOLD, p, "You have activated your invisibility ring!");
						}
					}
				}
				
			}

		}
	}


}
