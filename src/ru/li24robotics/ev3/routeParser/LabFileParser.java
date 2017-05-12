package ru.li24robotics.ev3.routeParser;


import ru.li24robotics.ev3.robolab.lab.LabItem;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class LabFileParser {
    private FileInputStream fileInputStream;
    private ObjectInputStream objectInputStream;

    public LabFileParser()
    {
        try {
            fileInputStream = new FileInputStream("MainLab.out");
            objectInputStream = new ObjectInputStream(fileInputStream);
        } catch (Exception e) {
            System.err.print("No File!!!");
        }
    }

    public ArrayList<ArrayList<LabItem>> getMainLab()
    {
        ArrayList<ArrayList<LabItem>> mainLab;
        try
        {
            mainLab = (ArrayList<ArrayList<LabItem>>) objectInputStream.readObject();
        }
        catch (Exception e) {
            System.err.println("May Not Convert Lab!!!");
            mainLab = null;
        }
        return mainLab;
    }
}

