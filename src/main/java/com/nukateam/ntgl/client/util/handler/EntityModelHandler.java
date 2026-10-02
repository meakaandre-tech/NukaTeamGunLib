package com.nukateam.ntgl.client.util.handler;

import com.nukateam.ntgl.common.data.WeaponData;
import com.nukateam.ntgl.common.foundation.item.interfaces.IWeapon;
import com.nukateam.ntgl.common.util.util.WeaponModifierHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

/**
 * Author: MrCrayfish
 * <p>
 * Fabric port: called from LivingEntityRendererMixin before the render state of a living entity is
 * extracted (was a RenderLivingEvent.Pre handler).
 */
public class EntityModelHandler {
    public static void onRenderEntityPre(LivingEntity entity, float partialTick) {
        var heldItem = entity.getMainHandItem();

        if (heldItem.getItem() instanceof IWeapon) {
            var heldAnimation = WeaponModifierHelper.getGripType(new WeaponData(heldItem, entity))
                    .getHeldAnimation();

            var aimProgress = AimingHandler.get()
                    .getAimProgress(entity, partialTick);

            heldAnimation.applyEntityPreRender(
                    entity,
                    InteractionHand.MAIN_HAND,
                    aimProgress);
        }
    }
}
