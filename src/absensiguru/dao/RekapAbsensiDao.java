/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package absensiguru.dao;

import absensiguru.helper.Koneksi;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Lenovo
 */
public class RekapAbsensiDao  {

    // Method untuk menampilkan data ke tabel
    public DefaultTableModel loadData() {
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Nama Guru");
    model.addColumn("Tanggal");
    model.addColumn("Jam Masuk");
    model.addColumn("Jam Pulang");
    model.addColumn("Status");

    String sql = "SELECT g.nama AS nama_guru, a.tanggal, a.jam_masuk, a.jam_pulang, a.status  FROM absensi a  JOIN guru g ON a.id_guru = g.id_guru ORDER BY a.tanggal DESC, g.nama ASC";


    try (Connection conn = Koneksi.konek();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Object[] row = {
                rs.getString("nama_guru"),
                rs.getString("tanggal"),
                rs.getString("jam_masuk"),
                rs.getString("jam_pulang"),
                rs.getString("status")
            };
            model.addRow(row);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null,
                "Gagal menampilkan data: " + e.getMessage(),
                "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }
    return model;
}
    public DefaultTableModel cariData(String keyword) {
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Nama Guru");
    model.addColumn("Tanggal");
    model.addColumn("Jam Masuk");
    model.addColumn("Jam Pulang");
    model.addColumn("Status");

    String sql = "SELECT g.nama AS nama_guru, a.tanggal, a.jam_masuk, a.jam_pulang, a.status "
            + "FROM absensi a JOIN guru g ON a.id_guru = g.id_guru "
            + "WHERE g.nama LIKE ? OR a.tanggal LIKE ? "
            + "ORDER BY a.tanggal DESC, g.nama ASC";

    try (Connection conn = Koneksi.konek();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, "%" + keyword + "%");
        ps.setString(2, "%" + keyword + "%");

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Object[] row = {
                rs.getString("nama_guru"),
                rs.getString("tanggal"),
                rs.getString("jam_masuk"),
                rs.getString("jam_pulang"),
                rs.getString("status")
            };
            model.addRow(row);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null,
                "Gagal mencari data: " + e.getMessage(),
                "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    return model;
}
 public DefaultTableModel cariData(int bulan, int tahun, int idGuru) {
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Nama Guru");
    model.addColumn("Tanggal");
    model.addColumn("Jam Masuk");
    model.addColumn("Jam Pulang");
    model.addColumn("Status");

    String sql = "SELECT g.nama AS nama_guru, a.tanggal, a.jam_masuk, a.jam_pulang, a.status "
               + "FROM absensi a JOIN guru g ON g.id = a.id_guru "
               + "WHERE MONTH(a.tanggal) = ? AND YEAR(a.tanggal) = ? AND a.id_guru = ? "
               + "ORDER BY a.tanggal ASC";

    try (Connection con = Koneksi.konek();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, bulan);
        pst.setInt(2, tahun);
        pst.setInt(3, idGuru);

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("nama_guru"),
                rs.getString("tanggal"),
                rs.getString("jam_masuk"),
                rs.getString("jam_pulang"),
                rs.getString("status")
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }

    return model;
}
 
 public DefaultTableModel filterData(int idGuru, int bulan, int tahun) {
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Nama Guru");
    model.addColumn("Tanggal");
    model.addColumn("Jam Masuk");
    model.addColumn("Jam Pulang");
    model.addColumn("Status");

    String sql = "SELECT g.nama AS nama_guru, a.tanggal, a.jam_masuk, a.jam_pulang, a.status "
            + "FROM absensi a JOIN guru g ON a.id_guru = g.id_guru WHERE 1=1 ";

    if (idGuru != 0) sql += "AND g.id_guru = " + idGuru + " ";
    if (bulan != 0) sql += "AND MONTH(a.tanggal) = " + bulan + " ";
    if (tahun != 0) sql += "AND YEAR(a.tanggal) = " + tahun + " ";

    sql += "ORDER BY a.tanggal DESC";

    try (Connection conn = Koneksi.konek();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Object[] row = {
                rs.getString("nama_guru"),
                rs.getString("tanggal"),
                rs.getString("jam_masuk"),
                rs.getString("jam_pulang"),
                rs.getString("status")
            };
            model.addRow(row);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null,
                "Gagal memuat data: " + e.getMessage(),
                "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    return model;
}


}
