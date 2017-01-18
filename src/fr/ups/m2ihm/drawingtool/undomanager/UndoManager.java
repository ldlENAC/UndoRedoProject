/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package fr.ups.m2ihm.drawingtool.undomanager;

import fr.ups.m2ihm.drawingtool.model.core.Rectangle;
import fr.ups.m2ihm.drawingtool.model.core.Shape;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author David
 */
public class UndoManager {

    public final static String REGISTER_AVAILABLE_PROPERTY = "registerAvailable";
    public final static String UNDO_COMMANDS_PROPERTY = "undo";
    public final static String REDO_COMMANDS_PROPERTY = "redo";
    public final static String MACRO_PROPERTY = "macro";
    private final PropertyChangeSupport support;
    private final Map<String, Boolean> eventAvailability;
    private final Stack<Command> undoableCommands;
    private final Stack<Command> redoableCommands;
    private final List<Macro> macros;

    public Boolean isUndoEnabled() {
        return PossibleState.UNDO_ONLY.equals(currentState) || PossibleState.UNDO_REDOABLE.equals(currentState);
    }

    public Boolean isRedoEnabled() {
        return PossibleState.REDO_ONLY.equals(currentState) || PossibleState.UNDO_REDOABLE.equals(currentState);
    }

    public void undoToCommand(int index) {
        int numberOfUndo = undoableCommands.search(undoableCommands.get(index)); // TODO simpler formula
        for (int i=0; i<numberOfUndo; i++){
            undo();
        }
    }    

    
    private enum PossibleState {

        IDLE, UNDO_ONLY, REDO_ONLY, UNDO_REDOABLE
    }
    private PossibleState currentState;

    private void gotoState(PossibleState state) {
        currentState = state;
        switch (currentState) {
            case IDLE:
                enableEvents(true, false, false);
                break;
            case UNDO_ONLY:
                enableEvents(true, true, false);
                break;
            case REDO_ONLY:
                enableEvents(true, false, true);
                break;
            case UNDO_REDOABLE:
                enableEvents(true, true, true);
                break;
        }
    }

    public void init() {
        gotoState(PossibleState.IDLE);
        firePropertyChange(UNDO_COMMANDS_PROPERTY, null, Collections.unmodifiableList(undoableCommands));
        firePropertyChange(REDO_COMMANDS_PROPERTY, null, Collections.unmodifiableList(redoableCommands));
        firePropertyChange(MACRO_PROPERTY, null, Collections.unmodifiableList(macros));
    }

    public UndoManager() {
        undoableCommands = new Stack<>();
        redoableCommands = new Stack<>();
        macros = new ArrayList<>();
        support = new PropertyChangeSupport(this);
        eventAvailability = new HashMap<>();
        eventAvailability.put(REGISTER_AVAILABLE_PROPERTY, null);
        eventAvailability.put(UndoEvent.UNDO.getPropertyName(), null);
        eventAvailability.put(UndoEvent.REDO.getPropertyName(), null);
        eventAvailability.put(UNDO_COMMANDS_PROPERTY, null);
        eventAvailability.put(REDO_COMMANDS_PROPERTY, null);
        eventAvailability.put(MACRO_PROPERTY, true);
    }

    public void registerCommand(Command command) {
        switch (currentState) {
            case IDLE:
                gotoState(PossibleState.UNDO_ONLY);
                command.execute();
                undoableCommands.push(command);
                redoableCommands.clear();
                firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                break;
            case UNDO_ONLY:
                gotoState(PossibleState.UNDO_ONLY);
                command.execute();
                undoableCommands.push(command);
                redoableCommands.clear();
                firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                break;
            case REDO_ONLY:
                gotoState(PossibleState.UNDO_ONLY);
                command.execute();
                undoableCommands.push(command);
                redoableCommands.clear();
                firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                break;
            case UNDO_REDOABLE:
                gotoState(PossibleState.UNDO_ONLY);
                command.execute();
                undoableCommands.push(command);
                redoableCommands.clear();
                firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                break;
        }
    }

