/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.model;

import com.sun.xml.internal.ws.policy.sourcemodel.ModelNode;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.BEGIN_DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.CANCEL_DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.END_DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.values;
import static fr.ups.m2ihm.drawingtool.model.DrawingStateMachine.GHOST_PROPERTY;
import static fr.ups.m2ihm.drawingtool.model.DrawingStateMachine.SHAPES_PROPERTY;
import fr.ups.m2ihm.drawingtool.model.core.DrawingToolCore;
import fr.ups.m2ihm.drawingtool.model.core.Line;
import fr.ups.m2ihm.drawingtool.undomanager.Command;
import fr.ups.m2ihm.drawingtool.undomanager.UndoManager;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author Léopold
 */
public class ExecuteMacroStateMachine implements DrawingStateMachine{

    private final PropertyChangeSupport support;
    private Line ghost;
    private Point p0;
    private final Map<MacroEventType, Boolean> eventAvailability;
    private UndoManager undoManager;
    private int index;
    private DrawingToolModel model;
    
    public enum MacroEventType {
        EXECUTE_MACRO("executeMacroProperty");
        
        private String propertyName;
        private MacroEventType(String s) {
            this.propertyName = s;
        }
        
        public String getPropertyName() {
            return this.propertyName;                   
        }
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
    }
    
    private void beginDraw(Point point, DrawingToolCore core) {
        System.out.println("plop");
        switch (currentState) {
            case MACRO_EXECUTED:
                p0 = point;
                gotoState(PossibleState.WAIT_CLICK);
                break;
            case WAIT_CLICK:
                break;                   
        }
    }
//
//    private void cancelDraw(DrawingToolCore core) {
//        switch (currentState) {
//            case IDLE:
//                break;            
//    }
//
//    private void draw(Point point, DrawingToolCore core) {
//        Line oldGhost;
//        switch (currentState) {
//            case IDLE:
//                break;            
//        }
//    }

    private void endDraw(DrawingToolCore core) {
        switch (currentState) {
            case WAIT_CLICK:       
                //fireEventAvailabilityChanged(MacroEventType., true);
                undoManager.executeMacro(index, p0);
                firePropertyChange(SHAPES_PROPERTY, null, core.getShapes());
                model.init();
                gotoState(PossibleState.MACRO_EXECUTED);
                break;
            case MACRO_EXECUTED:                
                break;
            
        }
    }

    private void gotoState(PossibleState possibleState) {
        currentState = possibleState;
        enableEvents(currentState.enableButtons);
    }

    private void enableEvents(boolean enableButtons) {
        fireEventAvailabilityChanged(MacroEventType.EXECUTE_MACRO, enableButtons);        
    }

    private void fireEventAvailabilityChanged(MacroEventType macroEventType, boolean newAvailability) {
        Boolean oldAvailability = eventAvailability.get(macroEventType);
        eventAvailability.put(macroEventType, newAvailability);
        firePropertyChange(macroEventType.getPropertyName(), oldAvailability, newAvailability);
    }

    @Override
    public void addPropertyListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyListener(String propertyName, PropertyChangeListener listener) {
        support.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyListener(String propertyName, PropertyChangeListener listener) {
        support.removePropertyChangeListener(propertyName, listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

    private enum PossibleState {
        MACRO_EXECUTED(true), WAIT_CLICK(false);
        boolean enableButtons;

        private PossibleState(boolean enableButtons) {
            this.enableButtons = enableButtons;
        }
        
    }

    private PossibleState currentState;

    public ExecuteMacroStateMachine() {
        support = new PropertyChangeSupport(this);
        ghost = null;
        eventAvailability = new EnumMap<>(MacroEventType.class);
        for (MacroEventType eventType : MacroEventType.values()) {
            eventAvailability.put(eventType, null);
        }
    }
    
    public ExecuteMacroStateMachine(DrawingToolModel model) {
        this.model = model;
        support = new PropertyChangeSupport(this);
        ghost = null;
        eventAvailability = new EnumMap<>(MacroEventType.class);
        for (MacroEventType eventType : MacroEventType.values()) {
            eventAvailability.put(eventType, null);
        }
    }

    @Override
    public void handleEvent(DrawingEvent event, DrawingToolCore core) {
        switch (event.getEventType()) {
            case BEGIN_DRAW:
                beginDraw(event.getPoint(), core);
                break;
            case CANCEL_DRAW:
                //cancelDraw(core);
                break;
            case DRAW:
//                draw(event.getPoint(), core);
                break;
            case END_DRAW:
                endDraw(core);
//                 executeMacroCommand();
                break;
        }
    }

    @Override
    public void init(DrawingToolCore core) {
        System.out.println("Init");
        Line oldGhost = ghost;
        ghost = null;
        gotoState(PossibleState.MACRO_EXECUTED);
        firePropertyChange(GHOST_PROPERTY, oldGhost, null);
        firePropertyChange(SHAPES_PROPERTY, null, core.getShapes());
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    
}
