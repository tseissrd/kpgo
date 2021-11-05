package com.springapp.kpgo.go;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
  private TablePoint koRulePoint;
  
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
    koRulePoint = null;
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
    return getBorderingPoints(point, new HashSet<>());
  }
  
  private Set<TablePoint> getBorderingPoints(TablePoint point, Set<TablePoint> exclude) {
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
      TablePoint otherPoint = table.getPoint(point.x + xDirection, point.y);
      if (!exclude.contains(otherPoint))
        borderingPoints.add(otherPoint);
    }
    
    for (Integer yDirection: yDirections) {
      TablePoint otherPoint = table.getPoint(point.x, point.y + yDirection);
      if (!exclude.contains(otherPoint))
        borderingPoints.add(otherPoint);
    }
    
    return borderingPoints;
  }
  
  public Set<TablePoint> getPointGroupFor(TablePoint point) {
    return getPointGroupFor(point, new HashSet<>());
  }
  
  private Set<TablePoint> getPointGroupFor(TablePoint point, Set<TablePoint> exclude) {
    Set<TablePoint> pointGroup = new HashSet<>();
    pointGroup.add(point);
    for (TablePoint otherPoint: getBorderingPoints(point)) {
      if (!exclude.contains(otherPoint)) {
        if ((otherPoint.getStone() != null) && (otherPoint.getStone().colour.equals(point.getStone().colour))) {
          Set<TablePoint> chainedExclude = new HashSet<>(exclude);
          chainedExclude.add(point);
          pointGroup.addAll(getPointGroupFor(otherPoint, chainedExclude));
        }
      }
    }
        
    return pointGroup;
  }
  
  public int getBreathsForPointGroup(Set<TablePoint> pointGroup) {
    
    int breaths = 0;
    
    for (TablePoint point: pointGroup) {
      final Set<TablePoint> borderingPoints = getBorderingPoints(point);
      int pointBreaths = borderingPoints.size();
      
      for (TablePoint otherPoint: borderingPoints) {
        if (otherPoint.getStone() != null)
          pointBreaths += -1;
      }

      if (pointBreaths > breaths)
        breaths = pointBreaths;
    }
    
    return breaths;
  }
  
  public boolean checkMove(Colour colour, TablePoint point) {
    if ((koRulePoint != null) && (point.equals(koRulePoint)))
      return false;
      
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
        if (otherPoint.getStone().colour.equals(colour)) {
          int groupBreaths = getBreathsForPointGroup(getPointGroupFor(otherPoint));
          if (groupBreaths > maxGroupBreaths)
            maxGroupBreaths = groupBreaths;
        }
      }
    }
    
    if ((personalBreaths > 0) || (maxGroupBreaths - 1 > 0))
      return true;
    else {
      return mockCheckMove(colour, point); // проверка на случаи, когда после хода вражеский камень будет захвачен и появятся новые дыхания
    }
  }
  
  public boolean mockCheckMove(Colour colour, TablePoint point) {
    final Game nextState = copy();
    final TablePoint pointInQuestion = nextState.getTable().getPoint(point.x, point.y);
    try {
      pointInQuestion.putStone(new Stone(colour));
    } catch (TablePoint.PointOccupiedException ex) {
      throw new Error(ex);
    }
    
    nextState.processTable();
    int nextStateBreaths = nextState.getBreathsForPointGroup(nextState.getPointGroupFor(pointInQuestion));
    if (nextStateBreaths == 0)
      return false;
    return true;
  }
  
  public void processTable() {
    Set<TablePoint> pointsToCheck = new HashSet<>(table.getPoints()
      .parallelStream()
      .filter((TablePoint point) -> {
        return (point.getStone() != null) && (!point.getStone().colour.equals(actsNext.getBowl().colour));
      }).toList());
    
    Set<TablePoint> pointsToClear = new HashSet<>();
    
    while (pointsToCheck.size() > 0) {
      TablePoint point = (TablePoint)pointsToCheck.toArray()[0];
      Set<TablePoint> pointGroup = getPointGroupFor(point);
      pointsToCheck.removeAll(pointGroup);
      if (getBreathsForPointGroup(pointGroup) == 0)
        pointsToClear.addAll(pointGroup);
    }
    
    // Ko rule protection
    if (pointsToClear.size() == 1)
      koRulePoint = (TablePoint)pointsToClear.toArray()[0];
    else
      koRulePoint = null;
    
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
  
  private Game copy() {
    Game gameCopy = null;
    try {
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(byteOut);
      out.writeObject(this);
      byte[] buf = byteOut.toByteArray();
      out.close();
      
      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf));
      gameCopy = (Game)in.readObject();
      in.close();
    } catch (Exception ex) {
      throw new Error(ex);
    }
    return gameCopy;
  }
  
}
