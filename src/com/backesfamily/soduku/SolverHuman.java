package com.backesfamily.soduku;

/**
 * Created with IntelliJ IDEA.
 * User: jbackes
 * Date: 9/11/15
 * Time: 12:24 PM
 */
public class SolverHuman extends AbstractSolver implements Solver {

  public SolverHuman(SudokuFrame sudokuFrame) {
    _sudokuFrame = sudokuFrame;
  }

  @Override
  public String getSolverName() {
    return "Human Like Solver";
  }

  @Override
  public boolean tryOne(int inRow, int inCol) {
    return false;
  }
}
