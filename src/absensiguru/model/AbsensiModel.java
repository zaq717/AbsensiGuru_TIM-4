/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package absensiguru.model;

import java.sql.Time;
import java.util.Date;

/**
 *
 * @author THINKPAD X280
 */
public class AbsensiModel {
    private int idAbsensi;
    private String idGuru,namaGuru,status;
    private Date tanggal;
    private Time jamMasuk;
    private Time jamPulang;

    public AbsensiModel(int idAbsensi, String idGuru, String namaGuru, String status, Date tanggal, Time jamMasuk, Time jamPulang) {
        this.idAbsensi = idAbsensi;
        this.idGuru = idGuru;
        this.namaGuru = namaGuru;
        this.status = status;
        this.tanggal = tanggal;
        this.jamMasuk = jamMasuk;
        this.jamPulang = jamPulang;
    }

    public AbsensiModel() {
    }
    
    

    public int getIdAbsensi() {
        return idAbsensi;
    }

    public void setIdAbsensi(int idAbsensi) {
        this.idAbsensi = idAbsensi;
    }

    public String getIdGuru() {
        return idGuru;
    }

    public void setIdGuru(String idGuru) {
        this.idGuru = idGuru;
    }

    public String getNamaGuru() {
        return namaGuru;
    }

    public void setNamaGuru(String namaGuru) {
        this.namaGuru = namaGuru;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public Time getJamMasuk() {
        return jamMasuk;
    }

    public void setJamMasuk(Time jamMasuk) {
        this.jamMasuk = jamMasuk;
    }

    public Time getJamPulang() {
        return jamPulang;
    }

    public void setJamPulang(Time jamPulang) {
        this.jamPulang = jamPulang;
    }
    
    
    }
