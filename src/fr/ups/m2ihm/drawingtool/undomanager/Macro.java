/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.undomanager;

import fr.ups.m2ihm.drawingtool.model.core.Shape;
import java.awt.Point;
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
        this.macro = macro;
        name = null;
        this.p0 = p0; 
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
}
