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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ManajemenDao extends Koneksi {

    private final Connection conn;
    private PreparedStatement ps;
    private String sql;

    public ManajemenDao() {
        conn = super.konek();
    }

    public void insert(ManajemenModel m) {
        try {
            sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, m.getusername());
            ps.setString(2, m.getpassword());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Data Berhasil ditambah!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal ditambah!" + e.getMessage());
        }
    }

    public void update(ManajemenModel m) {
        try {
            sql = "UPDATE users SET username=?,password=? WHERE id_user=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, m.getusername());
            ps.setString(2, m.getpassword());
            ps.setInt(3, m.getId());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Data Berhasil diubah!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal diubah!" + e.getMessage());
        }
    }

    public void delete(ManajemenModel m) {
        try {
            sql = "DELETE FROM users WHERE username=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, m.getusername());
            ps.executeUpdate();
            ps.close();
            JOptionPane.showMessageDialog(null, "Data Berhasil dihapus!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Data Berhasil dihapus!");
        }
    }

    public DefaultTableModel tampilData() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID User");
        model.addColumn("Username");
        model.addColumn("Password");

        String sql = "SELECT * FROM users";
        try (Connection conn = Koneksi.konek(); 
                PreparedStatement ps = conn.prepareStatement(sql); 
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getString("id_user");
                row[1] = rs.getString("username");
                row[2] = rs.getString("password");
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }
}
