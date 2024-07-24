package ihh.simpleminimap.data;

import ihh.simpleminimap.Translations;
import ihh.simpleminimap.api.SimpleMinimapApi;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

class Language extends LanguageProvider {
    public Language(PackOutput output) {
        super(output, SimpleMinimapApi.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(Translations.OPEN_MINIMAP_KEY, "Open Minimap");
        add(Translations.FULLSCREEN_MAP_TITLE, "Map");
    }
}
