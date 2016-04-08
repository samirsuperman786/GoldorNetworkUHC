package com.goldornetwork.uhc.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.PVPEnableEvent;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class TimerManager {
	//instances
	private UHC plugin;
	private ScatterManager scatterM;
	private TeamManager teamM;
	private VoteManager voteM;
	
	//storage
	private int timeTillMatchStart;
	private int timeTillPVPStart;
	private int timeTillVote;
	private boolean matchStart;
	private boolean isPVPEnabled;


	public TimerManager(UHC plugin, ScatterManager scatterM, TeamManager teamM, VoteManager voteM) {
		this.plugin=plugin;
		this.scatterM=scatterM;
		this.teamM=teamM;
		this.voteM=voteM;
	}
	
	/**
	 * This does the following: sets the status of the server to not running and cancels all ongoing timers
	 * 
	 */
	public void setup(){
		config();
		State.setState(State.NOT_RUNNING);
		matchStart=false;
		isPVPEnabled=false;
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
		isPVPEnabled= false;
		matchStart = true;
		countdownTimer();
		voteTimer();
	}
	
	private void voteTimer(){
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				voteM.generateOptions();
				MessageSender.broadcast("-[Options]");
				for(int i = 0; i<voteM.getNumberOfOptions(); i++){
					MessageSender.broadcast("Option " + (i + 1));
					for(Gamemode game : voteM.getOptions().get(i)){
						MessageSender.broadcast(ChatColor.AQUA + game.getName());
						
					}
					MessageSender.broadcast("---------------");
					
				}
				MessageSender.broadcast(ChatColor.LIGHT_PURPLE + "Please use /vote [option], also /info [gamemode]");
				
			}
		}.runTaskLater(plugin, 100L);
		
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				timeTillVote--;
				if(timeTillVote==0){
					voteM.enableOption(voteM.getWinner());
					MessageSender.broadcast("Option " + (voteM.getWinner()+1) + " has won.");
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

	/**
	 * Used to check if PVP has been enabled yet
	 * @return <code> True </code> if it has been enabled
	 */
	public boolean isPVPEnabled(){
		return isPVPEnabled;
	}


}
