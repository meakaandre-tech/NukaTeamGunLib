package com.nukateam.ntgl.mixin.ntgl.common;

import com.nukateam.ntgl.modules.gunpack.resource.NTGLPackManager;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Adds the NTGL gun packs (game dir/ntgl) to the pack repositories (NeoForge: AddPackFindersEvent).
 * A repository holding the vanilla server pack source is a data pack repository, one holding the
 * client pack source is the resource pack repository.
 */
@Mixin(PackRepository.class)
public class PackRepositoryMixin {
    @Shadow @Final @Mutable
    private Set<RepositorySource> sources;

    @Inject(method = "<init>([Lnet/minecraft/server/packs/repository/RepositorySource;)V", at = @At("RETURN"))
    private void ntgl$addGunPacks(RepositorySource[] originalSources, CallbackInfo ci) {
        PackType type = null;
        for (RepositorySource source : this.sources) {
            if (source instanceof ServerPacksSource) {
                type = PackType.SERVER_DATA;
                break;
            }
            if (source.getClass().getName().equals("net.minecraft.client.resources.ClientPackSource")) {
                type = PackType.CLIENT_RESOURCES;
                break;
            }
        }
        if (type == null) return;

        var newSources = new LinkedHashSet<>(this.sources);
        newSources.add(NTGLPackManager.createSource(type));
        this.sources = newSources;
    }
}
