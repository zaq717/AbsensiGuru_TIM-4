/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presensiguru.model;

/**
 *
 * @author Lenovo
 */
public class RekapAbsensiModel {

    private String tanggal;
    private String namaGuru;
    private String status;

    public RekapAbsensiModel(String tanggal, String namaGuru, String status) {
        this.tanggal = tanggal;
        this.namaGuru = namaGuru;
        this.status = status;
    }
}
