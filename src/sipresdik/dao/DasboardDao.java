package sipresdik.dao;

import sipresdik.helper.Koneksi;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DasboardDao {

    public int totalGuru() throws SQLException {
        String sql = "SELECT COUNT(*) AS total_guru FROM guru";
        try (Connection conn = Koneksi.konek();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) return rs.getInt("total_guru");
            return 0;
        }
    }

   
    public int hadir() throws SQLException {
        String sql = "SELECT COUNT(a.id_absensi) AS hadir_hari_ini " +
                     "FROM guru g JOIN absensi a ON g.id_guru = a.id_guru AND a.tanggal = CURDATE()";
        try (Connection conn = Koneksi.konek();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) return rs.getInt("hadir_hari_ini");
            return 0;
        }
    }

    public int tidakHadir() throws SQLException {
        String sql = "SELECT COUNT(g.id_guru) AS tidak_hadir_hari_ini " +
                     "FROM guru g LEFT JOIN absensi a ON g.id_guru = a.id_guru AND a.tanggal = CURDATE() " +
                     "WHERE a.id_absensi IS NULL";
        try (Connection conn = Koneksi.konek();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) return rs.getInt("tidak_hadir_hari_ini");
            return 0;
        }
    }

}
