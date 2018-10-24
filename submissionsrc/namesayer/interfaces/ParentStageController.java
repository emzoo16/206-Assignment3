package namesayer.interfaces;

/**
 * This interface is used by controllers which open child stages, it allows the controller to update once the
 * child stage is closed.
 */
public interface ParentStageController {
    public void stageHasClosed();
}
