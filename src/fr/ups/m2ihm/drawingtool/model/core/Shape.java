package fr.ups.m2ihm.drawingtool.model.core;

import java.awt.Point;

public interface Shape {
    
    Point getUpperLeftCorner();
    Point getLowerRightCorner();
    void translate(int dx, int dy);
    Shape clone();
}
