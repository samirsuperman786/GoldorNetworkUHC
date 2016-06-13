package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;

public class BedBombs extends Gamemode implements Listener {

	private Random random = new Random();
	private TeamManager teamM;
	
	public BedBombs(TeamManager teamM) {
		super("Bed Bombs", "BedBombs", "Beds explode when clicked on.");
		this.teamM=teamM;
	}
	
	@EventHandler
	public void on(PlayerInteractEvent e){
		if(!(State.getState().equals(State.INGAME))){
			return;
		}
		
		Block block = e.getClickedBlock();
		Player p = e.getPlayer();
		
		if(teamM.isPlayerInGame(p.getUniqueId())==false){
			return;
		}
		
		else if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		else if (block == null) {
			return;
		}
		
		else if(block.getType().equals(Material.BED_BLOCK)==false){
			return;
		}
		
		block.setType(Material.AIR);
		e.setCancelled(true);
		
		Location pLoc = block.getLocation();
		block.getWorld().createExplosion(pLoc.getX(), pLoc.getY(), pLoc.getZ(), random.nextInt(3) + 1, false, true);
		
	}
}
