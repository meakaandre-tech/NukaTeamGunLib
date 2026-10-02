package com.nukateam.ntgl.modules.gunpack.resource;

import com.nukateam.ntgl.platform.PlatformHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Gun packs: every zip or folder in &lt;game dir&gt;/ntgl that has an assets and/or data folder is added as an always-enabled resource pack and/or data pack.
 * <p>
 * Fabric has no AddPackFindersEvent; PackRepositoryMixin adds {@link #createSource(PackType)} to the
 * client resource pack repository and to the server data pack repositories. Packs are read with the
 * vanilla file/folder pack classes instead of the custom NTGLPackResources.
 */
public class NTGLPackManager {
    private static final List<Path> RESOURCE_PACKS = new ArrayList<>();
    private static final List<Path> DATA_PACKS = new ArrayList<>();

    public static void scanPacks() {
        var packsDir = PlatformHelper.getGameDir().resolve("ntgl");

        try {
            if (!Files.exists(packsDir)) {
                Files.createDirectories(packsDir);
            }

            RESOURCE_PACKS.clear();
            DATA_PACKS.clear();

            try (var stream = Files.list(packsDir)) {
                stream.forEach(NTGLPackManager::processPack);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processPack(Path packPath) {
        try {
            var isZip = Files.isRegularFile(packPath) && packPath.toString().endsWith(".zip");
            var isFolder = Files.isDirectory(packPath);

            if (!isZip && !isFolder) return;

            var fs = isZip ?
                    FileSystems.newFileSystem(packPath, (ClassLoader) null) :
                    FileSystems.getDefault();

            var root = isZip ? fs.getPath("/") : packPath;

            var hasAssets = Files.exists(root.resolve("assets"));
            var hasData = Files.exists(root.resolve("data"));

            if (hasAssets) RESOURCE_PACKS.add(packPath);
            if (hasData) DATA_PACKS.add(packPath);

            if (isZip) fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** The repository source that offers the scanned gun packs of the given type. */
    public static RepositorySource createSource(PackType type) {
        return consumer -> addPacks(consumer, type == PackType.CLIENT_RESOURCES ? RESOURCE_PACKS : DATA_PACKS, type);
    }

    private static void addPacks(Consumer<Pack> consumer, List<Path> packs, PackType type) {
        for (Path packPath : packs) {
            PackLocationInfo locInfo = new PackLocationInfo(
                    "ntgl/" + packPath.getFileName(),
                    Component.literal("NTGL Pack: " + packPath.getFileName()),
                    PackSource.BUILT_IN,
                    Optional.empty()
            );

            Pack.ResourcesSupplier files = Files.isDirectory(packPath)
                    ? new PathPackResources.PathResourcesSupplier(packPath)
                    : new FilePackResources.FileResourcesSupplier(packPath);
            // serves packs made for older versions in the layout 26.x expects (see LegacyGunPackResources)
            Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
                @Override
                public net.minecraft.server.packs.PackResources openPrimary(PackLocationInfo location) {
                    return new LegacyGunPackResources(files.openPrimary(location), type);
                }

                @Override
                public net.minecraft.server.packs.PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
                    return new LegacyGunPackResources(files.openFull(location, metadata), type);
                }
            };

            Pack pack = Pack.readMetaAndCreate(
                    locInfo,
                    supplier,
                    type,
                    new PackSelectionConfig(true, Pack.Position.TOP, false)
            );

            if (pack != null) {
                consumer.accept(pack);
            }
        }
    }
}
