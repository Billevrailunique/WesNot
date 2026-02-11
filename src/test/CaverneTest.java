package test;

import java.util.Random;

public class CaverneTest {

    int[][] cavtab = new int[20][20];
    Random ran = new Random(1814);

    CaverneTest(int density, int nbTurn) {
        init(density);
        for (int n = 0; n < nbTurn; n++) {
            one_step();
        }
        print_tab();
    }

    void init(int density) {
        // first step
        for (int i = 0; i < cavtab.length; i++) {
            for (int j = 0; j < cavtab[i].length; j++) {
                cavtab[i][j] = ran.nextInt(100) < density ? 1 : 0;
            }
        }
    }

    void print_tab() {
        for (int i = 0; i < cavtab.length; i++) {
            for (int j = 0; j < cavtab[i].length; j++) {
                //System.out.print(cavtab[i][j] + " ");
            }
            //System.out.println("");
        }
        //System.out.println("");
    }

    boolean overpopulated(int i, int j) {
        int result = 0;

        if (i == 0 && j == 0) {
            result = cavtab[0][1] + cavtab[1][1] + cavtab[1][0];
            return result == 3 || result == 0;
        }
        if (i == 0 && j == cavtab[0].length - 1) {
            result = cavtab[0][j - 1] + cavtab[1][j - 1] + cavtab[1][j];
            return result == 3 || result == 0;
        }
        if (i == cavtab.length - 1 && j == 0) {
            result = cavtab[i - 1][0] + cavtab[i - 1][1] + cavtab[i][1];
            return result == 3 || result == 0;
        }
        if (i == cavtab.length - 1 && j == cavtab[0].length - 1) {
            result = cavtab[i - 1][j - 1] + cavtab[i - 1][j] + cavtab[i][j - 1];
            return result == 3 || result == 0;
        }

        // Vérification des bords
        if (i == 0) {
            result = cavtab[i][j - 1] + cavtab[i][j + 1] + cavtab[i + 1][j - 1] + cavtab[i + 1][j]
                    + cavtab[i + 1][j + 1];
            return result < 3 && result > 0;
        }
        if (i == cavtab.length - 1) {
            result = cavtab[i][j - 1] + cavtab[i][j + 1] + cavtab[i - 1][j - 1] + cavtab[i - 1][j]
                    + cavtab[i - 1][j + 1];
            return result < 3 && result > 0;
        }
        if (j == 0) {
            result = cavtab[i - 1][j] + cavtab[i - 1][j + 1] + cavtab[i][j + 1] + cavtab[i + 1][j]
                    + cavtab[i + 1][j + 1];
            return result < 3 && result > 0;
        }
        if (j == cavtab[0].length - 1) {
            result = cavtab[i - 1][j - 1] + cavtab[i - 1][j] + cavtab[i][j - 1] + cavtab[i + 1][j - 1]
                    + cavtab[i + 1][j];
            return result < 3 && result > 0;
        }

        // Vérification des cellules internes
        result = cavtab[i - 1][j - 1] + cavtab[i - 1][j] + cavtab[i - 1][j + 1] +
                cavtab[i][j - 1] + cavtab[i][j + 1] +
                cavtab[i + 1][j - 1] + cavtab[i + 1][j] + cavtab[i + 1][j + 1];
        return result > 5 || result < 3;
    }

    void one_step() {
        int[][] newCavtab = new int[cavtab.length][cavtab[0].length];
        for (int i = 0; i < cavtab.length; i++) {
            for (int j = 0; j < cavtab[i].length; j++) {
                if (overpopulated(i, j)) {
                    newCavtab[i][j] = 1;
                } else {
                    newCavtab[i][j] = 0;
                }
            }
        }
        cavtab = newCavtab;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new CaverneTest(50, 4);
        }

    }
}
