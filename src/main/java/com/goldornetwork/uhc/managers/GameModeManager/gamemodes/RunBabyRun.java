package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class RunBabyRun extends Gamemode implements Listener{

	private TeamManager teamM;
	private UHC plugin;

	private final static double HEALTH_THRESHOLD=3;
	private Set<UUID> lowHealth = new HashSet<UUID>();
	private PotionEffect speedBuff = new PotionEffect(PotionEffectType.SPEED,Integer.MAX_VALUE, 2, true, true);

	
	public RunBabyRun(UHC plugin,TeamManager teamM) {
		super("Run Baby Run", "RunBabyRun", "If you are at or below " + HEALTH_THRESHOLD + " hearts, you gain speed 3.");
		this.plugin=plugin;
		this.teamM=teamM;
	}

	@EventHandler
	public void on(GameStartEvent e){
		runTask();
	}

	public void runTask(){

		new BukkitRunnable() {
			@Override
			public void run() {
				for(UUID u : teamM.getPlayersInGame()){
					if(Bukkit.getOfflinePlayer(u).isOnline()){
						Player target = Bukkit.getPlayer(u);
						if(target.getHealth()<=(HEALTH_THRESHOLD*2)){
							if(!(lowHealth.contains(u))){
								lowHealth.add(u);
								addBuffs(target);
							}
						}
						else{
							if(lowHealth.contains(u)){
								removeBuffs(target);
								lowHealth.remove(u);
							}
						}
					}
				}
			}
		}.runTaskTimer(plugin, 60L, 20L);
	}

	public void addBuffs(Player p){
		p.addPotionEffect(speedBuff, true);
	}

	public void removeBuffs(Player p){
		for(PotionEffect playerEffects : p.getActivePotionEffects()){
			if(PlayerUtils.arePotionsSimilar(playerEffects, speedBuff)){
				p.removePotionEffect(speedBuff.getType());
			}
		}
	}
}
