package org.cyclops.integratedtunnels.core;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * An extended fake player with more capabilities.
 * @author rubensworks
 */
public class ExtendedFakePlayer extends FakePlayer {

    private static GameProfile PROFILE = new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BB57-13D2C99CAE77"), "[IntegratedTunnels]");

    private long lastUpdateTick = 0;
    private long lastSwingUpdateTick = 0;
    private int ticksSinceLastTick = 0;

    public ExtendedFakePlayer(ServerLevel world) {
        super(world, PROFILE);
        // The inventory can expose the items of a network, so that items such as bows can consume them.
        // The inventory menu is not rebuilt for it, as fake players never open their inventory.
        this.inventory = new NetworkPlayerInventory(this);
        this.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);
        this.connection = new FakeNetHandlerPlayServer(world.getServer(), this);
    }

    @Override
    public NetworkPlayerInventory getInventory() {
        return (NetworkPlayerInventory) super.getInventory();
    }

    /**
     * Based on {@link net.minecraft.world.entity.player.Player#getProjectile(ItemStack)},
     * but projectiles are taken out of the network before they are returned,
     * instead of being read from the network's (virtual) inventory slots.
     */
    @Override
    public ItemStack getProjectile(ItemStack shootable) {
        if (!(shootable.getItem() instanceof ProjectileWeaponItem projectileWeaponItem)) {
            return ItemStack.EMPTY;
        }

        ItemStack heldProjectile = ProjectileWeaponItem.getHeldProjectile(this, projectileWeaponItem.getSupportedHeldProjectiles(shootable));
        if (!heldProjectile.isEmpty()) {
            return CommonHooks.getProjectile(this, shootable, heldProjectile);
        }

        Predicate<ItemStack> predicate = projectileWeaponItem.getAllSupportedProjectiles(shootable);
        NetworkPlayerInventory inventory = this.getInventory();
        for (int slot = 0; slot < inventory.getRealContainerSize(); slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if (predicate.test(itemStack)) {
                return CommonHooks.getProjectile(this, shootable, itemStack);
            }
        }

        int borrowedSlot = inventory.borrowFromNetwork(predicate);
        if (borrowedSlot >= 0) {
            return CommonHooks.getProjectile(this, shootable, inventory.getItem(borrowedSlot));
        }

        return CommonHooks.getProjectile(this, shootable, this.getAbilities().instabuild
                ? projectileWeaponItem.getDefaultCreativeAmmo(this, shootable) : ItemStack.EMPTY);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();
        int toTick = (int) (level.getGameTime() - this.lastUpdateTick);
        if (toTick > 0) {
            this.ticksSinceLastTick = toTick;
        }
        this.lastUpdateTick = level.getGameTime();

        this.attackStrengthTicker = (int) (level.getGameTime() - lastSwingUpdateTick);
        this.getInventory().tick();
        this.getCooldowns().tick();
    }

    @Override
    public void resetAttackStrengthTicker() {
        super.resetAttackStrengthTicker();
        lastSwingUpdateTick = level().getGameTime();
    }

    public void updateActiveHandSimulated() {
        if (this.isUsingItem()) {
            for (int i = 0; i < this.ticksSinceLastTick; i++) {
                if (this.isUsingItem()) {
                    ItemStack itemstack = this.getItemInHand(this.getUsedItemHand());
                    if (CommonHooks.canContinueUsing(this.useItem, itemstack)) {
                        this.useItem = itemstack;
                    }
                    // Based on LivingEntity#updateActiveHand
                    if (itemstack == this.useItem) {
                        if (!this.useItem.isEmpty()) {
                            useItemRemaining = EventHooks.onItemUseTick(this, useItem, useItemRemaining);
                            if (useItemRemaining > 0)
                                useItem.getItem().onUseTick(this.level(), this, useItem, useItemRemaining);
                        }

                        if (this.getUseItemRemainingTicks() <= 25 && this.getUseItemRemainingTicks() % 4 == 0) {
                            this.triggerItemUseEffects(this.useItem, 5);
                        }

                        if (--this.useItemRemaining <= 0 && !this.level().isClientSide() && !this.useItem.useOnRelease()) {
                            this.completeUsingItem();
                            break;
                        }
                    } else {
                        this.stopUsingItem();
                        break;
                    }
                }
            }
        } else {
            this.stopUsingItem();
        }
    }

    @Override
    public void startSleeping(BlockPos blockPos) {
        // Do nothing
    }

    @Override
    public @Nullable Entity changeDimension(DimensionTransition transition) {
        return this;
    }
}
