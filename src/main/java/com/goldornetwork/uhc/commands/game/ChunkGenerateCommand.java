package com.goldornetwork.uhc.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.ChunkGenerator;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class ChunkGenerateCommand extends UHCCommand{

	//instances
	private ChunkGenerator chunkG;
	
	public ChunkGenerateCommand(ChunkGenerator chunkG) {
		super("render", "[world] [radius]");
		this.chunkG=chunkG;
	}


	@Override
	public boolean execute(CommandSender sender, String[] args) {
		
		if(State.getState().equals(State.OPEN) || State.getState().equals(State.INGAME)){
			MessageSender.send(ChatColor.RED, sender, "Cannot enter chunk generation mode during the match!");
			return true;
		}
		else if(chunkG.isGenerating()==true){
			MessageSender.send(ChatColor.RED, sender, "Please cancel current generation first!");
			return true;
		}
		else if(args.length>0){
			if(args.length==2){
				if(Bukkit.getServer().getWorld(args[0])!=null){
					World world = Bukkit.getServer().getWorld(args[0]);
					if(world.getWorldBorder()!=null){
						if(Integer.valueOf(args[1])!=null){
							int radius = Integer.valueOf(args[1]);
							chunkG.loadGenerator(world, world.getWorldBorder().getCenter(), radius);
							MessageSender.broadcast("Generating chunks with a radius of " + radius + " in world \"" + world.getName()  + "\" ETC : " + (chunkG.getTimeTillCompleteInMinutes()) + " minutes");
							MessageSender.broadcast("Warning- the server should be empty during the generation period.");
							return true;
						}
						else{
							return false;
						}

					}
					else{
						if(Integer.valueOf(args[1])!=null){
							int radius = Integer.valueOf(args[1]);
							chunkG.loadGenerator(world, new Location(world, 0, 0, 0) , radius);
							MessageSender.broadcast("Generating chunks with a radius of " + radius + " in world \"" + world.getName() + "\" ETC : " + (chunkG.getTimeTillCompleteInMinutes()) + " minutes");
							MessageSender.broadcast("Warning- the server should be empty during the generation period.");
							return true;
						}
						else{
							return false;
						}
					}

				}
				else{
					MessageSender.send(ChatColor.RED, sender, "World " + args[0] + " does not exist!" );
					return true;
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
		List<String> toReturn = new ArrayList<String>();
		
		if(args.length==1){
			for(World world : Bukkit.getWorlds()){
				toReturn.add(world.getName());
			}
		}
		return toReturn;
	}


}
