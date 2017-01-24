package fr.ups.m2ihm.drawingtool.model;

public class PaletteEvent {
     private final PaletteEventType eventType;
     private int index;

    public PaletteEvent(PaletteEventType eventType) {
        this.eventType = eventType;
        this.index = -1;
    }
    
    public PaletteEvent(PaletteEventType eventType, int index) {
        this.eventType = eventType;
        this.index = index;
    }


    public PaletteEventType getEventType() {
        return eventType;
    }
    
    public int getMacroIndex() throws IndexOutOfBoundsException {
        if (index >= 0)
            return index;
        else throw new IndexOutOfBoundsException("No macro");
    }
    

   
}
