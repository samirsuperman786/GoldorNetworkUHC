package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.listeners.BackGround;
import com.goldornetwork.uhc.listeners.MoveEvent;
import com.goldornetwork.uhc.managers.GameModeManager.GameStartEvent;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;
import com.google.common.collect.ImmutableSet;


public class ScatterManager{

	//TODO check if spawn location is valid 

	//instances
	private UHC plugin;
	private TeamManager teamM;
	private MoveEvent moveE;
	private BackGround backG;
	//storage
	private boolean scatterComplete;
	private int radius;
	private boolean generated;
	private final int LOADS_PER_SECOND = 5;
	private int k;
	private int a;
	private BlockFace[] faces = new BlockFace[] { BlockFace.SELF, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST};

	//storage
	private Map<String, List<UUID>> teamToScatter = new HashMap<String, List<UUID>>();
	private Map<String, Location> locationsOfTeamSpawn = new HashMap<String, Location>();
	private Map<UUID, Location> locationsOfFFA = new HashMap<UUID, Location>();
	private List<UUID> lateScatters = new ArrayList<UUID>();
	private List<String> nameOfTeams = new ArrayList<String>();
	private List<UUID> FFAToScatter= new ArrayList<UUID>();

	public ScatterManager(UHC plugin, TeamManager teamM, MoveEvent moveE, BackGround backG) {
		this.plugin=plugin;
		this.teamM=teamM;
		this.moveE=moveE;
		this.backG=backG;
		config();
	}

	private void config(){
		plugin.getConfig().addDefault("radius", 1000);
		plugin.saveConfig();
	}

	/**
	 * Does the following: unfreezes players, disables pvp, disables mob spawning, disables natural regeneration, sets difficulty to hard, will clear scattered players, 
	 * will re-initialize the world border, will clear entities.
	 */
	public void setup(){
		moveE.unfreezePlayers();
		radius = plugin.getConfig().getInt("radius");
		getUHCWorld().setPVP(false);
		getUHCWorld().setGameRuleValue("doMobSpawning", "false");
		getUHCWorld().setGameRuleValue("naturalRegeneration", "false");
		getUHCWorld().setDifficulty(Difficulty.HARD);
		scatterComplete=false;
		teamToScatter.clear();
		locationsOfTeamSpawn.clear();
		lateScatters.clear();
		nameOfTeams.clear();
		WorldBorder wb = getUHCWorld().getWorldBorder();
		wb.setCenter(getUHCWorld().getSpawnLocation());
		wb.setSize(radius*2);
		wb.setDamageBuffer(0);
		wb.setDamageAmount(.5);
		wb.setWarningTime(15);
		wb.setWarningDistance(20);
		for(Entity e : getUHCWorld().getEntities()){
			if(!(e instanceof Player)){
				e.remove();
			}
		}
		//Test code here


	}

	/**
	 * Used to check state of scattering
	 * @return <code> True </code> if scattering has completed
	 */
	public boolean isScatteringComplete(){
		return scatterComplete;
	}
	public void enableFFA(){
		MessageSender.broadcast(ChatColor.GOLD + "Generating chunks...");
		FFAToScatter.addAll(teamM.getPlayersInGame());
		List<Location> toGenerate = new ArrayList<Location>();
		for(UUID u : FFAToScatter){
			Location loc = findValidLocation(getUHCWorld(), radius);
			toGenerate.add(loc);
			locationsOfFFA.put(u, loc);	
		}
		generate(toGenerate);
	}
	public void enableTeams(){
		MessageSender.broadcast(ChatColor.GOLD + "Generating chunks...");
		List<Location> toGenerate = new ArrayList<Location>();
		for(String team : teamM.getActiveTeams()){
			Location location = findValidLocation(getUHCWorld(), radius);
			toGenerate.add(location);
			locationsOfTeamSpawn.put(team, location);
			nameOfTeams.add(team);
			teamToScatter.put(team, teamM.getPlayersOnATeam(team));
		}
		generate(toGenerate);

	}

