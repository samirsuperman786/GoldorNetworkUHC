package com.goldornetwork.uhc.managers;

import org.bukkit.Location;
import org.bukkit.World;

import com.goldornetwork.uhc.utils.MessageSender;

public class ChunkGenerator implements Runnable{

	//instances
	public ChunkGenerator() {
		
	}

	//storage
	private int radius;
	private World world;

	//lengths to manipulate by
	int di =-16;
	int dj = 0;
	// length of current segment
	int segment_length;

	// current position (i, j) and how much of current segment we passed
	int i;
	int j;
	int segment_passed;
	private int k;


	//storage
	private boolean isGeneratingDone;
	private boolean startGenerating;
	private int chunksPerRun =15;
	private int chunksLeft;
	private int duration=0;


	public void loadGenerator(World world, Location center, int radius){
		this.world = world;
		this.radius = 16*(Math.round(radius/16));
		this.i=center.getBlockX();
		this.j=center.getBlockZ();
		this.chunksLeft=0;
		this.segment_length=1;
		this.segment_passed=0;
		this.k =0;
		this.startGenerating=true;
	}

	public double getTimeTillCompleteInMinutes(){
		return (((radius/16) * (radius/16) *4)/chunksPerRun)/60;
	}

	public boolean isGenerating(){
		return startGenerating;
	}

	public void pauseGeneration(){
		if(startGenerating){
			startGenerating=false;
		}
		else{
			//nothing
		}
	}
	public void resumeGeneration(){
		if(startGenerating==false){
			startGenerating=true;
		}
	}

	@Override
	public void run() {
		duration++;
		if(isGeneratingDone){
			MessageSender.broadcast("Done Preloading Chunks!");
			isGeneratingDone=false;
		}
		else if(startGenerating){
			if(duration% 10 ==0){//take a break every 10 seconds and save world
				if(startGenerating){
					world.save();
				}
				return;
			}
			if(duration %300 ==0){//updates every 5 minutes
				MessageSender.broadcast((getTimeTillCompleteInMinutes() - (chunksLeft/chunksPerRun))/60 + " minutes until \"" + world.getName() +"\" will finish rendering." );
			}

			else{
				for(int c = 0; c< chunksPerRun; c++){
					if(k <((radius/16) * (radius/16) *4)){
						chunksLeft--;
						++k;
						i += di;
						j += dj;
						++segment_passed;

						if (segment_passed == segment_length) {
							// done with current segment
							segment_passed = 0;

							// 'rotate' directions
							int buffer = di;
							di = -dj;
							dj = buffer;

							// increase segment length if necessary
							if (dj == 0) {
								++segment_length;
							}
						}
						world.loadChunk(i, j);
						//Bukkit.getPlayer("g0ldmanpox").teleport(new Location(world, i, world.getMaxHeight(), j));
					}
					else if(k==((radius/16) * (radius/16) *4)){
						startGenerating=false;
						isGeneratingDone=true;
					}

				}
			}



		}







	}




}
