package com.goldornetwork.uhc.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.GoneFishing;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.KingsManager;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.PotionSwap;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.SkyHigh;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.TheHobbitManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class JoinEvent implements Listener{

	//instances
	private TeamManager teamM;
	private TimerManager timerM;
	private ScatterManager scatterM;
	private PotionSwap potionS;
	private KingsManager kingM;
	private SkyHigh skyHighM;
	private GoneFishing goneFishingM;
	private TheHobbitManager hobbitM;
	//storage
	private boolean enableKings;
	private boolean enableGoneFishing;
	private boolean enableTheHobbit;
	private boolean enableSkyHigh;
	private boolean enablePotionSwap;

	public JoinEvent(TeamManager teamM, ScatterManager scatterM, PotionSwap potionS, KingsManager kingM, SkyHigh skyHighM, GoneFishing goneFishingM, TheHobbitManager hobbitM) {
		this.teamM=teamM;
		this.scatterM=scatterM;
		this.potionS=potionS;
		this.kingM=kingM;
		this.skyHighM=skyHighM;
		this.goneFishingM=goneFishingM;
		this.hobbitM=hobbitM;
	}

	public void setup(){
		enableKings=false;
		enableGoneFishing=false;
		enableTheHobbit=false;
		enableSkyHigh=false;
		enablePotionSwap=false;
	}
	public void enableKings(boolean val){
		this.enableKings= val;
	}
	public void enableGoneFishing(boolean val){
		this.enableGoneFishing = val;
	}
	public void enableTheHobbit(boolean val){
		this.enableTheHobbit=val;
	}
	public void enableSkyHigh(boolean val){
		this.enableSkyHigh=val;
	}
	public void enablePotionSwap(boolean val){
		this.enablePotionSwap=val;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
			if(timerM.hasCountDownEnded()){
				if(teamM.isPlayerInGame(e.getPlayer())){
					if(enablePotionSwap){
						if(potionS.getLatePotionPlayers().contains(p.getUniqueId())){
							potionS.giveAPlayerARandomPotion(p);
							potionS.removePlayerFromLateGive(p);
						}
					}
					if(enableGoneFishing){
						if(goneFishingM.getLateGoneFishing().contains(p.getUniqueId())){
							goneFishingM.giveAPlayerGoneFishingItems(p);
							goneFishingM.removeAPlayerFromLateGoneFishing(p);
						}
					}
					if(enableTheHobbit){
						if(hobbitM.getLateHobbits().contains(p.getUniqueId())){
							hobbitM.giveAPlayerHobbitItems(p);
							hobbitM.removePlayerFromLateHobbits(p);
						}
					}
					if(enableSkyHigh){
						if(skyHighM.getLateSkyHigh().contains(p.getUniqueId())){
							skyHighM.giveAPlayerSkyHighItems(p);
							skyHighM.removePlayerFromLateSkyHigh(p);
						}
					}

					if(teamM.isFFAEnabled()){
						if(scatterM.getLateScatters().contains(p.getUniqueId())){
							scatterM.lateScatterAPlayerInFFA(p);
							scatterM.removePlayerFromLateScatters(p);
							MessageSender.send(ChatColor.GREEN, p, "You have been late scattered!");
						}
					}
					else if(teamM.isTeamsEnabled()){
						if(enableKings){
							if(kingM.getLateKings().contains(p.getUniqueId())){
								kingM.giveAPlayerKingItems(p);
								kingM.removePlayerFromLateKings(p);
								MessageSender.send(ChatColor.GREEN, p, "You have received your king items!");
							}
						}

						if(teamM.isPlayerInGame(p) && scatterM.getLateScatters().contains(p.getUniqueId())){
							scatterM.lateScatterAPlayerInATeam(teamM.getTeamOfPlayer(p), p);
							scatterM.removePlayerFromLateScatters(p);
							MessageSender.send(ChatColor.GREEN, p, "You have been late scattered to your team spawn!");
						}
					}

				}
				else if(teamM.isPlayerInGame(e.getPlayer())==false){
					if(p.getWorld().equals(scatterM.getUHCWorld())==false){
						p.teleport(scatterM.getUHCWorld().getSpawnLocation());
					}
					if(teamM.isPlayerAnObserver(p)==false){
						teamM.addPlayerToObservers(p);

					}
					MessageSender.send(ChatColor.AQUA, p, "You are now spectating the game");

				}
			}
				
			
			
		

	}


}
