package fr.ups.m2ihm.drawingtool.model;


import java.beans.PropertyChangeListener;

public interface DrawingToolModel {
    void init();
    void handleEvent(DrawingEvent event);
    void handleEvent(PaletteEvent event);
    void undo();
    void redo();
    void addPropertyListener(PropertyChangeListener listener);
    void addPropertyListener(String propertyName, PropertyChangeListener listener);
    void removePropertyListener(PropertyChangeListener listener);
    void removePropertyListener(String propertyName, PropertyChangeListener listener);
    void undoToCommand(int index);
    void executeMacro(int index);
}
