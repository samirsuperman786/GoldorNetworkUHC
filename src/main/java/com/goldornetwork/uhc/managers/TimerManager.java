package com.goldornetwork.uhc.managers;

import org.bukkit.ChatColor;

import com.goldornetwork.uhc.utils.MessageSender;

public class TimerManager implements Runnable {
	
	private static TimerManager instance = new TimerManager();
	MessageSender ms = new MessageSender();

	private int timeTillMatchStart;
	
	private int timeTillPVPStart;
	private boolean startMatch;
	
	private boolean hasMatchBegun;
	
	private boolean matchStart;
	
	private boolean startPVPTimer;
	
	private boolean isPVPEnabled;
	
	public static TimerManager getInstance(){
		return instance;
	}
	public void setup(){
		timeTillMatchStart=-2;
		timeTillPVPStart=-2;
		startMatch=false;
		hasMatchBegun=false;
		matchStart=false;
		startPVPTimer=false;
		isPVPEnabled=false;
	}
	
	public void startMatch(boolean start, int timeTillMatchStarts, int timeTillPVPStarts){
		timeTillMatchStart = timeTillMatchStarts;
		this.timeTillPVPStart= timeTillPVPStarts;
		isPVPEnabled= false;
		matchStart = true;
		startMatch =true;
	}
	
	public void cancelMatch(){
		timeTillMatchStart=-1;
		
	}
	
	public boolean hasMatchStarted(){
		return matchStart;
	}
	
	public boolean hasCountDownEnded(){
		return hasMatchBegun;
	}
	
	public boolean isPVPEnabled(){
		return isPVPEnabled;
	}
	
	@Override
	public void run() {
		if(startMatch){
			if(timeTillMatchStart >0){
				if(timeTillMatchStart>60 && timeTillMatchStart%60 ==0){
					ms.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart/60 + ChatColor.RED + " minutes");
				}
					
				else if(timeTillMatchStart <= 60 && timeTillMatchStart >=30 &&timeTillMatchStart%10==0){
					ms.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart + ChatColor.RED + " seconds");
				}
				else if(timeTillMatchStart <=30 && timeTillMatchStart >=5 && timeTillMatchStart %5==0){
					ms.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart + ChatColor.RED + " seconds");
				}
				else if(timeTillMatchStart <= 5 && timeTillMatchStart >0){
					ms.broadcast("Match Starting in " + ChatColor.GRAY + timeTillMatchStart + ChatColor.RED + " seconds");		
				}
				timeTillMatchStart--;
				
			}
			else if(timeTillMatchStart == 0){
				ms.broadcast("Match has started!");
				hasMatchBegun = true;
				startPVPTimer= true;
				startMatch =false;
			}
		
		}
		
		else if(timeTillMatchStart == -1){
			ms.broadcast("Match canceled");
			timeTillMatchStart =-2; //-2 will act as a null value
		}
		
		else if(startPVPTimer = true){
			if(timeTillPVPStart>0){
				if(timeTillPVPStart >= (5*60) && timeTillPVPStart % (10*60) == 0){
					ms.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart/60 + ChatColor.RED + " minutes");
				}
				else if(timeTillPVPStart<= (4*60) && timeTillPVPStart >= (1*60) && timeTillPVPStart % (1*60) ==0){
					ms.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart/60 + ChatColor.RED + " minutes");
				}
				else if(timeTillPVPStart < 60 && timeTillPVPStart >=30 &&timeTillPVPStart%10==0){
					ms.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart + ChatColor.RED + " seconds");
				}
				else if(timeTillPVPStart <=30 && timeTillPVPStart >=5 && timeTillPVPStart %5==0){
					ms.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart + ChatColor.RED + " seconds");
				}
				else if(timeTillPVPStart <= 5 && timeTillPVPStart >0){
					ms.broadcast("PVP will be enabled in " + ChatColor.GRAY + timeTillPVPStart + ChatColor.RED + " seconds");		
				}
				timeTillPVPStart--;
			}
			else if(timeTillPVPStart==0){
				ms.broadcast("PVP has been enabled!");
				isPVPEnabled = true;
				startPVPTimer= false;
			}
			
		}
		
	}

}
