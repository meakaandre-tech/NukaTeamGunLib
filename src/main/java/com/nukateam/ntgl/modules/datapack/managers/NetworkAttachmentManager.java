package com.nukateam.ntgl.modules.datapack.managers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.nukateam.ntgl.modules.constants.Paths;
import com.nukateam.ntgl.modules.datapack.ConfigSupplier;
import com.nukateam.ntgl.modules.datapack.ConfigUtils;
import com.nukateam.ntgl.common.data.attachment.IAttachment;
import com.nukateam.ntgl.common.data.config.attachment.AttachmentConfig;
import com.nukateam.ntgl.common.network.message.weapon.S2CMessageUpdateAttachments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import com.nukateam.ntgl.Ntgl;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;
import java.util.*;

public class NetworkAttachmentManager extends SimplePreparableReloadListener<Map<IAttachment<?>, AttachmentConfig>> {
    private static List<IAttachment<?>> clientRegisteredAttachments = new ArrayList<>();
    private static NetworkAttachmentManager instance;

    private Map<Identifier, AttachmentConfig> registeredAttachments = new HashMap<>();

    public static void onServerStopped() {
        // the reload listener is registered once on Fabric and lives for the whole game session
    }

    public static void register() {
        NetworkAttachmentManager manager = new NetworkAttachmentManager();
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Ntgl.ntglResource("attachments"), manager);
        NetworkAttachmentManager.instance = manager;
    }

    @Override
    protected Map<IAttachment<?>, AttachmentConfig> prepare(ResourceManager manager, ProfilerFiller profiler) {
        return ConfigUtils.getConfigMap(manager, BuiltInRegistries.ITEM, (v) -> v instanceof IAttachment<?>, AttachmentConfig.class, Paths.ATTACHMENTS);
    }

    @Override
    protected void apply(Map<IAttachment<?>, AttachmentConfig> objects, ResourceManager resourceManager, ProfilerFiller profiler) {
        ImmutableMap.Builder<Identifier, AttachmentConfig> builder = ImmutableMap.builder();

        objects.forEach((abstractItem, config) -> {
            if(abstractItem instanceof Item item) {
                Validate.notNull(BuiltInRegistries.ITEM.getKey(item));
                builder.put(BuiltInRegistries.ITEM.getKey(item), config);
                abstractItem.setConfig(new ConfigSupplier<>(config));
            }
        });

        this.registeredAttachments = builder.build();
    }

    public void writeRegistered(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.registeredAttachments.size());
        this.registeredAttachments.forEach((id, config) -> {
            buffer.writeIdentifier(id);
            buffer.writeNbt(config.serializeNBT(null));
        });
    }

    public static ImmutableMap<Identifier, AttachmentConfig> readRegistered(FriendlyByteBuf buffer) {
        var size = buffer.readVarInt();

        if (size > 0) {
            var builder = ImmutableMap.<Identifier, AttachmentConfig>builder();

            for (int i = 0; i < size; i++) {
                var id = buffer.readIdentifier();
                AttachmentConfig config = AttachmentConfig.create(id, buffer.readNbt());
                builder.put(id, config);
            }
            return builder.build();
        }
        return ImmutableMap.of();
    }

    public static boolean updateRegisteredAttachments(S2CMessageUpdateAttachments message) {
        return updateRegisteredAttachments(message.getRegistered());
    }

    private static boolean updateRegisteredAttachments(Map<Identifier, AttachmentConfig> registered) {
        clientRegisteredAttachments.clear();
        if (registered != null) {
            for (Map.Entry<Identifier, AttachmentConfig> entry : registered.entrySet()) {
                Item item = BuiltInRegistries.ITEM.getValue(entry.getKey());
                if (!(item instanceof IAttachment<?>)) {
                    return false;
                }
                ((IAttachment<?>) item).setConfig(new ConfigSupplier<>(entry.getValue()));
                clientRegisteredAttachments.add((IAttachment<?>) item);
            }
            return true;
        }
        return false;
    }

    public Map<Identifier, AttachmentConfig> getRegisteredAttachments() {
        return this.registeredAttachments;
    }

    public static List<IAttachment<?>> getClientRegisteredAttachments() {
        return ImmutableList.copyOf(clientRegisteredAttachments);
    }

    @Nullable
    public static NetworkAttachmentManager get() {
        return instance;
    }

    public static class Supplier {
        private final AttachmentConfig config;

        private Supplier(AttachmentConfig config) {
            this.config = config;
        }

        public AttachmentConfig getConfig() {
            return this.config;
        }
    }
}
