package clientmods.snowyleaves.mixin;

import clientmods.snowyleaves.SnowyAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class RenderMixin {

    public int updateColour(int colour, float gradient) {
        int r = colour >> 16;
        int g = colour >> 8 & 0xFF;
        int b = colour & 0xFF;
        r += (int)(gradient*(255-r));
        g += (int)(gradient*(255-g));
        b += (int)(gradient*(255-b));
        return r << 16 | g << 8 | b;
    }

    @Inject(at = @At("RETURN"), method = "getColor", cancellable = true)
    public void getColour(BlockPos pos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir) {
        int colour = cir.getReturnValue();
        BlockState state = ((ClientWorld)(Object)this).getBlockState(pos);
        Block block = state.getBlock();
        if ((block instanceof LeavesBlock) && ((SnowyAccessor)block).isSnowy(state)) {
            colour = updateColour(colour, ((ClientWorld)(Object)this).getRainGradient(1));
            cir.setReturnValue(colour);
        }

    }


}
