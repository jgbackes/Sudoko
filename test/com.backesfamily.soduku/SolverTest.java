package com.backesfamily.soduku;

/**
 * Created with IntelliJ IDEA.
 * User: jbackes
 * Date: 9/11/15
 * Time: 1:15 PM
 */
public class SolverTest {
  SolverBruteForce bf = new SolverBruteForce(null);
  SolverHuman sh = new SolverHuman(null);
  SolverOptimized so = new SolverOptimized(null);

  public void testGetSolverName() throws Exception {
    assert (bf.getSolverName().equals("foo"));
    assert (sh.getSolverName().equals("bar"));
    assert (so.getSolverName().equals("fu"));
  }

  public void testTryOne() throws Exception {

  }
}
