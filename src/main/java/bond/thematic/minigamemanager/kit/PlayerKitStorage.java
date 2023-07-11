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

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.storage.ServerStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerKitStorage implements ServerStorage {
    private final Map<UUID, Identifier> kits;

    public PlayerKitStorage() {
        this.kits = new HashMap<>();
    }

    public void putPlayerKit(UUID uuid, Identifier identifier) {
        kits.put(uuid, identifier);
    }

    public Identifier getPlayerKit(UUID uuid) {
        return this.kits.getOrDefault(uuid, null);
    }

    @Override
    public NbtCompound toTag() {
        var nbt = new NbtCompound();
        var kitsList = new NbtList();
        kits.forEach((uuid, identifier) -> {
            if (identifier != null) {
                var entryTag = new NbtCompound();
                entryTag.putUuid("UUID", uuid);
                entryTag.putString("Kit", identifier.toString());
                kitsList.add(entryTag);
            }
        });

        nbt.put("Kits", kitsList);

        return nbt;
    }

    @Override
    public void fromTag(NbtCompound compoundTag) {
        compoundTag.getList("Kits", 10).forEach(entry -> {
            var entryTag = (NbtCompound) entry;
            kits.put(entryTag.getUuid("UUID"), Identifier.tryParse(entryTag.getString("Kit")));
        });
    }
}
