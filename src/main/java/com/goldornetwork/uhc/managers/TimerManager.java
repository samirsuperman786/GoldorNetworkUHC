package com.goldornetwork.uhc.managers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.PVPEnableEvent;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class TimerManager {
	/*
	 * TODO make a class that is triggered on match start event
	 * 
	 */
	//instances
	private ScatterManager scatterM;
	private TeamManager teamM;
	private UHC plugin;
	//storage
	private int timeTillMatchStart;
	private int timeTillPVPStart;
	private boolean hasMatchBegun;
	private boolean matchStart;
	private boolean isPVPEnabled;





	public TimerManager(UHC plugin, ScatterManager scatterM, TeamManager teamM) {
		this.plugin=plugin;
		this.scatterM=scatterM;
		this.teamM=teamM;
	}
	
	/**
	 * This does the following: sets the status of the server to not running and cancels all ongoing timers
	 * 
	 */
	public void setup(){
		State.setState(State.NOT_RUNNING);
		hasMatchBegun=false;
		timeTillMatchStart=-2;
		timeTillPVPStart=-2;
		matchStart=false;
		isPVPEnabled=false;
	}

	/**
	 * Used to start the match with a given time till it starts, and a time till PVP is enabled.
	 * This allows players to create and join teams
	 * @param timeTillMatchStarts - the time to wait until the match starts
	 * @param timeTillPVPStarts - the time to wait until PVP is enabled after the match has started
	 */
	public void startMatch(int timeTillMatchStarts, int timeTillPVPStarts){
		State.setState(State.OPEN);
		this.timeTillMatchStart = timeTillMatchStarts;
		this.timeTillPVPStart= timeTillPVPStarts;
		hasMatchBegun=false;
		isPVPEnabled= false;
		matchStart = true;
		countdownTimer();
	}
	
	/**
	 * When called, this will begin the count down to the start of the match
	 */
	private void countdownTimer(){
		new BukkitRunnable() {
			@Override
			public void run() {
				if(timeTillMatchStart >0){
					if(timeTillMatchStart>=60 && timeTillMatchStart%60 ==0){
						MessageSender.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart/60 + ChatColor.RED + " minutes");
					}
					else if(timeTillMatchStart <60 && timeTillMatchStart >=30 &&timeTillMatchStart%10==0){
						MessageSender.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart + ChatColor.RED + " seconds");
					}
					else if(timeTillMatchStart <=30 && timeTillMatchStart >=5 && timeTillMatchStart %5==0){
						MessageSender.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart + ChatColor.RED + " seconds");
					}
					else if(timeTillMatchStart <= 5 && timeTillMatchStart >0){
						MessageSender.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart + ChatColor.RED + " seconds");		
					}
					timeTillMatchStart--;

				}
				else if(timeTillMatchStart == 0){
					MessageSender.broadcast("Match has started!");
					State.setState(State.SCATTER);
					
					if(teamM.isFFAEnabled()){
						scatterM.scatterFFA();
					}
					else if(teamM.isTeamsEnabled()){
						scatterM.scatterTeams();
					}
					for(Player all : Bukkit.getServer().getOnlinePlayers()){
						if(teamM.isPlayerInGame(all)==false){
							MessageSender.send(ChatColor.AQUA, all, "You are now spectating the game");
							teamM.addPlayerToObservers(all);
							all.teleport(scatterM.getCenter());
						}
					}
					pvpTimer();
					hasMatchBegun = true;
					cancel();
				}
				else if(timeTillMatchStart==-1){
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}
	
	/**
	 * When called, this will begin the count down till PVP is enabled
	 */
	private void pvpTimer(){
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(timeTillPVPStart>0){
					if(timeTillPVPStart >= (5*60) && timeTillPVPStart % (10*60) == 0){
						MessageSender.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart/60 + ChatColor.RED + " minutes");
					}
					else if(timeTillPVPStart<= (4*60) && timeTillPVPStart >= (1*60) && timeTillPVPStart % (1*60) ==0){
						MessageSender.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart/60 + ChatColor.RED + " minutes");
					}
					else if(timeTillPVPStart < 60 && timeTillPVPStart >=30 &&timeTillPVPStart%10==0){
						MessageSender.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart + ChatColor.RED + " seconds");
					}
					else if(timeTillPVPStart <=30 && timeTillPVPStart >=5 && timeTillPVPStart %5==0){
						MessageSender.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart + ChatColor.RED + " seconds");
					}
					else if(timeTillPVPStart <= 5 && timeTillPVPStart >0){
						MessageSender.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart + ChatColor.RED + " seconds");		
					}
					timeTillPVPStart--;
				}
				else if(timeTillPVPStart==0){
					Bukkit.getPluginManager().callEvent(new PVPEnableEvent());
					MessageSender.broadcast("PVP has been enabled!");
					scatterM.shrinkBorder();
					scatterM.getUHCWorld().setPVP(true);
					isPVPEnabled = true;
					cancel();
				}
				
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}
	
	
	/**
	 * When called, this cancels the timer till match starts
	 * @see countdownTimer()
	 */
	public void cancelMatch(){
		MessageSender.broadcast("Match canceled");
		timeTillMatchStart=-1;
		teamM.setup();
	}

	/**
	 * Used to check if the match has started 
	 * @return <code> True </code> if the match has started
	 */
	public boolean hasMatchStarted(){
		return matchStart;
	}

	/** Used to check if the count down has ended
	 * @return <code> True </code> if the count down has ended
	 */
	public boolean hasCountDownEnded(){
		if(hasMatchBegun){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Used to check if PVP has been enabled yet
	 * @return <code> True </code> if it has been enabled
	 */
	public boolean isPVPEnabled(){
		return isPVPEnabled;
	}


}
