package com.backesfamily.soduku;

/**
 * User: jbackes
 * Date: Dec 21, 2006
 * Time: 11:17:52 AM
 * Copyright 2007, Jeffrey G. Backes. All rights reserved.
 */

public class Puzzle {
  String name;
  String difficulty;
  int[][] values;

  public Puzzle(String name, String difficulty, int[][] values) {
    this.name = name;
    this.difficulty = difficulty;
    this.values = values;
  }

  public String getName() {
    return name == null ? "" : name;
  }

  public int[][] getValues() {
    return values;
  }
}
