package dev.clientcapes;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/** Loads every PNG in .minecraft/config/clientcapes/capes/ as a selectable cape. */
public class CapeManager {
    private static final Map<String, ClientAsset.ResourceTexture> CAPES = new LinkedHashMap<>();
    private static final List<Identifier> REGISTERED = new ArrayList<>();

    public static Path dir() {
        return FabricLoader.getInstance().getConfigDir().resolve("clientcapes").resolve("capes");
    }

    public static List<String> names() {
        return new ArrayList<>(CAPES.keySet());
    }

    public static ClientAsset.Texture get(String name) {
        return CAPES.get(name);
    }

    /** Must be called on the render thread. */
    public static void reload() {
        Minecraft mc = Minecraft.getInstance();
        for (Identifier id : REGISTERED) mc.getTextureManager().release(id);
        REGISTERED.clear();
        CAPES.clear();

        try {
            Files.createDirectories(dir());
        } catch (IOException e) {
            System.err.println("[ClientCapes] Could not create cape folder: " + e);
            return;
        }

        int counter = 0;
        try (Stream<Path> files = Files.list(dir())) {
            List<Path> pngs = files
                    .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png"))
                    .sorted().toList();
            for (Path p : pngs) {
                String file = p.getFileName().toString();
                String name = file.substring(0, file.length() - 4);
                try (InputStream in = Files.newInputStream(p)) {
                    NativeImage img = normalize(NativeImage.read(in));
                    String safe = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "_");
                    Identifier id = Identifier.fromNamespaceAndPath("clientcapes", "cape/" + (counter++) + "_" + safe);
                    DynamicTexture tex = new DynamicTexture(() -> "clientcapes:" + name, img);
                    tex.upload();
                    mc.getTextureManager().register(id, tex);
                    REGISTERED.add(id);
                    // id + texturePath both point at our dynamic texture
                    CAPES.put(name, new ClientAsset.ResourceTexture(id, id));
                } catch (Exception e) {
                    System.err.println("[ClientCapes] Skipping " + file + ": " + e);
                }
            }
        } catch (IOException e) {
            System.err.println("[ClientCapes] Could not list cape folder: " + e);
        }

        if (!CapeConfig.NONE.equals(CapeConfig.INSTANCE.selectedCape)
                && !CAPES.containsKey(CapeConfig.INSTANCE.selectedCape)) {
            CapeConfig.INSTANCE.selectedCape = CapeConfig.NONE;
        }
    }

    /**
     * The cape model expects a 64x32 texture (or a multiple of it for HD capes).
     * Many cape images are distributed cropped to 22x17 (or a multiple) - pad those onto a 64x32 canvas.
     */
    private static NativeImage normalize(NativeImage src) {
        int w = src.getWidth(), h = src.getHeight();
        if (w % 22 == 0 && h == (w / 22) * 17) {
            int scale = w / 22;
            NativeImage out = new NativeImage(64 * scale, 32 * scale, true);
            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++)
                    out.setPixel(x, y, src.getPixel(x, y));
            src.close();
            return out;
        }
        if (w != h * 2) {
            src.close();
            throw new IllegalArgumentException("Unsupported size " + w + "x" + h + " (need 64x32, 22x17 or a multiple)");
        }
        return src;
    }
}
