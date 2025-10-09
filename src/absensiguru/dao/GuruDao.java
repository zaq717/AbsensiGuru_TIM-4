/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package absensiguru.dao;

import absensiguru.helper.Koneksi;
import absensiguru.model.GuruModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author HP
 */
public class GuruDao {
    
    public void insert(GuruModel guru){
    String sql = "INSERT INTO guru (nip, nama, jenis_kelamin, alamat) VALUES (?, ?, ? ?)";
        try (Connection conn = Koneksi.konek();
                PreparedStatement ps = conn.prepareStatement(sql)){
                 ps.setString(1, guru.getNip());
                ps.setString(2, guru.getNama());
                ps.setString(3, guru.getJenisKelamin());
                ps.setString(4, guru.getAlamat());
                ps.executeUpdate();
            } catch (SQLException e) {
            System.err.println("Gagal insert data : " + e.getMessage());
            }      
        }
    
    
    public void delete(String nip){
        String sql = "DELETE FROM guru WHERE nip=?";
        try (Connection conn = Koneksi.konek();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, nip);
                ps.executeUpdate();
            }catch (SQLException e) {
                System.err.println("Gagal hapus data : " + e.getMessage());
        }
    }
    
    public List<GuruModel> getAll(){
        List<GuruModel> list = new ArrayList<>();
        String sql = "SELECT * FROM guru";
        try (Connection conn = Koneksi.konek();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)){
        while (rs.next()){
            GuruModel guru = new GuruModel();
            guru.setNip(rs.getString("nip"));
            guru.setNama(rs.getString("nama"));
            guru.setJenisKelamin(rs.getString("jenis_kelamin"));
            guru.setAlamat(rs.getString("alamat"));
            list.add(guru);
        } 
       } catch (Exception e) {
               System.err.println("Gagal tampil data: " + e.getMessage());
       }
       return list;
      }
   }
