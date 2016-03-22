package com.goldornetwork.uhc.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class BreakEvent implements Listener{
	private static BreakEvent instance = new BreakEvent();
	private TeamManager teamM = TeamManager.getInstance();
	private MessageSender ms = new MessageSender();
	private Map<UUID, Integer> amountOfDiamondsMined = new HashMap<UUID, Integer>();
	private int diamondThreshold = 20;
	
	
	public static BreakEvent getInstance(){
		return instance;
	}
	public void setup(){
		amountOfDiamondsMined.clear();
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void OnPlayerBreakEvent(BlockBreakEvent e){
		Player p = e.getPlayer();
		
		if(e.getBlock().getType().equals(Material.DIAMOND_ORE)){
			//TODO make below true
			if(teamM.isPlayerInGame(p)==false){
				if(amountOfDiamondsMined.containsKey(p.getUniqueId())==false){
					amountOfDiamondsMined.put(p.getUniqueId(), 1);
				}
				else if(amountOfDiamondsMined.containsKey(p.getUniqueId())){
					amountOfDiamondsMined.put(p.getUniqueId(), amountOfDiamondsMined.get(p.getUniqueId()) + 1);
					if(amountOfDiamondsMined.get(p.getUniqueId())>=diamondThreshold && amountOfDiamondsMined.get(p.getUniqueId()) % 10 == 0){
						ms.sendToOPS(p.getName() + " has mined " + ChatColor.GRAY + amountOfDiamondsMined.get(p.getUniqueId()) + " diamonds");
					}
				}
				
			}
		}
		
		
		
	}
	
	
}
