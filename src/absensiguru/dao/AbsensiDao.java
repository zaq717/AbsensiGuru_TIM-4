/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package absensiguru.dao;

import absensiguru.helper.Koneksi;
import absensiguru.model.AbsensiModel;
import absensiguru.model.GuruModel;
import com.mysql.cj.protocol.Resultset;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Time;



/**
 *
 * @author THINKPAD X280
 */
public class AbsensiDao {
      public List<AbsensiModel> getAbsensiHariIni() throws SQLException {
        List<AbsensiModel> list = new ArrayList<>();
        String sql = "SELECT a.*, g.nama AS nama_guru "
                + " FROM absensi a JOIN guru g ON a.id_guru = g.id_guru WHERE a.tanggal = CURDATE()";

        try (Connection conn = Koneksi.konek();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AbsensiModel ab = new AbsensiModel();
                ab.setIdAbsensi(rs.getInt("id_absensi"));
                ab.setNamaGuru(rs.getString("nama_guru"));
                ab.setIdGuru(rs.getString("id_guru"));
                ab.setTanggal(rs.getDate("tanggal"));
                ab.setJamMasuk(rs.getTime("jam_masuk"));
                ab.setJamPulang(rs.getTime("jam_pulang"));
                ab.setStatus(rs.getString("status"));
                list.add(ab);
            }
        }

        return list;
    }

    public void prosesAbsensi(String kodeGuru) throws SQLException {
    Connection conn = Koneksi.konek();
    PreparedStatement ps;
    ResultSet rs;

    // Cek guru
    String sqlGuru = "SELECT * FROM guru WHERE id_guru = ?";
    ps = conn.prepareStatement(sqlGuru);
    ps.setString(1, kodeGuru);
    rs = ps.executeQuery();

    if (rs.next()) {
        String idGuru = rs.getString("id_guru");

        java.sql.Time waktuSekarang = new java.sql.Time(System.currentTimeMillis());
        java.sql.Time jamMasukIdeal = java.sql.Time.valueOf("07:00:00");
        java.sql.Time jamPulangIdeal = java.sql.Time.valueOf("10:30:00");

        // Cek apakah sudah absen hari ini
        String sqlCek = "SELECT * FROM absensi WHERE id_guru=? AND tanggal=CURDATE()";
        ps = conn.prepareStatement(sqlCek);
        ps.setString(1, idGuru);
        ResultSet rsCek = ps.executeQuery();

        if (rsCek.next()) {
            // Sudah absen → update jam pulang
            Time jamMasuk = rsCek.getTime("jam_masuk");
            String status;

            if (waktuSekarang.before(jamPulangIdeal)) {
                status = "Pulang Cepat";
            } else if (jamMasuk.after(jamMasukIdeal)) {
                status = "Terlambat";
            } else {
                status = "Hadir Lengkap";
            }

            String sqlUpdate = "UPDATE absensi SET jam_pulang=CURTIME(), status=? "
                             + "WHERE id_guru=? AND tanggal=CURDATE()";
            ps = conn.prepareStatement(sqlUpdate);
            ps.setString(1, status);
            ps.setString(2, idGuru);
            ps.executeUpdate();
            System.out.println("Absensi pulang disimpan. Status: " + status);

        } else {
            // Belum absen → insert data masuk
            String status = waktuSekarang.after(jamMasukIdeal) ? "Terlambat" : "Hadir Tidak Lengkap";

            String sqlInsert = "INSERT INTO absensi (id_guru, jam_masuk, status, tanggal) "
                             + "VALUES (?, CURTIME(), ?, CURDATE())";
            ps = conn.prepareStatement(sqlInsert);
            ps.setString(1, idGuru);
            ps.setString(2, status);
            ps.executeUpdate();

            System.out.println("Absensi masuk disimpan. Status: " + status);
        }

    } else {
        throw new SQLException("Guru dengan ID " + kodeGuru + " tidak ditemukan!");
    }

    conn.close();
}

    public String ambilDariQR(String dataQR) {
        //contoh QR: "ID:4|Nama:Ahmad"
        String[] parts = dataQR.split("\\|");
        for (String part : parts) {
            if (part.startsWith("ID:")) {
                return  part.replace("ID:","").trim();
            }
        }
          return "";
    }   
}