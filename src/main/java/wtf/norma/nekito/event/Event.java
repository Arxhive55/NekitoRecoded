package wtf.norma.nekito.event;

import wtf.norma.nekito.module.Module;
import wtf.norma.nekito.Nekito;

public class Event {

    public EventType type;
    private boolean canceled;

    public static void dispatch(Event e) {
        Nekito.INSTANCE.getModuleManager().getModules().stream().filter(Module::isToggled).forEach(m -> m.onEvent(e));
    }

    public boolean isPre() {
        return type != null && type == EventType.PRE;
    }

    public boolean isPost() {
        return type != null && type == EventType.POST;
    }

    public EventType getType() {
        return type;
    }




    public void setType(EventType type) {
        this.type = type;
    }

    public boolean isCanceled(){
        return canceled;
    }

    public void setCancelled(boolean cancelled) {
        this.canceled = cancelled;
    }
}
