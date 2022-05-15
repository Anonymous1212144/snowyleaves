package clientmods.snowyleaves.mixin;

import clientmods.snowyleaves.SnowyAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(LeavesBlock.class)
public class LeavesMixin extends Block implements SnowyAccessor {
    private static final BooleanProperty SNOWY;

    public LeavesMixin(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isSnowy(BlockState state) {return state.get(SNOWY);}

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(CallbackInfo cir) {
        setDefaultState(stateManager.getDefaultState().with(LeavesBlock.DISTANCE, 7).with(LeavesBlock.PERSISTENT, false).with(SNOWY, false));
    }

    public boolean updateSnow(World world, BlockPos pos) {
        int top = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos).getY();
        if (top > pos.getY()) {
            BlockPos p = pos.up();
            for (int i = 0; i < top; i++) {
                if (world.getLightLevel(LightType.SKY, p) == 15) break;
                Block block = world.getBlockState(p).getBlock();
                if (!block.equals(Blocks.AIR) && !block.equals(Blocks.SNOW) && !block.equals(Blocks.SNOW_BLOCK) && !(block instanceof LeavesBlock)) {
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
        boolean snow = updateSnow(world, pos);
        world.setBlockState(pos, state.with(SNOWY, snow));
    }

    @Inject(at = @At("TAIL"), method = "appendProperties")
    public void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo cir) {
        builder.add(SNOWY);
    }

    static {
        SNOWY = Properties.SNOWY;
    }
}
