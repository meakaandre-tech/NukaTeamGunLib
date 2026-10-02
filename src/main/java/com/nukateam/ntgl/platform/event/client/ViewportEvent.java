package com.nukateam.ntgl.platform.event.client;

import com.nukateam.ntgl.platform.Event;

/**
 * Camera related events, raised from CameraMixin. Stand-in for NeoForge's ViewportEvent.
 */
public abstract class ViewportEvent extends Event {
    private final float partialTick;

    protected ViewportEvent(float partialTick) {
        this.partialTick = partialTick;
    }

    public float getPartialTick() {
        return partialTick;
    }

    /** Lets listeners change the field of view (in degrees) the game computed. */
    public static class ComputeFov extends ViewportEvent {
        private final boolean usedConfiguredFov;
        private float fov;

        public ComputeFov(float partialTick, float fov, boolean usedConfiguredFov) {
            super(partialTick);
            this.fov = fov;
            this.usedConfiguredFov = usedConfiguredFov;
        }

        public float getFOV() {
            return fov;
        }

        public void setFOV(float fov) {
            this.fov = fov;
        }

        /** True for the world FOV (the option value is used), false for the first-person hand FOV. */
        public boolean usedConfiguredFov() {
            return usedConfiguredFov;
        }
    }

    /** Lets listeners change the camera angles (in degrees) after the game set them up. */
    public static class ComputeCameraAngles extends ViewportEvent {
        private float yaw;
        private float pitch;
        private float roll;

        public ComputeCameraAngles(float partialTick, float yaw, float pitch, float roll) {
            super(partialTick);
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }

        public float getYaw() {
            return yaw;
        }

        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        public float getPitch() {
            return pitch;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        public float getRoll() {
            return roll;
        }

        public void setRoll(float roll) {
            this.roll = roll;
        }
    }
}
