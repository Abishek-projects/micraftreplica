package mygame;

public class TerrainGenerator {
    private static final FastNoiseLite baseNoise = new FastNoiseLite();   // Controls biome scale (plains vs mountains)
    private static final FastNoiseLite detailNoise = new FastNoiseLite(); // Adds fine surface variation

    static {
        // Base noise: low frequency → smooth variation across large distances
        baseNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        baseNoise.SetFrequency(0.01f);
        baseNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        baseNoise.SetFractalOctaves(3);

        // Detail noise: high frequency → bumpy surface detail
        detailNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        detailNoise.SetFrequency(0.05f);
        detailNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        detailNoise.SetFractalOctaves(2);
    }

    public static int getHeight(int x, int z) {
        // Base controls where terrain is flat or mountainous
        float base = baseNoise.GetNoise(x, z);    // -1 to 1
        float detail = detailNoise.GetNoise(x, z);

        // Map base noise into terrain type zones
        float mountainFactor = (base + 1f) / 2f;  // normalize 0–1

        // Plains height (lower, smoother)
        int plainsBase = 6;
        int plainsVariation = 4;

        // Mountain height (higher, rougher)
        int mountainBase = 14;
        int mountainVariation = 12;

        // Blend between plains and mountain zones
        float height = mix(
            plainsBase + plainsVariation * detail,
            mountainBase + mountainVariation * detail,
            mountainFactor * mountainFactor  // emphasize high base regions
        );

        return Math.max(1, (int) height);
    }
    private static final FastNoiseLite woodNoise = new FastNoiseLite();

static {
    woodNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
    woodNoise.SetFrequency(0.8f);  // higher frequency → more sparse/random placement
}


    private static float mix(float a, float b, float t) {
        return a + (b - a) * t;
    }
     public static boolean shouldPlaceWood(int x, int z) {
        float n = woodNoise.GetNoise(x, z); // value between -1 and 1
        return n > 0.8f;                     // threshold controls density
    }
}
