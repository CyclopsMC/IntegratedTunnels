package org.cyclops.integratedtunnels.item;

import com.google.common.collect.Maps;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.cyclops.integratedtunnels.RegistryEntries;

import java.util.Map;
import java.util.Objects;

/**
 * A dummy pickaxe that can harvest everything.
 * @author rubensworks
 */
public class ItemDummyPickAxe extends DiggerItem {

    private static final Map<EnchantmentData, ItemStack> ITEMSTACKS = Maps.newHashMap();

    public ItemDummyPickAxe(Item.Properties properties) {
        super(Tiers.DIAMOND, BlockTags.MINEABLE_WITH_PICKAXE, properties);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return true;
    }

    public static ItemStack getItemStack(boolean silkTouch, int fortune) {
        EnchantmentData data = new EnchantmentData(silkTouch, fortune);
        return ITEMSTACKS.computeIfAbsent(data, (key) -> {
            ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_DUMMY_PICKAXE, 1);
            if (silkTouch) {
                itemStack.enchant(ServerLifecycleHooks.getCurrentServer().registryAccess().registry(Registries.ENCHANTMENT).get().getHolderOrThrow(Enchantments.SILK_TOUCH), 1);
            }
            if (fortune > 0) {
                itemStack.enchant(ServerLifecycleHooks.getCurrentServer().registryAccess().registry(Registries.ENCHANTMENT).get().getHolderOrThrow(Enchantments.FORTUNE), fortune);
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
