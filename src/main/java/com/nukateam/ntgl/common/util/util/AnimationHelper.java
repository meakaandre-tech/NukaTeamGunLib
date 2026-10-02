package com.nukateam.ntgl.common.util.util;

import com.geckolib.cache.GeckoLibResources;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.animation.Animation;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.model.GeoModel;

import javax.annotation.Nullable;
import java.util.List;

public class AnimationHelper<T extends GeoAnimatable> {
    private final T animatable;
    private final GeoModel model;

    public AnimationHelper(T animatable, GeoModel model) {
        this.animatable = animatable;
        this.model = model;
    }

    /**
     * Deprecated: Use #syncAnimation(AnimationTest, int, String...) instead
     * <p>
     * Sets the animation controller speed so that the animation duration matches the target duration
     */
    @Deprecated
    public void syncAnimation(AnimationTest event, String animationName, int targetDuration) {
        var multiplier = (float) getSpeedMultiplier(animationName, targetDuration);
        event.setControllerSpeed(multiplier);
    }

    /**
     * Sets the animation controller speed so that the animation duration matches the target duration
     */
    public void syncAnimation(AnimationTest event, int targetDuration, String... animations) {
        var multiplier = getSpeedMultiplier(targetDuration, List.of(animations));
        event.setControllerSpeed((float) multiplier);
    }

    public void syncAnimation(AnimationTest event, int targetDuration, Iterable<String> animations) {
        var multiplier = getSpeedMultiplier(targetDuration, animations);
        event.setControllerSpeed((float) multiplier);
    }

    public double getSpeedMultiplier(double targetDuration, Iterable<String> animations) {
        var generalDuration = 0.0;
        for (String name : animations)
            generalDuration += getAnimationDuration(name);

        return generalDuration / targetDuration;
    }

    public double getSpeedMultiplier(String animationName, double targetDuration) {
        var duration = getAnimationDuration(animationName);
        return duration / targetDuration;
    }

    public double getAnimationDuration(String animationName) {
        var animation = getAnimation(animationName);
        // GeckoLib 5 stores the length in seconds, the callers work in ticks
        return animation != null ? animation.length() * 20.0 : 1;
    }

    public boolean containsAnimation(String animationName) {
        return getAnimation(animationName) != null;
    }

    @Nullable
    public Animation getAnimation(String animationName){
        try {
            var animationResource = model.getAnimationResource(animatable);
            if (animationResource == null) return null;
            var bakedAnimations = GeckoLibResources.getBakedAnimations().cache().get(animationResource);
            return bakedAnimations != null ? (Animation) bakedAnimations.animations().get(animationName) : null;
        }
        catch (RuntimeException e){
            return null;
        }
    }

    public boolean hasAnimation(String animationName){
        return getAnimation(animationName) != null;
    }
}
