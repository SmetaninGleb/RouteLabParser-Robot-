package ru.li24robotics.ev3;


import ru.li24robotics.ev3.robolab.cubeFinder.Route;
import ru.li24robotics.ev3.robolab.cubeFinder.RouteIteration;
import ru.li24robotics.ev3.robolab.lab.LabItem;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class RouteGenerator
{
    private ArrayList<ArrayList<LabItem>> mainLab;
    private FileOutputStream fileOutputStream;
    private ObjectOutputStream objectOutputStream;
    private int cubeCountTake;
    private int[] endItemCors;
    private ArrayList<int[]> cubeCors;

    public RouteGenerator(ArrayList<ArrayList<LabItem>> mainLab, int [] endItemCors, int cubeCountTake)
    {
        this.mainLab = mainLab;
        this.endItemCors = endItemCors;
        this.cubeCountTake = cubeCountTake;
    }

    private void parseRouteToFile(Route route, String nameRouteFile)
    {
        try {
            fileOutputStream = new FileOutputStream(nameRouteFile);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(route);
        } catch(Exception e) {
            System.err.println("Output Exception!!! No File " + nameRouteFile + "!!!");
            fileOutputStream = null;
            objectOutputStream = null;
        }
    }

    ArrayList<ArrayList<int[]>> cubeCollection;
    private void getCubeCollections(int step, ArrayList<int[]> usedCubes)
    {
        if(step - 1 == cubeCountTake)
        {
            cubeCollection.add(usedCubes);
            return;
        }
        for(int i = 0; i < cubeCors.size(); i++)
        {
            boolean isAdd = true;
            for(int j = 0; j < usedCubes.size(); j++)
            {
                if(usedCubes.get(j).equals(cubeCors.get(i)))
                {
                    isAdd = false;
                }
            }
            if(isAdd)
            {
                usedCubes.add(cubeCors.get(i));
                getCubeCollections(step + 1, usedCubes);
                usedCubes.remove(usedCubes.size() - 1);
            }
        }
    }

    private Route getRouteFromTwoPoints(int[] startCors, int[] endCors)
    {
        ArrayList<ArrayList<Integer>> _used = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<int[]>> _parent = new ArrayList<ArrayList<int[]>>();
        for(int i = 0; i < mainLab.size(); i++)
        {
            _used.add(i, new ArrayList<Integer>());
            _parent.add(i, new ArrayList<int[]>());
            for (int j = 0; j < mainLab.get(i).size(); j++)
            {
                _used.get(i).add(j, 0);
                _parent.get(i).add(j, new int[] {0, 0});
            }
        }
        Queue<int []> _bfsQueue = new PriorityQueue<int[]>();
        _bfsQueue.add(startCors);
        while (!_bfsQueue.isEmpty())
        {
            if(_bfsQueue.element().equals(endCors))
            {
                break;
            }
            int [] _now = _bfsQueue.remove();
            if(_now[0] != 0 && !mainLab.get(_now[0]).get(_now[1]).toLeft.isWallIsHere()
                    && _used.get(_now[0] - 1).get(_now[1]) != 1)
            {
                _bfsQueue.add(new int[] {_now[0] - 1, _now[1]});
                _used.get(_now[0] - 1).set(_now[1], 1);
                _parent.get(_now[0] - 1).set(_now[1], _now);
            }
            if(_now[0] != mainLab.size() - 1 && !mainLab.get(_now[0]).get(_now[1]).toRight.isWallIsHere()
                    && _used.get(_now[0] + 1).get(_now[1]) != 1)
            {
                _bfsQueue.add(new int[] {_now[0] + 1, _now[1]});
                _used.get(_now[0] + 1).set(_now[1], 1);
                _parent.get(_now[0] + 1).set(_now[1], _now);
            }
            if(_now[1] != 0 && !mainLab.get(_now[0]).get(_now[1]).toBack.isWallIsHere()
                    && _used.get(_now[0]).get(_now[1] - 1) != 1)
            {
                _bfsQueue.add(new int[] {_now[0], _now[1] - 1});
                _used.get(_now[0]).set(_now[1] - 1, 1);
                _parent.get(_now[0]).set(_now[1] - 1, _now);
            }
            if(_now[1] != mainLab.get(_now[0]).size() - 1 && !mainLab.get(_now[0]).get(_now[1]).toForward.isWallIsHere()
                    && _used.get(_now[0]).get(_now[1] + 1) != 1)
            {
                _bfsQueue.add(new int[] {_now[0], _now[1] + 1});
                _used.get(_now[0]).set(_now[1] + 1, 1);
                _parent.get(_now[0]).set(_now[1] + 1, _now);
            }
        }
        if(_bfsQueue.isEmpty() || (!_bfsQueue.isEmpty() && !_bfsQueue.element().equals(endCors)))
        {
            return null;
        }
        Route _nowRoute = new Route();

        int [] _nowScanParent = endCors;
        while(!_nowScanParent.equals(startCors))
        {
            if(_parent.get(_nowScanParent[0]).get(_nowScanParent[1])[0] == _nowScanParent[0] - 1)
            {
                RouteIteration _nowIteration = new RouteIteration("ToRight", 1);
                _nowRoute.addRouteIterationToStart(_nowIteration);
            }
            if(_parent.get(_nowScanParent[0]).get(_nowScanParent[1])[0] == _nowScanParent[0] + 1)
            {
                RouteIteration _nowIteration = new RouteIteration("ToLeft", 1);
                _nowRoute.addRouteIterationToStart(_nowIteration);
            }
            if(_parent.get(_nowScanParent[0]).get(_nowScanParent[1])[1] == _nowScanParent[1] - 1)
            {
                RouteIteration _nowIteration = new RouteIteration("Forward", 1);
                _nowRoute.addRouteIterationToStart(_nowIteration);
            }
            if(_parent.get(_nowScanParent[0]).get(_nowScanParent[1])[1] == _nowScanParent[1] + 1)
            {
                RouteIteration _nowIteration = new RouteIteration("Backward", 1);
                _nowRoute.addRouteIterationToStart(_nowIteration);
            }
        }
        return _nowRoute;
    }

    private void getCubesCors()
    {
        for(int i = 0; i < mainLab.size(); i++)
        {
            for(int j = 0; j < mainLab.get(i).size(); j++)
            {
                if(mainLab.get(i).get(j).isCubeHere)
                {
                    cubeCors.add(new int[] {i, j});
                }
            }
        }
    }

}
