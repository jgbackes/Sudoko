package com.backesfamily.soduku;

/**
 * User: jbackes
 * Date: Dec 20, 2006
 * Time: 5:46:06 PM
 * Copyright 2007, Jeffrey G. Backes. All rights reserved.
 */

public interface Solver {
  public void solver(int[][] newValues);
  public void reset(Puzzle puzzle);
  public int getVisits(int row, int col);
  public String getSolverName();
  public void showProgress(final int row, final int col, final int value);
  public void load(int[][] values);
  public boolean isEmpty(int row, int col, int value);
  public boolean tryOne(final int inRow, final int inCol);
}
