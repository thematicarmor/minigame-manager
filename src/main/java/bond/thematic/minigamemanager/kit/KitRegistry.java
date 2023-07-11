/*
 * What follows is the license of the template, including init.py, all files under src/, etc.
 *
 * -- Begin template license --
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Restioson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * -- End template license --
 *
 * What follows is the license of the original mod which the template (excluding init.py) was based on. It is
 * available here: https://github.com/NucleoidMC/D-Coudre/
 *
 * -- Begin original mod license --
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 CatCore
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * -- End original mod license --
 */

package bond.thematic.minigamemanager.kit;

import bond.thematic.minigamemanager.MinigameManagerMod;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

public class KitRegistry {
    private static final TinyRegistry<Kit> KITS = TinyRegistry.create();
    private static final Predicate<Identifier> KIT_PREDICATE = path -> path.getPath().endsWith(".json");

    @SuppressWarnings("unused") // api
    public static void register(String gameName) {
        ResourceManagerHelper serverData = ResourceManagerHelper.get(ResourceType.SERVER_DATA);

        serverData.registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("skywars", "skywars_kits");
            }

            @Override
            public void reload(ResourceManager manager) {
                KITS.clear();

                Map<Identifier, Resource> kits = new TreeMap<>(manager.findResources("kits/common", KIT_PREDICATE));
                kits.putAll(manager.findResources("kits/" + gameName, KIT_PREDICATE));

                kits.forEach((path, resource) -> {
                    try {
                        try (Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                            JsonElement json = JsonParser.parseReader(reader);

                            Identifier identifier = identifierFromPath(path);

                            DataResult<Kit> result = Kit.CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst);

                            result.result().ifPresent(game -> KITS.register(identifier, game));

                            result.error().ifPresent(error -> MinigameManagerMod.LOGGER.error("Failed to decode kit at {}: {}", path, error.toString()));
                        }
                    } catch (IOException e) {
                        MinigameManagerMod.LOGGER.error("Failed to kit at {}", path, e);
                    }
                });
            }
        });
    }

    private static Identifier identifierFromPath(Identifier location) {
        String path = location.getPath();
        path = path.substring("skywarsKits//".length(), path.length() - ".json".length());
        return new Identifier(location.getNamespace(), path);
    }

    @Nullable
    public static Kit get(Identifier identifier) {
        return KITS.get(identifier);
    }

    @Nullable
    public static Identifier getId(Kit kit) {
        return KITS.getIdentifier(kit);
    }

    public static TinyRegistry<Kit> getKITS() {
        return KITS;
    }
}
