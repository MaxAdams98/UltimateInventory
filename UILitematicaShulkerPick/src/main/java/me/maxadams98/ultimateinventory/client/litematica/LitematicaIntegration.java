package me.maxadams98.ultimateinventory.client.litematica;

/**
 * Main integration class for Litematica support.
 * Registers the event listener when Litematica is present.
 */
public class LitematicaIntegration {
    private static boolean registered = false;
    
    /**
     * Registers the Litematica pick block listener.
     * This will only succeed if Litematica is present at runtime.
     */
    public static void register() {
        if (registered) {
            return;
        }

        try {
            // Check if Litematica is available and get the event handler
            Class<?> eventHandlerClass = Class.forName("fi.dy.masa.litematica.schematic.pickblock.SchematicPickBlockEventHandler");

            Object eventHandler = eventHandlerClass.getMethod("getInstance").invoke(null);

            // Create our listener implementation
            LitematicaPickBlockListenerImpl impl = new LitematicaPickBlockListenerImpl();

            // Create a dynamic proxy that implements the interface
            Class<?> listenerInterface = Class.forName("fi.dy.masa.litematica.interfaces.ISchematicPickBlockEventListener");
            Object listenerInstance = java.lang.reflect.Proxy.newProxyInstance(
                listenerInterface.getClassLoader(),
                new Class<?>[] { listenerInterface },
                new LitematicaInvocationHandler(impl)
            );

            // Register it
            eventHandlerClass.getMethod("registerSchematicPickBlockEventListener", listenerInterface)
                .invoke(eventHandler, listenerInstance);

            registered = true;
        } catch (ClassNotFoundException e) {
            // Litematica not present - this is normal
            registered = false;
        } catch (Exception e) {
            System.out.println("[UltimateInventory] Failed to register Litematica listener: " + e.getClass().getSimpleName());
            registered = false;
        }
    }
    
    public static boolean isRegistered() {
        return registered;
    }
}

