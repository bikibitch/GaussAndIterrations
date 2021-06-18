package com.company;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Matrix {
    private double[][] array;
    private double[][] firstArray;
    private double[] sums;
    private double[] rootsGauss;
    private double[] rootsItr;
    private int rowNumber;
    private int columnNumber;
    private double epsilon;


    private void create(int n, int m) {
        firstArray = new double[n][];
        for (int i = 0; i < n; i++)
            firstArray[i] = new double[m];
    }

    public void print() {
        int i, j;
        for (i = 0; i < rowNumber; i++) {
            for (j = 0; j < columnNumber; j++)
                System.out.printf("%15.6E", array[i][j]);
//                System.out.printf("%7.1f ", array[i][j]);
            System.out.println();
        }
        System.out.println();
    }

    public void init(String s) throws FileNotFoundException {
        File file = new File(s);
        Scanner scan = new Scanner(file);
        Pattern pat = Pattern.compile("[ \t]+");
        String str = scan.nextLine();
        String[] sn = pat.split(str);
        rowNumber = Integer.parseInt(sn[0]);
        columnNumber = Integer.parseInt(sn[1]) + 1;
        epsilon = Math.pow(10, -Double.parseDouble(sn[2]) - 1);
        create(rowNumber, columnNumber);
        int variant = Integer.parseInt(sn[3]);
        if (variant == 1) {
            for (int i = 0; i < rowNumber; i++) {
                str = scan.nextLine();
                sn = pat.split(str);
                for (int j = 0; j < columnNumber; j++)
                    firstArray[i][j] = Double.parseDouble(sn[j]);
            }
        }
        else if (variant == 2) {
            Random rand = new Random();
            for (int i = 0; i < rowNumber; i++) {
                for (int j = 0; j < columnNumber; j++) {
                    firstArray[i][j] = rand.nextInt(20) - 10;
                }
            }
        }
        scan.close();

        array = new double[rowNumber][columnNumber];
        for (int i = 0; i < rowNumber; i++)
            array[i] = Arrays.copyOf(firstArray[i], columnNumber);

    }

    //решение системы методом Гаусса
    public void findSolutionGauss() {
        makeTriangle();
        print();
        if (makeTriangle() == 0) {
            findRootsGauss();
            for (int i = 0; i < rootsGauss.length; i++) {
                System.out.printf("x"+ (i + 1) + " = " + "%12.6E\n", rootsGauss[i]);
            }
            System.out.println();
        }
        else if ( isZero(array[rowNumber - 1][columnNumber - 2]) )
                if ( isZero(array[rowNumber - 1][columnNumber - 1]) )
                    System.out.println("Система имеет бесконечное количество решений");
                else
                    System.out.println("Система не имеет решений");
            else
                System.out.println("Система вырожденная");
    }

    //приведение к треугольному виду
    private int makeTriangle() {
        for (int i = 0; i < rowNumber; i++) {
            if (array[i][i] == 0)
                if (firstNotZeroElement(i, i) != -1)
                    swapLines(i, firstNotZeroElement(i, i));
                else
                    return 1; // вырожденнная

                for (int j = i + 1; j < rowNumber; j++) {
                    double multiplier = array[j][i] / array [i][i];
                    for (int k = i; k < columnNumber; k++) {
                        array[j][k] -= array[i][k] * multiplier;
                    }
            }
        }
        return 0; // все норм
    }

    //нахождение корней
    private void findRootsGauss() {
        rootsGauss = new double[columnNumber - 1];
        for (int i = rowNumber - 1; i >= 0 ; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < rowNumber; j++)
                sum += array[i][j] * rootsGauss[j];
            rootsGauss[i] = (array[i][columnNumber - 1] - sum) / array[i][i];
        }
    }

    //проверка корней
    //public void checkSolution() {
