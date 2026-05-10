package model;

public class Resource {

    private String name;
    private Process holder;

    public Resource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Process getHolder() {
        return holder;
    }

    public void setHolder(Process holder) {
        this.holder = holder;
    }

    public boolean isLocked() {
        return holder != null;
    }

    public void release(int originalPriority) {
        holder = null;
    }
}