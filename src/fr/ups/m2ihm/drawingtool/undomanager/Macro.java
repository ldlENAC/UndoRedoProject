/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.undomanager;

import fr.ups.m2ihm.drawingtool.model.core.Shape;
import java.util.List;

/**
 *
 * @author Léopold
 */
public class Macro {
    private List<Command> macro;

    public Macro(List<Command> macro) {
        this.macro = macro;
    }

    public void execute() {
        macro.forEach(command -> command.execute());
    }

    public void undo() {
        for (int i=macro.size(); i>0; i--){
            macro.get(i).undo();
        }
    }    
}
