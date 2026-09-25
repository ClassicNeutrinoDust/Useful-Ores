#!/usr/bin/env python3
"""
Regenerate the Useful Ores chiseled-block textures.

Approach:
- Uses ONLY Pillow and deterministic pixel operations.
- Keeps each ore's existing chiseled design as the geometry mask.
- Uses the ore's normal block texture as the material/albedo layer.
- Adds directional embossing, recess darkening, raised-edge highlights,
  and a restrained face-frame bevel to increase perceived depth.

No AI / generative image model is used.
"""

from pathlib import Path
import random
from PIL import Image, ImageOps

ORES = [
    "arcanite", "argentite", "chromite", "enderium", "fulgurite",
    "ilmenite", "lonsdaleite", "nyxium", "osmium", "phosgene",
    "scheelite", "solarite", "sperrylite", "voidshard", "zephyrite", "painite",
]

ROOT = Path(__file__).resolve().parents[1]
TEX = ROOT / "src/main/resources/assets/useful_ores/textures/block"


def clamp(v, lo=0, hi=255):
    return max(lo, min(hi, v))


def generate(name: str, seed: int) -> Image.Image:
    rng = random.Random(seed)

    base = Image.open(TEX / f"{name}_block.png").convert("RGB")
    old = Image.open(TEX / f"chiseled_{name}_block.png").convert("RGB")

    gray = ImageOps.grayscale(old)
    gp = gray.load()
    bp = base.load()

    # Start from the existing chiseled design, but move its albedo toward
    # the corresponding ore block so the family remains visually coherent.
    out = Image.new("RGB", (16, 16))
    op = out.load()
    for y in range(16):
        for x in range(16):
            pattern = (gp[x, y] - 128) / 128.0
            factor = 1.0 + pattern * 0.24
            jitter = rng.choice([-1, 0, 0, 0, 1])
            op[x, y] = tuple(
                clamp(round(bp[x, y][i] * factor) + jitter)
                for i in range(3)
            )

    # Luminance of the old chisel image is used only as a height/shape mask.
    h = [[gp[x, y] for x in range(16)] for y in range(16)]

    # Directional pixel embossing: upper-left light, lower-right shadow.
    for y in range(16):
        for x in range(16):
            dx = (h[y][min(15, x + 1)] - h[y][max(0, x - 1)]) / 255.0
            dy = (h[min(15, y + 1)][x] - h[max(0, y - 1)][x]) / 255.0
            edge = -0.62 * dx - 0.70 * dy
            shade = 1.0 + edge * 0.75

            local_min = min(
                h[yy][xx]
                for yy in range(max(0, y - 1), min(16, y + 2))
                for xx in range(max(0, x - 1), min(16, x + 2))
            )
            if h[y][x] - local_min < -18:
                shade *= 0.94
            if h[y][x] > 210:
                shade *= 1.035

            op[x, y] = tuple(clamp(round(v * shade)) for v in op[x, y])

    # Pixel-hard outer face bevel.
    for x in range(2, 14):
        op[x, 2] = tuple(clamp(round(v * 1.06)) for v in op[x, 2])
        op[x, 13] = tuple(clamp(round(v * 0.92)) for v in op[x, 13])
    for y in range(2, 14):
        op[2, y] = tuple(clamp(round(v * 1.06)) for v in op[2, y])
        op[13, y] = tuple(clamp(round(v * 0.92)) for v in op[13, y])

    return out.convert("RGBA")


def main():
    for i, ore in enumerate(ORES):
        generate(ore, 5000 + i).save(
            TEX / f"chiseled_{ore}_block.png"
        )


if __name__ == "__main__":
    main()
