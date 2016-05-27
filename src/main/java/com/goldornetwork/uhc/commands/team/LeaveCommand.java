package com.goldornetwork.uhc.commands.team;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		if(!(sender instanceof Player)){
			MessageSender.noConsole(sender);
			return true;
		}
		else if(teamM.isPlayerInGame(p)==false){
			MessageSender.send(ChatColor.RED, sender, "You are not on a team");
			return true;
		}
		else if(args.length==0){
			if(State.getState().equals(State.INGAME) || State.getState().equals(State.SCATTER)){
				p.setHealth(0);
				return true;
			}
			else if(State.getState().equals(State.OPEN)){
				if(teamM.isFFAEnabled()){
					teamM.removePlayerFromFFA(p);
					MessageSender.send(ChatColor.GOLD, p, "You have left the FFA!");
					return true;
				}
				else if(teamM.isTeamsEnabled()){
					if(teamM.isPlayerOwner(p)){
						teamM.removePlayerFromOwner(p);
					}
					else{
						for(UUID u : teamM.getPlayersOnATeam(teamM.getTeamOfPlayer(p))){
							if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
								MessageSender.alertMessage(Bukkit.getServer().getPlayer(u), ChatColor.GOLD, teamM.getColorOfPlayer(p) + p.getName() + ChatColor.GOLD + " has left the team.");
							}
						}
						teamM.removePlayerFromTeam(p);
					}
					return true;
				}
				else{
					return false;
				}
			}
			else{
				return false;
			}

		}
		else{
			return false;
		}

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null;
	}


}
