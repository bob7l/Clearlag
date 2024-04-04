package me.minebuilders.clearlag;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class ChunkKey {

    private int x;

    private int z;

    private World world;

    public ChunkKey(Chunk chunk) {
        this(chunk.getX(), chunk.getZ(), chunk.getWorld());
    }

    public ChunkKey(int x, int z, World world) {
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public ChunkKey(Location l) {
        this(l.getBlockX() >> 4, l.getBlockZ() >> 4, l.getWorld());
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public int hashCode() {
        return x + (z * 3000);
    }

    @Override
    public boolean equals(Object ob) {
        return ob.getClass() == ChunkKey.class && equals((ChunkKey) ob);
    }

    public boolean equals(ChunkKey ck) {
        return ck.getZ() == z && ck.getX() == x;
    }

}
