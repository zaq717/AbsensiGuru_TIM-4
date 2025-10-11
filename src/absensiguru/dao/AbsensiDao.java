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
import javax.swing.JOptionPane;



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

    // 1️⃣ Cek apakah guru terdaftar
    String sqlGuru = "SELECT * FROM guru WHERE id_guru = ?";
    ps = conn.prepareStatement(sqlGuru);
    ps.setString(1, kodeGuru);
    rs = ps.executeQuery();

    if (rs.next()) {
        String idGuru = rs.getString("id_guru");

        // Waktu acuan
        java.sql.Time waktuSekarang = new java.sql.Time(System.currentTimeMillis());
        java.sql.Time jamAwalMasuk = java.sql.Time.valueOf("07:00:00");
        java.sql.Time jamAkhirMasuk = java.sql.Time.valueOf("10:19:59");
        java.sql.Time jamMulaiPulang = java.sql.Time.valueOf("10:20:00");

        // Tolak absen sebelum jam 7
        if (waktuSekarang.before(jamAwalMasuk)) {
            JOptionPane.showMessageDialog(null,
                    "Belum waktunya absen! Absen masuk mulai pukul 07:00.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            conn.close();
            return;
        }

        // 2️⃣ Cek apakah guru sudah absen hari ini
        String sqlCek = "SELECT * FROM absensi WHERE id_guru=? AND tanggal=CURDATE()";
        ps = conn.prepareStatement(sqlCek);
        ps.setString(1, idGuru);
        ResultSet rsCek = ps.executeQuery();

        if (rsCek.next()) {
            // Sudah absen masuk sebelumnya
            Time jamMasuk = rsCek.getTime("jam_masuk");
            Time jamPulang = rsCek.getTime("jam_pulang");

            // 🔒 Cegah absen pulang dua kali
            if (jamPulang != null) {
                JOptionPane.showMessageDialog(null,
                        "Anda sudah absen pulang hari ini! Tidak bisa absen dua kali.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                conn.close();
                return;
            }

            // 🕥 Cegah absen pulang sebelum jam 10:20
            if (waktuSekarang.before(jamMulaiPulang)) {
                JOptionPane.showMessageDialog(null,
                        "Belum waktunya absen pulang! Absen pulang mulai pukul 10:20.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                conn.close();
                return;
            }

            // ✅ Jika waktunya sudah cukup → update jam pulang
            String sqlUpdate = "UPDATE absensi SET jam_pulang=CURTIME(), status='Hadir Lengkap' "
                    + "WHERE id_guru=? AND tanggal=CURDATE()";
            ps = conn.prepareStatement(sqlUpdate);
            ps.setString(1, idGuru);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null,
                    "Absensi pulang berhasil!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            //System.out.println("Absensi pulang disimpan.");

        } else {
            // Belum absen → lakukan absen masuk
            if (waktuSekarang.after(jamAkhirMasuk)) {
                JOptionPane.showMessageDialog(null,
                        "Sudah lewat waktu absen masuk! Absen masuk maksimal pukul 10:19.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                conn.close();
                return;
            }

            String sqlInsert = "INSERT INTO absensi (id_guru, jam_masuk, status, tanggal) "
                    + "VALUES (?, CURTIME(), 'Hadir Tidak Lengkap', CURDATE())";
            ps = conn.prepareStatement(sqlInsert);
            ps.setString(1, idGuru);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null,
                    "Absensi masuk berhasil!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            //System.out.println("Absensi masuk disimpan.");
        }

    } else {
        JOptionPane.showMessageDialog(null,
                "Guru dengan ID " + kodeGuru + " tidak ditemukan!",
                "Error", JOptionPane.ERROR_MESSAGE);
        //throw new SQLException("Guru dengan ID " + kodeGuru + " tidak ditemukan!");
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