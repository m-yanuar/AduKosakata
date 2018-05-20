package com.payjoe.adukosakata.Models;

/**
 * Created by Payjoe on 2/21/2018.
 */

public class KosakataModels {
    public int index;
    public String indo;
    public String ing;
    public String kategori;

    public KosakataModels(){

    }

    public KosakataModels(int index, String indo, String ing, String kategori) {
        this.index = index;
        this.indo = indo;
        this.ing = ing;
        this.kategori = kategori;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getIndo() {
        return indo;
    }

    public void setIndo(String indo) {
        this.indo = indo;
    }

    public String getIng() {
        return ing;
    }

    public void setIng(String ing) {
        this.ing = ing;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
}
