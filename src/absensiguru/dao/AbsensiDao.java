/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package absensiguru.dao;

import absensiguru.helper.Koneksi;
import absensiguru.model.AbsensiModel;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
//import java.sql.Time;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.time.LocalTime;
import java.time.ZoneId;



/**
 *
 * @author THINKPAD X280
 */
public class AbsensiDao {
     
      public List<AbsensiModel> getAbsensiHariIni() throws SQLException {
        List<AbsensiModel> list = new ArrayList<>();
        String sql = "SELECT a.*, g.nama AS nama_guru "
                + " FROM absensi a JOIN guru g ON a.id_guru = g.id_guru WHERE a.tanggal = CURDATE()"
                + "ORDER BY a.tanggal DESC,a.jam_masuk DESC";

        try (Connection conn = Koneksi.konek();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AbsensiModel ab = new AbsensiModel();
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

    // Mengecek apakah guru terdaftar
    String sqlGuru = "SELECT * FROM guru WHERE id_guru = ?";
    ps = conn.prepareStatement(sqlGuru);
    ps.setString(1, kodeGuru);
    rs = ps.executeQuery();

    if (!rs.next()) {
        notifikasi("Guru dengan ID " + kodeGuru + " tidak ditemukan!",
                "Error", JOptionPane.ERROR_MESSAGE, 2000);
        conn.close();
        return;
    }

    String idGuru = rs.getString("id_guru");

    // Tentukan waktu
    LocalTime waktuSekarang = LocalTime.now(ZoneId.of("Asia/Jakarta"));
    LocalTime jamAwalMasuk = LocalTime.of(7, 0, 0);
    LocalTime jamAkhirMasuk = LocalTime.of(10, 29, 59);
    LocalTime jamMulaiPulang = LocalTime.of(10, 30, 0);
    
        System.out.println("DEBUG: Waktu Sekarang" + waktuSekarang);

    // Tolak absen sebelum jam awal
    if (waktuSekarang.isBefore(jamAwalMasuk)) {
        notifikasi("Belum waktunya absen! Absen masuk mulai pukul 07:00.",
                "Peringatan", JOptionPane.WARNING_MESSAGE, 2000);
        conn.close();
        return;
    }

    // Cek apakah guru sudah absen hari ini
    String sqlCek = "SELECT * FROM absensi WHERE id_guru=? AND tanggal=CURDATE()";
    ps = conn.prepareStatement(sqlCek);
    ps.setString(1, idGuru);
    ResultSet rsCek = ps.executeQuery();

    if (rsCek.next()) {
        //mengonversi waktu dari database (java.sql.Time) ke waktu (LocalTime)
        java.sql.Time sqljamPulang = rsCek.getTime("jam_pulang");
        LocalTime jamPulang = (sqljamPulang != null) ? sqljamPulang.toLocalTime() :null;
        // Sudah absen masuk, cek jam pulang
        if (jamPulang != null) {
            notifikasi("Anda sudah absen pulang hari ini!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE, 2000);
            conn.close();
            return;
        }

        if (waktuSekarang.isBefore(jamMulaiPulang)) {
            notifikasi("Belum waktunya absen pulang! Mulai pukul 10:30.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE, 2000);
            conn.close();
            return;
        }

        // Update jam pulang
        String sqlUpdate = "UPDATE absensi SET jam_pulang=CURTIME(), status='Hadir Lengkap' "
                + "WHERE id_guru=? AND tanggal=CURDATE()";
        ps = conn.prepareStatement(sqlUpdate);
        ps.setString(1, idGuru);
        ps.executeUpdate();

        notifikasi("Absensi pulang berhasil!",
                "Berhasil", JOptionPane.INFORMATION_MESSAGE, 2000);
    } else {
        // Belum absen masuk
        if (waktuSekarang.isAfter(jamAkhirMasuk)) {
            notifikasi("Sudah lewat waktu absen masuk! Maksimal pukul 10:29.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE, 2000);
            
        } else {
            // Insert data absen masuk
            String sqlInsert = "INSERT INTO absensi (id_guru, jam_masuk, status, tanggal) "
                    + "VALUES (?, CURTIME(), 'Hadir Tidak Lengkap', CURDATE())";
            ps = conn.prepareStatement(sqlInsert);
            ps.setString(1, idGuru);
            ps.executeUpdate();

            notifikasi("Absensi masuk berhasil!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE, 2000);
        }
    }

    conn.close();
}



    public String ambilDariQR(String dataQR) {
        String[] parts = dataQR.split("\\|");
        for (String part : parts) {
            if (part.startsWith("ID:")) {
                return  part.replace("ID:","").trim();
            }
        }
          return "";
    }   
    public void notifikasi(String pesan,String title, int tipePesan, int timeMills){
        JOptionPane pane = new JOptionPane(pesan,tipePesan);//
        JDialog dialog = pane.createDialog((java.awt.Component)null,title);
        //timer untuk pesan
        new javax.swing.Timer(timeMills, e -> dialog.dispose()).start();
        dialog.setVisible(true);
}
}