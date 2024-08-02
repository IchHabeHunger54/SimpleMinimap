package ihh.simpleminimap;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Holds the mod's config values.
 */
public final class Config {
    public static final ModConfigSpec spec;
    public static final ModConfigSpec.IntValue minimapScale;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        minimapScale = builder.defineInRange("scale", 2, 1, 16);
        spec = builder.build();
    }
}
