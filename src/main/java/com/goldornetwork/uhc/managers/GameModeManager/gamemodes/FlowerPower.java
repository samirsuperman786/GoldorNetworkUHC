package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.google.common.collect.ImmutableSet;

public class FlowerPower extends Gamemode implements Listener{


	private Random random = new Random();
	private TeamManager teamM;


	public FlowerPower(TeamManager teamM) {
		super("Flower Power", "FlowerPower", "When you break a yellow or red flower, a random amount of a random item drops.");
		this.teamM= teamM;
	}
	private static final Set<Material> INVALID_BLOCKS = ImmutableSet.of(
			Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA, Material.BED_BLOCK, Material.BEDROCK,
			Material.PISTON_EXTENSION, Material.PISTON_BASE, Material.PISTON_MOVING_PIECE, Material.PISTON_STICKY_BASE, Material.REDSTONE_WIRE,
			Material.WHEAT, Material.SIGN, Material.SIGN_POST, Material.WALL_SIGN, Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR,
			Material.IRON_DOOR, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR, Material.WOOD_DOOR, Material.WOODEN_DOOR, Material.REDSTONE_TORCH_ON,
			Material.REDSTONE_TORCH_OFF, Material.GLOWING_REDSTONE_ORE, Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON,
			Material.REDSTONE_LAMP_ON, Material.REDSTONE_LAMP_OFF, Material.SUGAR_CANE_BLOCK, Material.PUMPKIN_STEM, Material.MELON_STEM,
			Material.NETHER_WARTS, Material.BREWING_STAND, Material.CAULDRON, Material.TRIPWIRE, Material.FLOWER_POT, Material.CROPS, Material.POTATO,
			Material.CARROT, Material.SKULL, Material.BARRIER, Material.COMMAND, Material.STANDING_BANNER, Material.WALL_BANNER, Material.DAYLIGHT_DETECTOR_INVERTED,
			Material.ENDER_PORTAL, Material.ENDER_PORTAL_FRAME, Material.MOB_SPAWNER, Material.MONSTER_EGG, Material.MONSTER_EGGS, Material.DIODE_BLOCK_OFF,
			Material.DIODE_BLOCK_ON, Material.DIODE, Material.IRON_DOOR_BLOCK, Material.TRIPWIRE_HOOK, Material.DOUBLE_STONE_SLAB2, Material.STONE_SLAB2,
			Material.FIREWORK_CHARGE, Material.FIRE, Material.FIREBALL, Material.BURNING_FURNACE, Material.CAKE, Material.CAKE_BLOCK, Material.COMMAND_MINECART,
			Material.DEAD_BUSH, Material.DOUBLE_PLANT, Material.DOUBLE_STEP, Material.WOOD_DOUBLE_STEP, Material.STEP, Material.ENCHANTED_BOOK,
			Material.EXPLOSIVE_MINECART, Material.EYE_OF_ENDER, Material.HAY_BLOCK, Material.HUGE_MUSHROOM_1, Material.HUGE_MUSHROOM_2, Material.LADDER,
			Material.LEAVES, Material.LEAVES_2, Material.LOG_2, Material.LOG, Material.LONG_GRASS, Material.MELON_BLOCK, Material.MAP, Material.MYCEL,
			Material.NETHER_STALK, Material.PORTAL, Material.PRISMARINE, Material.PRISMARINE_CRYSTALS, Material.PRISMARINE_SHARD, Material.SOIL, Material.VINE,
			Material.WRITTEN_BOOK, Material.EXPLOSIVE_MINECART, Material.TNT, Material.GOLD_BLOCK, Material.YELLOW_FLOWER, Material.RED_ROSE
			);

	@Override
	public void onEnable() {}

	@Override
	public void onDisable() {}

	@EventHandler(priority = EventPriority.HIGH)
	public void on(BlockBreakEvent e){
		if(State.getState().equals(State.INGAME)){
			Player p = e.getPlayer();
			if(teamM.isPlayerInGame(p.getUniqueId())){
				if(e.getBlock().getType().equals(Material.YELLOW_FLOWER) || e.getBlock().getType().equals(Material.RED_ROSE)){
					e.setCancelled(true);
					e.getBlock().setType(Material.AIR);
					run(p, e.getBlock().getLocation());
				}
			}
		}
	}

	public void run(Player p, Location loc){
		boolean foundItem=false;

		while(foundItem==false){

			int item = random.nextInt(Material.values().length);
			Material found = (Material.values()[item]);

			if(INVALID_BLOCKS.contains(found)==false){
				int amount;
				if(ARMOR_ITEMS.contains(found)){
					amount = random.nextInt(5);
				}
				else{
					amount = random.nextInt(64);
				}
				p.getWorld().dropItem(loc, new ItemStack(found, amount));
				foundItem=true;
				break;
			}
		}
	}
	
	private static final Set<Material> ARMOR_ITEMS = ImmutableSet.of(
			Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET, 
			Material.IRON_BOOTS, Material.IRON_LEGGINGS, Material.IRON_CHESTPLATE, Material.IRON_HELMET,
			Material.GOLD_BOOTS, Material.GOLD_LEGGINGS, Material.GOLD_CHESTPLATE, Material.GOLD_HELMET,
			Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET,
			Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET
			);
}
