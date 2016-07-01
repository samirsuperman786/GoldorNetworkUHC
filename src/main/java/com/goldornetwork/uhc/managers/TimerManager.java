package com.goldornetwork.uhc.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.chat.ChatManager;
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.managers.world.customevents.GameOpenEvent;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;
import com.goldornetwork.uhc.managers.world.customevents.MeetupEvent;
import com.goldornetwork.uhc.managers.world.customevents.PVPEnableEvent;
import com.goldornetwork.uhc.utils.MessageSender;

public class TimerManager implements Listener{


	private UHC plugin;
	private ScatterManager scatterM;
	private TeamManager teamM;
	private VoteManager voteM;
	private ChatManager chatM;
	private WorldManager worldM;
	
	private int timeTillMatchStart;
	private int timeTillPVPStart;
	private int timeTillMeetup;
	private int timeTillVote;


	public TimerManager(UHC plugin, ScatterManager scatterM, TeamManager teamM, VoteManager voteM, ChatManager chatM, WorldManager worldM){
		this.plugin=plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.scatterM=scatterM;
		this.teamM=teamM;
		this.voteM=voteM;
		this.chatM=chatM;
		this.worldM=worldM;
	}

	public void setup(){
		config();
		State.setState(State.NOT_RUNNING);
	}

	private void config(){
		plugin.getConfig().addDefault("TIME-TILL-MATCH-START", 15);
		plugin.getConfig().addDefault("TIME-TILL-PVP-START", 30);
		plugin.getConfig().addDefault("TIME-TILL-MEETUP", 30);
		plugin.getConfig().addDefault("TIME-TILL-VOTE-END", 5);
		plugin.saveConfig();
		this.timeTillMatchStart = (plugin.getConfig().getInt("TIME-TILL-MATCH-START")*60);
		this.timeTillPVPStart = (plugin.getConfig().getInt("TIME-TILL-PVP-START")*60);
		this.timeTillMeetup=(plugin.getConfig().getInt("TIME-TILL-MEETUP") *60);
		this.timeTillVote = (plugin.getConfig().getInt("TIME-TILL-VOTE-END")*60);
	}

	public void startMatch(Player online){
		State.setState(State.OPEN);
		Bukkit.getServer().getPluginManager().callEvent(new GameOpenEvent());
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

	private void countdownTimer(){

		new BukkitRunnable(){
			double startingTime = timeTillMatchStart;

			@Override
			public void run(){

				float percentage = (float) (timeTillMatchStart/startingTime);
				setExpTimer(percentage);

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
					if(timeTillMatchStart>60){
						double time = timeTillMatchStart;
						setLvlTimer((int)Math.ceil(time/60.0));
					}
					if(timeTillMatchStart <=60 && timeTillMatchStart>0){
						setLvlTimer(timeTillMatchStart);
					}
					
					timeTillMatchStart--;
				}
				else if(timeTillMatchStart == 0){
					setLvlTimer(0);
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

	private void pvpTimer(){

		new BukkitRunnable() {
			@Override
			public void run() {
				if(timeTillPVPStart>0){
					if(timeTillPVPStart >= (5*60) && timeTillPVPStart % (5*60) == 0){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "PVP will be enabled in " + ChatColor.DARK_RED + timeTillPVPStart/60 + ChatColor.DARK_AQUA + " minutes.");
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
					meetupTimer();
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	private void meetupTimer(){

		new BukkitRunnable() {
			@Override
			public void run() {
				if(timeTillMeetup>0){
					if(timeTillMeetup >= (5*60) && timeTillMeetup % (5*60) == 0){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "Meetup is in " + ChatColor.DARK_RED + timeTillMeetup/60 + ChatColor.DARK_AQUA + " minutes.");
					}
					if(timeTillMeetup== (299)){
						scatterM.preMeetupSetup();
					}
					else if(timeTillMeetup<= (4*60) && timeTillMeetup > (1*60) && timeTillMeetup % (1*60) ==0){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "Meetup is in " + ChatColor.DARK_RED + timeTillMeetup/60 + ChatColor.DARK_AQUA + " minutes.");
					}
					else if(timeTillMeetup <= 60 && timeTillMeetup >=30 &&timeTillMeetup%10==0){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "Meetup is in " + ChatColor.DARK_RED + timeTillMeetup + ChatColor.DARK_AQUA + " seconds.");
					}
					else if(timeTillMeetup <=30 && timeTillMeetup >=5 && timeTillMeetup %5==0){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "Meetup is in " + ChatColor.DARK_RED + timeTillMeetup + ChatColor.DARK_AQUA + " seconds.");
					}
					else if(timeTillMeetup <= 5 && timeTillMeetup >1){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "Meetup is in " + ChatColor.DARK_RED + timeTillMeetup + ChatColor.DARK_AQUA + " seconds.");		
					}
					else if(timeTillMeetup==1){
						MessageSender.broadcast(ChatColor.DARK_AQUA + "Meetup is in " + ChatColor.DARK_RED + timeTillMeetup + ChatColor.DARK_AQUA + " second.");
					}

					timeTillMeetup--;
				}
				else if(timeTillMeetup==0){
					Bukkit.getPluginManager().callEvent(new MeetupEvent());
					MessageSender.broadcast("Meetup is now.");
					MessageSender.broadcastTitle(ChatColor.GOLD + "Meetup is now!", ChatColor.RED + "Get moving to (0, 0)");
					cancel();
				}	
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	private void setExpTimer(float percentage){
		for(Player online : plugin.getServer().getOnlinePlayers()){
			online.setExp(percentage);
		}
	}
	
	private void setLvlTimer(int lvl){
		for(Player online : plugin.getServer().getOnlinePlayers()){
			online.setLevel(lvl);
		}
	}
	
	public int getTimeTillMatchStart(){
		return timeTillMatchStart;
	}

	public int getTimeTillPVP(){
		return timeTillPVPStart;
	}

	public int getTimeTillMeetup(){
		return timeTillMeetup;
	}
}
