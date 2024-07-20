package ihh.simpleminimap;

import ihh.simpleminimap.api.SimpleMinimapApi;
import ihh.simpleminimap.api.storage.IMapManager;
import ihh.simpleminimap.storage.MapManager;

public final class SimpleMinimapApiImpl implements SimpleMinimapApi {
    private static final IMapManager MAP_MANAGER = new MapManager();

    @Override
    public IMapManager getMapManager() {
        return MAP_MANAGER;
    }
}
