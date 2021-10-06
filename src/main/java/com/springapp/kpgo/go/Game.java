/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.go;

/**
 *
 * @author Sovereign
 */
public class Game
  implements Runnable
{
  
  private final Player[] players;
  private final Table table;
  private boolean ended;
  private Player winner;
  
  public Game(Player player1, Player player2, int tableWidth, int tableHeight) {
    players = new Player[2];
    players[0] = player1;
    players[1] = player2;
    
    table = new Table(tableWidth, tableHeight);
    winner = null;
    ended = false;
  }

  @Override
  public void run() {
    while (true) {
      for (Player player: players) {
        if (player.passed) {
          ended = true;
          break;
        }
        
      }
      
      if (ended)
        break;
    }
  }
  
}
