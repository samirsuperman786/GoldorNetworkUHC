package com.goldornetwork.uhc.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class AntiXray {

	private static AntiXray instance = new AntiXray();
	private MessageSender ms = new MessageSender();
	
	private Map<UUID, Integer> amountOfDiamondsMined = new HashMap<UUID, Integer>();
	private int diamondThreshold = 20;
	
	public static AntiXray getInstace(){
		return instance;
	}
	public void setup(){
		amountOfDiamondsMined.clear();
	}
	public void run(Player p, BlockBreakEvent e){
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
