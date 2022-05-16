package clientmods.snowyleaves;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface SnowyAccessor {
    boolean isSnowy(World world, BlockPos pos);
}
