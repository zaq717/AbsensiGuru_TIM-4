/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package absensiguru.dao;

import absensiguru.helper.Koneksi;
import absensiguru.model.ManajemenModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManajemenDao {

    public void insert(ManajemenModel m) {
        try {
            Connection conn = Koneksi.konek();
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, m.getusername());
            ps.setString(2, m.getpassword());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(ManajemenModel m, String usernameLama) {
    String sql = "UPDATE users SET username=?, password=? WHERE username=?";
    try (Connection conn = Koneksi.konek();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, m.getusername());
        ps.setString(2, m.getpassword());
        ps.setString(3, usernameLama);
        ps.executeUpdate();
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    public void delete(ManajemenModel m) {
        try {
            Connection conn = Koneksi.konek();
            String sql = "DELETE FROM users WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, m.getusername());
            ps.executeUpdate();
            ps.close();
            System.out.println("Data user berhasil dihapus!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ManajemenModel> getall() {
        List<ManajemenModel> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (
                Connection conn = Koneksi.konek(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ManajemenModel m = new ManajemenModel();
                m.setusername(rs.getString("username"));
                m.setpassword(rs.getString("password"));
                list.add(m);
            }
        } catch (Exception e) {
            System.out.println("Gagal Menampilkan data user: " + e.getMessage());
        }
        return list;
    }

}
