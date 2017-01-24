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

    public Macro(List<Command> macro, Point p0) {
        this.macro = new ArrayList<>();
        populateMacro(macro);
        name = null;
        this.p0 = p0; 
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
        
    }
    public void execute(Point source) {
//        macro.forEach(command -> command.execute());
        for (Command command : macro){
            System.out.println("MACRO: Command: " + command.getShape().getClass().getSimpleName() + "\nULC:" + command.getShape().getUpperLeftCorner() + "\nLRC: " + command.getShape().getLowerRightCorner());
            command.getShape().translate(source.x - p0.x, source.y - p0.y);
            command.execute();
        }
    }

    public void undo() {
        for (int i=macro.size(); i>0; i--){
            macro.get(i).undo();
        }
    }    

    @Override
    public Shape getShape() {
        return null;
    }

    @Override
    public Macro clone() {
        return new Macro(macro, p0);
    }
}
