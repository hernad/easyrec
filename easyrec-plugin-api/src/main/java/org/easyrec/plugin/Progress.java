package org.easyrec.plugin;

public class Progress {
    public static final Progress NOT_RUNNING = new Progress(0, 0, "Not running");

    private int totalSteps = 1;
    private int currentSteps = 0;
    private String message;

    public Progress(int currentSteps, int totalSteps, String message) {
        this.totalSteps = totalSteps;
        this.currentSteps = currentSteps;
        this.message = message;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public int getCurrentSteps() {
        return currentSteps;
    }

    public String getMessage() {
        return message;
    }

}
