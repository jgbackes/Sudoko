package com.backesfamily.soduku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Copyright Jeffrey G. Backes. All Rights Reserved.
 * User: jbackes
 * Date: 2/12/15
 * Time: 9:48 PM
 */
public class PuzzleLoader {

  private static final String CVS_SPLIT_BY = ",";

  private static final Lock _lock = new ReentrantLock();
  private static PuzzleLoader _instance;
  private static Puzzle _currentPuzzle = null;

  private ArrayList<Puzzle> _allPuzzles;

  private PuzzleLoader() {
  }

  public static PuzzleLoader getInstance() {
    _lock.lock();
    try {
      if (_instance == null) {
        _instance = new PuzzleLoader();
        _instance.readPuzzles();
        _currentPuzzle = _instance._allPuzzles.get(0);
      }
    } finally {
      _lock.unlock();
    }
    return _instance;
  }

  public Puzzle getCurrentPuzzle() {
    return _currentPuzzle;
  }

  public void setCurrentPuzzleByName(String puzzleName) {
    for(Puzzle p : _allPuzzles) {
      if (p.getName().equals(puzzleName)) {
        _currentPuzzle = p;
        break;
      }
    }
  }

  public Vector<String> getAllPuzzleNames() {
    Vector<String> result = new Vector<>();

    for(Puzzle p : _allPuzzles) {
      result.add(p.getName());
    }

    return result;
  }

  private void readPuzzles() {
    _allPuzzles = new ArrayList<>();
    URL url = this.getClass().getResource("/text/puzzles.txt");

    File puzzleFile = new File(url.getPath());

    try {
      BufferedReader input = new BufferedReader(new FileReader(puzzleFile));
      for(;;) {
        Puzzle puzzle = getPuzzle(input);
        if (puzzle != null) {
          _allPuzzles.add(puzzle);
        } else {
          break;
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private Puzzle getPuzzle(BufferedReader puzzleInput) {
    Puzzle result = null;

    try {
      String puzzleName = puzzleInput.readLine();
      String puzzleDifficulty = puzzleInput.readLine();
      if (puzzleName != null && puzzleDifficulty != null) {
        int[][] values = new int[9][9];
        for(int i=0;i<9;i++) {
          String line = puzzleInput.readLine();
          String[] columns = line.split(CVS_SPLIT_BY);
          for(int j=0;j<9;j++) {
            values[i][j] = Integer.parseInt(columns[j].trim());
          }
        }
        result = new Puzzle(puzzleName, puzzleDifficulty, values);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return result;
  }
}
