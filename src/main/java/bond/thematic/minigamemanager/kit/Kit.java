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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.ArrayList;
import java.util.List;

public class Kit {
    public static final Codec<Kit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("kit_name").forGetter(kit -> kit.name),
            ItemStack.CODEC.fieldOf("icon").forGetter(kit -> kit.icon),
            Codec.list(ItemStack.CODEC).fieldOf("armor").forGetter(kit -> kit.armor),
            Codec.list(ItemStack.CODEC).fieldOf("items").forGetter(kit -> kit.items),
            Codec.list(Cooldown.CODEC).optionalFieldOf("cooldowns", new ArrayList<>()).forGetter(kit -> kit.cooldowns)
    ).apply(instance, Kit::new));

    public final String name;
    public final ItemStack icon;
    public final List<ItemStack> armor;
    public final List<ItemStack> items;
    public final List<Cooldown> cooldowns;

    public Kit(String name, ItemStack icon, List<ItemStack> armor, List<ItemStack> items, List<Cooldown> cooldowns) {
        this.name = name;
        this.icon = icon;
        this.armor = armor;
        this.items = items;
        this.cooldowns = cooldowns;
    }

    public MutableText displayName() {
        return Text.translatable("skywars.kit." + name);
    }

    @SuppressWarnings("unused") // api
    public void equipPlayer(ServerPlayerEntity player) {
        for (ItemStack itemStack : this.items) {
            player.getInventory().insertStack(ItemStackBuilder.of(itemStack).build());
        }

        player.equipStack(EquipmentSlot.HEAD, ItemStackBuilder.of(this.armor.get(0)).build());
        player.equipStack(EquipmentSlot.CHEST, ItemStackBuilder.of(this.armor.get(1)).build());
        player.equipStack(EquipmentSlot.LEGS, ItemStackBuilder.of(this.armor.get(2)).build());
        player.equipStack(EquipmentSlot.FEET, ItemStackBuilder.of(this.armor.get(3)).build());

        for (Cooldown cooldown : cooldowns) {
            Item item = Registries.ITEM.get(cooldown.identifier);
            if (item != null) {
                player.getItemCooldownManager().set(item, cooldown.durationSec * 20);
            }
        }
    }

    public static class Cooldown {
        public static final Codec<Cooldown> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(cooldown -> cooldown.identifier),
                Codec.INT.fieldOf("duration").forGetter(cooldown -> cooldown.durationSec)
        ).apply(instance, Cooldown::new));

        public final Identifier identifier;
        public final int durationSec;

        public Cooldown(Identifier identifier, int durationSec) {
            this.identifier = identifier;
            this.durationSec = durationSec;
        }
    }
}
