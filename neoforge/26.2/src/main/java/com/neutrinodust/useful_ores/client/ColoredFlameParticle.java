package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.particle.ColoredFlameOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

public class ColoredFlameParticle extends RisingParticle {

    protected ColoredFlameParticle(ClientLevel level, double x, double y, double z,
                                    double xd, double yd, double zd,
                                    TextureAtlasSprite sprite, int color) {
        super(level, x, y, z, xd, yd, zd, sprite);
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        this.setColor(r, g, b);
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.OPAQUE;
    }

    @Override
    public void move(double xa, double ya, double za) {
        this.setBoundingBox(this.getBoundingBox().move(xa, ya, za));
        this.setLocationFromBoundingbox();
    }

    @Override
    public float getQuadSize(float a) {
        float s = (this.age + a) / this.lifetime;
        return this.quadSize * (1.0F - s * s * 0.5F);
    }

    @Override
    public int getLightCoords(float a) {
        return LightCoordsUtil.addSmoothBlockEmission(super.getLightCoords(a), (this.age + a) / this.lifetime);
    }

    public static class Provider implements ParticleProvider<ColoredFlameOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ColoredFlameOptions options, ClientLevel level,
                                        double x, double y, double z,
                                        double xAux, double yAux, double zAux, RandomSource random) {
            ColoredFlameParticle particle = new ColoredFlameParticle(
                    level, x, y, z, xAux, yAux, zAux, this.sprites.get(random), options.color());
            if (options.scale() != 1.0F) {
                particle.scale(options.scale());
            }
            return particle;
        }
    }
}

