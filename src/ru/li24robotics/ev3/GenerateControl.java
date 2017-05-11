package ru.li24robotics.ev3;


public class GenerateControl
{
    public static void main(String[] args) {
        LabFileParser labFileParser = new LabFileParser();
        RouteGenerator routeGenerator = new RouteGenerator(labFileParser.getMainLab(), );
    }
}
