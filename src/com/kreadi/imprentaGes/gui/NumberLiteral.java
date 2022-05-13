/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kreadi.imprentaGes.gui;

/**
 *
 * @author fabnun
 */
public class NumberLiteral {

    private static final String[] words = new String[]{
        "cero", "un", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez",
        "once", "doce", "trece", "catorce", "quince", "dieci", "veinti", "veinte", "treinta",
        "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa", "ciento",
        "doscientos", "trescientos", "cuatrocientos", "quinientos", "seiscientos",
        "setecientos", "ochocientos", "novecientos", "cien"};

    public static String N2L(int num) {
        String tmp = "";
        int dcn;
        if (num < 0) {
            tmp = "";
        } else {
            if (num == 0) {
                tmp = words[0];
            } else {
                dcn = (num % 100);
                if (dcn < 16) {
                    tmp = words[dcn];
                    if (dcn == 0) {
                        tmp = "";
                    }
                } else {
                    if (dcn < 30) {
                        tmp = words[(dcn / 10) + 15] + words[(dcn % 10)];
                        if (dcn == 20) {
                            tmp = words[18];
                        }
                    } else {
                        if ((dcn % 10) != 0) {
                            tmp = " y " + words[(dcn % 10)];
                        }
                        tmp = words[(dcn / 10) + 16] + tmp;
                    }
                }
                dcn = (num % 1000);
                if (dcn == 100) {
                    tmp = words[35];
                }
                if (dcn > 100) {
                    tmp = words[25 + (dcn / 100)] + " " + tmp;
                }

                if (num > 999) {
                    dcn = ((num / 1000) % 1000);
                    if (dcn == 1) {
                        tmp = "mil " + tmp;
                    } else {
                        tmp = N2L(dcn) + " mil " + tmp;
                    }

                }
                if (num > 999999) {
                    dcn = (num / 1000000);
                    if (dcn == 1) {
                        tmp = "un millon " + tmp;
                    } else {
                        tmp = N2L(dcn) + " millones " + tmp;
                    }
                }
            }
        }
        return tmp;
    }
}
