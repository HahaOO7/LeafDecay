package at.haha007.leafdecay;

import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class LeafDecay extends JavaPlugin implements Listener {

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (!Tag.LOGS.isTagged(block.getType())) return;
        if (!player.hasPermission("leafdecay.use")) return;
        if (!hasNeighborLeave(block))
            return;
        //just throw time at it, could be done with dp
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (int x = -7; x <= 7; x++) {
                int finalX = x;
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    for (int y = -7; y <= 7; y++) {
                        for (int z = -7; z <= 7; z++) {
                            Block b = block.getRelative(finalX, y, z);
                            if (!(b.getBlockData() instanceof Leaves leaves))
                                continue;
                            if (leaves.isPersistent())
                                continue;
                            if (fillLogDistances(b, 7)) continue;
                            b.breakNaturally();
                        }
                    }
                }, x + 7);
            }
        }, 1);
    }


    private boolean hasNeighborLeave(Block block) {
        if (isLeaf(block.getRelative(BlockFace.UP)))
            return true;
        if (isLeaf(block.getRelative(BlockFace.DOWN)))
            return true;
        if (isLeaf(block.getRelative(BlockFace.WEST)))
            return true;
        if (isLeaf(block.getRelative(BlockFace.EAST)))
            return true;
        if (isLeaf(block.getRelative(BlockFace.NORTH)))
            return true;
        return isLeaf(block.getRelative(BlockFace.SOUTH));
    }

    private boolean isLeaf(Block block) {
        return Tag.LEAVES.isTagged(block.getType());
    }

    //dfs
    private boolean fillLogDistances(Block block, int distance) {
        if (distance <= 0) return false;

        //is the vanilla way of finding out if a block is supporting leaves
        if (Tag.LOGS.isTagged(block.getType())) return true;

        if (!Tag.LEAVES.isTagged(block.getType()))
            return false;

        distance--;
        if (fillLogDistances(block.getRelative(BlockFace.UP), distance))
            return true;
        if (fillLogDistances(block.getRelative(BlockFace.DOWN), distance))
            return true;
        if (fillLogDistances(block.getRelative(BlockFace.EAST), distance))
            return true;
        if (fillLogDistances(block.getRelative(BlockFace.WEST), distance))
            return true;
        if (fillLogDistances(block.getRelative(BlockFace.SOUTH), distance))
            return true;
        return fillLogDistances(block.getRelative(BlockFace.NORTH), distance);
    }
}
