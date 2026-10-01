package dev.clientcapes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CapeConfigScreen extends Screen {
    private final Screen parent;
    private final List<String> options = new ArrayList<>();
    private int index;
    private Button capeButton;
    private Button enabledButton;

    public CapeConfigScreen(Screen parent) {
        super(Component.literal("Client Capes"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        options.clear();
        options.add(CapeConfig.NONE);
        options.addAll(CapeManager.names());
        index = Math.max(0, options.indexOf(CapeConfig.INSTANCE.selectedCape));

        int cx = this.width / 2;
        int y = this.height / 4;

        addRenderableWidget(Button.builder(Component.literal("<"), b -> step(-1))
                .bounds(cx - 100, y, 20, 20).build());
        capeButton = addRenderableWidget(Button.builder(capeLabel(), b -> step(1))
                .bounds(cx - 78, y, 156, 20).build());
        addRenderableWidget(Button.builder(Component.literal(">"), b -> step(1))
                .bounds(cx + 80, y, 20, 20).build());

        enabledButton = addRenderableWidget(Button.builder(enabledLabel(), b -> {
            CapeConfig.INSTANCE.enabled = !CapeConfig.INSTANCE.enabled;
            CapeConfig.INSTANCE.save();
            b.setMessage(enabledLabel());
        }).bounds(cx - 100, y + 26, 200, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Reload cape folder"), b -> {
            CapeManager.reload();
            rebuildWidgets();
        }).bounds(cx - 100, y + 52, 200, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Open cape folder"),
                b -> Util.getPlatform().openPath(CapeManager.dir()))
                .bounds(cx - 100, y + 78, 200, 20).build());

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
                .bounds(cx - 100, this.height - 30, 200, 20).build());
    }

    private void step(int dir) {
        index = Math.floorMod(index + dir, options.size());
        CapeConfig.INSTANCE.selectedCape = options.get(index);
        CapeConfig.INSTANCE.save();
        capeButton.setMessage(capeLabel());
    }

    private Component capeLabel() {
        return Component.literal("Cape: " + options.get(index));
    }

    private Component enabledLabel() {
        return Component.literal("Show cape: " + (CapeConfig.INSTANCE.enabled ? "ON" : "OFF"));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        g.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);
        if (CapeManager.names().isEmpty()) {
            g.drawCenteredString(this.font,
                    Component.literal("No capes found - put PNG files in the cape folder, then Reload."),
                    this.width / 2, this.height / 4 + 108, 0xFFFFAAAA);
        }
    }
}
