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
import com.goldornetwork.uhc.managers.ModifierManager.actions.GoneFishing;
import com.goldornetwork.uhc.managers.ModifierManager.actions.KingsManager;
import com.goldornetwork.uhc.managers.ModifierManager.actions.PotionSwap;
import com.goldornetwork.uhc.managers.ModifierManager.actions.SkyHigh;
import com.goldornetwork.uhc.managers.ModifierManager.actions.TheHobbitManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class JoinEvent implements Listener{

	//instances
	private static JoinEvent instance = new JoinEvent();
	private TeamManager teamM =  TeamManager.getInstance();
	private TimerManager timerM =  TimerManager.getInstance();
	private ScatterManager scatterM = ScatterManager.getInstance();
	private MessageSender ms = new MessageSender();
	private PotionSwap potionS = PotionSwap.getInstance();
	private KingsManager kingM = KingsManager.getInstance();
	private SkyHigh skyHighM = SkyHigh.getInstance();
	private GoneFishing goneFishingM = GoneFishing.getInstance();
	private TheHobbitManager hobbitM = TheHobbitManager.getInstance();
	//storage
	private boolean enableKings;
	private boolean enableGoneFishing;
	private boolean enableTheHobbit;
	private boolean enableSkyHigh;
	private boolean enablePotionSwap;

	public static JoinEvent getInstance(){
		return instance;
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent e){
		if(timerM.hasCountDownEnded()){
				Player p = e.getPlayer();
				if(teamM.isPlayerInGame(p)){
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
							ms.send(ChatColor.GREEN, p, "You have been late scattered!");
						}
					}
					else if(teamM.isTeamsEnabled()){
						if(enableKings){
							if(kingM.getLateKings().contains(p.getUniqueId())){
								kingM.giveAPlayerKingItems(p);
								kingM.removePlayerFromLateKings(p);
								ms.send(ChatColor.GREEN, p, "You have received your king items!");
							}
						}

						if(teamM.isPlayerInGame(p) && scatterM.getLateScatters().contains(p.getUniqueId())){
							scatterM.lateScatterAPlayerInATeam(teamM.getTeamOfPlayer(p), p);
							scatterM.removePlayerFromLateScatters(p);
							ms.send(ChatColor.GREEN, p, "You have been late scattered to your team spawn!");
						}
					}

				}
				else if(teamM.isPlayerInGame(p)==false){
					if(p.getWorld().equals(scatterM.getUHCWorld())==false){
						p.teleport(scatterM.getUHCWorld().getSpawnLocation());
					}
					if(teamM.isPlayerAnObserver(p)==false){
						teamM.addPlayerToObservers(p);

					}
					ms.send(ChatColor.AQUA, p, "You are now spectating the game");

				}
			
			
		}

	}


}
