package com.backesfamily.soduku;

/**
 * User: jbackes
 * Date: Dec 20, 2006
 * Time: 5:46:06 PM
 * Copyright 2007, Jeffrey G. Backes. All rights reserved.
 */

public interface Solver {
  void solver(final int[][] newValues);

  void reset(final Puzzle puzzle);

  int getVisits(final int row, final int col);

  String getSolverName();

  void showProgress(final int row, final int col, final int value);

  void load(final int[][] values);

  boolean isEmpty(final int row, final int col, final int value);

  boolean tryOne(final int inRow, final int inCol);
}
