package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class KillSwitch extends Gamemode implements Listener{

	
	public KillSwitch() {
		super("Kill Switch", "KillSwitch", "When a player kills another player, that player will switch inventories!");
	}
	@Override
	public void onEnable() {}
	
	@Override
	public void onDisable() {}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void on(PlayerDeathEvent e){
		if(State.getState().equals(State.INGAME)){
			Player p = e.getEntity();
			if(p.getKiller() instanceof Player){
				Player killer = p.getKiller();
				run(p, killer, e);
			}
		}
	}
	private void run(Player target, Player killer, PlayerDeathEvent e){
		ItemStack[] targetInventory = target.getInventory().getContents();
		Inventory killerInventory = killer.getInventory();
		killerInventory.clear();
		killerInventory.setContents(targetInventory);
		killer.getInventory().setArmorContents(target.getInventory().getArmorContents());
		e.getDrops().clear();
		MessageSender.alertMessage(killer, ChatColor.GOLD, "You have switched inventories with " +  target.getName());
	}
	
}
