package com.backesfamily.soduku;

import javax.swing.*;

/**
 * User: jbackes
 * Date: Nov 20, 2008
 * Time: 7:24:35 PM
 * Copyright 2007, Jeffrey G. Backes. All rights reserved.
 */

public abstract class AbstractSolver implements Solver {
  protected int[][] _values = new int[9][9];
  protected int[][] _visits = new int[9][9];

  protected SudokuFrame _sudokuFrame;

  abstract public String getSolverName();

  public void showProgress(final int row, final int col, final int value) {
    try {
      if (_sudokuFrame.shouldShowProgress()) {
        SwingUtilities.invokeAndWait(new Runnable() {
          public void run() {
            _sudokuFrame.setUserInputRowColumn(row, col, value);
          }
        });
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void load(int[][] values) {
    for (int i = 0; i < 9; i++) {
      System.arraycopy(values[i], 0, this._values[i], 0, 9);
    }
    _visits = new int[9][9];
  }

  public boolean isEmpty(int row, int col, int value) {
    return !rowHas(row, value) && !columnHas(col, value) && !blockHas(row, col, value);
  }

  /**
   * Get the number of times a given cell was changed
   *
   * @param row Row of the cell
   * @param col Col of the cell
   * @return number of times we changed the value of this cell
   */
  public int getVisits(int row, int col) {
    return _visits[row][col];
  }

  /**
   * Clear the visits array as well as the UI
   *
   * @param puzzle The current puzzle being displayed
   */
  public void reset(Puzzle puzzle) {
    _visits = new int[9][9];
    _sudokuFrame.setUserInput(puzzle.getValues());
  }

  public void solver(int[][] newValues) {
    _sudokuFrame.setTime(-1);
    load(newValues);
    long time = System.nanoTime();

    // Here is where the work gets done, start the recursive solution
    tryOne(0, 0);

    final double endTime = ((double) (System.nanoTime() - time)) / ((double) (1000 * 1000));

    // Now that we have the results, display them on the swing thread
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (_sudokuFrame.shouldShowProgress()) {
          _sudokuFrame.setUserInput(_values);
        }

        _sudokuFrame.setTime(endTime);
        _sudokuFrame.setUserInput(_values);
        _sudokuFrame.setAllDone();
      }
    });
  }

  public RowCol nextRowCol(int row, int col) {
    col++;
    if (col > 8) {
      col = 0;
      row++;
    }

    return new RowCol(row, col);
  }

  public RowCol nextZero(int row, int col) {
    RowCol result = null;

    while (row < 9 && col < 9 && result == null) {
      if (_values[row][col] == 0) {
        result = new RowCol(row, col);
      } else {
        RowCol next = nextRowCol(row, col);
        row = next.row;
        col = next.col;
      }
    }

    return result;
  }

//////////// Private Methods /////////////
  /**
   * Return true if any cell in a row of cells contains a given value
   *
   * @param row   Row to check
   * @param value Value we are looking for
   * @return return true if the value is in the row
   */
  private boolean rowHas(int row, int value) {
    return _values[row][0] == value ||
        _values[row][1] == value ||
        _values[row][2] == value ||
        _values[row][3] == value ||
        _values[row][4] == value ||
        _values[row][5] == value ||
        _values[row][6] == value ||
        _values[row][7] == value ||
        _values[row][8] == value;
  }

  /**
   * Return true if any cell in a column of cells contains a given value
   *
   * @param col   Column to check
   * @param value Value we are looking for
   * @return true if the value is in the column
   */
  private boolean columnHas(int col, int value) {

    return _values[0][col] == value ||
        _values[1][col] == value ||
        _values[2][col] == value ||
        _values[3][col] == value ||
        _values[4][col] == value ||
        _values[5][col] == value ||
        _values[6][col] == value ||
        _values[7][col] == value ||
        _values[8][col] == value;
  }

  /**
   * Return true if a any cell in a block of cells that contains row and col
   * contains the value
   *
   * @param row   Row to check
   * @param col   Column to check
   * @param value Value we are looking for
   * @return true if the value is in the block
   */
  private boolean blockHas(int row, int col, int value) {
    row -= (row % 3);
    col -= (col % 3);
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        if (_values[r + row][c + col] == value) {
          return true;
        }
      }
    }
    return false;
  }

//  private void dumpVisits() {
//      for (int row = 0; row < 9; row++) {
//          for (int col = 0; col < 9; col++) {
//              System.out.print(_visits[row][col] + " ");
//          }
//          System.out.println();
//      }
//  }

  /**
   * Class to store two ints
   */
  class RowCol {
    public int row;
    public int col;

    public RowCol(int row, int col) {
      this.row = row;
      this.col = col;
    }
  }
}
