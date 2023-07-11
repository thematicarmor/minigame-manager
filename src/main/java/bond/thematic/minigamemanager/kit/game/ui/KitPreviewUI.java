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

import bond.thematic.minigamemanager.kit.Kit;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class KitPreviewUI extends SimpleGui {
    private final KitSelectorUI selectorUI;
    private final Kit kit;

    public KitPreviewUI(KitSelectorUI selectorUI, Kit kit) {
        super(ScreenHandlerType.GENERIC_9X3, selectorUI.getPlayer(), false);
        this.selectorUI = selectorUI;
        this.kit = kit;
        this.setTitle(kit.displayName());
    }

    @Override
    public void onOpen() {
        int pos = 0;

        for (ItemStack itemStack : this.kit.items) {
            this.setSlot(pos++, itemStack.copy());
        }

        pos = 0;

        for (ItemStack itemStack : this.kit.armor) {
            this.setSlot(9 + pos, itemStack.copy());
            pos++;
        }

        this.setSlot(this.size - 1, new GuiElementBuilder(Items.BARRIER)
                .setName(Text.translatable("text.skywars.return_selector").setStyle(Style.EMPTY.withItalic(false)))
                .setCallback((x, y, z) -> {
                    this.player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.MASTER, 0.5f, 1);
                    selectorUI.open();
                    this.close();

                })
        );

        super.onOpen();
    }
}
