package com.nukateam.ntgl.client.render.particle;

import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;

/**
 * Author: MrCrayfish
 */
public class BloodParticle extends SingleQuadParticle {
    public BloodParticle(ClientLevel world, double x, double y, double z, TextureAtlasSprite sprite) {
        super(world, x, y, z, 0.1, 0.1, 0.1, sprite);
        this.setColor(0.541F, 0.027F, 0.027F);
        this.gravity = 1.5F;
        this.quadSize = 0.0625F;
        this.lifetime = (int) (12.0F / (this.random.nextFloat() * 0.9F + 0.1F));
    }

    @Override
    protected Layer getLayer() {
        return Layer.OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.onGround) {
            this.xd = 0;
            this.zd = 0;
            this.quadSize *= 0.95F;
        }
    }

    @Override
    public void extract(QuadParticleRenderState renderState, Camera camera, float partialTicks) {
        var projectedView = camera.position();
        float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - projectedView.x());
        float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - projectedView.y());
        float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - projectedView.z());

        if (this.onGround) {
            y += 0.01;
        }

        var rotation = Direction.NORTH.getRotation();
        if (this.roll == 0.0F) {
            if (!this.onGround) {
                rotation = new Quaternionf(camera.rotation());
            }
        } else {
            rotation = new Quaternionf(camera.rotation());
            float angle = Mth.lerp(partialTicks, this.oRoll, this.roll);
            rotation.mul(Axis.ZP.rotation(angle));
        }

        this.extractRotatedQuad(renderState, rotation, x, y, z, partialTicks);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new BloodParticle(worldIn, x, y, z, this.spriteSet.get(random));
        }
    }
}
