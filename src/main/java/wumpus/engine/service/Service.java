package wumpus.engine.service;

/**
 * An engine operation which performs tasks at a regular interval ("tick").
 */
public interface Service {

    /**
     * Contains the task to be executed at the regular interval.
     */
    void tick();

}
