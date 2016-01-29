package com.rc2s.client.models;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class Led extends Sphere
{
    private final double SIZE_MODIFIER = 4.;
    
    //private final Sphere component;
    
    private double x, y, z;
    private double size;
    private boolean activated;
    private Color color;
    
    public Led(double x, double y, double z, double size, boolean activated, Color color)
    {
        super(size);
        
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        this.activated = activated;
        this.color = color;
        
        /*this.component = new Sphere(size);
        this.component.setTranslateX(x * size * SIZE_MODIFIER);
        this.component.setTranslateY(y * size * SIZE_MODIFIER);
        this.component.setTranslateZ(z * size * SIZE_MODIFIER);*/
        this.setTranslateX(x * size * SIZE_MODIFIER);
        this.setTranslateY(y * size * SIZE_MODIFIER);
        this.setTranslateZ(z * size * SIZE_MODIFIER);
        
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(Color.BLACK);
        this.setMaterial(material);
        /*this.component.setMaterial(material);*/
        
        //this.component.setOnMouseClicked((MouseEvent e) -> {
        this.setOnMouseClicked((MouseEvent e) -> {
            PhongMaterial newColor = new PhongMaterial();
            newColor.setSpecularColor(Color.BLACK);
            newColor.setDiffuseColor(this.activated ? Color.BLACK : this.color);
            
            //this.component.setMaterial(newColor);
            this.setMaterial(newColor);
            this.activated = !this.activated;
        });
    }

    /*public Sphere getComponent()
    {
        return component;
    }*/

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
        //this.component.setTranslateX(x);
        this.setTranslateX(x);
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
        //this.component.setTranslateY(y);
        this.setTranslateY(y);
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
        //this.component.setTranslateZ(z);
        this.setTranslateZ(z);
    }

    public double getSize()
    {
        return size;
    }

    public void setSize(double size)
    {
        this.size = size;
    }

    public boolean isActivated()
    {
        return activated;
    }

    public void setActivated(boolean activated)
    {
        this.activated = activated;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }
}
