/*
 * Copyright (c) 2018-2020 C4
 *
 * This file is part of Colytra, a mod made for Minecraft.
 *
 * Colytra is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Colytra is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Colytra.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.colytra.client;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.colytra.common.ColytraConfig;
import top.theillusivec4.colytra.common.ElytraNBT;

public class EventHandlerClient {

  private static void getColytraTooltip(ItemStack chestStack, List<ITextComponent> tooltip) {

    if (!ElytraNBT.hasUpgrade(chestStack)) {
      return;
    }
    ItemStack elytraStack = ElytraNBT.getElytra(chestStack);

    if (elytraStack.isEmpty()) {
      return;
    }

    tooltip.add(new StringTextComponent(""));
    tooltip.add(
        new TranslationTextComponent("item.minecraft.elytra").applyTextStyle(TextFormatting.AQUA));

    if (ColytraConfig.getColytraMode() == ColytraConfig.ColytraMode.NORMAL) {

      if (elytraStack.hasTag()) {
        int i = 0;
        CompoundNBT tag = elytraStack.getTag();

        if (tag != null && tag.contains("HideFlags", 99)) {
          i = tag.getInt("HideFlags");
        }

        if ((i & 1) == 0) {
          ListNBT nbttaglist = elytraStack.getEnchantmentTagList();

          for (int j = 0; j < nbttaglist.size(); ++j) {
            CompoundNBT nbttagcompound = nbttaglist.getCompound(j);
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
                .getValue(ResourceLocation.tryCreate(nbttagcompound.getString("id")));

            if (enchantment != null) {
              tooltip.add(new StringTextComponent(" ")
                  .appendSibling(enchantment.getDisplayName(nbttagcompound.getInt("lvl"))));
            }
          }
        }
      }

      if (ElytraNBT.isUseable(chestStack, elytraStack)) {
        tooltip.add(new StringTextComponent(" ").appendSibling(
            new TranslationTextComponent("item.durability",
                elytraStack.getMaxDamage() - elytraStack.getDamage(), elytraStack.getMaxDamage())));
      } else {
        tooltip.add(new StringTextComponent(" ").appendSibling(
            new TranslationTextComponent("tooltip.colytra.broken")
                .applyTextStyle(TextFormatting.RED)));
      }
    }
  }

  @SubscribeEvent
  public void onItemTooltip(ItemTooltipEvent evt) {
    ItemStack itemstack = evt.getItemStack();
    List<ITextComponent> tooltip = evt.getToolTip();
    getColytraTooltip(itemstack, tooltip);
  }
}
