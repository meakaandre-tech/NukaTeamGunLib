package com.nukateam.ntgl.client.render.particle;

import com.mojang.math.Axis;
import com.nukateam.ntgl.Config;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

/**
 * Author: MrCrayfish
 */
public class BulletHoleParticle extends SingleQuadParticle {
    private final Direction direction;
    private final BlockPos pos;
    private final Quaternionf rotation;
    private int uOffset;
    private int vOffset;
    private float textureDensity;

    public BulletHoleParticle(ClientLevel world, double x, double y, double z, Direction direction, BlockPos pos) {
        super(world, x, y, z, getSprite(world, pos));
        this.setSprite(this.sprite);
        this.direction = direction;
        this.pos = pos;
        // the quad of a particle lies in the XY plane; lay it flat on the face that was hit
        this.rotation = direction.getRotation().mul(Axis.XP.rotationDegrees(-90F));
        this.lifetime = (int) (Config.CLIENT.particle.bulletHoleLifeMin.get() + world.getRandom().nextFloat() * (Config.CLIENT.particle.bulletHoleLifeMax.get() - Config.CLIENT.particle.bulletHoleLifeMin.get()));
        this.hasPhysics = false;
        this.gravity = 0.0F;
        this.quadSize = 0.05F;

        /* Expire the particle straight away if the block is air */
        BlockState state = world.getBlockState(pos);
        if (world.getBlockState(pos).isAir())
            this.remove();

        int color = this.getBlockColor(state, world, pos, direction);
        this.rCol = ((float) (color >> 16 & 255) / 255.0F) / 3.0F;
        this.gCol = ((float) (color >> 8 & 255) / 255.0F) / 3.0F;
        this.bCol = ((float) (color & 255) / 255.0F) / 3.0F;
        this.alpha = 0.9F;
    }

    private int getBlockColor(BlockState state, ClientLevel world, BlockPos pos, Direction direction) {
        //Add an exception for grass blocks
        if (state.getBlock() == Blocks.GRASS_BLOCK)
            return Integer.MAX_VALUE;

        var tintSource = Minecraft.getInstance().getBlockColors().getTintSource(state, 0);
        return tintSource != null ? tintSource.colorInWorld(state, world, pos) : -1;
    }

    @Override
    protected void setSprite(TextureAtlasSprite sprite) {
        super.setSprite(sprite);
        this.uOffset = this.random.nextInt(16);
        this.vOffset = this.random.nextInt(16);
        this.textureDensity = (sprite.getU1() - sprite.getU0()) / 16.0F; //Assuming TESLA_TEXTURE is a square
    }

    private static TextureAtlasSprite getSprite(ClientLevel world, BlockPos pos) {
        var models = Minecraft.getInstance().getModelManager().getBlockStateModelSet();
        return models.getParticleMaterial(world.getBlockState(pos)).sprite();
    }

    @Override
    protected float getU0() {
        return this.sprite.getU0() + this.uOffset * this.textureDensity;
    }

    @Override
    protected float getV0() {
        return this.sprite.getV0() + this.vOffset * this.textureDensity;
    }

    @Override
    protected float getU1() {
        return this.getU0() + this.textureDensity;
    }

    @Override
    protected float getV1() {
        return this.getV0() + this.textureDensity;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.getBlockState(this.pos).isAir()) {
            this.remove();
        }
    }

    @Override
    public void extract(QuadParticleRenderState renderState, Camera camera, float partialTicks) {
        var view = camera.position();
        float particleX = (float) (Mth.lerp((double) partialTicks, this.xo, this.x) - view.x());
        float particleY = (float) (Mth.lerp((double) partialTicks, this.yo, this.y) - view.y());
        float particleZ = (float) (Mth.lerp((double) partialTicks, this.zo, this.z) - view.z());

        float threshold = Config.CLIENT.particle.bulletHoleFadeThreshold.get().floatValue();
        float fade = threshold >= 1.0f ? 1.0f : 1.0f - (Math.max((float) this.age - (float) this.lifetime * threshold, 0) / ((float) this.lifetime - (float) this.lifetime * threshold));

        float baseAlpha = this.alpha;
        this.alpha = baseAlpha * fade;
        this.extractRotatedQuad(renderState, this.rotation, particleX, particleY, particleZ, partialTicks);
        this.alpha = baseAlpha;
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT_TERRAIN;
    }
}
