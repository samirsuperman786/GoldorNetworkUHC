package com.goldornetwork.uhc.managers.ModifierManager.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DealDamage implements Runnable {

	private static DealDamage instance = new DealDamage();
	private List<UUID> playersToDamage = new ArrayList<UUID>();
	double damageAmount;
		public static DealDamage getInstance() {
		return instance;
	}
			
		public void addPlayerToTick(Player p, double damageAmount){
			playersToDamage.add(p.getUniqueId());
		}
			
	@Override
	public void run() {
		for(UUID u : playersToDamage){
			Bukkit.getServer().getPlayer(u).damage(damageAmount);
		}
	};
	
	
}
