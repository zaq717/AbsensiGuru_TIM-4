/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package absensiguru.dao;

import absensiguru.helper.Koneksi;
import absensiguru.model.AbsensiModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class AbsensiDao extends Koneksi {

    private final Connection koneksi;//koenksi aktif untuk menghubungkan ke database
    private PreparedStatement ps;//digunakan untuk query dengan parameter
    private Statement st;//digunakan untuk query tanpa parameter
    private ResultSet rs;//digunakan untuk menyimpan data hasil query
    private String Query;//digunakan untuk syntax sql
    AbsensiModel am = new AbsensiModel();

    public AbsensiDao() {
        koneksi = super.konek();//untuk buka koneksi setiap dao aktif
    }
    //method mengambil data absensi hari ini
    public ResultSet getAbsensiHariIni() {
        Query = "SELECT a.*, g.nama AS nama_guru "
                + " FROM absensi a JOIN guru g ON a.id_guru = g.id_guru WHERE a.tanggal = CURDATE() "
                + "ORDER BY a.tanggal DESC,a.jam_masuk DESC";
        try {
            st = koneksi.createStatement();
            rs = st.executeQuery(Query);//eksekusi progam dan disimpan ke objek rs
        } catch (SQLException e) {//penanganan kesalahan sql
            JOptionPane.showMessageDialog(null, "Data Gagal diTampilkan");
        }
        return rs;//mengembalikan nilai rs berisi data absensi
    }
    //method mengecek apakah guru terdaftar?
    public boolean CekGuru(String idGuru) throws SQLException {
        Query = "SELECT * FROM guru WHERE id_guru = ?";
        ps = koneksi.prepareStatement(Query);
        ps.setString(1, idGuru);//mengisi parameter id guru
        rs = ps.executeQuery();//eksekusi query dan hasil disimpan ke objek rs

             if (!rs.next()) {//jika id guru tidak ada di dataabases
            notifikasi("Guru dengan ID " + idGuru + " tidak ditemukan!",
                    "Error", JOptionPane.ERROR_MESSAGE, 2000);
            return false;
            }
        return true;//return true jika id_guru ditemukan
    }

    public boolean CekAbsenHariIni(String idGuru) throws SQLException {
        Query = "SELECT * FROM absensi WHERE id_guru=? AND tanggal=CURDATE()";
        ps = koneksi.prepareStatement(Query);
        ps.setString(1, idGuru);
        rs = ps.executeQuery();

        if (rs.next()) {
            Time jamMasuk = rs.getTime("jam_masuk");//mengambil jam masuk dari hasil query disimpan ke variabel jamMasuk
            Time jamPulang = rs.getTime("jam_pulang");//mengambil jam pulang dari hasil query disimpan ke variabel jamPulang

            am.setJamMasuk(jamMasuk != null ? jamMasuk.toLocalTime() : null);//menyimpan nilai jam masuk hasil query ke jamMasuk di model jika ada datanya
            am.setJamPulang(jamPulang != null ? jamPulang.toLocalTime() : null);//menyimpan nilai jam pulang hasil query ke jamPulang di model jika ada datanya

            return true;
        }
        return false;
    }

    private void absenMasuk(String idGuru) throws SQLException {//bertipe data void karena untuk insert aja tidak mengembalikan nilai
        Query = "INSERT INTO absensi (id_guru, jam_masuk, status, tanggal) VALUES (?, CURTIME(), 'Hadir Tidak Lengkap', CURDATE())";
        ps = koneksi.prepareStatement(Query);
        ps.setString(1, idGuru);
        ps.executeUpdate();
    }

    private void absenPulang(String idGuru) throws SQLException {//bertipe data void karena untuk update aja tidak mengembalikan nilai
        Query = "UPDATE absensi SET jam_pulang=CURTIME(), status='Hadir Lengkap' WHERE id_guru=? AND tanggal=CURDATE()";
        ps = koneksi.prepareStatement(Query);
        ps.setString(1, idGuru);
        ps.executeUpdate();
    }

    public void ProsesAbsensi(String idGuru) throws SQLException{
        try {//pengecekan id guru apakah terdaftar?
            if (!CekGuru(idGuru)) {
                return;
            }
            //aturan waktu absen
            LocalTime waktuSekarang = LocalTime.now(ZoneId.of("Asia/Jakarta"));//local time waktu jakarta (WIB).
            LocalTime jamAwalMasuk = LocalTime.of(07, 00, 00);
            LocalTime jamAkhirMasuk = LocalTime.of(10, 43, 59);
            LocalTime jamMulaiPulang = LocalTime.of(10, 44, 00);
            //cek apakah sudah absen hari ini
            boolean sudahAbsen = CekAbsenHariIni(idGuru);
            //absen masuk
            if (!sudahAbsen) {
                //jika waktu sekarang sebelum waktu absen masuk
                if (waktuSekarang.isBefore(jamAwalMasuk)) {
                    notifikasi("Belum waktunya absen masuk!",
                            "Peringatan", JOptionPane.WARNING_MESSAGE, 2000);
                    return;
                }//jika waktu sekarang sudah lewat waktu absen masuk
                if (waktuSekarang.isAfter(jamAkhirMasuk)) {
                    notifikasi("Sudah lewat waktu absen masuk!",
                            "Peringatan", JOptionPane.WARNING_MESSAGE, 2000);
                    return;
                }//jika waktu sekarang berada pada zona waktu absen masuk
                absenMasuk(idGuru);//menambahkan absen masuk
                notifikasi("Absensi masuk berhasil!",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE, 2000);
                return;
            }
            //absen pulang
            if (am.getJamPulang() != null) {//jika absen pulang sudah terisi maka sudah absen pulang
                notifikasi("Anda sudah absen pulang hari ini!",
                        "Peringatan", JOptionPane.WARNING_MESSAGE, 2000);
                return;
            }//jika absen pulang sebelum waktunya absen pulang
            if (waktuSekarang.isBefore(jamMulaiPulang)) {
                notifikasi("Belum waktunya absen pulang!",
                        "Peringatan", JOptionPane.WARNING_MESSAGE, 2000);
                return;
            }//jika waktu sudah berada pada zona waktu absen pulang
            absenPulang(idGuru);//lakukan absen pulang
            notifikasi("Absensi pulang berhasil!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE, 2000);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Terjadi kesalahan: " + e.getMessage());
        }
    }
    //method mengambil id guru di data qr
    public String ambilDariQR(String dataQR) {
        //contoh QR: "ID:4|Nama:Ahmad"
        String[] parts = dataQR.split("\\|");//memisahkan data dengan |
        for (String part : parts) {//membaca setiap data 
            if (part.startsWith("ID:")) {//jika ada data dengan teks ID maka ambil datanya
                return part.replace("ID:", "").trim();//teks ID rubah ke kosong dan hapus spasi berlebihnya
            }
        }
        return "";//mengembalikan nilai kosong jika id tidak ditemukan
    }

    public void notifikasi(String pesan, String title, int tipePesan, int durasi) {
        JOptionPane pane = new JOptionPane(pesan, tipePesan);//buat joption pane dari pesan dan tipe pesan
        JDialog dialog = pane.createDialog((java.awt.Component) null, title);//buat Jdialog dari joption pane dan beri judul JDialog
        //timer untuk pesan
        dialog.setModal(false);//mengatur dialog agar tidak mengunci layar utama
        new javax.swing.Timer(durasi, e -> dialog.dispose()).start();//digunakan untuk mengatur action setelah timer selesai
        dialog.setVisible(true);//tampilkan notifikasi
    }
}
