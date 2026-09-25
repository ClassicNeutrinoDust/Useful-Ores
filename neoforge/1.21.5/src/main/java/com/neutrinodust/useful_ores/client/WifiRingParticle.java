package com.neutrinodust.useful_ores.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;

public class WifiRingParticle extends RisingParticle {
    private static final float START_SCALE = 0.35F;
    private static final float END_SCALE = 2.0F;

    protected WifiRingParticle(ClientLevel level, double x, double y, double z,
                               double xd, double yd, double zd, TextureAtlasSprite sprite) {
        super(level, x, y, z, xd, yd, zd);
        this.setSprite(sprite);
        this.setColor(1.0F, 0.82F, 0.35F);
        this.lifetime = 14;
        this.gravity = 0.0F;
        this.hasPhysics = false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void move(double xa, double ya, double za) {
        this.setBoundingBox(this.getBoundingBox().move(xa, ya, za));
        this.setLocationFromBoundingbox();
    }

    @Override
    public float getQuadSize(float a) {
        float s = (this.age + a) / this.lifetime;
        return this.quadSize * (START_SCALE + s * (END_SCALE - START_SCALE));
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 1.0F - (float) this.age / (float) this.lifetime;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }
        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level,
                                       double x, double y, double z,
                                       double xAux, double yAux, double zAux) {
            return new WifiRingParticle(level, x, y, z, xAux, yAux, zAux, this.sprites.get(0, 1));
        }
    }
}
