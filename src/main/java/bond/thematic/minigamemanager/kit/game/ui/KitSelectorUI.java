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

package bond.thematic.minigamemanager.kit.game.ui;

import bond.thematic.minigamemanager.MinigameManagerMod;
import bond.thematic.minigamemanager.entity.MinigamePlayer;
import bond.thematic.minigamemanager.kit.Kit;
import bond.thematic.minigamemanager.kit.KitRegistry;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class KitSelectorUI extends SimpleGui {
    private final MinigamePlayer playerData;
    private final List<Kit> kits;

    KitSelectorUI(ServerPlayerEntity player, MinigamePlayer data, List<Kit> kits) {
        super(getType(kits.size()), player, kits.size() > 53);
        this.playerData = data;
        this.kits = kits;
        this.setTitle(Text.translatable("text.skywars.select_kit"));
    }

    private static ScreenHandlerType<?> getType(int size) {
        if (size <= 8) {
            return ScreenHandlerType.GENERIC_9X1;
        } else if (size <= 17) {
            return ScreenHandlerType.GENERIC_9X2;
        } else if (size <= 26) {
            return ScreenHandlerType.GENERIC_9X3;
        } else if (size <= 35) {
            return ScreenHandlerType.GENERIC_9X4;
        } else if (size <= 44) {
            return ScreenHandlerType.GENERIC_9X5;
        } else {
            return ScreenHandlerType.GENERIC_9X6;
        }
    }


    @SuppressWarnings("unused") // api
    public static void openSelector(ServerPlayerEntity player) {
        new KitSelectorUI(player, new MinigamePlayer(player), KitRegistry.getKITS().values().stream().toList()).open();
    }

    @SuppressWarnings("unused") // api
    public static void openSelector(ServerPlayerEntity player, MinigamePlayer data, List<Identifier> kits) {
        var kitsList = new ArrayList<Kit>();

        for (Identifier id : kits) {
            Kit kit = KitRegistry.get(id);
            if (kit != null) {
                kitsList.add(kit);
            }
        }

        new KitSelectorUI(player, data, kitsList).open();
    }

    @Override
    public void onOpen() {
        int pos = 0;

        for (Kit kit : this.kits) {
            var icon = GuiElementBuilder.from(kit.icon);
            icon.setName(kit.displayName());
            icon.hideFlags();
            icon.addLoreLine(Text.translatable("text.skywars.click_select").formatted(Formatting.GRAY));
            icon.addLoreLine(Text.translatable("text.skywars.click_preview").formatted(Formatting.GRAY));
            if (kit == this.playerData.selectedKit) {
                icon.addLoreLine(Text.translatable("text.skywars.selected").formatted(Formatting.GREEN));
                icon.glow();
            }

            icon.setCallback((index, clickType, action) -> {
                if (clickType.isLeft) {
                    this.player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.MASTER, 0.5f, 1);
                    MinigameManagerMod.KIT_STORAGE.putPlayerKit(player.getUuid(), KitRegistry.getId(kit));
                    changeKit(this.playerData, kit);
                } else if (clickType.isRight) {
                    this.player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.MASTER, 0.5f, 1);
                    new KitPreviewUI(this, kit).open();
                    this.close();
                }
                this.onOpen();
            });

            this.setSlot(pos, icon);
            pos++;
        }

        super.onOpen();
    }

    public static void changeKit(MinigamePlayer playerData, Kit kit) {
        playerData.selectedKit = kit;
    }
}
