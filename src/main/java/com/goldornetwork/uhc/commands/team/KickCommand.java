package com.goldornetwork.uhc.commands.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class KickCommand extends UHCCommand{


	private TeamManager teamM;


	public KickCommand(TeamManager teamM) {
		super("kick", "[player]");
		this.teamM=teamM;
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		if(!(State.getState().equals(State.OPEN))){
			MessageSender.send(sender, ChatColor.RED + "Can only perform this action during open phase.");
			return true;
		}
		else if(args.length==1){

			if(teamM.isPlayerInGame(sender.getUniqueId())){
				if(teamM.isPlayerOwner(teamM.getTeamOfPlayer(sender.getUniqueId()), sender.getUniqueId())){
					if(PlayerUtils.isPlayerOnline(args[0])){
						Player target = PlayerUtils.getPlayer(args[0]);
						if(sender.equals(target)){
							MessageSender.send(sender, ChatColor.RED + "You can not kick yourself from your team.");
							return true;
						}
						else if(teamM.areTeamMates(sender.getUniqueId(), target.getUniqueId())){
							teamM.removePlayerFromTeam(target.getUniqueId());

							MessageSender.alertMessage(target, ChatColor.RED + "You have been kicked from team "
									+ teamM.getColorOfTeam(teamM.getTeamOfPlayer(sender.getUniqueId()))  
									+ teamM.getTeamNameProper(teamM.getTeamOfPlayer(sender.getUniqueId())) 
									+ ChatColor.RED + " by " + teamM.getColorOfPlayer(sender.getUniqueId()) + sender.getName());

							for(UUID u : teamM.getPlayersOnATeam(teamM.getTeamOfPlayer(sender.getUniqueId()))){
								if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){

									MessageSender.alertMessage(Bukkit.getServer().getPlayer(u), ChatColor.GREEN + target.getName()
									+ ChatColor.GOLD + " has been kicked from the team.");

								}
							}
							return true;
						}
						else{
							MessageSender.send(sender, ChatColor.RED + "Player " + target.getName() + " is not on your team.");
							return true;
						}
					}
					else{
						MessageSender.send(sender, ChatColor.RED + "Player " + args[0] + " is not online.");
						return true;
					}
				}
				else{
					MessageSender.send(sender, ChatColor.RED + "You are not the owner.");
					return true;
				}
			}
			else{
				MessageSender.send(sender, ChatColor.RED + "You are not in the game.");
				return true;
			}
		}
		else{
			return false;
		}
	}

	@Override
	public List<String> tabComplete(Player sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();

		if(args.length==1){

			if(teamM.isPlayerOnTeam(sender.getUniqueId())){
				if(teamM.isPlayerOwner(teamM.getTeamOfPlayer(sender.getUniqueId()), sender.getUniqueId())){
					for(UUID u : teamM.getPlayersOnATeam(teamM.getTeamOfPlayer(sender.getUniqueId()))){
						toReturn.add(Bukkit.getServer().getOfflinePlayer(u).getName());
					}
				}
			}
		}
		return toReturn;
	}
}
