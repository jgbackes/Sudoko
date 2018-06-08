package com.backesfamily.soduku;

/**
 * User: jbackes
 * Date: Dec 20, 2006
 * Time: 5:46:06 PM
 * Copyright 2007, Jeffrey G. Backes. All rights reserved.
 */

public class SolverBruteForce extends AbstractSolver implements Solver {

  public SolverBruteForce(SudokuFrame sudokuFrame) {
    _sudokuFrame = sudokuFrame;
  }

  @Override
  public String getSolverName() {
      return "BruteForce";
  }

  public boolean tryOne(final int inRow, final int inCol) {
    boolean result = false;

    RowCol nextBlank = nextZero(inRow, inCol);

    if (nextBlank != null) {
      final int xRow = nextBlank.row;
      final int xCol = nextBlank.col;

      for (int guess = 1; guess <= 9; guess++) {
        if (isEmpty(xRow, xCol, guess)) {
          _values[xRow][xCol] = guess;
          _visits[xRow][xCol]++;
          showProgress(xRow, xCol, guess);
          RowCol next = nextRowCol(xRow, xCol);

          if (tryOne(next.row, next.col)) {
            result = true;
            break;
          }
        }
      }
      // We did not find a number that worked, clear up our guess and cause backtracking
      if (!result) {
        _values[xRow][xCol] = 0;
        showProgress(xRow, xCol, 0);
      }
    } else {
      result = true; // all done, simply unwind the recursion
    }
    return result;
  }
}
