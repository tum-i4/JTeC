package edu.tum.sse.jtec.instrumentation.systemevent;

public final class SystemInstrumentationEvent {

    private long timestamp;
    private String pid;
    private Action action;
    private Target target;
    private String value;

    public SystemInstrumentationEvent(final long timestamp, final String pid, final Action action, final Target target, final String value) {
        this.timestamp = timestamp;
        this.pid = pid;
        this.action = action;
        this.target = target;
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public enum Action {
        OPEN("OPEN"), CONNECT("CONNECT"), START("START"), SPAWN("SPAWN");

        private final String action;

        Action(final String action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }
    }

    public enum Target {
        FILE("FILE"), RESOURCE("RESOURCE"), SOCKET("SOCKET"), THREAD("THREAD"), PROCESS("PROCESS");

        private final String target;

        Target(final String target) {
            this.target = target;
        }

        @Override
        public String toString() {
            return target;
        }
    }
}
