package org.cyclops.integratedtunnels.item;

import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.ToolItem;
import org.cyclops.integratedtunnels.RegistryEntries;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * A dummy pickaxe that can harvest everything.
 * @author rubensworks
 */
public class ItemDummyPickAxe extends ToolItem {

    private static final Map<EnchantmentData, ItemStack> ITEMSTACKS = Maps.newHashMap();

    public ItemDummyPickAxe(Item.Properties properties) {
        super(1000, 1000, ItemTier.DIAMOND, Collections.emptySet(), properties);
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState blockIn) {
        return true;
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, BlockState state) {
        return true;
    }

    public static ItemStack getItemStack(boolean silkTouch, int fortune) {
        EnchantmentData data = new EnchantmentData(silkTouch, fortune);
        return ITEMSTACKS.computeIfAbsent(data, (key) -> {
            ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_DUMMY_PICKAXE, 1);
            if (silkTouch) {
                itemStack.enchant(Enchantments.SILK_TOUCH, 1);
            }
            if (fortune > 0) {
                itemStack.enchant(Enchantments.BLOCK_FORTUNE, fortune);
            }
            return itemStack;
        });
    }

    public static class EnchantmentData {

        private final boolean silkTouch;
        private final int fortune;

        public EnchantmentData(boolean silkTouch, int fortune) {
            this.silkTouch = silkTouch;
            this.fortune = fortune;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EnchantmentData)) return false;
            EnchantmentData that = (EnchantmentData) o;
            return silkTouch == that.silkTouch &&
                    fortune == that.fortune;
        }

        @Override
        public int hashCode() {
            return Objects.hash(silkTouch, fortune);
        }
    }

}
