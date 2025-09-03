package wtf.norma.nekito.module.impl.combat;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import wtf.norma.nekito.Nekito;
import wtf.norma.nekito.module.Module;
import wtf.norma.nekito.event.impl.movement.EventMotion;
import wtf.norma.nekito.module.impl.other.Tickbase;
import wtf.norma.nekito.settings.impl.BooleanSetting;
import wtf.norma.nekito.settings.impl.ModeSetting;
import wtf.norma.nekito.settings.impl.NumberSetting;
import wtf.norma.nekito.util.Time.TimerUtility;
import wtf.norma.nekito.util.packet.PacketUtility;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class KillAura extends Module implements Subscriber {

    public static EntityLivingBase target;

    public TimerUtility t = new TimerUtility();

    public NumberSetting range = new NumberSetting("Range", 3, 1, 6, 0.5f);
    public NumberSetting aps = new NumberSetting("APS", 12, 1, 20, 0.1f);
    public ModeSetting rotations = new ModeSetting("Rotations", "Recoded", "Recoded", "Basic", "None");
    public BooleanSetting onlyPlayers = new BooleanSetting("Only players", true);

    public KillAura() {
        super("KillAura", Category.COMBAT, Keyboard.KEY_R);
        this.addSettings(rotations, range, aps, onlyPlayers);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Nekito.EVENT_BUS.subscribe(this);
        target = null;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Nekito.EVENT_BUS.unsubscribe(this);
    }

    public float[] rotations(EntityLivingBase entity) {

        // fuck ass rotations. -Arxhive55

        double x = entity.posX - mc.thePlayer.posX;
        double y = entity.posY - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) + 1.5;
        double z = entity.posZ - mc.thePlayer.posZ;

        double u = MathHelper.sqrt(x * x + z * z);

        float u2 = (float) (MathHelper.atan2(z, x) * (180D / Math.PI) - 90.0F);
        float u3 = (float) (-MathHelper.atan2(y, u) * (180D / Math.PI));

        return new float[]{u2, u3};
    }

    @Subscribe
    private final Listener<EventMotion> listener = new Listener<>(event -> {
        if (event.isPre()) {
            target = getTarget(range.getValue());
            if (target != null) {
                if (rotate(target, event)) {
                    if (t.hasReached((long) (1000 / aps.getValue()))) {
                        if (Nekito.INSTANCE.getModuleManager().getModule(Criticals.class).isToggled()) {
                            PacketUtility.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0645, mc.thePlayer.posZ, false));
                            PacketUtility.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                        }
                        mc.thePlayer.swingItem();
                        mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                        t.reset();
                    }
                }
            }
        }
    });

    public float[] recodedRotations(EntityLivingBase entity) {

        // non fuck ass rotations. -Arxhive55

        final Vec3 eyePos = mc.thePlayer.getPositionEyes(1.0f);
        final double diffX = entity.posX - eyePos.xCoord;
        final double diffY = entity.posY + entity.getEyeHeight() / 1.2f - eyePos.yCoord;
        final double diffZ = entity.posZ - eyePos.zCoord;
        final double distXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 57.29577951308232) - 90.0f;
        float pitch = (float) (-(Math.atan2(diffY, distXZ) * 57.29577951308232));
        yaw += (float) ((Math.random() - 0.5) * 2 * 2);
        pitch += (float) ((Math.random() - 0.5) * 2 * 2);
        pitch = Math.max(-90F, Math.min(90F, pitch));
        return new float[] {yaw, pitch};
    }

    public boolean rotate(EntityLivingBase target, EventMotion event) {
        float[] rots;
        switch (rotations.getMode()) {
            case "Basic":
                rots = rotations(target);
                event.setYaw(rots[0]);
                event.setPitch(rots[0]);
                mc.thePlayer.rotationYawHead = rots[0];
                mc.thePlayer.renderYawOffset = rots[0];
                break;
            case "Recoded":
                rots = recodedRotations(target);
                event.setYaw(rots[0]);
                event.setPitch(rots[0]);
                mc.thePlayer.rotationYawHead = rots[0];
                mc.thePlayer.renderYawOffset = rots[0];
                break;
            case "None":
                break;
        }
        return true;
    }


    public EntityLivingBase getTarget(double range) {
        List<Entity> targets = mc.theWorld.getLoadedEntityList().stream().filter(entity -> entity instanceof EntityLivingBase).filter(entity -> entity != mc.thePlayer).filter(entity -> !entity.isDead).filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= range).sorted(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity))).collect(Collectors.toList());

        if (onlyPlayers.isEnabled())
            targets = targets.stream().filter(EntityPlayer.class::isInstance).collect(Collectors.toList());

        if (!targets.isEmpty()) {
            EntityLivingBase target = (EntityLivingBase) targets.get(0);
            return target;
        }
        return null;
    }

}
