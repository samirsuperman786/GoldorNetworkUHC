package com.goldornetwork.uhc.managers.ModifierManager.actions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class TheHobbitManager implements Listener{

	private static TheHobbitManager instance = new TheHobbitManager();
	private TimerManager timerM = TimerManager.getInstance();
	private TeamManager teamM = TeamManager.getInstance();
	private MessageSender ms = new MessageSender();
	private boolean enableTheHobbit;

	public static TheHobbitManager getInstance(){
		return instance;
	}
	public void setup(){
		enableTheHobbit=false;
	}
	
	public void enableTheHobbit(boolean val){
		this.enableTheHobbit=val;
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void onUse(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(enableTheHobbit){
			if(timerM.hasCountDownEnded()){
				if(teamM.isPlayerInGame(p)){
					if(p.getItemInHand().equals(Material.GOLD_NUGGET)){
						if(p.getItemInHand().hasItemMeta()){
							if(p.getItemInHand().getItemMeta().hasDisplayName()){
								if(p.getItemInHand().getItemMeta().getDisplayName().equals("The Magic Ring of Invisibility")){
									p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30*20, 1));
									p.getInventory().remove(p.getItemInHand());
									ms.send(ChatColor.GOLD, p, "You have activated your invisibility ring!");
								}
							}
						}
					}
				}
				
			}

		}
	}


}
