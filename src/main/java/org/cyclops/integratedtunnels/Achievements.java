package org.cyclops.integratedtunnels;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cyclops.cyclopscore.player.ItemCraftedAchievements;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectVariable;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.core.evaluate.expression.LazyExpression;
import org.cyclops.integrateddynamics.core.evaluate.operator.Operators;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeList;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.item.AspectVariableFacade;
import org.cyclops.integrateddynamics.core.part.event.PartWriterAspectEvent;
import org.cyclops.integrateddynamics.item.ItemVariable;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.part.PartTypes;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * Obtainable achievements in this mod.
 * @author rubensworks
 */
public class Achievements {

    public static ItemStack makeAspectItemStack(IAspect aspect) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager().getRegistry(IVariableFacadeHandlerRegistry.class);
        return registry.writeVariableFacadeItem(new ItemStack(ItemVariable.getInstance()),
                new AspectVariableFacade(false, 0, aspect), Aspects.REGISTRY);
    }

    private static final Achievements _INSTANCE = new Achievements();

    public static final Achievement INTERFACE_ITEM   = new ExtendedAchievement("interfaceItem",   1, -1, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()),   null);
    public static final Achievement INTERFACE_FLUID  = new ExtendedAchievement("interfaceFluid",  1, -2, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()),  null);
    public static final Achievement INTERFACE_ENERGY = new ExtendedAchievement("interfaceEnergy", 1, -3, new ItemStack(PartTypes.INTERFACE_ENERGY.getItem()), null);

    public static final Achievement EXPORTER_ITEM = new ExtendedAchievement("exporterItem", 2, -1, new ItemStack(PartTypes.EXPORTER_ITEM.getItem()), null);
    public static final Achievement IMPORTER_ITEM = new ExtendedAchievement("importerItem", 2, -2, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()), null);

    public static final Achievement IMPORT_ALL_ITEMS = new ExtendedAchievement("importAllItems", 3, -1, makeAspectItemStack(TunnelAspects.Write.Item.BOOLEAN_IMPORT), null);

    public static final Achievement EXPORT_ITEMS_LIMIT = new ExtendedAchievement("exportItemsLimit", 3, -2, makeAspectItemStack(TunnelAspects.Write.Item.ITEMSTACK_EXPORT), null);

    public static final Achievement IMPORT_ITEMS_LIST = new ExtendedAchievement("importItemsList", 3, -3, makeAspectItemStack(TunnelAspects.Write.Item.LIST_IMPORT), null);

    public static final Achievement EXPORT_ENCHANTABLE_ITEMS = new ExtendedAchievement("exportEnchantableItems", 3, -4, makeAspectItemStack(TunnelAspects.Write.Item.PREDICATE_EXPORT), null);

    private static final Achievement[] ACHIEVEMENTS = {
            INTERFACE_ITEM,
            INTERFACE_FLUID,
            INTERFACE_ENERGY,

            EXPORTER_ITEM,
            IMPORTER_ITEM,

            IMPORT_ALL_ITEMS,

            EXPORT_ITEMS_LIMIT,

            IMPORT_ITEMS_LIST,

            EXPORT_ENCHANTABLE_ITEMS
    };

    private Achievements() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Register the achievements.
     */
    public static void registerAchievements() {
        AchievementPage.registerAchievementPage(new AchievementPage(Reference.MOD_NAME, ACHIEVEMENTS));

        ItemCraftedAchievements.register(PartTypes.INTERFACE_ITEM.getItem(),   INTERFACE_ITEM);
        ItemCraftedAchievements.register(PartTypes.INTERFACE_FLUID.getItem(),  INTERFACE_FLUID);
        ItemCraftedAchievements.register(PartTypes.INTERFACE_ENERGY.getItem(), INTERFACE_ENERGY);

        ItemCraftedAchievements.register(PartTypes.EXPORTER_ITEM.getItem(), EXPORTER_ITEM);
        ItemCraftedAchievements.register(PartTypes.IMPORTER_ITEM.getItem(), IMPORTER_ITEM);
    }

    @SubscribeEvent
    public void onPartWriterAspect(PartWriterAspectEvent event) {
        try {
            IVariable variable = ((IPartStateWriter) event.getPartState()).getVariable(event.getPartNetwork());
            if (event.getPartType() == PartTypes.IMPORTER_ITEM && event.getEntityPlayer() != null) {
                if (event.getAspect() == TunnelAspects.Write.Item.BOOLEAN_IMPORT
                        && variable.getValue() instanceof ValueTypeBoolean.ValueBoolean
                        && ((ValueTypeBoolean.ValueBoolean) variable.getValue()).getRawValue()) {
                    event.getEntityPlayer().addStat(IMPORT_ALL_ITEMS);
                } else if (event.getAspect() == TunnelAspects.Write.Item.LIST_IMPORT
                        && variable.getValue() instanceof ValueTypeList.ValueList) {
                    event.getEntityPlayer().addStat(IMPORT_ITEMS_LIST);
                }
            } else if (event.getPartType() == PartTypes.EXPORTER_ITEM && event.getEntityPlayer() != null) {
                if (event.getAspect() == TunnelAspects.Write.Item.BOOLEAN_EXPORT
                        && variable instanceof LazyExpression
                        && ((LazyExpression) variable).getOperator() == Operators.RELATIONAL_LT) {
                    IVariable variable0 = ((LazyExpression) variable).getInput()[0];
                    IValue value1 = ((LazyExpression) variable).getInput()[1].getValue();
                    if (variable0 instanceof IAspectVariable
                            && ((IAspectVariable) variable0).getAspect() == Aspects.Read.Inventory.INTEGER_COUNT
                            && value1.getType() == ValueTypes.INTEGER
                            && ((ValueTypeInteger.ValueInteger) value1).getRawValue() == 10) {
                        event.getEntityPlayer().addStat(EXPORT_ITEMS_LIMIT);
                    }
                } else if (event.getAspect() == TunnelAspects.Write.Item.PREDICATE_EXPORT
                        && variable instanceof LazyExpression
                        && ((LazyExpression) variable).getOperator() == Operators.OBJECT_ITEMSTACK_ISENCHANTABLE) {
                    event.getEntityPlayer().addStat(EXPORT_ENCHANTABLE_ITEMS);
                }
            }
        } catch (EvaluationException e) {

        }
    }

    static class ExtendedAchievement extends Achievement {

        public ExtendedAchievement(String id, int column, int row, ItemStack item, Achievement parent) {
            super(Reference.MOD_ID + "." + id, Reference.MOD_ID + "." + id, column, row, item, parent);
            registerStat();
        }

    }

}
