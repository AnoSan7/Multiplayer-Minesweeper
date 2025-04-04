package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_SIZE = 10;
    private static final int DEFAULT_MINES = 20;

    private final int size;
    private final int numberOfMines;
    private final Cell[][] cells;
    private boolean minesPlaced = false;

    public Board() {
        this(DEFAULT_SIZE, DEFAULT_MINES);
    }

    public Board(int size, int numberOfMines) {
        this.size = size;
        this.numberOfMines = numberOfMines;
        this.cells = new Cell[size][size];
        initializeCells();
    }

    private void initializeCells() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    public void placeMines(int firstClickX, int firstClickY) {
        if (minesPlaced) {
            return;
        }

        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < numberOfMines) {
            int x = random.nextInt(size);
            int y = random.nextInt(size);
            // Ensure that the mine is not placed on the first click position
            // and that the cell is not already a mine
            if ((x != firstClickX || y != firstClickY) && !cells[x][y].isMine()) {
                cells[x][y].setMine(true);
                minesPlaced++;
            }
        }

        calculateAdjacentMines();
        this.minesPlaced = true;
    }

    public void placeMinesFromPositions(List<MinePosition> positions) {
        if (minesPlaced) {
            return;
        }

        // Clear any existing mines
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j].setMine(false);
            }
        }

        // Place mines at specified positions
        for (MinePosition pos : positions) {
            cells[pos.x][pos.y].setMine(true);
        }

        calculateAdjacentMines();
        this.minesPlaced = true;
    }

    public List<MinePosition> getMinePositions() {
        List<MinePosition> positions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j].isMine()) {
                    positions.add(new MinePosition(i, j));
                }
            }
        }
        return positions;
    }

    private void calculateAdjacentMines() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!cells[i][j].isMine()) {
                    int count = countAdjacentMines(i, j);
                    cells[i][j].setAdjacentMines(count);
                }
            }
        }
    }

    private int countAdjacentMines(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (isValidPosition(newX, newY) && cells[newX][newY].isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public int getSize() {
        return size;
    }

    public boolean areMinesPlaced() {
        return minesPlaced;
    }

    public static class MinePosition implements Serializable {
        private static final long serialVersionUID = 1L;
        public final int x;
        public final int y;

        public MinePosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void toggleFlag(int x, int y) {
        if (x >= 0 && x < size && y >= 0 && y < size && !cells[x][y].isRevealed()) {
            cells[x][y].setFlagged(!cells[x][y].isFlagged());
        }
    }

    public void revealCell(int x, int y) {
        if (x >= 0 && x < size && y >= 0 && y < size && !cells[x][y].isFlagged()) {
            cells[x][y].setRevealed(true);
            if (cells[x][y].getAdjacentMines() == 0 && !cells[x][y].isMine()) {
                // Reveal adjacent cells recursively
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int newX = x + i;
                        int newY = y + j;
                        if (newX >= 0 && newX < size && newY >= 0 && newY < size
                            && !cells[newX][newY].isRevealed()) {
                            revealCell(newX, newY);
                        }
                    }
                }
            }
        }
    }
}