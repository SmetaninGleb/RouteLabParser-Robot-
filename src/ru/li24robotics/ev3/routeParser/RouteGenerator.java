package ru.li24robotics.ev3.routeParser;


import ru.li24robotics.ev3.robolab.cubeFinder.Route;
import ru.li24robotics.ev3.robolab.cubeFinder.RouteIteration;
import ru.li24robotics.ev3.robolab.lab.LabItem;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class RouteGenerator
{
    private ArrayList<ArrayList<LabItem>> mainLab;
    private FileOutputStream fileOutputStream;
    private ObjectOutputStream objectOutputStream;
    private int cubeCountTake;
    private int[] endItemCors;
    private ArrayList<int[]> cubeCors;
    private ArrayList<ArrayList<int[]>> cubeCollection;


    public RouteGenerator(ArrayList<ArrayList<LabItem>> mainLab, int [] endItemCors, int cubeCountTake)
    {
        this.mainLab = mainLab;
        this.endItemCors = endItemCors;
        this.cubeCountTake = cubeCountTake;
        this.cubeCors = new ArrayList<int[]>();
        this.cubeCollection = new ArrayList<ArrayList<int[]>>();
    }

    public void generate()
    {
        getCubesCors();
        getCubeCollection(1, new ArrayList<int[]>());
        parseAllRoute();
    }

    private void parseAllRoute()
    {
        for(int i = 0; i < mainLab.size(); i++)
        {
            for(int j = 0; j < mainLab.get(i).size(); j++)
            {
                Route now = getOptimalRouteForItem(new int[] {i, j}, endItemCors);
                parseRouteToFile(now, "[" + i + "][" + j + "].route");
            }
        }
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

    private Route getOptimalRouteForItem(int[] corItem, int[] corEndRoute)
    {
        Route _nowAnsRoute = null;
        for(int i = 0; i < cubeCollection.size(); i++)
        {
            Route _nowIterationRoute;
            _nowIterationRoute = getRouteFromTwoPoints(corItem, cubeCollection.get(i).get(0));
            if(_nowIterationRoute == null)
            {
                continue;
            }
            for(int j = 0; j < cubeCollection.get(i).size() - 1; j++)
            {
                ArrayList<RouteIteration> _nowRouteIterationList = _nowIterationRoute.getRouteList();
                _nowRouteIterationList.addAll(getRouteFromTwoPoints(cubeCollection.get(i).get(j),
                        cubeCollection.get(i).get(j + 1)).getRouteList());
                _nowIterationRoute = new Route(_nowRouteIterationList);
            }
            ArrayList<RouteIteration> _nowRouteIterationList = _nowIterationRoute.getRouteList();
            _nowRouteIterationList.addAll(getRouteFromTwoPoints(cubeCollection.get(i).get(cubeCollection.get(i).size() - 1),
                    corEndRoute).getRouteList());
            _nowIterationRoute = new Route(_nowRouteIterationList);
            if(i == 0)
            {
                _nowAnsRoute = _nowIterationRoute;
                continue;
            }
            if(_nowAnsRoute.getRouteSize() > _nowIterationRoute.getRouteSize())
            {
                _nowAnsRoute = _nowIterationRoute;
            }
        }
        return _nowAnsRoute;
    }

    private void getCubeCollection(int step, ArrayList<int[]> usedCubes)
    {
        if(step - 1 == cubeCountTake)
        {
            cubeCollection.add(( ArrayList<int[]>)usedCubes.clone());
            return;
        }
        for(int i = 0; i < cubeCors.size(); i++)
        {
            boolean _isAdd = true;
            for(int j = 0; j < usedCubes.size(); j++)
            {
                if(usedCubes.get(j).equals(cubeCors.get(i)))
                {
                    _isAdd = false;
                }
            }
            if(_isAdd)
            {
                usedCubes.add(cubeCors.get(i));
                getCubeCollection(step + 1, usedCubes);
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
        Queue<int []> _bfsQueue = new LinkedList<int[]>();
        _bfsQueue.add(startCors);
        _used.get(startCors[0]).set(startCors[1], 1);
        while (!_bfsQueue.isEmpty())
        {
            if(Arrays.equals(_bfsQueue.element(), endCors))
            {
                break;
            }
            int [] _now = _bfsQueue.poll();
            if(_now[0] > 0 && !mainLab.get(_now[0]).get(_now[1]).toLeft.isWallIsHere()
                    && _used.get(_now[0] - 1).get(_now[1]) != 1)
            {
                _bfsQueue.add(new int[] {_now[0] - 1, _now[1]});
                _used.get(_now[0] - 1).set(_now[1], 1);
                _parent.get(_now[0] - 1).set(_now[1], _now);
            }
            if(_now[0] < mainLab.size() - 1 && !mainLab.get(_now[0]).get(_now[1]).toRight.isWallIsHere()
                    && _used.get(_now[0] + 1).get(_now[1]) != 1)
            {
                _bfsQueue.add(new int[] {_now[0] + 1, _now[1]});
                _used.get(_now[0] + 1).set(_now[1], 1);
                _parent.get(_now[0] + 1).set(_now[1], _now);
            }
            if(_now[1] > 0 && !mainLab.get(_now[0]).get(_now[1]).toBack.isWallIsHere()
                    && _used.get(_now[0]).get(_now[1] - 1) != 1)
            {
                _bfsQueue.add(new int[] {_now[0], _now[1] - 1});
                _used.get(_now[0]).set(_now[1] - 1, 1);
                _parent.get(_now[0]).set(_now[1] - 1, _now);
            }
            if(_now[1] < mainLab.get(_now[0]).size() - 1 && !mainLab.get(_now[0]).get(_now[1]).toForward.isWallIsHere()
                    && _used.get(_now[0]).get(_now[1] + 1) != 1)
            {
                _bfsQueue.add(new int[] {_now[0], _now[1] + 1});
                _used.get(_now[0]).set(_now[1] + 1, 1);
                _parent.get(_now[0]).set(_now[1] + 1, _now);
            }
        }
        if(_bfsQueue.isEmpty() || (!_bfsQueue.isEmpty() && !Arrays.equals(_bfsQueue.element(), endCors)))
        {
            return null;
        }
        Route _nowRoute = new Route();

        int [] _nowScanParent = endCors.clone();
        String _previousType = "";
        while(!Arrays.equals(_nowScanParent, startCors))
        {
            if(_parent.get(_nowScanParent[0]).get(_nowScanParent[1])[0] == _nowScanParent[0] - 1)
            {
                if(_previousType == "ToRight")
                {
                    _nowRoute.getRouteList().get(0).setValue
                            (_nowRoute.getRouteList().get(0).getValue() + 1);
                }
                else {
                    RouteIteration _nowIteration = new RouteIteration("ToRight", 1);
                    _nowRoute.addRouteIterationToStart(_nowIteration);
                }
                _nowScanParent[0] -= 1;
                _previousType = "ToRight";
                continue;
            }
            if(_parent.get(_nowScanParent[0]).get(_nowScanParent[1])[0] == _nowScanParent[0] + 1)
            {
                if(_previousType == "ToLeft")
                {
                    _nowRoute.getRouteList().get(0).setValue
                            (_nowRoute.getRouteList().get(0).getValue() + 1);
                }
                else {
                    RouteIteration _nowIteration = new RouteIteration("ToLeft", 1);
                    _nowRoute.addRouteIterationToStart(_nowIteration);
                }
                _nowScanParent[0] += 1;
                _previousType = "ToLeft";
                continue;
            }
            if(_parent.get(_nowScanParent[0]).get(_nowScanParent[1])[1] == _nowScanParent[1] - 1)
            {
                if(_previousType == "Forward")
                {
                    _nowRoute.getRouteList().get(0).setValue
                            (_nowRoute.getRouteList().get(0).getValue() + 1);
                }
                else {
                    RouteIteration _nowIteration = new RouteIteration("Forward", 1);
                    _nowRoute.addRouteIterationToStart(_nowIteration);
                }
                _nowScanParent[1] -= 1;
                _previousType = "Forward";
                continue;
            }
            if(_parent.get(_nowScanParent[0]).get(_nowScanParent[1])[1] == _nowScanParent[1] + 1)
            {
                if(_previousType == "Backward")
                {
                    _nowRoute.getRouteList().get(0).setValue
                            (_nowRoute.getRouteList().get(0).getValue() + 1);
                }
                else {
                    RouteIteration _nowIteration = new RouteIteration("Backward", 1);
                    _nowRoute.addRouteIterationToStart(_nowIteration);
                }
                _nowScanParent[1] += 1;
                _previousType = "Backward";
                continue;
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
