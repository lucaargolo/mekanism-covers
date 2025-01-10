package dev.lucaargolo.mekanismcovers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.lucaargolo.mekanismcovers.mixed.TileEntityTransmitterMixed;
import mekanism.common.block.transmitter.BlockTransmitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    static void renderLevel(RenderLevelStageEvent event) {
        if(!MekanismCoversClient.SHADER_COVER_RENDERING || !MekanismCoversClient.isCoverTransparentFast() || !MekanismCoversClient.hasShaderPack()) {
            return;
        }
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            for(Map.Entry<BlockPos, BlockState> entry : MekanismCovers.POSSIBLE_BLOCKS.entrySet()) {
                ClientLevel level = Minecraft.getInstance().level;
                if(!level.isLoaded(entry.getKey())) {
                    return;
                }
                if(level.getBlockState(entry.getKey()).getBlock() instanceof BlockTransmitter<?>) {
                    if(entry.getValue() == null) {
                        continue;
                    }

                    var baseConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(Sheets.translucentCullBlockSheet());
                    var wrappedConsumer = new VertexConsumerWrapper(baseConsumer) {
                        @Override
                        public VertexConsumer setColor(int r, int g, int b, int a) {
                            super.setColor(r, g, b, 120);
                            return this;
                        }
                    };

                    var cameraPos = event.getCamera().getPosition();
                    event.getPoseStack().pushPose();
                    event.getPoseStack().translate(entry.getKey().getX()-cameraPos.x, entry.getKey().getY()-cameraPos.y, entry.getKey().getZ()-cameraPos.z);

                    var model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(entry.getValue());
                    for(var renderType : model.getRenderTypes(entry.getValue(), RandomSource.create(), ModelData.EMPTY)) {
                        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(event.getPoseStack().last(), wrappedConsumer, entry.getValue(),
                                model, 1f, 1f, 1f, LightTexture.pack(level.getBrightness(LightLayer.BLOCK, entry.getKey()), level.getBrightness(LightLayer.SKY, entry.getKey())),
                                OverlayTexture.NO_OVERLAY, model.getModelData(level, entry.getKey(), entry.getValue(), ModelData.EMPTY), renderType
                        );
                    }
                    event.getPoseStack().popPose();
                }
            }
        }
    }

    @SubscribeEvent
    static void changeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() == Minecraft.getInstance().player) {
            MekanismCovers.POSSIBLE_BLOCKS.clear();
        }
    }
}
