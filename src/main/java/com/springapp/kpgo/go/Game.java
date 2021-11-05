/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.go;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sovereign
 */
public class Game
implements Serializable
{
  
  private final Player[] players;
  private final Table table;
  private boolean ended;
  private Player winner;
  private Player actsNext;
  
  protected Game() {
    players = null;
    table = null;
  }
  
  public Game(Player player1, Player player2, int tableWidth, int tableHeight) {
    players = new Player[2];
    players[0] = player1;
    players[1] = player2;
    
    table = new Table(tableWidth, tableHeight);
    int whiteBowlPlayerNumber = new Random(Calendar.getInstance().toInstant().getEpochSecond()).nextInt(2);
    players[whiteBowlPlayerNumber].assignBowl(new Bowl(Colour.WHITE));
    players[1 - whiteBowlPlayerNumber].assignBowl(new Bowl(Colour.BLACK));
    actsNext = players[whiteBowlPlayerNumber];
    winner = null;
    ended = false;
  }
  
  public Player[] getPlayers() {
    return Arrays.copyOf(players, 2);
  }
  
  public Table getTable() {
    return table;
  }
  
  public Player playerToAct() {
    return actsNext;
  }
  
  public Set<TablePoint> getBorderingPoints(TablePoint point) {
    Set<TablePoint> borderingPoints = new HashSet<>();
    List<Integer> xDirections = new LinkedList();
    List<Integer> yDirections = new LinkedList();
    
    if (point.x > 0)
      xDirections.add(-1);
    if (point.x < table.width - 1)
      xDirections.add(1);
    if (point.y > 0)
      yDirections.add(-1);
    if (point.y < table.height - 1)
      yDirections.add(1);
    
    for (Integer xDirection: xDirections) {
      for (Integer yDirection: yDirections) {
        TablePoint otherPoint = table.getPoint(point.x + xDirection, point.y + yDirection);
        borderingPoints.add(otherPoint);
      }
    }
    
    return borderingPoints;
  }
  
  public Set<TablePoint> getPointGroupFor(TablePoint point) {
    Set<TablePoint> pointGroup = new HashSet<>();
    pointGroup.add(point);
    for (TablePoint otherPoint: getBorderingPoints(point)) {
      if ((otherPoint.getStone() != null) && (otherPoint.getStone().colour.equals(point.getStone().colour)))
        pointGroup.addAll(getPointGroupFor(otherPoint));
    }
        
    return pointGroup;
  }
  
  public int getBreathsForPointGroup(Set<TablePoint> pointGroup) {
    int breaths = 0;
    
    for (TablePoint point: pointGroup) {
      final Set<TablePoint> borderingPoints = getBorderingPoints(point);
      int pointBreaths = borderingPoints.size();
      
      for (TablePoint otherPoint: borderingPoints) {
        if ((otherPoint.getStone() != null) && (!otherPoint.getStone().colour.equals(point.getStone().colour)))
          pointBreaths += -1;
      }

      if (pointBreaths > breaths)
        breaths = pointBreaths;
    }
    
    return breaths;
  }
  
  public boolean checkMove(Stone stone, TablePoint point) {
    // сделать проверку на случаи, когда после хода вражеский камень будет захвачен и появятся новые дыхания
    Set<TablePoint> borderingPoints = getBorderingPoints(point);
    int personalBreaths = borderingPoints.size();
    int maxGroupBreaths = 0;
    
    for (TablePoint otherPoint: borderingPoints) {
      if (otherPoint.getStone() != null) {
        personalBreaths += -1;
      }
    }
    
    for (TablePoint otherPoint: borderingPoints) {
      if (otherPoint.getStone() != null) {
        if (otherPoint.getStone().colour.equals(point.getStone().colour)) {
          int groupBreaths = getBreathsForPointGroup(getPointGroupFor(otherPoint));
          if (groupBreaths > maxGroupBreaths)
            maxGroupBreaths = groupBreaths;
        }
      }
    }
    
    if ((personalBreaths > 0) || (maxGroupBreaths - 1 > 0))
      return true;
    else
      return false;
  }
  
  public void processTable() {
    Set<TablePoint> pointsToCheck = new HashSet<>(table.getPoints()
      .parallelStream()
      .filter((TablePoint point) -> {
        return !point.getStone().colour.equals(actsNext.getBowl().colour);
      }).toList());
    
    Set<TablePoint> pointsToClear = new HashSet<>();
    
    for (TablePoint point: pointsToCheck) {
      Set<TablePoint> pointGroup = getPointGroupFor(point);
      pointsToCheck.removeAll(pointGroup);
      if (getBreathsForPointGroup(pointGroup) == 0)
        pointsToClear.addAll(pointGroup);
    }
    
    pointsToClear.parallelStream()
      .forEach(point -> {
        try {
          point.removeStone();
        } catch (TablePoint.PointVacantException ex) {
          throw new Error(ex);
        }
      });
  }
  
  public void nextMove() {
    processTable();
    if (actsNext == players[0])
      actsNext = players[1];
    else
      actsNext = players[0];
  }
  
}
