package com.nukateam.ntgl.client.model.gun;

import com.nukateam.geo.render.ItemAnimator;
import com.nukateam.ntgl.client.animators.WeaponAnimator;
import com.nukateam.ntgl.client.model.IGlowingModel;
import com.nukateam.ntgl.client.util.helpers.GeoModelHelper;
import com.nukateam.ntgl.common.util.util.WeaponStateHelper;
import com.nukateam.geo.render.AnimatableGeoModel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public class GeoWeaponModel<Animator extends WeaponAnimator> extends AnimatableGeoModel<Animator> implements IGlowingModel<Animator> {
    public static final GeoWeaponModel INSTANCE = new GeoWeaponModel();

    @Override
    public Identifier getModelResource(Animator animator) {
        return GeoModelHelper.getGunResource(animator, "weapons/", "");
    }

    @Override
    public Identifier getTextureResource(Animator animator) {
        var textures = animator.getConfig().getTextures();
        var variant = WeaponStateHelper.getVariant(animator.getStack());
        var resource = textures.containsKey(variant) ?
                textures.get(variant) :
                GeoModelHelper.getGunResource(animator, "textures/weapons/" + animator.getId().getPath() + "/", ".png".formatted());

        return resource;
    }

    @Override
    public Identifier getAnimationResource(Animator animator) {
        return GeoModelHelper.getGunResource(animator, "weapons/", "");
    }

    @Override
    public RenderType getRenderType(Animator animatable, Identifier texture) {
        return RenderTypes.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public Identifier getGlowingTextureResource(Animator animator) {
        var name = animator.getId().getPath();
        var modId = animator.getId().getNamespace();

        return Identifier.tryBuild(modId, "textures/weapons/" + name + "/" + name + "_glowmask" + ".png");
    }
}
