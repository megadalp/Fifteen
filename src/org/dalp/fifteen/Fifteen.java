package org.dalp.fifteen;

// Demonstrate GridLayout - с допиливанием до игры "Пятнашки" (dalp 2023)




import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//FYI закомментил, потому что мешает попробовать собрать пятнашки, чтоб запустить на машине без явы
import org.dalp.util.Formatter;
import org.dalp.demolib.*;

public class Fifteen extends Frame {
    static int n = 4;
    static boolean shuffle = false;
    Button[][] bList = new Button[n][n]; // массив объектов кнопок
    String[][] lList = new String[n][n]; // оперативный массив меток
    Button clickedButton;
    String msg = "";

    public static int randomInt(int min, int max) {
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    public Fifteen() {

        // Use GridLayout.
        setLayout(new GridLayout(n, n));

        setFont(new Font("SansSerif", Font.BOLD, 24));

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int k = i * n + j + 1;
                if (k < (n * n)) {
                    lList[i][j] = "" + k;
                } else {
                    lList[i][j] = "";
                }
            }
        }

        // сказано перемешать
        if (shuffle) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int swapI = randomInt(0, n - 1);
                    int swapj = randomInt(0, n - 1);
                    if ((swapI != i) || (swapj != j)) {
                        String tmp = lList[i][j];
                        lList[i][j] = lList[swapI][swapj];
                        lList[swapI][swapj] = tmp;
                    }
                }
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                bList[i][j] = new Button(lList[i][j]);
                bList[i][j].setName("button" + (i + 1) + "-" + (j + 1));
                bList[i][j].setActionCommand(i + "," + j);
            }
        }
        for (int i = 0; i < bList.length; i++) {
            for (int j = 0; j < bList[i].length; j++) {
                if (bList[i][j] != null) {
                    add(bList[i][j]);
                    // FI: Ибо ActionListener - интерфейс функциональный,
                    // там один абстрактный метод (без дефолтной реализации) - actionPerformed().
                    bList[i][j].addActionListener(
                        (ae) -> {
                            msg = ae.getActionCommand();
                            clickedButton = (Button) ae.getSource();
                            repaint();
                        }
                    );
                }
            }
        }
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    public void paint(Graphics g) {
        if ((clickedButton != null) && !clickedButton.getLabel().equals("")) {
            String[] ndx = msg.split(",", 2);
            if ((ndx[0] != null) && (ndx[1] != null)) {
                int ndxI = Integer.parseInt(ndx[0]);
                int ndxJ = Integer.parseInt(ndx[1]);
                if (lList[ndxI][ndxJ] != null) {
                    // Ищем пустую метку
                    int emptyI = -1;
                    int emptyJ = -1;
                    // В строке
                    for (int j = 0; j < n; j++) {
                        if (lList[ndxI][j].equals("")) {
                            emptyJ = j;
                            break;
                        }
                    }
                    // В колонке
                    if (emptyJ < 0) {
                        for (int i = 0; i < n; i++) {
                            if (lList[i][ndxJ].equals("")) {
                                emptyI = i;
                                break;
                            }
                        }
                    }
                    // пустышка найдена в строке или колонке
                    if (!((emptyI < 0) && (emptyJ < 0))) {
                        // перемещаем метки в оперативном массиве -
                        // сдвигаем метки от нажатой кнопы в сторону пустой,
                        // пустую пишем на место нажатой
                        if (emptyI >= 0) {
                            // по вертикали
                            int step = emptyI > ndxI ? -1 : 1;
                            int i = emptyI;
                            do {
                                lList[i][ndxJ] = lList[i + step][ndxJ];
                            } while ((i += step) != ndxI);
                        } else {
                            // по горизонтали
                            int step = emptyJ > ndxJ ? -1 : 1;
                            int i = emptyJ;
                            do {
                                lList[ndxI][i] = lList[ndxI][i + step];
                            } while ((i += step) != ndxJ);
                        }
                        // на нажатой кнопе размещаем пустую
                        lList[ndxI][ndxJ] = "";
                    }
                }
            }
            // метки кнопам переписываем из оперативного массива меток
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    bList[i][j].setLabel(lList[i][j]);
                }
            }
        }
    }

    // public static void main(String[] args) {
    //     if (args.length > 0) {
    //         for (String arg : args) {
    //             if (arg.trim().matches("^\\d+$")) {
    //                 // м.б. указан размер игрового поля
    //                 if (Integer.parseInt(arg) > 1) {
    //                     Fifteen.n = Integer.parseInt(arg);
    //                 }
    //                 // м.б. указано "Перемешать числа"
    //             } else if (arg.trim().matches("^\\-+shuffle$")) {
    //                 Fifteen.shuffle = true;
    //                 break;
    //             }
    //         }
    //     }
    //
    //     Fifteen appwin = new Fifteen();
    //
    //     // dalp - окно по центру текущего экрана
    //     GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    //     int screenW = gd.getDisplayMode().getWidth();
    //     int screenH = gd.getDisplayMode().getHeight();
    //     int winW = Math.min(800, Fifteen.n * 80);
    //     int winH = Math.min(800, Fifteen.n * 80);
    //     int posX = (int) ((float) (screenW - winW) / 2);
    //     int posY = (int) ((float) (screenH - winH) / 2);
    //     appwin.setSize(new Dimension(winW, winH));
    //     appwin.setLocation(new Point(posX, posY));
    //
    //     //FYI закомментил, потому что мешает попробовать собрать пятнашки, чтоб запустить на машине без явы
    //     // appwin.setTitle("Пятнашки" + " " + Formatter.asNumberInWords(Fifteen.n) + " на " + Formatter.asNumberInWords(Fifteen.n));
    //     appwin.setTitle("Пятнашки");
    //     appwin.setVisible(true);
    // }
}
class FifteenRun {

    public static void main(String[] args) {
        if (args.length > 0) {
            for (String arg : args) {
                if (arg.trim().matches("^\\d+$")) {
                    // м.б. указан размер игрового поля
                    if (Integer.parseInt(arg) > 1) {
                        Fifteen.n = Integer.parseInt(arg);
                    }
                    // м.б. указано "Перемешать числа"
                } else if (arg.trim().matches("^\\-+shuffle$")) {
                    Fifteen.shuffle = true;
                    break;
                }
            }
        }

        Fifteen appwin = new Fifteen();

        // dalp - окно по центру текущего экрана
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenW = gd.getDisplayMode().getWidth();
        int screenH = gd.getDisplayMode().getHeight();
        int winW = Math.min(800, Fifteen.n * 80);
        int winH = Math.min(800, Fifteen.n * 80);
        int posX = (int) ((float) (screenW - winW) / 2);
        int posY = (int) ((float) (screenH - winH) / 2);
        appwin.setSize(new Dimension(winW, winH));
        appwin.setLocation(new Point(posX, posY));

        appwin.setTitle("Пятнашки"
            //FYI тестирую подключение внешних модулей
            + " " + Formatter.asNumberInWords(Fifteen.n) + " на " + Formatter.asNumberInWords(Fifteen.n)
            + " (just test only: " + (new LibOne().methodOne()) + " and " + (new LibTwo().methodTwoOne())
            + ")"
        );
        // appwin.setTitle("Пятнашки" + " - " + new LibOne().methodOne());
        appwin.setVisible(true);
    }
}
