package ru.li24robotics.ev3.routeParser;


import java.util.Scanner;

public class GenerateControl
{
    public static void main(String[] args) {
        LabFileParser labFileParser = new LabFileParser();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input count of taken cubes:");
        int cubeCount;
        cubeCount = scanner.nextInt();
        System.out.println();
        System.out.println("Input coordinates of end item(X, Y):");
        int corEndX = scanner.nextInt();
        int corEndY = scanner.nextInt();
        int[] corsEnd = {corEndX, corEndY};
        RouteGenerator routeGenerator = new RouteGenerator(labFileParser.getMainLab(), corsEnd, cubeCount);
        routeGenerator.generate();
    }
}
