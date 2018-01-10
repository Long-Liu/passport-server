package org.infinity.passport.domain;

public enum HealthState {
    GOOD("健康","蓝色"), WARNING("警告","橙色"), ERROR("错误","红色");

    private String name;

    private String color;

    HealthState(String name,String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "HealthState{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
