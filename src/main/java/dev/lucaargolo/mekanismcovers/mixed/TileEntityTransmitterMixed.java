package dev.lucaargolo.mekanismcovers.mixed;

import net.minecraft.world.level.block.state.BlockState;

public interface TileEntityTransmitterMixed {

    BlockState mekanism_covers$getCoverState();

    void mekanism_covers$setCoverState(BlockState coverState);

    void mekanism_covers$onUpdateClient();

}
