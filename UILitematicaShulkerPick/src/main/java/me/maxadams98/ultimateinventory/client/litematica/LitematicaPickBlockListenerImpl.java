package me.maxadams98.ultimateinventory.client.litematica;

import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import me.maxadams98.ultimateinventory.client.util.PickBlockUtils;
import me.maxadams98.ultimateinventory.client.PrinterIntegration;

/**
 * Implementation of Litematica's pick block event listener interface.
 * Handles pick block events from Litematica and triggers shulker box search if needed.
 */
class LitematicaPickBlockListenerImpl {
    private ItemStack lastPickBlockStack = ItemStack.EMPTY;
    private boolean isProcessingPickBlock = false; // Prevent concurrent processing
    private long processingStartTime = 0; // Track when processing started for timeout
    private static Class<?> levelClass;
    private static Class<?> blockPosClass;
    private static Class<?> blockStateClass;
    
    static {
        try {
            // Try to load the classes if available (for proper type checking)
            levelClass = Class.forName("net.minecraft.world.level.Level");
            blockPosClass = Class.forName("net.minecraft.core.BlockPos");
            blockStateClass = Class.forName("net.minecraft.world.level.block.state.BlockState");
        } catch (ClassNotFoundException e) {
            // Classes not available, use Object
            levelClass = Object.class;
            blockPosClass = Object.class;
            blockStateClass = Object.class;
        }
    }
    
    public Supplier<String> getName() {
        return () -> "UltimateInventory";
    }
    
    public void onSchematicPickBlockCancelled(Supplier<String> cancelledBy) {
        lastPickBlockStack = ItemStack.EMPTY;
        isProcessingPickBlock = false; // Reset processing state on cancellation
    }
    
    public Object onSchematicPickBlockStart(boolean closest) {
        return getSuccessResult();
    }

    // Use Object for parameters we don't actually use - Java will match the interface at runtime
    public Object onSchematicPickBlockPreGather(Object schematicWorld, Object pos, Object expectedState) {
        return getSuccessResult();
    }

    public Object onSchematicPickBlockPrePick(Object schematicWorld, Object pos, Object expectedState, ItemStack stack) {
        // Don't overwrite if we're currently processing a pick block event
        if (!isProcessingPickBlock) {
            lastPickBlockStack = stack.copy();
        }

        return getSuccessResult();
    }

    public void onSchematicPickBlockSuccess() {
        // The mixin handler (LitematicaMixinHandler) does the actual shulker search logic
        // This event listener just tracks operations for debugging purposes

        // Reset state for next operation
        lastPickBlockStack = ItemStack.EMPTY;
        isProcessingPickBlock = false;
    }
    
    private Object getSuccessResult() {
        try {
            Class<?> resultClass = Class.forName("fi.dy.masa.litematica.schematic.pickblock.SchematicPickBlockEventResult");
            return resultClass.getEnumConstants()[0]; // SUCCESS
        } catch (Exception e) {
            return null;
        }
    }
}

