package com.goldornetwork.uhc.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.State;

public class AntiXray implements Listener{


	private Map<UUID, Integer> amountOfDiamondsMined = new HashMap<UUID, Integer>();
	private final int DIAMONDTHRESHOLD = 20;

	
	public AntiXray(UHC plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(BlockBreakEvent e){
		if(State.getState().equals(State.INGAME)){
			if(e instanceof Player){
				if(e.getBlock().getType().equals(Material.DIAMOND_ORE)){
					Player p = e.getPlayer();
					run(p, e);
				}
			}
		}
	}

	private void run(Player p, BlockBreakEvent e){
		if(amountOfDiamondsMined.containsKey(p.getUniqueId())==false){
			amountOfDiamondsMined.put(p.getUniqueId(), 1);
		}
		else if(amountOfDiamondsMined.containsKey(p.getUniqueId())){
			amountOfDiamondsMined.put(p.getUniqueId(), amountOfDiamondsMined.get(p.getUniqueId()) + 1);
			if(amountOfDiamondsMined.get(p.getUniqueId())>=DIAMONDTHRESHOLD && amountOfDiamondsMined.get(p.getUniqueId()) % 10 == 0){
				MessageSender.sendToOPS(p.getName() + " has mined " + ChatColor.GRAY + amountOfDiamondsMined.get(p.getUniqueId()) + " diamonds");
			}
		}
	}
}
