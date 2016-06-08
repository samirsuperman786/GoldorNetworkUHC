package com.goldornetwork.uhc.managers;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.managers.world.events.GameOpenEvent;
import com.goldornetwork.uhc.managers.world.events.GameStartEvent;
import com.goldornetwork.uhc.managers.world.events.PVPEnableEvent;
import com.goldornetwork.uhc.managers.world.listeners.team.ChatManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class TimerManager implements Listener{
	//instances
	private UHC plugin;
	private ScatterManager scatterM;
	private TeamManager teamM;
	private VoteManager voteM;
	private ChatManager chatM;
	private WorldManager worldM;

	//storage
	private int timeTillMatchStart;
	private int timeTillPVPStart;
	private int timeTillVote;
	private boolean matchStart;
	//private boolean isPVPEnabled;


	public TimerManager(UHC plugin, ScatterManager scatterM, TeamManager teamM, VoteManager voteM, ChatManager chatM, WorldManager worldM) {
		this.plugin=plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.scatterM=scatterM;
		this.teamM=teamM;
		this.voteM=voteM;
		this.chatM=chatM;
		this.worldM=worldM;
	}

	/**
	 * This does the following: sets the status of the server to not running and cancels all ongoing timers
	 * 
	 */
	public void setup(){
		config();
		State.setState(State.NOT_RUNNING);
		matchStart=false;
	}

	private void config(){
		plugin.getConfig().addDefault("TIME-TILL-MATCH-START", 15);
		plugin.getConfig().addDefault("TIME-TILL-PVP-START", 40);
		plugin.getConfig().addDefault("TIME-TILL-VOTE-END", 5);
		plugin.saveConfig();
		this.timeTillMatchStart = (plugin.getConfig().getInt("TIME-TILL-MATCH-START")*60);
		this.timeTillPVPStart = (plugin.getConfig().getInt("TIME-TILL-PVP-START")*60);
		this.timeTillVote = (plugin.getConfig().getInt("TIME-TILL-VOTE-END")*60);
	}

	/**
	 * Used to start the match with a given time till it starts, and a time till PVP is enabled.
	 * This allows players to create and join teams
	 * @param timeTillMatchStarts - the time to wait until the match starts
	 * @param timeTillPVPStarts - the time to wait until PVP is enabled after the match has started
	 */
	public void startMatch(){
		State.setState(State.OPEN);
		Bukkit.getServer().getPluginManager().callEvent(new GameOpenEvent());
		matchStart = true;
		chatM.mutePlayers();
		countdownTimer();
		voteTimer();
	}

	private void voteTimer(){

		voteM.broadcastOptions();
		voteM.generateOptions();

		new BukkitRunnable() {

			@Override
			public void run() {
				timeTillVote--;
				if(timeTillVote==0){
					voteM.enableOption(voteM.getWinner());
					MessageSender.broadcast("Option " + (voteM.getWinner()+1) + " has won with " + ChatColor.GRAY + voteM.getWinnerVotes() + ChatColor.GOLD + " votes.");
					cancel();
				}

			}
		}.runTaskTimer(plugin, 100L, 20L);
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
						MessageSender.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart/60 + ChatColor.GOLD + " minutes");
					}
					else if(timeTillMatchStart <60 && timeTillMatchStart >=30 &&timeTillMatchStart%10==0){
						MessageSender.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart + ChatColor.GOLD+ " seconds");
					}
					else if(timeTillMatchStart <=30 && timeTillMatchStart >=5 && timeTillMatchStart %5==0){
						MessageSender.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart + ChatColor.GOLD+ " seconds");
					}
					else if(timeTillMatchStart <= 5 && timeTillMatchStart >0){
						MessageSender.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart +ChatColor.GOLD+ " seconds");		
					}
					timeTillMatchStart--;

				}
				else if(timeTillMatchStart == 0){

					MessageSender.broadcast("Match has started!");
					State.setState(State.SCATTER);
					if(teamM.isTeamsEnabled()){
						scatterM.scatter();
					}
					for(Player all : Bukkit.getServer().getOnlinePlayers()){
						if(teamM.isPlayerInGame(all.getUniqueId())==false){
							teamM.addPlayerToObservers(all);
							all.teleport(worldM.getCenter());
						}
					}
					//pvpTimer();
					cancel();

				}

				else if(timeTillMatchStart==-1){
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	@EventHandler
	public void on(GameStartEvent e){
		pvpTimer();
	}
	/**
	 * When called, this will begin the count down till PVP is enabled
	 */
	private void pvpTimer(){
		new BukkitRunnable() {

			@Override
			public void run() {
				if(timeTillPVPStart>0){
					if(timeTillPVPStart >= (5*60) && timeTillPVPStart % (5*60) == 0){
						MessageSender.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart/60 + ChatColor.GOLD + " minutes");
					}
					if(timeTillPVPStart== (299)){
						scatterM.prePVPSetup();
					}
					else if(timeTillPVPStart<= (4*60) && timeTillPVPStart >= (1*60) && timeTillPVPStart % (1*60) ==0){
						MessageSender.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart/60 + ChatColor.GOLD + " minute(s)");
					}
					else if(timeTillPVPStart < 60 && timeTillPVPStart >=30 &&timeTillPVPStart%10==0){
						MessageSender.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart + ChatColor.GOLD + " seconds");
					}
					else if(timeTillPVPStart <=30 && timeTillPVPStart >=5 && timeTillPVPStart %5==0){
						MessageSender.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart + ChatColor.GOLD + " seconds");
					}
					else if(timeTillPVPStart <= 5 && timeTillPVPStart >0){
						MessageSender.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart + ChatColor.GOLD + " second(s)");		
					}
					timeTillPVPStart--;
				}
				else if(timeTillPVPStart==0){
					Bukkit.getPluginManager().callEvent(new PVPEnableEvent());
					MessageSender.broadcast("PVP has been enabled!");
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
		State.setState(State.NOT_RUNNING);
		teamM.setup();
	}

	/**
	 * Used to check if the match has started 
	 * @return <code> True </code> if the match has started
	 */
	public boolean hasMatchStarted(){
		return matchStart;
	}
	
	public int getTimeTillMatchStart(){
		return timeTillMatchStart;
	}
	
	public int getTimeTillPVP(){
		return timeTillPVPStart;
	}


}
