package com.goldornetwork.uhc.commands.team;

import java.util.ArrayList;
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

public class KickCommand extends UHCCommand{

	private TeamManager teamM;
	public KickCommand(TeamManager teamM) {
		super("kick", "[player]");
		this.teamM=teamM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player kicker = (Player) sender;
		if(!(sender instanceof Player)){
			MessageSender.noConsole(sender);
			return true;
		}
		else if(!(State.getState().equals(State.OPEN))){
			MessageSender.send(kicker, ChatColor.RED + "Can only perform this action during open phase.");
			return true;
		}
		else if(args.length==1){
			if(teamM.isPlayerInGame(kicker.getUniqueId())){
				if(teamM.isPlayerOwner(teamM.getTeamOfPlayer(kicker.getUniqueId()), kicker.getUniqueId())){
					if(Bukkit.getOfflinePlayer(args[0]).isOnline()){
						Player target = Bukkit.getPlayer(args[0]);
						if(kicker.equals(target)){
							MessageSender.send(kicker, ChatColor.RED + "You can not kick yourself from your team.");
							return true;
						}
						else if(teamM.areTeamMates(kicker, target)){
							teamM.removePlayerFromTeam(target.getUniqueId());
							MessageSender.alertMessage(target, ChatColor.RED + "You have been kicked from team " + teamM.getColorOfTeam(teamM.getTeamOfPlayer(kicker.getUniqueId())) + teamM.getTeamNameProper(teamM.getTeamOfPlayer(kicker.getUniqueId())) + ChatColor.RED + " by " + teamM.getColorOfPlayer(kicker.getUniqueId()) + kicker.getName());
							for(UUID u : teamM.getPlayersOnATeam(teamM.getTeamOfPlayer(kicker.getUniqueId()))){
								if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
									MessageSender.alertMessage(Bukkit.getServer().getPlayer(u), ChatColor.GREEN + target.getName() + ChatColor.GOLD + " has been kicked from the team.");
								}
							}
							return true;
						}
						else{
							MessageSender.send(kicker, ChatColor.RED + "Player " + target.getName() + " is not on your team.");
							return true;
						}
						
					}
					else{
						MessageSender.send(kicker, ChatColor.RED + "Player " + args[0] + " is not online.");
						return true;
					}
					
				}
				else{
					MessageSender.send(kicker, ChatColor.RED + "You are not the owner.");
					return true;
				}
				
			}
			else{
				MessageSender.send(kicker, ChatColor.RED + "You are not in the game.");
				return true;
			}
		}
		else{
			return false;
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		Player p = (Player) sender;
		if(args.length==1){
			if(teamM.isPlayerOnTeam(p.getUniqueId())){
				if(teamM.isPlayerOwner(teamM.getTeamOfPlayer(p.getUniqueId()), p.getUniqueId())){
					for(UUID u : teamM.getPlayersOnATeam(teamM.getTeamOfPlayer(p.getUniqueId()))){
						toReturn.add(Bukkit.getServer().getOfflinePlayer(u).getName());
					}
				}
				
			}
		}
		return toReturn;
	}

}
