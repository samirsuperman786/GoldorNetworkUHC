package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.ChunkGenerator;
import com.goldornetwork.uhc.managers.world.CoordXZ;
import com.goldornetwork.uhc.managers.world.WorldFactory;
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.managers.world.events.FindLocationEvent;
import com.goldornetwork.uhc.managers.world.events.GameStartEvent;
import com.goldornetwork.uhc.managers.world.events.LateScatterEvent;
import com.goldornetwork.uhc.managers.world.events.ScatterEndEvent;
import com.goldornetwork.uhc.managers.world.events.ScatterStartEvent;
import com.goldornetwork.uhc.managers.world.events.TeleportTeamEvent;
import com.goldornetwork.uhc.managers.world.listeners.MoveEvent;
import com.goldornetwork.uhc.managers.world.listeners.team.ChatManager;
import com.goldornetwork.uhc.utils.Medic;
import com.goldornetwork.uhc.utils.MessageSender;
import com.google.common.collect.ImmutableSet;

import net.minecraft.server.v1_8_R3.MinecraftServer;


public class ScatterManager implements Listener{

	//TODO check if spawn location is valid 

	//instances
	private UHC plugin;
	private TeamManager teamM;
	private MoveEvent moveE;
	private ChatManager chatM;
	private WorldFactory worldF;
	private ChunkGenerator chunkG;
	private WorldManager worldM;
	//storage
	private boolean scatterComplete;
	private boolean teleported;
	private boolean lateScatterComplete;
	private int radius;
	private int timer;
	private BlockFace[] faces = new BlockFace[] {
			BlockFace.SELF, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, 
			BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST};

	//storage
	private List<Location> validatedLocs = new ArrayList<Location>();
	private List<Chunk> chunksToKeepLoaded = new ArrayList<Chunk>();
	private Map<String, List<UUID>> teamToScatter = new HashMap<String, List<UUID>>();
	private Map<String, Location> locationsOfTeamSpawn = new HashMap<String, Location>();
	private List<UUID> lateScatters = new ArrayList<UUID>();
	private List<String> nameOfTeams = new ArrayList<String>();
	private BlockingQueue<String> teamReadyToScatter;
	private BlockingQueue<UUID> lateScatterReadyToScatter;
	private Map<String, Boolean> isTeamOnline = new HashMap<String, Boolean>();
	private List<UUID> lateSafeCheck = new ArrayList<UUID>();

	public ScatterManager(UHC plugin, TeamManager teamM, MoveEvent moveE, ChatManager chatM, WorldFactory worldF, ChunkGenerator chunkG, WorldManager worldM) {
		this.plugin=plugin;
		this.teamM=teamM;
		this.moveE=moveE;
		this.chatM=chatM;
		this.worldF=worldF;
		this.chunkG=chunkG;
		this.worldM=worldM;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
		config();
		radius = plugin.getConfig().getInt("radius");
		scatterComplete=false;
		teamToScatter.clear();
		locationsOfTeamSpawn.clear();
		lateScatters.clear();
		nameOfTeams.clear();
	}

	@EventHandler
	public void on(ChunkUnloadEvent e){
		if(State.getState().equals(State.SCATTER)){
			if(chunksToKeepLoaded.contains(e.getChunk())){
				e.setCancelled(true);
			}
		}
	}

	/**
	 * Used to check state of scattering
	 * @return <code> True </code> if scattering has completed
	 */
	public boolean isScatteringComplete(){
		return scatterComplete;
	}
	public void scatter(){
		nameOfTeams.addAll(teamM.getActiveTeams());
		this.teamReadyToScatter= new ArrayBlockingQueue<>(nameOfTeams.size());
		this.lateScatterReadyToScatter= new ArrayBlockingQueue<>((nameOfTeams.size() * teamM.getTeamSize()) + 1);
		teamReadyToScatter.addAll(nameOfTeams);

		for(String team : teamM.getActiveTeams()){
			teamToScatter.put(team, teamM.getPlayersOnATeam(team));
			this.isTeamOnline.put(team, false);
		}
		plugin.getServer().getPluginManager().callEvent(new ScatterStartEvent());
		findLocations(nameOfTeams.size());
	}

