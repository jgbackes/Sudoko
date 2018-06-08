package com.backesfamily.soduku;

/**
 * User: jbackes
 * Date: Dec 20, 2006
 * Time: 5:46:06 PM
 * Copyright 2007, Jeffrey G. Backes. All rights reserved.
 */

public class SolverOptimized extends AbstractSolver {

  public SolverOptimized(SudokuFrame sudokuFrame) {
    _sudokuFrame = sudokuFrame;
  }

  // Row Column Value array, true if at [x][y] value z is legal
  private boolean[][][] available = new boolean[9][9][9];

  @Override
  public String getSolverName() {
    return "Optimized";
  }

  public void solver(int[][] newValues) {
    super.solver(newValues);
    computeRequiredChecks(newValues);
  }

  @Override
  public void reset(Puzzle puzzle) {
    super.reset(puzzle);
    computeRequiredChecks(_values);
  }

  public boolean tryOne(final int inRow, final int inCol) {
    boolean result = false;

    RowCol nextBlank = nextZero(inRow, inCol);

    if (null != nextBlank) {
      final int xrow = nextBlank.row;
      final int xcol = nextBlank.col;

      for (int guess = 0; guess < 9; guess++) {

        if (available[xrow][xcol][guess] && isEmpty(xrow, xcol, guess)) {
          _values[xrow][xcol] = guess;
          _visits[xrow][xcol]++;
          showProgress(xrow, xcol, guess);
          RowCol next = nextRowCol(xrow, xcol);

          if (tryOne(next.row, next.col)) {
            result = true;
            break;
          }
        }
      }
      // We did not find a number that worked, clear up our guess and cause backtracking
      if (!result) {
        _values[xrow][xcol] = 0;
        showProgress(xrow, xcol, 0);
      }
    } else {
      result = true; // all done, simply unwind the recursion
    }
    return result;
  }

  private void computeRequiredChecks(int[][] values) {
    for (int r = 0; r < 9; r++) {
      for (int c = 0; c < 9; c++) {
        if (values[r][c] == 0) {
          for (int v = 0; v < 9; v++) {
            available[r][c][v] = isEmpty(r, c, v);
          }
        }
      }
    }
  }
}
