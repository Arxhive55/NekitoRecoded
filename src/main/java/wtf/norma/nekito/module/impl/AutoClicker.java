package wtf.norma.nekito.module.impl;


import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import wtf.norma.nekito.Nekito;
import wtf.norma.nekito.event.impl.update.EventUpdate;
import wtf.norma.nekito.module.Module;
import wtf.norma.nekito.settings.impl.BooleanSetting;
import wtf.norma.nekito.settings.impl.NumberSetting;
import wtf.norma.nekito.util.Time.TimerUtility;


public class AutoClicker extends Module implements Subscriber {

    private final TimerUtility timer = new TimerUtility();
    public NumberSetting cwelMIN = new NumberSetting("Min Left APS", 12, 1, 20, 1);
    public NumberSetting cwelMAX = new NumberSetting("Max Left APS", 15, 1, 20, 1);
    public BooleanSetting right = new BooleanSetting("Right", false);
    public NumberSetting pedalMIN = new NumberSetting("Min Right APS", 20, 1, 40, 1);
    public NumberSetting pedalMAX = new NumberSetting("Max Right APS", 22, 1, 40, 1);





    public AutoClicker() {
        super("AutoClicker", Category.LEGIT, Keyboard.KEY_NONE);
        this.addSettings(cwelMIN, cwelMAX, right, pedalMIN, pedalMAX);
    }

    @Override
    public void onEnable() {
        Nekito.EVENT_BUS.subscribe(this);
        timer.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        Nekito.EVENT_BUS.unsubscribe(this);
        timer.reset();
        super.onDisable();
    }

    @Subscribe
    private final Listener<EventUpdate> listener = new Listener<>(event ->{
        if (Minecraft.getMinecraft().currentScreen == null && Mouse.isButtonDown(0)) {
            if (mc.thePlayer.isUsingItem()) return;
            if (timer.hasReached(1000 / RandomUtils.nextInt((int) cwelMIN.getValue(), (int) cwelMAX.getValue()))) {
                KeyBinding.setKeyBindState(-100, true);
                KeyBinding.onTick(-100);
                timer.reset();
            } else {
                KeyBinding.setKeyBindState(-100, false);
            }
            if (right.isEnabled()) {
                if (Minecraft.getMinecraft().currentScreen == null && Mouse.isButtonDown(1)) {
                    if (timer.hasReached(1000 / RandomUtils.nextInt((int) pedalMIN.getValue(), (int) pedalMAX.getValue()))) {
                        int key = mc.gameSettings.keyBindUseItem.getKeyCode();
                        KeyBinding.setKeyBindState(key, true);
                        KeyBinding.onTick(key);
                        timer.reset();
                    } else {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                    }

                }
            }
        }
    });

}
