package wtf.norma.nekito.module.impl;

import org.lwjgl.input.Keyboard;
import wtf.norma.nekito.event.Event;
import wtf.norma.nekito.event.impl.EventMotion;
import wtf.norma.nekito.module.Module;
import wtf.norma.nekito.settings.impl.BooleanSetting;
import wtf.norma.nekito.settings.impl.ModeSetting;
import wtf.norma.nekito.settings.impl.NumberSetting;
import wtf.norma.nekito.util.Time.TimerUtility;
import wtf.norma.nekito.util.player.MovementUtil;

public class Fly extends Module {
    private final TimerUtility cwel = new TimerUtility();
    public ModeSetting mode = new ModeSetting("Mode", "Capabilities", "Capabilities", "Motion");
    public NumberSetting speed = new NumberSetting("Speed", 0.5f, 0, 10, 0.1f);
    public BooleanSetting stopOnDisable = new BooleanSetting("Stop on disable", true);
    double startY = 0;


    public Fly() {
        super("Fly", Category.MOVEMENT, Keyboard.KEY_F);
        this.addSettings(mode, speed, stopOnDisable);
    }

    @Override
    public void onEnable() {
        startY = mc.thePlayer.posY;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.timer.timerSpeed = 1f;
        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.capabilities.setFlySpeed(0.05f);
        if (stopOnDisable.isEnabled()) new MovementUtil().strafe(0);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventMotion) {
            if (e.isPre()) {
                switch (mode.getMode().toUpperCase()) {
                    case "CAPABILITIES":
                        mc.thePlayer.capabilities.isFlying = true;
                        mc.thePlayer.capabilities.setFlySpeed(speed.getValue());
                        break;
                    case "MOTION":
                        MovementUtil.setMotion(speed.getValue());
                        if (mc.gameSettings.keyBindSneak.isKeyDown()) mc.thePlayer.motionY -= speed.getValue() / 2;
                        if (mc.gameSettings.keyBindJump.isKeyDown()) mc.thePlayer.motionY += speed.getValue() / 2;
                        if (mc.thePlayer.movementInput.jump) {
                            mc.thePlayer.motionY = 0.5f;
                        }
                        // i was drunk ok?
                        else if (mc.thePlayer.movementInput.sneak) {
                            mc.thePlayer.motionY = -0.5f;
                        } else {
                            mc.thePlayer.motionY = 0;
                        }
                        break;


                }
            }
        }

    }
}