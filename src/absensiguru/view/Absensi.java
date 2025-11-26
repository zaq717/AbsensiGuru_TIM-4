package absensiguru.view;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;
import absensiguru.dao.AbsensiDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class Absensi extends javax.swing.JPanel {

    AbsensiDao dao = new AbsensiDao();
    DefaultTableModel model;

    public Absensi() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("FlatLaf Error");
        }
        initComponents();
        model = new DefaultTableModel();
        tblAbsensi.setModel(model);
        load_table();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txtScan.requestFocusInWindow();
            }
        });
    }

    void load_table() {
        model.setRowCount(0);//mengahapus semua data kolom yang sebelumnya ada ditabel
        model.setColumnCount(0);
        //menambahkan kolom ke JTable
        model.addColumn("Nama Guru");
        model.addColumn("Tanggal");
        model.addColumn("Jam Masuk");
        model.addColumn("Jam Pulang");
        model.addColumn("Status");

        try {
            ResultSet result = dao.getAbsensiHariIni();//memanggil method getAbsenHariIni
            while (result.next()) {
                    //memanggil baris data dalam database ke JTable
                model.addRow(new Object[]{
                    result.getString("nama_guru"),
                    result.getString("tanggal"),
                    result.getString("jam_masuk"),
                    result.getString("jam_pulang"),
                    result.getString("status")
                });
            }
            tblAbsensi.setModel(model);//mengeset tblAbsensi dengan pengaturan diatas
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        barAtas = new javax.swing.JPanel();
        lbAbsensi = new javax.swing.JLabel();
        pnDasar = new javax.swing.JPanel();
        lbDataAbsensi = new javax.swing.JLabel();
        lbJamMasukJamPulang = new javax.swing.JLabel();
        tbAbsensi = new javax.swing.JScrollPane();
        tblAbsensi = new javax.swing.JTable();
        txtScan = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        barAtas.setBackground(new java.awt.Color(16, 185, 129));

        lbAbsensi.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lbAbsensi.setForeground(new java.awt.Color(255, 255, 255));
        lbAbsensi.setText("Absensi");

        javax.swing.GroupLayout barAtasLayout = new javax.swing.GroupLayout(barAtas);
        barAtas.setLayout(barAtasLayout);
        barAtasLayout.setHorizontalGroup(
            barAtasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(barAtasLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(lbAbsensi, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(519, Short.MAX_VALUE))
        );
        barAtasLayout.setVerticalGroup(
            barAtasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbAbsensi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        add(barAtas, java.awt.BorderLayout.PAGE_START);

        lbDataAbsensi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbDataAbsensi.setText("Data Absensi Masuk Hari Ini");

        lbJamMasukJamPulang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbJamMasukJamPulang.setText("Jam Masuk : 07 : 00 : 00 | Jam Pulang : 10 : 30 : 00");

        tblAbsensi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblAbsensi.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                tblAbsensiAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        tblAbsensi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAbsensiMouseClicked(evt);
            }
        });
        tbAbsensi.setViewportView(tblAbsensi);

        txtScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtScanActionPerformed(evt);
            }
        });
        txtScan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtScanKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout pnDasarLayout = new javax.swing.GroupLayout(pnDasar);
        pnDasar.setLayout(pnDasarLayout);
        pnDasarLayout.setHorizontalGroup(
            pnDasarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnDasarLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lbJamMasukJamPulang, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(185, 185, 185))
            .addGroup(pnDasarLayout.createSequentialGroup()
                .addGroup(pnDasarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnDasarLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(pnDasarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbAbsensi, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
                            .addComponent(txtScan)))
                    .addGroup(pnDasarLayout.createSequentialGroup()
                        .addGap(205, 205, 205)
                        .addComponent(lbDataAbsensi)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        pnDasarLayout.setVerticalGroup(
            pnDasarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnDasarLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(lbDataAbsensi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbJamMasukJamPulang)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tbAbsensi, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(txtScan, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        add(pnDasar, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void tblAbsensiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAbsensiMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_tblAbsensiMouseClicked

    private void tblAbsensiAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_tblAbsensiAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_tblAbsensiAncestorAdded

    private void txtScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtScanActionPerformed
        // TODO add your handling code here:
        String dataQR = txtScan.getText();


        try {
            String idGuru = dao.ambilDariQR(dataQR);

            dao.ProsesAbsensi(idGuru);
            load_table();
        } catch (SQLException ex) {
            dao.notifikasi("Gagal menyimpan absensi: " + ex.getMessage(),
                    "Peringatan", JOptionPane.INFORMATION_MESSAGE, 2000);
        }

        txtScan.setText("");
        txtScan.requestFocusInWindow();

    }//GEN-LAST:event_txtScanActionPerformed

    private void txtScanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtScanKeyPressed

    }//GEN-LAST:event_txtScanKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel barAtas;
    private javax.swing.JLabel lbAbsensi;
    private javax.swing.JLabel lbDataAbsensi;
    private javax.swing.JLabel lbJamMasukJamPulang;
    private javax.swing.JPanel pnDasar;
    private javax.swing.JScrollPane tbAbsensi;
    private javax.swing.JTable tblAbsensi;
    private javax.swing.JTextField txtScan;
    // End of variables declaration//GEN-END:variables
}
