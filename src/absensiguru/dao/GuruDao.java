/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package absensiguru.dao;

import absensiguru.helper.Koneksi;
import absensiguru.model.GuruModel;
import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author HP
 */
public class GuruDao {

    public void insert(GuruModel guru) {
        String sql = "INSERT INTO guru (nip, nama, jenis_kelamin, alamat) VALUES (?, ?, ?, ?)";
        try (Connection conn = Koneksi.konek(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // Menentukan nilai jenis kelamin sesuai database
            String jK;
            if (guru.getJenisKelamin().equalsIgnoreCase("Laki-Laki")) {
                jK = "L";
            } else if (guru.getJenisKelamin().equalsIgnoreCase("Perempuan")) {
                jK = "P";
            } else {
                jK = null;
            }

            ps.setString(1, guru.getNip());
            ps.setString(2, guru.getNama());
            ps.setString(3, jK);
            ps.setString(4, guru.getAlamat());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil Ditambahkan");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data Gagal Ditambahkan");
        }
    }

    public void update(GuruModel guru) {
        String sql = "UPDATE guru SET nama = ?, jenis_kelamin = ?, alamat = ? WHERE id_guru = ?";

        try (Connection conn = Koneksi.konek(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // Konversi jenis kelamin ke format database
            String jk;
            if (guru.getJenisKelamin().equalsIgnoreCase("Laki-laki")) {
                jk = "L";
            } else if (guru.getJenisKelamin().equalsIgnoreCase("Perempuan")) {
                jk = "P";
            } else {
                jk = null;
            }

            // Urutan parameter sesuai query
            ps.setString(1, guru.getNama());
            ps.setString(2, jk);
            ps.setString(3, guru.getAlamat());
            ps.setInt(4, guru.getId());  // untuk WHERE

            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Data Berhasil Diubah");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Data Gagal Diubah! Error: " + e.getMessage());
        }
    }

    public void delete(GuruModel guru) {
        String sql = "DELETE FROM guru WHERE id_guru = ?";
        try (Connection conn = Koneksi.konek(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, guru.getId());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil dihapus");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data Gagal dihapus");
        }
    }

    public ResultSet TampilGuru() {
        ResultSet rs = null;
        String sql = "SELECT * FROM guru";

        try {
            Connection conn = Koneksi.konek();
            PreparedStatement ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Data gagal ditampilkan: " + e.getMessage(),
                    "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }

        return rs;
    }

}
