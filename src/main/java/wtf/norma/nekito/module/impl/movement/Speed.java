package wtf.norma.nekito.module.impl.movement;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import wtf.norma.nekito.Nekito;
import wtf.norma.nekito.module.Module;
import wtf.norma.nekito.event.impl.update.EventUpdate;
import wtf.norma.nekito.settings.impl.ModeSetting;
import wtf.norma.nekito.util.player.MovementUtil;


public class Speed extends Module implements Subscriber {

    public ModeSetting mode = new ModeSetting("Mode", "Vulcan", "Vulcan", "Ground", "LowHop", "Matrix Timer", "Ground Strafe");

    public Speed() {
        super("Player Speed", Category.MOVEMENT, Keyboard.KEY_G);
        addSettings(mode);
    }

    public static boolean isOnGround() {
        if (!mc.thePlayer.onGround) return false;
        return mc.thePlayer.isCollidedVertically;
    }

    @Override
    public void onEnable() {
        Nekito.EVENT_BUS.subscribe(this);
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
        Nekito.EVENT_BUS.unsubscribe(this);
        mc.timer.timerSpeed = 1F;
        mc.thePlayer.jumpMovementFactor = 0.02F;
    }

    @Subscribe
    private final Listener<EventUpdate> listener = new Listener<>(event ->{
        switch (mode.getMode()) {
            case "Vulcan":
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    mc.thePlayer.motionY = 0.44;
                    MovementUtil.strafe((double) MovementUtil.getSpeed());
                } else if (!mc.thePlayer.onGround && mc.thePlayer.ticksExisted % 15 == 0) {
                    mc.thePlayer.motionY = -0.44;
                }

                break;

            case "Ground":
                if (MovementUtil.isMoving()) {
                    if (mc.thePlayer.onGround) {
                        // fuck does this bypass? vanilla ac??!??!
                        MovementUtil.setMotion(MovementUtil.getSpeed() + 0.3f);
                    }
                }
                break;

            case "LowHop":
                if (mc.thePlayer.onGround && mc.thePlayer.moveForward > 0) {
//                    double speed = 0.5;
//                    float yaw = mc.thePlayer.rotationYaw * 0.0174532920F;
                    mc.thePlayer.motionY = 0.2;
                    // this is the saddest thing I have ever seen. why are u using sin and cos for a fucking speed for god's sake.
//                    mc.thePlayer.motionX -= MathHelper.sin(yaw) * (speed / 2);
//                    mc.thePlayer.motionZ += MathHelper.cos(yaw) * (speed / 2);
                }
                break;

            case "Matrix Timer":
                float timerValue = mc.thePlayer.fallDistance <= 0.22f ? 2f :
                        (float) (mc.thePlayer.fallDistance < 1.25f ? 0.67 : 1f);
                if (MovementUtil.isMoving()) {
                    mc.timer.timerSpeed = timerValue;
                    mc.thePlayer.jumpMovementFactor = 0.026423f; //slay 🎅🎅🎅🎅🎅🎅🎅
                    if (isOnGround()) {
                        mc.thePlayer.jump();
                    }
                } else {
                    mc.timer.timerSpeed = 1.0f;
                }
                break;
            case "Ground Strafe":
                if (MovementUtil.isMoving() && mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    MovementUtil.strafe((double) MovementUtil.getSpeed());
                }
                break;
        }
    });
}

