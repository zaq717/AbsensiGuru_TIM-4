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
import java.sql.Statement;
import java.sql.Time;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 *
 * @author THINKPAD X280
 */
public class AbsensiDao extends Koneksi{
    private final Connection koneksi;
    private PreparedStatement ps;
    private Statement st;
    private ResultSet rs;
    private String Query;
    AbsensiModel am = new AbsensiModel();

    public AbsensiDao() {
        koneksi = super.konek();
    }
    
    
    public ResultSet getAbsensiHariIni(){
        String query = "SELECT a.*, g.nama AS nama_guru "
                + " FROM absensi a JOIN guru g ON a.id_guru = g.id_guru WHERE a.tanggal = CURDATE() "
                + "ORDER BY a.tanggal DESC,a.jam_masuk DESC";
        try {
            st = koneksi.createStatement();
            rs = st.executeQuery(query);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Data Gagal diTampilkan");
        }       
        return rs;
    }
    

    public boolean CekGuru(String idGuru) throws SQLException {
        Query = "SELECT * FROM guru WHERE id_guru = ?";//mengecek apakah guru terdaftar
        ps = koneksi.prepareStatement(Query);
        ps.setString(1, idGuru);
        rs = ps.executeQuery();

        if (!rs.next()) {
            notifikasi("Guru dengan ID " + idGuru + " tidak ditemukan!",
                    "Error", JOptionPane.ERROR_MESSAGE,2000);
            return false;
        }
        return true;
    }

    public boolean CekAbsenHariIni(String idGuru) throws SQLException {
        Query = "SELECT * FROM absensi WHERE id_guru=? AND tanggal=CURDATE()";
        ps = koneksi.prepareStatement(Query);
        ps.setString(1, idGuru);
        rs = ps.executeQuery();

        if (rs.next()) {
            Time jamMasuk = rs.getTime("jam_masuk");
            Time jamPulang = rs.getTime("jam_pulang");
                
                am.setJamMasuk(jamMasuk!= null ? jamMasuk.toLocalTime() : null);
                am.setJamPulang(jamPulang != null ? jamPulang.toLocalTime() : null);
            
            return true;
        }
        return false;
    }

    private void absenMasuk(String idGuru) throws SQLException {
        Query = "INSERT INTO absensi (id_guru, jam_masuk, status, tanggal) VALUES (?, CURTIME(), 'Hadir Tidak Lengkap', CURDATE())";
        ps = koneksi.prepareStatement(Query);
        ps.setString(1, idGuru);
        ps.executeUpdate();
    }

    private void absenPulang(String idGuru) throws SQLException {
        Query = "UPDATE absensi SET jam_pulang=CURTIME(), status='Hadir Lengkap' WHERE id_guru=? AND tanggal=CURDATE()";
        ps = koneksi.prepareStatement(Query);
        ps.setString(1, idGuru);
        ps.executeUpdate();
    }

    public void ProsesAbsensi(String idGuru) throws SQLException {
        am.setIdGuru(idGuru);//untuk setIdGuru untuk mengetahui idguru mana yang diproses;
        try {
            //aturan waktu absen
            LocalTime waktuSekarang = LocalTime.now(ZoneId.of("Asia/Jakarta"));
            LocalTime jamAwalMasuk = LocalTime.of(07, 00,00);
            LocalTime jamAkhirMasuk = LocalTime.of(22, 59,59);
            LocalTime jamMulaiPulang = LocalTime.of(23, 00,00);

            boolean sudahAbsen = CekAbsenHariIni(idGuru);//cek apakah sudah absen hari ini

            if (!sudahAbsen) {//absen masuk
                   //jika waktu sekarang sebelum waktu absen masuk
                if (waktuSekarang.isBefore(jamAwalMasuk)) {
                    notifikasi("Belum waktunya absen masuk!",
                            "Peringatan", JOptionPane.WARNING_MESSAGE,2000);
                    return;
                }//jika waktu sekarang sudah lewat waktu absen masuk
                if (waktuSekarang.isAfter(jamAkhirMasuk)) {
                    notifikasi("Sudah lewat waktu absen masuk!",
                            "Peringatan", JOptionPane.WARNING_MESSAGE,2000);
                    return;
                }//jika waktu sekarang berada pada zona waktu absen masuk
                absenMasuk(idGuru);//menambahkan absen masuk
                notifikasi("Absensi masuk berhasil!",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE,2000);
                return;
            }//absen pulang
            if (am.getJamPulang() != null) {//jika absen masuk sudah terisi maka sudah absen pulang
                notifikasi("Anda sudah absen pulang hari ini!",
                        "Peringatan", JOptionPane.WARNING_MESSAGE,2000);
                return;
            }//jika waktu absen sebelum waktunya absen pulang
            if (waktuSekarang.isBefore(jamMulaiPulang)) {
                notifikasi("Belum waktunya absen pulang!",
                        "Peringatan", JOptionPane.WARNING_MESSAGE,2000);
                return;
            }//jika waktu sudah berada pada zona waktu absen pulang
            absenPulang(idGuru);//lakukan absen pulang
            notifikasi("Absensi pulang berhasil!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE,2000);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Terjadi kesalahan: " + e.getMessage());
        }
    }

    public String ambilDariQR(String dataQR) {
        //contoh QR: "ID:4|Nama:Ahmad"
        String[] parts = dataQR.split("\\|");
        for (String part : parts) {
            if (part.startsWith("ID:")) {
                return part.replace("ID:", "").trim();
            }
        }
        return "";
    }

    public void notifikasi(String pesan, String title, int tipePesan, int timeMills) {
        JOptionPane pane = new JOptionPane(pesan, tipePesan);//
        JDialog dialog = pane.createDialog((java.awt.Component) null, title);
        //timer untuk pesan
        dialog.setModal(false);
        new javax.swing.Timer(timeMills, e -> dialog.dispose()).start();
        dialog.setVisible(true);
    }
} 