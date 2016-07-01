package com.goldornetwork.uhc.commands.team;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class LeaveCommand extends UHCCommand{


	private TeamManager teamM;


	public LeaveCommand(TeamManager teamM) {
		super("leave", "");
		this.teamM=teamM;
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		if(teamM.isPlayerOnTeam(sender.getUniqueId())==false){
			MessageSender.send(sender, ChatColor.RED + "You are not on a team.");
			return true;
		}
		else if(args.length==0){

			if(State.getState().equals(State.INGAME) || State.getState().equals(State.SCATTER)){
				sender.setHealth(0);
				return true;
			}
			else if(State.getState().equals(State.OPEN)){
				if(teamM.isTeamsEnabled()){
					if(teamM.isPlayerOwner(teamM.getTeamOfPlayer(sender.getUniqueId()), sender.getUniqueId())){
						String team = teamM.getTeamOfPlayer(sender.getUniqueId());
						teamM.removePlayerFromOwner(team, sender.getUniqueId());

						for(UUID u : teamM.getPlayersOnATeam(team)){
							if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
								MessageSender.alertMessage(Bukkit.getServer().getPlayer(u), ChatColor.RED + "Your team has been disbanded by " + sender.getName());
							}
						}
						
						teamM.disbandTeam(team);
						
					}
					else{
						for(UUID u : teamM.getPlayersOnATeam(teamM.getTeamOfPlayer(sender.getUniqueId()))){
							if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
								MessageSender.alertMessage(Bukkit.getServer().getPlayer(u), ChatColor.GREEN + sender.getName() + ChatColor.GOLD + " has left the team.");
							}
						}
						teamM.removePlayerFromTeam(sender.getUniqueId());
					}
					return true;
				}
				else{
					MessageSender.send(sender, "Teams are not enabled yet.");
					return true;
				}
			}
			else{
				MessageSender.send(sender, "Can not leave a team yet.");
				return true;
			}
		}
		else{
			return false;
		}
	}

	@Override
	public List<String> tabComplete(Player sender, String[] args) {
		return null;
	}
}
