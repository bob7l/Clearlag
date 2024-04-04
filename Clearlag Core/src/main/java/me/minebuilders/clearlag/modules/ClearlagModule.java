package me.minebuilders.clearlag.modules;

/**
 * Created by TCP on 2/3/2016.
 */
public abstract class ClearlagModule implements Module {

    protected boolean enabled = false;

    public void setEnabled() {
        this.enabled = true;
    }

    public void setDisabled() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