	private void generate(List<Location> loc){
		a=0;
		new BukkitRunnable() {

			@Override
			public void run() {
				if(a==(loc.size()-1)){
					if(teamM.isFFAEnabled()){
						scatterFFA();
					}
					else if(teamM.isTeamsEnabled()){
						scatterTeams();
					}
					cancel();
				}
				getUHCWorld().loadChunk(getUHCWorld().getChunkAt(loc.get(a)));
				a++;
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}
	/**
	 * Will scatter all teams and freeze them until scattering has completed
	 */
	private void scatterTeams(){
		MessageSender.broadcast(ChatColor.GOLD + "Scattering players...");
		k=0;
		moveE.freezePlayers();
		new BukkitRunnable() {
			@Override
			public void run() {
				if(teamToScatter.isEmpty()){
					Bukkit.getServer().getLogger().info("Error at scattering");
					cancel();
					return;
				}
				else{
					if(k>=teamM.getActiveTeams().size()){
						setupStartingOptions();
						scatterComplete=true;
						cancel();
					}
					else{
						for(UUID u : teamToScatter.get(nameOfTeams.get(k))){
							Location location = locationsOfTeamSpawn.get(nameOfTeams.get(k));
							OfflinePlayer p = Bukkit.getOfflinePlayer(u);
							if(p.isOnline()==false){
								lateScatters.add(u);
							}
							else if(p.isOnline()==true){
								Player target = (Player) p;
								initializePlayer(target);
								target.teleport(location);

							}
						}
						k++;
					}

				}

			}
		}.runTaskTimer(plugin, 0L, 20L);
	}


	/**
	 * Will scatter all players in game and freeze them until complete
	 */
	private void scatterFFA(){
		moveE.freezePlayers();
		k=0;
		new BukkitRunnable() {

			@Override
			public void run() {
				for(int i = 0; i<LOADS_PER_SECOND; i++){
					if(k>=FFAToScatter.size()){
						scatterComplete=true;
						setupStartingOptions();
						cancel();
						return;
					}
					OfflinePlayer p = Bukkit.getOfflinePlayer(FFAToScatter.get(k));
					Location location = locationsOfFFA.get(FFAToScatter.get(k));
					if(p.isOnline()==false){
						lateScatters.add(p.getUniqueId());
					}
					else if(p.isOnline()==true){
						Player target = (Player) p;
						initializePlayer(target);
						target.teleport(location);
					}
					k++;
				}

			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	/** Used to get the radius of the map
	 * @return <code> Integer </code> radius of the map
	 */
	public int getRadius(){
		return radius;
	}
	/**
	 * Used to get a safe teleportable location 
	 * @param world - the world the location should be found in
	 * @param radius - the radius of the world that the locations should be found within
	 * @return <code> Location </code> location of safe spawn
	 * @see validate()
	 */
	private Location findValidLocation(World world, int radius){
		boolean valid = false;
		Location location = new Location(world, 0, 0, 0);
		while(valid ==false){
			Random random = new Random();
			int x = random.nextInt(radius * 2) - radius;
			int z = random.nextInt(radius * 2) - radius;
			x= x+ getCenter().getBlockX();
			z= z+ getCenter().getBlockZ();
			//int x = random.nextInt((radius*2) - (-radius*2) +1) + (-radius*2);
			//int z = random.nextInt((radius*2) - (-radius*2) +1) + (-radius*2);
			location.setX(x);
			location.setZ(z);
			location.setY(getUHCWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()));
			if(validate(location.clone())){
				valid=true;
				break;
			}
		}
		return location;
	}

	/**
	 * Used to check if the a given location is appropriate for teleporting conditions
	 * @param loc - the location to check
	 * @return <code> True </code> if location is safe to teleport player to
	 * @see findValidLocation()
	 */
	private boolean validate(Location loc){
		boolean valid = true;
		if(loc.getBlockY()<60){
			valid =false;
		}

		for(Material notValid : INVALID_SPAWN_BLOCKS){
			for(BlockFace face : faces){
				//getting the block at land
				if(loc.getBlock().getRelative(face).getType().equals(notValid) || loc.getBlock().getRelative(face).getType().equals(Material.AIR)){
					valid=false;
				}

				//getting the block above land
				if(!(loc.clone().add(0, 1, 0).getBlock().getRelative(face).getType().equals(Material.AIR))){
					valid=false;
				}

				//getting the block 2 above land
				if(!(loc.clone().add(0, 2, 0).getBlock().getRelative(face).getType().equals(Material.AIR))){
					valid = false;
				}
			}
		}

		return valid;
	}


	public void handleLateScatter(Player p){
		if(teamM.isFFAEnabled()){
			lateScatterAPlayerInFFA(p);
		}
		else if(teamM.isTeamsEnabled()){
			lateScatterAPlayerInATeam(teamM.getTeamOfPlayer(p), p);
		}
	}
	/**
	 * Used to handle players who were disconnected and need to be scattered explicitly in a FFA context
	 * @param p - the player who needs to be teleported
	 */
	private void lateScatterAPlayerInFFA(Player p){
		Location location = locationsOfFFA.get(p.getUniqueId());
		initializePlayer(p);
		p.teleport(location);
	}

	/**
	 * Used to handle players who were disconnected and need to be scattered explicitly in a team context
	 * @param team - the team of the player
	 * @param p - the player who needs to be teleported 
	 */
	private void lateScatterAPlayerInATeam(String team, Player p){
		initializePlayer(p);
		p.teleport(locationsOfTeamSpawn.get(team.toLowerCase()));
	}
	private void initializePlayer(Player p){
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.setGameMode(GameMode.SURVIVAL);
		p.setBedSpawnLocation(getCenter());
		p.setLevel(0);
		p.setExp(0L);
	}
	/**
	 * Used to indicate that the player no longer needs to be scattered
	 * @param p - the player who no longer needs to be scattered
	 */
	public void removePlayerFromLateScatters(Player p){
		lateScatters.remove(p.getUniqueId());
	}


	/** Used to retrieve a list of players who need to be scattered
	 * @return <code> List </code> of players who need to be scattered
	 */
	public List<UUID> getLateScatters(){
		return this.lateScatters;
	}

	/**
	 * Used to get the world the match is being played in
	 * @return <code> World </code> of match
	 */
	public World getUHCWorld(){
		//TODO make a rotation list of viable UHC maps
		return plugin.getServer().createWorld(new WorldCreator("lol"));
	}
	/**
	 * Used to retrieve the center of the match
	 * @return <code> Location </code> of the center of the match
	 */
	public Location getCenter(){
		return getUHCWorld().getSpawnLocation();
	}

	/**
	 * Used to shrink the border of the world, ideally when PVP is enabled
	 */
	public void shrinkBorder(){
		getUHCWorld().getWorldBorder().setSize(400, 15*60);
		MessageSender.broadcast("The worldborder will now slowly shrink to a radius of 400.");
	}

	/**
	 * A list of blocks that we do not want players to spawn on
	 * @see findValidLocation()
	 * @see validate()
	 */
	private static final Set<Material> INVALID_SPAWN_BLOCKS = ImmutableSet.of(
			Material.STATIONARY_LAVA,
			Material.LAVA, 
			Material.WATER, 
			Material.STATIONARY_WATER, 
			Material.CACTUS,
			Material.LEAVES,
			Material.LEAVES_2
			);

	/**
	 * Called when scattering has completed, it sets up world conditions
	 */
	private void setupStartingOptions() {
		//adding a slight delay 
		new BukkitRunnable() {

			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(new GameStartEvent());
				State.setState(State.INGAME);
				moveE.unfreezePlayers();
				MessageSender.broadcast(ChatColor.GOLD + "Scattering complete!");
				backG.unMutePlayers();
				getUHCWorld().setGameRuleValue("doMobSpawning", "true");
				getUHCWorld().setGameRuleValue("dodaylightcycle", "true");
				getUHCWorld().setTime(0);
			}
		}.runTaskLater(plugin, 100L);

	}

	public void prePVPSetup(){
		getUHCWorld().setTime(0);
		getUHCWorld().setGameRuleValue("dodaylightcycle", "false");
	}
	public World getLobby(){
		return plugin.getServer().createWorld(new WorldCreator("Lobby"));
	}

}
