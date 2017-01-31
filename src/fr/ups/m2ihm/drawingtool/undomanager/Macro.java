/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.undomanager;

import fr.ups.m2ihm.drawingtool.model.core.Shape;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Léopold
 */
public class Macro implements Command {
    private List<Command> macro;
    private String name;
    private Point p0;
    private Point source;
    private UndoManager manager;

    public Macro(List<Command> macro, Point p0, UndoManager manager) {
        this.macro = new ArrayList<>();
        populateMacro(macro);
        this.manager = manager;
        name = null;
        this.p0 = p0; 
        this.source = null;
    }
    
    public Macro(List<Command> macro, Point p0, UndoManager manager, String name, Point source) {
        this(macro, p0, manager);
        this.name = name;
        this.source = source;
    }
    
    private void populateMacro(List<Command> entries){
        for (Command entry : entries){
            macro.add(entry.clone());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    

    public void execute(){
        for (Command command : macro){                                   
            command.execute();
        }
    }

    public void setSource(Point source) {
        this.source = source;
        for (Command command : macro) {
            if (!(command instanceof Macro))
                command.getShape().translate(source.x - p0.x, source.y - p0.y);
        }
    }

    public void undo() {
        for (Command command : macro){
            command.undo();
        }
    }    

    @Override
    public Shape getShape() {
        return null;
    }

    @Override
    public Macro clone() {
        if (this.source != null)
            return new Macro(macro,(Point) p0.clone(), manager, this.name, (Point)this.source.clone());
        else {
            Macro m = new Macro(macro, (Point)p0.clone(), manager);
            m.setName(this.name);
            return m;
        }       
    }
}
