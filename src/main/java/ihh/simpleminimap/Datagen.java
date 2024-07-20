package ihh.simpleminimap;

import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

class Datagen {
    static void gatherData(GatherDataEvent event) {
        event.getGenerator().<DataProvider>addProvider(event.includeClient(), LanguageDatagen::new);
    }

    private static final class LanguageDatagen extends LanguageProvider {
        public LanguageDatagen(PackOutput output) {
            super(output, SimpleMinimap.MOD_ID, "en_us");
        }

        @Override
        protected void addTranslations() {

        }
    }
}
