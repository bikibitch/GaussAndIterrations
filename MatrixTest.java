package com.company;

import java.io.FileNotFoundException;

public class MatrixTest {

    public static void main(String[] args) {
        Matrix mat = new Matrix();
        try
        {
            mat.init("file.txt");
        }
        catch (FileNotFoundException e)
        {
            System.out.println("FILE NOT FOUND!!!");
        }
        mat.print();
        mat.findSolutionGauss();
        mat.findSolutionIterations();
    }


}