//        double[] lastColumn = new double[rowNumber];
//        System.out.println("Полученный столбец свободных членов:");
//        for (int i = 0; i < rowNumber; i++) {
//            for (int j = 0; j < columnNumber - 1; j++) {
//                lastColumn[i] += rootsGauss[j] * firstArray[i][j];
//            }
//            System.out.printf("%15.6E", lastColumn[i]);
//        }
//        System.out.println();
//        System.out.println("Заданный столбец свободных членов:");
//        for (int i = 0; i < rowNumber; i++) {
//            System.out.printf("%15.6E", firstArray[i][columnNumber - 1]);
//        }
//    }

    //поиск первого ненулевого элемента в заданном столбце после индекса
    private int firstNotZeroElement(int column, int index) {
            int answer = -1;
            for (int i = index; i < rowNumber; i++) {
                if (!isZero(array[i][column])) {
                    answer = i;
                    break;
                }
            }
        return (answer);
    }

    //перестановка строк
    private void swapLines(int firstRaw, int secondRaw) {
            double[] temp = array[firstRaw];
            array[firstRaw] = array[secondRaw];
            array[secondRaw] = temp;
    }
    private void swapLines(int firstRaw, int secondRaw, double[][] array) {
        double[] temp = array[firstRaw];
        array[firstRaw] = array[secondRaw];
        array[secondRaw] = temp;
    }


    private boolean isZero(double a){
        return (Math.abs(a) < epsilon);
    }

    // Итерационный метод
    //ДУС - CSS (sufficient convergence condition)

    public void findSolutionIterations() {
        for (int i = 0; i < rowNumber; i++)
            array[i] = Arrays.copyOf(firstArray[i], columnNumber);
        print();
        fillSums();
        if(isDiagonalZero(array))
            if (!removeZeroes()) {
                System.out.println("Нельзя решить итерационным методом");
                return;
            }
        if (!isCSSTrue())
            swapLinesForCSS();
        if (isCSSTrue()) {
                changeArray();
                print();
                findRootsItr();

                for (int i = 0; i < rootsItr.length; i++)
                    if (Double.isInfinite(rootsItr[i]) || Double.isNaN(rootsItr[i]))
                        System.out.println("Нельзя решить итерационным методом");
                    else
                        System.out.printf("x" + (i + 1) + " = " + "%12.6E\n", rootsItr[i]);
            }
        else {
                changeArray();
                print();
                if (findRootsItrCtrl())
                    for (int i = 0; i < rootsItr.length; i++)
                        System.out.printf("x" + (i + 1) + " = " + "%12.6E\n", rootsItr[i]);
                else
                    System.out.println("Нельзя решить итерационным методом, система расходится");
        }
    }


    //выполняется ли ДУС
    private boolean isCSSTrue(){
        boolean res = false;
        double diff;
        for (int i = 0; i < rowNumber; i++) {
            diff = sums[i] - 2 * Math.abs(array[i][i]);
            if (diff < 0) {
                if (diff <= 0)
                    res = true;
            }
            else return res;
        }
        return res;
    }

    private void swapLinesForCSS(){
        for (int i = 0; i < rowNumber; i++) {
            if(!isDiagElemMoreSum(i)) {
                if (findLineWithMax(i) != -1)
                    swapLines(findLineWithMax(i), i);
                else return;
            }
        }
    }

    private void findRootsItr() {
        rootsItr = new double[columnNumber - 1];
        double[] prevRoots;
        double max;
        do {
            prevRoots = Arrays.copyOf(rootsItr, rootsItr.length);
            for (int i = 0; i < rowNumber; i++) {
                rootsItr[i] = array[i][columnNumber - 1];
                for (int j = 0; j < columnNumber - 1; j++) {
                    if (i != j)
                        rootsItr[i] -= array[i][j] * rootsItr[j];
                }
            }
            max = Double.MIN_VALUE;
            for (int i = 0; i < rootsItr.length; i++) {
                double diff = Math.abs(rootsItr[i] - prevRoots[i]);
                max = Math.max(diff, max);
            }
        } while (max >= epsilon);
    }

    private boolean findRootsItrCtrl(){
        double diff;
        double max = Double.MIN_VALUE;
        rootsItr = new double[columnNumber - 1];
        double x;
        for (int k = 0; k < 10; k++) {
            x = rootsItr[0];
            for (int i = 0; i < rowNumber; i++) {
                rootsItr[i] = array[i][columnNumber - 1];
                for (int j = 0; j < columnNumber - 2; j++) {
                    if (i != j)
                        rootsItr[i] -= array[i][j] * rootsItr[j];
                }
            }
            diff = Math.abs(x - rootsItr[0]);
            if (k > 5) {
                if (diff > max)
                    max = diff;
            }
        }
        if (max > epsilon) return false;

        do {
            x = rootsItr[0];
            for (int i = 0; i < rowNumber; i++) {
                rootsItr[i] = array[i][columnNumber - 1];
                for (int j = 0; j < columnNumber - 2; j++) {
                    if (i != j)
                        rootsItr[i] -= array[i][j] * rootsItr[j];
                }
            }
        } while (Math.abs(rootsItr[0] - x) >= epsilon);

        return true;

    }

    //изменение матрицы для решения итерационным методом
    private void changeArray() {
        for (int i = 0; i < rowNumber; i++) {
            double cur = array[i][i];
            for (int j = 0; j < columnNumber - 1; j++) {
                    array[i][j] /= cur;
            }
            array[i][columnNumber - 1] /= cur;
        }
    }

    //проверка диагонали на нуль
    private boolean isDiagonalZero(double[][] array) {
        for (int i = 0; i < rowNumber; i++)
            if (isZero(array[i][i]))
                return true;
        return false;
    }

    //устранение нулей на диагонали
    private boolean removeZeroes() {
        int nextLine = 0;
        int counter = 0;
        double[][] newArray = new double[rowNumber][columnNumber];

        for (int i = 0; i < rowNumber; i++)
            newArray[i] = Arrays.copyOf(array[i], columnNumber);

        for (int i = 0; i < rowNumber; i++) {
            if(isZero(newArray[i][i])) {
                int line = firstNotZeroElement(i, nextLine);
                if(line == -1)
                    return false;
                else {
                    swapLines(line, i, newArray);
                    if (isZero(newArray[line][line])) {
                        swapLines(line, i, newArray);
                        if (counter == rowNumber - 1)
                            return false;
                        nextLine = line + 1;
                        i--;
                        counter++;
                    }
                }
            }
        }
        if (!isDiagonalZero(newArray)){
            array = newArray;
            return true;
        }
        else
            return false;
    }

    //поиск сумм строк по модулю
    private void fillSums(){
        sums = new double[rowNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < columnNumber - 1; j++) {
                sums[i] += Math.abs(array[i][j]);
            }
        }
    }

    private boolean isDiagElemMoreSum(int i) {
        double diff = sums[i] - 2 * Math.abs(array[i][i]);
        if (diff < 0)
            return diff <= 0;
       return false;
    }

    private int findLineWithMax(int index){
        double diff;
        for (int i = 0; i < rowNumber; i++) {
            if(i != index) {
                diff = sums[i] - 2 * Math.abs(array[i][index]);
                if (diff < 0)
                    if (diff <= 0)
                        return i;
            }
        }
        return -1;
    }
}

