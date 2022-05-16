package clientmods.snowyleaves.mixin;

import clientmods.snowyleaves.SnowyAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(LeavesBlock.class)
public class LeavesMixin extends Block implements SnowyAccessor {
    @Shadow @Final public static BooleanProperty PERSISTENT;
    public boolean isSnowing = false;
    public long Time = 0;

    public LeavesMixin(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isSnowy(World world, BlockPos pos) {return updateSnow(world, pos);}

    public boolean updateSnow(World world, BlockPos pos) {
        int top = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos).getY();
        if (top > pos.getY()) {
            BlockPos p = pos.up();
            for (int i = 0; i < top; i++) {
                if (world.getLightLevel(LightType.SKY, p) == 15) return true;
                Block block = world.getBlockState(p).getBlock();
                if (!(block instanceof LeavesBlock) && !block.equals(Blocks.AIR) && !block.equals(Blocks.SNOW) && !block.equals(Blocks.SNOW_BLOCK)) {
                    return false;
                }
                p = p.up();
            }
        }
        return true;
    }

    @Inject(at = @At("HEAD"), method = "randomDisplayTick")
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo cir) {
        if (world.getBiome(pos).value().doesNotSnow(pos)) return;
        if (world.isRaining() ^ isSnowing) {
            Time = world.getTime();
            isSnowing = world.isRaining();
            return;
        } else if ((world.getTime()-Time)>400) return;
        boolean p = state.get(PERSISTENT);
        world.setBlockState(pos, state.with(PERSISTENT, !p));
        world.setBlockState(pos, state.with(PERSISTENT, p));
    }
    
}