	@EventHandler
	public void on(FindLocationEvent e){
		if(timer >=e.getNumberOfLocations()){

			int j = 0;
			for(String team : teamM.getActiveTeams()){
				locationsOfTeamSpawn.put(team, validatedLocs.get(j));
				j++;
			}
			generate(validatedLocs);
		}

		else{

			World UHCWorld= worldM.getUHCWorld();
			CoordXZ toLoad= randomLocation(UHCWorld, radius);
			UHCWorld.getChunkAt(toLoad.x, toLoad.z).load(true);

			((CraftWorld)UHCWorld).getHandle().chunkProviderServer.getChunkAt(toLoad.x, toLoad.z, new Runnable() {
				@Override
				public void run() {
					MinecraftServer.getServer().processQueue.add(new Runnable() {
						@Override
						public void run() {
							new BukkitRunnable() {

								@Override
								public void run() {
									Location toCheck = new Location(UHCWorld, toLoad.x, UHCWorld.getHighestBlockYAt(toLoad.x, toLoad.z), toLoad.z);
									if(validate(toCheck)){
										validatedLocs.add(toCheck);
										chunksToKeepLoaded.add(toCheck.getChunk());
										++timer;
									}
									else{
										UHCWorld.unloadChunkRequest(toLoad.x, toLoad.z);
									}
									new BukkitRunnable() {

										@Override
										public void run() {
											plugin.getServer().getPluginManager().callEvent(new FindLocationEvent(e.getNumberOfLocations()));

										}
									}.runTaskLater(plugin, 5L);

								}
							}.runTaskLater(plugin, 5L);




						}
					});
				}
			});




		}
	}
	private void findLocations(int numberOfLocationsToFind){
		chunkG.cancelGeneration();
		timer=0;
		MessageSender.broadcast("Finding locations...");
		plugin.getServer().getPluginManager().callEvent(new FindLocationEvent(numberOfLocationsToFind));
	}
	private void generate(List<Location> loc){
		timer=0;
		MessageSender.broadcast("Generating...");
		new BukkitRunnable() {

			@Override
			public void run() {
				if(timer>=loc.size()){

					if(teamM.isTeamsEnabled()){
						scatterTeams();
					}
					cancel();

				}
				else{
					int x = loc.get(timer).getBlockX();
					int z = loc.get(timer).getBlockZ();
					boolean isXPos= x >= 0;
					boolean isZPos= z >= 0;
					int popX = isXPos ? x-16 : x+16;
					int popZ = isZPos ? z-16 : z+16;

					worldM.getUHCWorld().getChunkAt(x, z).load(true);
					worldM.getUHCWorld().getChunkAt(popX, popZ).load(false);
					++timer;
				}

			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	private boolean moveToNextTeam(String currentTeam){
		boolean move=false;
		if(teleported){
			move=true;
		}
		if(isTeamOnline.get(currentTeam)==false){
			move=true;
		}
		return move;
	}
	@EventHandler
	public void on(TeleportTeamEvent e) throws InterruptedException{
		if(teamReadyToScatter.isEmpty()){
			scatterComplete=true;
			plugin.getServer().getPluginManager().callEvent(new ScatterEndEvent());
			return;
		}
		else{
			String team = teamReadyToScatter.poll();

			Location location = locationsOfTeamSpawn.get(team).clone();
			location.getChunk().load(true);

			((CraftWorld)location.getWorld()).getHandle().chunkProviderServer.getChunkAt(location.getBlockX(), location.getBlockZ(), new Runnable() {
				@Override
				public void run() {
					MinecraftServer.getServer().processQueue.add(new Runnable() {
						@Override
						public void run() {

							new BukkitRunnable() {

								@Override
								public void run() {
									//Location safeLocation = new Location(location.getWorld(), location.getBlockX(), location.getWorld().getHighestBlockYAt(location), location.getBlockZ());

									for(UUID u : teamToScatter.get(team)){
										OfflinePlayer p = Bukkit.getOfflinePlayer(u);
										if(p.isOnline()==false){
											lateScatters.add(u);
										}
										else if(p.isOnline()==true){
											isTeamOnline.put(team, true);
											Player target = (Player) p;
											initializePlayer(target);
											teleported = target.teleport(location);
											safeCheck(target);
										}

									}

								}
							}.runTaskLater(plugin, 10L);


							new BukkitRunnable() {

								@Override
								public void run() {

									if(moveToNextTeam(team)){
										new BukkitRunnable() {

											@Override
											public void run() {
												Bukkit.getPluginManager().callEvent(new TeleportTeamEvent());

											}
										}.runTaskLater(plugin, 130L);
										cancel();
									}


								}
							}.runTaskTimer(plugin, 20L, 20L);

						}
					});
				}
			});
		}


	}
	/**
	 * Will scatter all teams and freeze them until scattering has completed
	 */
	private void scatterTeams(){
		timer=0;
		MessageSender.broadcast("Scattering teams...");
		moveE.freezePlayers();
		Bukkit.getPluginManager().callEvent(new TeleportTeamEvent());
	}




	/** Used to get the radius of the map
	 * @return <code> Integer </code> radius of the map
	 */
	public int getRadius(){
		return radius;
	}

	private CoordXZ randomLocation(World world, int radius){
		Random random = new Random();
		int x = random.nextInt(radius * 2) - radius;
		int z = random.nextInt(radius * 2) - radius;
		x= x+ worldM.getCenter().getBlockX();
		z= z+ worldM.getCenter().getBlockZ();
		return new CoordXZ(x, z);
	}


	/**
	 * Used to check if the a given location is appropriate for teleporting conditions
	 * @param loc - the location to check
	 * @return <code> True </code> if location is safe to teleport player to
	 * @see findValidLocation()
	 */
	private boolean validate(Location loc){
		boolean valid = true;
		Block landBlock = loc.clone().add(0, -1, 0).getBlock();
		Block oneAboveLand = loc.clone().getBlock();
		Block twoAboveLand = loc.clone().add(0, 1, 0).getBlock();

		checkLoop:
			for(BlockFace face : faces){

				if(VALID_SPAWN_BLOCKS.contains(landBlock.getRelative(face).getType())==false){
					valid=false;
					break checkLoop;
				}

				else if(oneAboveLand.getRelative(face).getType()!=(Material.AIR)){
					valid=false;
					break checkLoop;
				}

				else if(twoAboveLand.getRelative(face).getType()!=(Material.AIR)){
					valid = false;
					break checkLoop;
				}

			}


		return valid;
	}


	@EventHandler
	public void on(PlayerJoinEvent e){
		Player target = e.getPlayer();
		if(State.getState().equals(State.INGAME)|| State.getState().equals(State.SCATTER)){
			if(teamM.isPlayerInGame(e.getPlayer().getUniqueId())){
				if(getLateScatters().contains(target.getUniqueId()) && !(lateScatterReadyToScatter.contains(target.getUniqueId()))){
					handleLateScatter(target);
				}
				if(lateSafeCheck.contains(target.getUniqueId())){
						safeCheck(target);
						lateSafeCheck.remove(target.getUniqueId());
					
				}

			}
			else if(teamM.isPlayerInGame(e.getPlayer().getUniqueId())==false){
				if(target.getWorld().equals(worldM.getUHCWorld())==false){
					target.teleport(worldM.getUHCWorld().getSpawnLocation());
				}
				if(teamM.isPlayerAnObserver(target.getUniqueId())==false){
					teamM.addPlayerToObservers(target);
				}
				else{
					MessageSender.send(ChatColor.AQUA, target, "You are now spectating the game");
				}

			}
		}

	}

	@EventHandler
	public void on(LateScatterEvent e){
		if(lateScatterReadyToScatter.isEmpty()){
			return;
		}
		else if(!(teamM.isPlayerOnTeam(lateScatterReadyToScatter.peek()))){
			lateScatterReadyToScatter.poll();
			Bukkit.getPluginManager().callEvent(new LateScatterEvent());
		}
		else{
			UUID u = lateScatterReadyToScatter.poll();
			Location location = locationsOfTeamSpawn.get(teamM.getTeamOfPlayer(u));
			location.getChunk().load(true);

			((CraftWorld)location.getWorld()).getHandle().chunkProviderServer.getChunkAt(location.getBlockX(), location.getBlockZ(), new Runnable() {
				@Override
				public void run() {
					MinecraftServer.getServer().processQueue.add(new Runnable() {
						@Override
						public void run() {

							Location safeLocation = new Location(location.getWorld(), location.getBlockX(), location.getWorld().getHighestBlockYAt(location), location.getBlockZ());


							OfflinePlayer p = Bukkit.getOfflinePlayer(u);
							if(teamM.isPlayerOnTeam(u)){
								if(p.isOnline()==false){
									lateScatters.add(u);
									Bukkit.getPluginManager().callEvent(new LateScatterEvent());
								}
								else if(p.isOnline()==true){
									Player target = (Player) p;
									initializePlayer(target);
									lateScatterComplete = target.teleport(safeLocation);
									safeCheck(target);
									new BukkitRunnable() {

										@Override
										public void run() {

											if(lateScatterComplete){
												new BukkitRunnable() {

													@Override
													public void run() {
														Bukkit.getPluginManager().callEvent(new LateScatterEvent());

													}
												}.runTaskLater(plugin, 120L);
												cancel();
											}


										}
									}.runTaskTimer(plugin, 20L, 20L);


								}
							}


						}
					});
				}
			});
		}


	}

	private void safeCheck(OfflinePlayer p){

		new BukkitRunnable() {

			@Override
			public void run() {

				if(p.isOnline()){
					Player target = (Player) p;
					Block atLand = target.getLocation().clone().add(0, -1, 0).getBlock();
					Block oneAboveLand = target.getLocation().getBlock();
					Block twoAboveLand = target.getLocation().clone().add(0, 1, 0).getBlock();
					Block threeAboveLand = target.getLocation().clone().add(0, 2, 0).getBlock();

					for(BlockFace face : faces){
						atLand.getRelative(face).setType(Material.GLASS);
						oneAboveLand.getRelative(face).setType(Material.AIR);
						twoAboveLand.getRelative(face).setType(Material.AIR);
						threeAboveLand.getRelative(face).setType(Material.AIR);
					}
				}
				else{
					lateSafeCheck.add(p.getUniqueId());
				}


			}
		}.runTaskLater(plugin, 5L);
	}
	public void handleLateScatter(Player p){
		if(teamM.isTeamsEnabled()){
			removePlayerFromLateScatters(p);
			lateScatterReadyToScatter.offer(p.getUniqueId());
			Bukkit.getPluginManager().callEvent(new LateScatterEvent());
		}
	}

	private void initializePlayer(Player p){
		Medic.heal(p);
		p.setMaxHealth(20);
		p.teleport(worldF.getLobby().getSpawnLocation());
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.setGameMode(GameMode.SURVIVAL);
		p.setBedSpawnLocation(worldM.getCenter());
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
	 * A list of blocks that we do not want players to spawn on
	 * @see findValidLocation()
	 * @see validate()
	 */
	private final Set<Material> VALID_SPAWN_BLOCKS = ImmutableSet.of(
			Material.GRASS,
			Material.SAND
			);

	@EventHandler
	public void on(ScatterEndEvent e){
		setupStartingOptions();
	}
	public void setupStartingOptions() {
		new BukkitRunnable() {

			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(new GameStartEvent());
				State.setState(State.INGAME);
				moveE.unfreezePlayers();
				MessageSender.broadcast("Scattering complete!");
				chatM.unMutePlayers();
				for(Player online: Bukkit.getServer().getOnlinePlayers()){
					online.setHealth(online.getHealth());
				}

			}
		}.runTaskLater(plugin, 100L);

	}

	public void prePVPSetup(){
		worldM.getUHCWorld().setTime(0);
		worldM.getUHCWorld().setGameRuleValue("dodaylightcycle", "false");

	}

}
