package com.goldornetwork.uhc.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class TimerManager implements Runnable {
	/*
	 * TODO make a class that is triggered on match start event
	 * 
	 */
	//instances
	private ScatterManager scatterM;
	private TeamManager teamM;
	//storage
	private int timeTillMatchStart;
	private int timeTillPVPStart;
	private boolean startMatch;
	private boolean hasMatchBegun;
	private boolean matchStart;
	private boolean startPVPTimer;
	private boolean isPVPEnabled;
	
	
	
	
	
	public TimerManager(ScatterManager scatterM, TeamManager teamM) {
		this.scatterM=scatterM;
		this.teamM=teamM;
	}
	public void setup(){
		State.setState(State.NOT_RUNNING);
		hasMatchBegun=false;
		timeTillMatchStart=-2;
		timeTillPVPStart=-2;
		startMatch=false;
		matchStart=false;
		startPVPTimer=false;
		isPVPEnabled=false;
	}
	
	public void startMatch(boolean start, int timeTillMatchStarts, int timeTillPVPStarts){
		State.setState(State.OPEN);
		timeTillMatchStart = timeTillMatchStarts;
		this.timeTillPVPStart= timeTillPVPStarts;
		hasMatchBegun=false;
		isPVPEnabled= false;
		matchStart = true;
		startMatch =true;
	}
	
	public void cancelMatch(){
		State.setState(State.NOT_RUNNING);
		timeTillMatchStart=-1;
	}
	
	public boolean hasMatchStarted(){
		return matchStart;
	}
	
	public boolean hasCountDownEnded(){
		if(hasMatchBegun){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean isPVPEnabled(){
		return isPVPEnabled;
	}
	
	
	@Override
	public void run() {
		if(startMatch){
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
						teamM.addPlayerToObservers(all);
					}
				}
				hasMatchBegun = true;
				startPVPTimer= true;
				startMatch =false;
			}
		
		}
		
		else if(timeTillMatchStart == -1){
			teamM.setup();
			MessageSender.broadcast("Match canceled");
			matchStart=false;
			timeTillMatchStart =-2; //-2 will act as a null value
		}
		
		else if(startPVPTimer == true){
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
				MessageSender.broadcast("PVP has been enabled!");
				scatterM.shrinkBorder();
				scatterM.getUHCWorld().setPVP(true);
				isPVPEnabled = true;
				timeTillPVPStart=-2;
				startPVPTimer= false;
			}
			else if(timeTillPVPStart==-2){
				//nothing
			}
			
		}
		
	}

}
