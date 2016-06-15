package com.goldornetwork.uhc.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.managers.world.events.GameOpenEvent;
import com.goldornetwork.uhc.managers.world.events.GameStartEvent;
import com.goldornetwork.uhc.managers.world.events.PVPEnableEvent;
import com.goldornetwork.uhc.managers.world.listeners.team.ChatManager;
import com.goldornetwork.uhc.utils.MessageSender;

import net.md_5.bungee.api.chat.TextComponent;

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
	private BossBar bossBar;
	private boolean matchStart;


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
	public void startMatch(Player online){
		State.setState(State.OPEN);
		Bukkit.getServer().getPluginManager().callEvent(new GameOpenEvent());
		matchStart = true;
		chatM.mutePlayers();
		countdownTimer();
		voteTimer();
		//bossBar(online);
	}
	private void bossBar(Player online){
		BossBar bossBar = BossBarAPI.addBar(online, // The receiver of the BossBar
				new TextComponent("Match starting in"), // Displayed message
				BossBarAPI.Color.BLUE, // Color of the bar
				BossBarAPI.Style.NOTCHED_20, // Bar style
				1.0f, // Progress (0.0 - 1.0)
				timeTillMatchStart, // Timeout
				1); // Timeout-interval
		this.bossBar=bossBar;
	}

	private void voteTimer(){

		voteM.broadcastOptions();
		voteM.generateOptions();

		new BukkitRunnable() {
			
			
			@Override
			public void run() {
				timeTillVote--;
				if(timeTillVote>=(2*60) && timeTillVote%(2*60) ==0 && voteM.isActive()){
					voteM.broadcastOptions();
				}
				else if(timeTillVote==0){
					voteM.enableOption(voteM.getWinner());
					MessageSender.broadcast("Option " + (voteM.getWinner()+1) + " has won with " + ChatColor.GRAY + voteM.getWinnerVotes() + ChatColor.GOLD + " votes.");
					cancel();
				}

			}
		}.runTaskTimer(plugin, 100L, 20L);
	}

	@EventHandler
	public void on(PlayerJoinEvent e){
		//Player player = e.getPlayer();
		//this.bossBar.addPlayer(player);
	}

	/**
	 * When called, this will begin the count down to the start of the match
	 */
	private void countdownTimer(){
		new BukkitRunnable() {
			//double startingTime = timeTillMatchStart;

			@Override
			public void run() {

				/*float percentage = (float) (timeTillMatchStart/startingTime);
				bossBar.setProgress(percentage);
				if(timeTillMatchStart>60){
					double k = timeTillMatchStart;
					int toSet = (int) Math.ceil(k/60);
					bossBar.setMessage(ChatColor.DARK_AQUA + "Match starting in " + ChatColor.DARK_RED + toSet + ChatColor.DARK_AQUA + " minutes.");
				}
				else{
					bossBar.setMessage(ChatColor.DARK_AQUA + "Match starting in " + ChatColor.DARK_RED + timeTillMatchStart + ChatColor.DARK_AQUA + " seconds.");
				}
*/
				if(timeTillMatchStart >0){

					if(timeTillMatchStart>60 && timeTillMatchStart%60 ==0){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "Match Starting in " + ChatColor.DARK_RED + timeTillMatchStart/60 + ChatColor.DARK_AQUA + " minutes.");
					}
					else if(timeTillMatchStart <=60 && timeTillMatchStart >=30 &&timeTillMatchStart%10==0){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "Match Starting in " + ChatColor.DARK_RED + timeTillMatchStart + ChatColor.DARK_AQUA + " seconds.");
					}
					else if(timeTillMatchStart <=30 && timeTillMatchStart >=5 && timeTillMatchStart %5==0){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "Match Starting in " + ChatColor.DARK_RED + timeTillMatchStart + ChatColor.DARK_AQUA + " seconds.");
					}
					else if(timeTillMatchStart <= 5 && timeTillMatchStart >1){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "Match Starting in " + ChatColor.DARK_RED + timeTillMatchStart +ChatColor.DARK_AQUA + " seconds.");		
					}
					else if(timeTillMatchStart==1){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "Match Starting in " + ChatColor.DARK_RED + timeTillMatchStart +ChatColor.DARK_AQUA + " second.");
					}
					timeTillMatchStart--;

				}
				else if(timeTillMatchStart == 0){

					MessageSender.broadcast("Match has started!");
					MessageSender.broadcastTitle(ChatColor.GOLD + "Match has started!", ChatColor.GOLD + "Scattering...");
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
					cancel();

				}


			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	@EventHandler
	public void on(GameStartEvent e){
		pvpTimer();
		MessageSender.broadcastBigTitle(ChatColor.GOLD + "Go!");
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
						MessageSender.broadcast(ChatColor.DARK_AQUA + "PVP will be enabled in " + ChatColor.DARK_RED + timeTillPVPStart/60 + ChatColor.GOLD + " minutes.");
					}
					if(timeTillPVPStart== (299)){
						scatterM.prePVPSetup();
					}
					else if(timeTillPVPStart<= (4*60) && timeTillPVPStart > (1*60) && timeTillPVPStart % (1*60) ==0){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "PVP will be enabled in " + ChatColor.DARK_RED + timeTillPVPStart/60 + ChatColor.DARK_AQUA + " minutes.");
					}
					else if(timeTillPVPStart <= 60 && timeTillPVPStart >=30 &&timeTillPVPStart%10==0){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "PVP will be enabled in " + ChatColor.DARK_RED + timeTillPVPStart + ChatColor.DARK_AQUA + " seconds.");
					}
					else if(timeTillPVPStart <=30 && timeTillPVPStart >=5 && timeTillPVPStart %5==0){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "PVP will be enabled in " + ChatColor.DARK_RED + timeTillPVPStart + ChatColor.DARK_AQUA + " seconds.");
					}
					else if(timeTillPVPStart <= 5 && timeTillPVPStart >1){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "PVP will be enabled in " + ChatColor.DARK_RED + timeTillPVPStart + ChatColor.DARK_AQUA + " seconds.");		
					}
					else if(timeTillPVPStart==1){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "PVP will be enabled in " + ChatColor.DARK_RED + timeTillPVPStart + ChatColor.DARK_AQUA + " second.");
					}
					timeTillPVPStart--;
				}
				else if(timeTillPVPStart==0){
					Bukkit.getPluginManager().callEvent(new PVPEnableEvent());
					MessageSender.broadcast("PVP has been enabled.");
					MessageSender.broadcastTitle(ChatColor.GOLD + "Meetup is now!", ChatColor.RED + "Get moving to (0, 0)");
					cancel();
				}

			}
		}.runTaskTimer(plugin, 0L, 20L);
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
