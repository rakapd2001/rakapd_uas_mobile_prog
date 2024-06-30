package com.example.firebase;

public class ItemList {

    private String id;
    private String judul;
    private String kategori;
    private String keterangan;
    private String imageUrl;

    public ItemList(String judul, String kategori, String keterangan, String imageUrl) {
        this.judul = judul;
        this.kategori = kategori;
        this.keterangan = keterangan;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public String getKategori() {
        return kategori;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
