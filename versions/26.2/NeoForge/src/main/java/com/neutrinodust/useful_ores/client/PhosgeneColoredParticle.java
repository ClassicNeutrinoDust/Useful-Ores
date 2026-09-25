package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.particle.PhosgeneBubbleParticleOptions;
import com.neutrinodust.useful_ores.particle.PhosgeneEffectParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;

public final class PhosgeneColoredParticle extends RisingParticle {
    private final boolean bubble;
    private final double centerX;
    private final double centerZ;
    private final double baseY;
    private double angle;
    private double radius;

    private PhosgeneColoredParticle(ClientLevel level, double x, double y, double z,
                                    double xd, double yd, double zd,
                                    TextureAtlasSprite sprite, int color, float scale, boolean bubble,
                                    RandomSource random) {
        super(level, x, y, z, xd, yd, zd, sprite);
        this.bubble = bubble;
        this.centerX = x;
        this.centerZ = z;
        this.baseY = y;
        this.angle = random.nextDouble() * (Math.PI * 2.0);
        this.radius = 0.015 + random.nextDouble() * 0.035;
        this.setColor(((color >> 16) & 255) / 255.0F,
                ((color >> 8) & 255) / 255.0F,
                (color & 255) / 255.0F);
        this.scale(scale);
        this.gravity = 0.0F;
        this.hasPhysics = false;

        this.lifetime = bubble ? 78 : 88;
        this.friction = 0.965F;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();

        float life = (float) this.age / (float) this.lifetime;
        this.alpha = 0.72F * (1.0F - life);

        this.angle += bubble ? 0.19 : 0.145;
        this.radius = Math.min(0.46, this.radius + (bubble ? 0.0042 : 0.0052));

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.x = this.centerX + Math.cos(this.angle) * this.radius;
        this.z = this.centerZ + Math.sin(this.angle) * this.radius;
        this.y = this.baseY + Math.min(2.0, this.age * (bubble ? 0.0255 : 0.0235));

        this.xd = 0.0;
        this.zd = 0.0;
        this.yd = 0.0;
    }

    public static final class MistProvider implements ParticleProvider<PhosgeneEffectParticleOptions> {
        private final SpriteSet sprites;
        public MistProvider(SpriteSet sprites) { this.sprites = sprites; }

        @Override
        public Particle createParticle(PhosgeneEffectParticleOptions o, ClientLevel level,
                                        double x, double y, double z, double xd, double yd, double zd,
                                        RandomSource random) {
            return new PhosgeneColoredParticle(level, x, y, z, xd, yd, zd,
                    sprites.get(random), o.color(), o.scale(), false, random);
        }
    }

    public static final class BubbleProvider implements ParticleProvider<PhosgeneBubbleParticleOptions> {
        private final SpriteSet sprites;
        public BubbleProvider(SpriteSet sprites) { this.sprites = sprites; }

        @Override
        public Particle createParticle(PhosgeneBubbleParticleOptions o, ClientLevel level,
                                        double x, double y, double z, double xd, double yd, double zd,
                                        RandomSource random) {
            return new PhosgeneColoredParticle(level, x, y, z, xd, yd, zd,
                    sprites.get(random), o.color(), o.scale(), true, random);
        }
    }
}

