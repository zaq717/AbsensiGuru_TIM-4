/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package absensiguru.dao;

import absensiguru.helper.Koneksi;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class RekapAbsensiDao {

    private Connection conn;

    public RekapAbsensiDao() {
        conn = Koneksi.konek();
    }

    // ----------------- GET TAHUN -----------------
    public DefaultTableModel getComboTahun() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("tahun");

        String sql = "SELECT DISTINCT YEAR(tanggal) AS tahun FROM absensi ORDER BY tahun DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("tahun")});
            }

        } catch (Exception e) {
            System.out.println("Error getComboTahun: " + e.getMessage());
        }

        return model;
    }

    // ----------------- GET GURU -----------------
    public DefaultTableModel getComboGuru() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("nama");

        String sql = "SELECT nama FROM guru ORDER BY nama ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("nama")});
            }

        } catch (Exception e) {
            System.out.println("Error getComboGuru: " + e.getMessage());
        }

        return model;
    }

    // ---------------- GET REKAP ----------------
    public DefaultTableModel getRekap(String bulan, String tahun, String guru) {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Tanggal");
        model.addColumn("Jam Masuk");
        model.addColumn("Jam Pulang");
        model.addColumn("Status");

        StringBuilder sql = new StringBuilder(
                "SELECT a.tanggal, a.jam_masuk, a.jam_pulang, a.status "
                + "FROM absensi a JOIN guru g ON a.id_guru = g.id_guru WHERE 1=1 "
        );

        // Filter bulan
        if (!bulan.equals("Semua")) {
            sql.append(" AND MONTH(a.tanggal) = ").append(convertBulan(bulan));
        }

        // Filter tahun
        if (!tahun.equals("Semua")) {
            sql.append(" AND YEAR(a.tanggal) = ").append(tahun);
        }

        // Filter guru
        if (!guru.equals("Semua")) {
            sql.append(" AND g.nama = '").append(guru).append("'");
        }

        // Urutkan berdasarkan tanggal
        sql.append(" ORDER BY a.tanggal ASC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString()); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("tanggal"),
                    rs.getString("jam_masuk"),
                    rs.getString("jam_pulang"),
                    rs.getString("status")
                });
            }

        } catch (Exception e) {
            System.out.println("Error getRekap: " + e.getMessage());
        }

        return model;
    }

    // ----------------- KONVERSI BULAN -----------------
    private int convertBulan(String bulan) {
        switch (bulan) {
            case "Januari":
                return 1;
            case "Februari":
                return 2;
            case "Maret":
                return 3;
            case "April":
                return 4;
            case "Mei":
                return 5;
            case "Juni":
                return 6;
            case "Juli":
                return 7;
            case "Agustus":
                return 8;
            case "September":
                return 9;
            case "Oktober":
                return 10;
            case "November":
                return 11;
            case "Desember":
                return 12;
        }
        return 0;
    }
}
