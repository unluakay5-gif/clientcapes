package dev.clientcapes;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ClientCapesMod implements ClientModInitializer {
    public static KeyMapping OPEN_MENU;

    @Override
    public void onInitializeClient() {
        CapeConfig.load();

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("clientcapes", "main"));
        OPEN_MENU = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.clientcapes.open", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, category));

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> CapeManager.reload());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_MENU.consumeClick()) {
                client.setScreen(new CapeConfigScreen(client.screen));
            }
        });
    }
}
