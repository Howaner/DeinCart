package de.howaner.DeinCart.util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class Route {
	private Location startSpawn;
	private Location endSpawn;
	private List<Block> startRails = new ArrayList<Block>();
	private float startDirection;
	
	public Location getStartSpawn() {
		return this.startSpawn;
	}
	
	public Location getEndSpawn() {
		return this.endSpawn;
	}
	
	public List<Block> getStartRails() {
		return this.startRails;
	}
	
	public float getStartDirection() {
		return this.startDirection;
	}
	
	public void setStartSpawn(Location startSpawn) {
		this.startSpawn = startSpawn;
	}
	
	public void setEndSpawn(Location endSpawn) {
		this.endSpawn = endSpawn;
	}
	
	public void setStartRails(List<Block> rails) {
		this.startRails = rails;
	}
	
	public void addStartRail(Block block) {
		this.startRails.add(block);
	}
	
	public void removeStartRail(Block block) {
		this.startRails.remove(block);
	}
	
	public boolean isStartRail(Block block) {
		return this.startRails.contains(block);
	}
	
	public void setStartDirection(float direction) {
		this.startDirection = direction;
	}
	
	public boolean isComplete() {
		return (
			(this.startSpawn != null) &&
			(this.endSpawn != null) &&
			(!this.startRails.isEmpty())
		);
	}
}
