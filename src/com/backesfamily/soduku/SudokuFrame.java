package com.backesfamily.soduku; /**
 * User: jbackes
 * Date: Dec 21, 2006
 * Time: 11:25:27 AM
 * Copyright 2007, Jeffrey G. Backes. All rights reserved.
 */

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 */
public class SudokuFrame extends JFrame {
    private JTextField[][] textFields;

    private Solver solver;

    private JLabel timeLabel = new JLabel("Total time:");
    private JComboBox<String> puzzleChoice = new JComboBox<>();

    final JComboBox<String> solutionChoice = new JComboBox<>();
    final JCheckBox showProgress = new JCheckBox("Show Progress");
    final JButton startButt = new JButton("Start");
    final JButton resetButton = new JButton("Reset");

    final Vector<Solver> allSolvers = new Vector<>();
    final Vector<String> puzzleNames = new Vector<>();

    public SudokuFrame() {
        final ArrayList<Class<?>> solvers = ReflectionHelper.findClassesImplementing(AbstractSolver.class, AbstractSolver.class.getPackage());
        if (null != solvers) {
            for (Class solver : solvers) {
                try {
                    Constructor<?> ctor = solver.getConstructor(SudokuFrame.class);
                    Solver object = (Solver) ctor.newInstance(this);
                    allSolvers.add(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                this.solver = allSolvers.get(0);
            }
            initUI();
        }
    }

    private void initUI() {
        setTitle("Sudoku Solver");

        final JPanel gameBoard = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gameBoard.add(new JLabel("Total time:"));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final JPanel majorGrid = new JPanel(new GridLayout(3, 3, 0, 0));
        majorGrid.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        textFields = new DigitField[9][9];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                final JPanel section = new JPanel(new GridLayout(3, 3, 2, 2));
                section.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                majorGrid.add(section);
                for (int i = row * 3; i < (row * 3) + 3; i++) {
                    for (int j = col * 3; j < (col * 3) + 3; j++) {
                        textFields[i][j] = new DigitField(2);
                        textFields[i][j].setHorizontalAlignment(JTextField.CENTER);
                        textFields[i][j].setInputVerifier(new InputVerifier() {
                            @Override
                            public boolean verify(JComponent input) {
                                JTextField field = (JTextField) input;
                                String text = field.getText();
                                field.setForeground(text.equals("0") ? Color.RED : Color.BLACK);
                                return true;
                            }
                        });
                        section.add(textFields[i][j]);
                    }
                }
            }
        }

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(majorGrid, BorderLayout.CENTER);

        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        Vector<String> solverNames = new Vector<>();
        for (Solver s : allSolvers) {
            solverNames.add(s.getSolverName());
        }
        solutionChoice.setModel(new DefaultComboBoxModel<>(solverNames));

        puzzleNames.addAll(PuzzleLoader.getInstance().getAllPuzzleNames());
        puzzleChoice.setModel(new DefaultComboBoxModel<>(puzzleNames));
        String firstPuzzleName = puzzleNames.get(0);
        PuzzleLoader.getInstance().setCurrentPuzzleByName(firstPuzzleName);

        // by default show progress
        showProgress.setSelected(true);

        startButt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startButt.setEnabled(false);
                resetButton.setEnabled(false);
                solutionChoice.setEnabled(false);
                new Thread(new Runnable() {
                    public void run() {
                        solver.solver(getUserInput());
                    }
                }).start();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        PuzzleLoader instance = PuzzleLoader.getInstance();
                        String selection = (String) puzzleChoice.getSelectedItem();
                        instance.setCurrentPuzzleByName(selection);
                        solver.reset(instance.getCurrentPuzzle());
                    }
                }).start();
            }
        });

        solutionChoice.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        JComboBox comboBox = (JComboBox) e.getSource();
                        String selection = (String) comboBox.getModel().getSelectedItem();
                        for (Solver s : allSolvers) {
                            if (selection.equals(s.getSolverName())) {
                                SudokuFrame.this.solver = s;
                            }
                        }
                        System.out.println(selection);
                    }
                }).start();
            }
        });

        puzzleChoice.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                for (int row = 0; row < 9; row++) {
                    for (int col = 0; col < 9; col++) {
                        textFields[row][col].setForeground(Color.BLACK);
                        textFields[row][col].setBackground(Color.WHITE);
                    }
                }
                JComboBox comboBox = (JComboBox) e.getSource();
                String selection = (String) comboBox.getModel().getSelectedItem();

                PuzzleLoader instance = PuzzleLoader.getInstance();
                instance.setCurrentPuzzleByName(selection);
                solver.reset(instance.getCurrentPuzzle());
            }
        });

        buttonPanel.add(solutionChoice);
        buttonPanel.add(showProgress);
        buttonPanel.add(startButt);
        buttonPanel.add(resetButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        final JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.add(timeLabel, BorderLayout.WEST);
        timePanel.add(puzzleChoice, BorderLayout.EAST);
        getContentPane().add(timePanel, BorderLayout.NORTH);

        //setResizable(false);
        setLocationRelativeTo(null);

        pack();
        setVisible(true);
    }

    public boolean shouldShowProgress() {
        return showProgress.isSelected();
    }

    public void setAllDone() {
        startButt.setEnabled(true);
        resetButton.setEnabled(true);
        solutionChoice.setEnabled(true);
    }

    public void setTime(final double time) {
        if (time == -1) {
            timeLabel.setText("Total time: working...");
        } else {
            timeLabel.setText("Total time: " + time + " ms");
        }
    }

    public void setUserInputRowColumn(int row, int col, int value) {
        Color backColor = Color.WHITE;

        if (value == 0) {
            textFields[row][col].setForeground(Color.RED);
            int visits = solver.getVisits(row, col);
            if (visits == 0) {
                backColor = Color.WHITE;
            } else if (visits < 100) {
                backColor = Color.LIGHT_GRAY;
            } else if (visits < 1000) {
                backColor = Color.GRAY;
            } else if (visits < 5000) {
                backColor = Color.DARK_GRAY;
            } else {
                backColor = Color.BLACK;
            }
        }

        textFields[row][col].setBackground(backColor);
        textFields[row][col].setText("" + value);
    }

    public void setUserInput(int[][] values) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                setUserInputRowColumn(row, col, values[row][col]);
            }
        }
    }

    public int[][] getUserInput() {
        final int[][] values = new int[9][9];
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (textFields[row][col].getText().length() == 0) {
                    values[row][col] = 0;
                } else {
                    values[row][col] = Integer.parseInt(textFields[row][col].getText());
                }
            }
        }
        return values;
    }

    private class DigitField extends JTextField {

        public DigitField(int cols) {
            super(cols);
        }

        protected Document createDefaultModel() {
            return new DigitDocument();
        }

        private class DigitDocument extends PlainDocument {
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

                if (offs > 0) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                if (str == null) {
                    return;
                }
                try {
                    int value = Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                super.insertString(offs, str, a);
            }
        }
    }
}
