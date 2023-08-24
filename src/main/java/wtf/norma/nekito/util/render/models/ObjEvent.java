package wtf.norma.nekito.util.render.models;

public class ObjEvent {

    // author: saph
// thanks for it


    public ObjModel model;
    public EventType type;
    public Object[] data = new Object[0];

    public ObjEvent(ObjModel caller, EventType type) {
        this.model = caller;
        this.type = type;
    }

    public boolean canBeCancelled() {
        return this.type.isCancelable();
    }

    public ObjEvent setData(Object... data) {
        this.data = data;
        return this;
    }

    public enum EventType {
        PRE_RENDER_ALL(true),
        PRE_RENDER_GROUPS(true),
        PRE_RENDER_GROUP(true),
        POST_RENDER_ALL(false),
        POST_RENDER_GROUPS(false),
        POST_RENDER_GROUP(false);

        private final boolean cancel;

        EventType(boolean cancelable) {
            this.cancel = cancelable;
        }

        public boolean isCancelable() {
            return this.cancel;
        }
    }
}
