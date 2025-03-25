package ru.maxthetomas.votvevents.event;

public class Event {
    EventResource resource;

    private boolean _isRunning = false;

    public boolean IsRunning() {
        return _isRunning;
    }

    public void Run(EventContext context) {
        _isRunning = true;

    }
}
