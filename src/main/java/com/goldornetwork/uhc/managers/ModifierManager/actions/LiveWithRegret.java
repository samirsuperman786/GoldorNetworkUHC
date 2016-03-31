package com.goldornetwork.uhc.managers.ModifierManager.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class LiveWithRegret {

	private static LiveWithRegret instance = new LiveWithRegret();
	private PotionSwap potionS = PotionSwap.getInstance();
	private TeamManager teamM = TeamManager.getInstance();
	private MessageSender ms = new MessageSender();
	private DeathEvent deathE = DeathEvent.getInstance();
	private Map<UUID, Integer> numberOfDeaths = new HashMap<UUID, Integer>();
	
	public static LiveWithRegret getInstance(){
		return instance;
	}
	public void setup(){
		numberOfDeaths.clear();
	}
	
	public void run(Player p, PlayerDeathEvent e){
		if(numberOfDeaths.containsKey(p.getUniqueId())==false){
			numberOfDeaths.put(p.getUniqueId(), 1);
			p.setHealth(p.getMaxHealth());
			giveAPlayerARandomDebuff(p);
			ms.broadcast(teamM.getColorOfPlayer(p) + p.getName() + " has died and respawned with debuffs.");
		}
		else if(numberOfDeaths.containsKey(p.getUniqueId())){
			if(numberOfDeaths.get(p.getUniqueId())>1){
				deathE.playerDied(p, e);
			}
		}
	}
	
	
	private void giveAPlayerARandomDebuff(Player p){
		Random random = new Random();
		switch(random.nextInt(5)){
		case 1: p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
				ms.alertMessage(p, ChatColor.GOLD, "You have respawned with a" + ChatColor.GRAY + " slowness debuff");
				break;
		case 2: p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 1));
				ms.alertMessage(p, ChatColor.GOLD, "You have respawned with a" + ChatColor.GRAY + " weakness debuff");
				break;
		case 3: p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Integer.MAX_VALUE, 1));
				ms.alertMessage(p, ChatColor.GOLD, "You have respawned with a" + ChatColor.GRAY + " hunger debuff");
				break;
		case 4: p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10*60*20, 1));
				ms.alertMessage(p, ChatColor.GOLD, "You have respawned with a" + ChatColor.GRAY + " blindness debuff");
				break;
		case 5: p.setHealth(10);
				ms.alertMessage(p, ChatColor.GOLD, "You have respawned with a" + ChatColor.GRAY + "health debuff");
				break;
		default: Bukkit.getServer().getLogger().info("Unexpected error at executing PotionSwap");
		
		}
	}
	
}
