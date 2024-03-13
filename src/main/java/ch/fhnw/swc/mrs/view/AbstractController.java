package ch.fhnw.swc.mrs.view;

import ch.fhnw.swc.mrs.api.MRSServices;

public abstract class AbstractController {

    private TabFrame tabs;
    private MRSServices backend;

    protected void setParent(TabFrame tabs) {
        this.tabs = tabs;
    }

    /**
     * @param aBackend Set the back-end component of this application.
     */
    public void setBackend(MRSServices aBackend) {
        if (aBackend == null) {
            throw new IllegalArgumentException("backend must not be null.");
        }
        backend = aBackend;
    }

    /**
     * @return the main ui-frame of this application.
     */
    protected TabFrame getParent() {
        return tabs;
    }

    /**
     * @return the back-end component of this application.
     */
    protected MRSServices getBackend() {
        return backend;
    }

    /**
     * Reload this controllers view with new data from the back-end.
     */
    public abstract void reload();

}