    public void undo() {
        Command undoneCommand;
        switch (currentState) {
            case IDLE:
                break;
            case UNDO_ONLY:
                if (undoableCommands.size() == 1) {
                    gotoState(PossibleState.REDO_ONLY);
                    undoneCommand = undoableCommands.pop();
                    undoneCommand.undo();
                    redoableCommands.push(undoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (undoableCommands.size() > 1) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    undoneCommand = undoableCommands.pop();
                    undoneCommand.undo();
                    redoableCommands.push(undoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                }
                break;
            case REDO_ONLY:
                break;
            case UNDO_REDOABLE:
                if (undoableCommands.size() == 1) {
                    gotoState(PossibleState.REDO_ONLY);
                    undoneCommand = undoableCommands.pop();
                    undoneCommand.undo();
                    redoableCommands.push(undoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (undoableCommands.size() > 1) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    undoneCommand = undoableCommands.pop();
                    undoneCommand.undo();
                    redoableCommands.push(undoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                }
                break;
        }
    }

    public void redo() {
        Command redoneCommand;
        switch (currentState) {
            case IDLE:
                break;
            case UNDO_ONLY:
                break;
            case REDO_ONLY:
                if (redoableCommands.size() == 1) {
                    gotoState(PossibleState.UNDO_ONLY);
                    redoneCommand = redoableCommands.pop();
                    redoneCommand.execute();
                    undoableCommands.push(redoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (redoableCommands.size() > 1) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    redoneCommand = redoableCommands.pop();
                    redoneCommand.execute();
                    undoableCommands.push(redoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                }
                break;
            case UNDO_REDOABLE:
                if (redoableCommands.size() == 1) {
                    gotoState(PossibleState.UNDO_ONLY);
                    redoneCommand = redoableCommands.pop();
                    redoneCommand.execute();
                    undoableCommands.push(redoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (redoableCommands.size() > 1) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    redoneCommand = redoableCommands.pop();
                    redoneCommand.execute();
                    undoableCommands.push(redoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                }
                break;
        }
    }

    private void enableEvents(
            boolean registerEnabled,
            boolean undoEnabled,
            boolean redoEnabled) {
        fireEventAvailabilityChanged(REGISTER_AVAILABLE_PROPERTY, registerEnabled);
        fireEventAvailabilityChanged(UndoEvent.UNDO.getPropertyName(), undoEnabled);
        fireEventAvailabilityChanged(UndoEvent.REDO.getPropertyName(), redoEnabled);
    }

    private void fireEventAvailabilityChanged(String propertyName, boolean newAvailability) {
        Boolean oldAvailability = eventAvailability.get(propertyName);
        eventAvailability.put(propertyName, newAvailability);
        firePropertyChange(propertyName, oldAvailability, newAvailability);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        support.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        support.removePropertyChangeListener(propertyName, listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

    private boolean isInSelection(Shape shape, Rectangle rectangle) {
        boolean result = false;
        double x1 = shape.getUpperLeftCorner().x;
        double x2 = shape.getLowerRightCorner().x;
        double X1 = rectangle.getUpperLeftCorner().x;
        double X2 = rectangle.getLowerRightCorner().x;

        double y1 = shape.getUpperLeftCorner().y;
        double y2 = shape.getLowerRightCorner().y;
        double Y1 = rectangle.getUpperLeftCorner().y;
        double Y2 = rectangle.getLowerRightCorner().y;

        if (X1 <= x1 && x1 <= X2) {
            if (X1 <= x2 && x2 <= X2) {
                if (Y1 <= y1 && y1 <= Y2) {
                    if (Y1 <= y2 && y2 <= Y2) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    public void undoRegionalProcess(Rectangle selection) {
        for (int i=undoableCommands.size()-1; i>=0; i--) {
            if (isInSelection(undoableCommands.get(i).getShape(), selection)){
                undoableCommands.get(i).undo();
                undoableCommands.remove(i);
                return;
            }
        }
    }
    
    public void recordMacro(Rectangle selection) {
        List<Command> commandsMacro = new ArrayList<>();
        
        for (int i=0; i<undoableCommands.size(); i++) {
            Command command = undoableCommands.get(i);
            if (isInSelection(command.getShape(), selection)){
                commandsMacro.add(command);
            }
        }
        
        //ArrayList<Macro> oldMacros = new ArrayList<>(macros);
        macros.add(new Macro(commandsMacro));
        firePropertyChange(MACRO_PROPERTY, null, macros);        
    }
    
    public void executeMacro(int index) {
        macros.get(index).execute();
    }    
}
