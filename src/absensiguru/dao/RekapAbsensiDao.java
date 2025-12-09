/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package absensiguru.dao;

import absensiguru.helper.Koneksi;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.JTable;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.net.URL;

public class RekapAbsensiDao {

    private Connection conn;

    public RekapAbsensiDao() {
        conn = Koneksi.konek();
    }

    // ====================== GET TAHUN ======================
    public DefaultTableModel getComboTahun() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("tahun");

        String sql = "SELECT DISTINCT YEAR(tanggal) AS tahun FROM absensi ORDER BY tahun DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("tahun")});
            }
        } catch (Exception e) {
            System.out.println("Error getComboTahun: " + e.getMessage());
        }
        return model;
    }

    // ====================== GET GURU ======================
    public DefaultTableModel getComboGuru() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("nama");

        String sql = "SELECT nama FROM guru ORDER BY nama ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("nama")});
            }
        } catch (Exception e) {
            System.out.println("Error getComboGuru: " + e.getMessage());
        }
        return model;
    }

    // ====================== GET REKAP DETAIL ======================
    public DefaultTableModel getRekap(String bulan, String tahun, String guru) {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Tanggal");
        model.addColumn("Nama Guru");
        model.addColumn("Jam Masuk");
        model.addColumn("Jam Pulang");
        model.addColumn("Status");

        StringBuilder sql = new StringBuilder(
                "SELECT a.tanggal, g.nama, a.jam_masuk, a.jam_pulang, a.status "
                + "FROM absensi a "
                + "JOIN guru g ON a.id_guru = g.id_guru "
                + "WHERE 1=1 "
        );

        // Filter Bulan
        if (!bulan.equals("Semua")) {
            sql.append(" AND MONTH(a.tanggal) = ").append(convertBulan(bulan));
        }
        // Filter Tahun
        if (!tahun.equals("Semua")) {
            sql.append(" AND YEAR(a.tanggal) = ").append(tahun);
        }
        // Filter Guru
        if (!guru.equals("Semua")) {
            sql.append(" AND g.nama = '").append(guru).append("'");
        }

        sql.append(" ORDER BY a.tanggal ASC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString()); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("tanggal"),
                    rs.getString("nama"),
                    rs.getString("jam_masuk"),
                    rs.getString("jam_pulang"),
                    rs.getString("status")
                });
            }

        } catch (Exception e) {
            System.out.println("Error getRekap: " + e.getMessage());
        }

        return model;
    }

    // ====================== GET REKAP TOTAL (PER GURU) ======================
    public DefaultTableModel getRekapTotal(String bulan, String tahun) {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("No");
        model.addColumn("Nama Guru");
        model.addColumn("Hadir Lengkap");
        model.addColumn("Hadir Tidak Lengkap");

        StringBuilder sql = new StringBuilder(
                "SELECT g.nama, "
                + "SUM(CASE WHEN a.status = 'Hadir Lengkap' THEN 1 ELSE 0 END) AS lengkap, "
                + "SUM(CASE WHEN a.status = 'Hadir Tidak Lengkap' THEN 1 ELSE 0 END) AS tidak_lengkap "
                + "FROM guru g LEFT JOIN absensi a ON g.id_guru = a.id_guru WHERE 1=1"
        );

        if (!bulan.equals("Semua")) {
            sql.append(" AND MONTH(a.tanggal) = ").append(convertBulan(bulan));
        }
        if (!tahun.equals("Semua")) {
            sql.append(" AND YEAR(a.tanggal) = ").append(tahun);
        }

        sql.append(" GROUP BY g.nama ORDER BY g.nama ASC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString()); ResultSet rs = ps.executeQuery()) {

            int no = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                    no++,
                    rs.getString("nama"),
                    rs.getInt("lengkap"),
                    rs.getInt("tidak_lengkap")
                });
            }

        } catch (Exception e) {
            System.out.println("Error getRekapTotal: " + e.getMessage());
        }

        return model;
    }

    // ----------------- KONVERSI BULAN -----------------
    private int convertBulan(String bulan) {
        switch (bulan) {
            case "Januari":
                return 1;
            case "Februari":
                return 2;
            case "Maret":
                return 3;
            case "April":
                return 4;
            case "Mei":
                return 5;
            case "Juni":
                return 6;
            case "Juli":
                return 7;
            case "Agustus":
                return 8;
            case "September":
                return 9;
            case "Oktober":
                return 10;
            case "November":
                return 11;
            case "Desember":
                return 12;
        }
        return 0;
    }

    public void cetakOtomatis(String guru, String bulan, String tahun, JTable table) {
        if (guru == null || bulan == null || tahun == null || table == null) {
            JOptionPane.showMessageDialog(null, "Parameter cetak otomatis tidak valid.");
            return;
        }

        try {
            if (guru.equalsIgnoreCase("Semua")) {
                cetakRekapSemuaGuru(bulan, tahun, table);
            } else {
                cetakRekapPerGuru(guru, bulan, tahun, table);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error cetak otomatis: " + e.toString());
        }
    }

    public void cetakRekapPerGuru(String guru, String bulan, String tahun, JTable table) {
        try {

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Simpan Rekap Per Guru");
            chooser.setSelectedFile(new java.io.File("Rekap_" + guru.replaceAll("\\s+", "_") + "_" + bulan + "_" + tahun + ".pdf"));

            int userOption = chooser.showSaveDialog(null);
            if (userOption != JFileChooser.APPROVE_OPTION) {
                return;
            }

            String fileName = chooser.getSelectedFile().getAbsolutePath();
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                fileName += ".pdf";
            }

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, new FileOutputStream(fileName));
            doc.open();

            // ==================== HEADER LENGKAP =====================
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{20, 80});

            // LOGO
            try {
                Image logo = Image.getInstance(getClass().getResource("/Image/Logo_MI.png"));
                logo.scaleToFit(70, 70);

                PdfPCell cellLogo = new PdfPCell(logo);
                cellLogo.setBorder(Rectangle.NO_BORDER);
                cellLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
                headerTable.addCell(cellLogo);
            } catch (Exception e) {
                PdfPCell empty = new PdfPCell(new Phrase(""));
                empty.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(empty);
            }

            // JUDUL SAMPING LOGO
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font subFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            Paragraph judul = new Paragraph(
                    "DAFTAR HADIR GURU MI NURUL HUDA III\n"
                    + "TAHUN AJARAN 2025/2026\n"
                    + "BULAN " + bulan.toUpperCase() + " " + tahun,
                    titleFont
            );
            judul.setAlignment(Element.ALIGN_RIGHT);

            PdfPCell cellJudul = new PdfPCell(judul);
            cellJudul.setBorder(Rectangle.NO_BORDER);
            cellJudul.setVerticalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cellJudul);

            doc.add(headerTable);

            // ===== Garis bawah header =====
            doc.add(new Paragraph("______________________________________________________________________________\n\n"));

            // ===========================================================
            Font normal = new Font(Font.FontFamily.HELVETICA, 11);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);

            doc.add(new Paragraph("Nama Guru : " + guru + "\n\n", normal));

            PdfPTable pdfTable = new PdfPTable(4);
            pdfTable.setWidthPercentage(100);
            pdfTable.setSpacingBefore(8f);
            pdfTable.setSpacingAfter(8f);

            String[] headers = {"NO", "JUMLAH HADIR LENGKAP", "JUMLAH HADIR LENGKAP TIDAK LENGKAP", "STATUS"};
            for (String h : headers) {
                PdfPCell c = new PdfPCell(new Phrase(h, headerFont));
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                c.setPadding(5);
                c.setBackgroundColor(BaseColor.LIGHT_GRAY);
                pdfTable.addCell(c);
            }

            int lengkap = 0, tidak = 0;

            TableModel model = table.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                String tgl = model.getValueAt(i, 0) != null ? model.getValueAt(i, 0).toString() : "";
                String jm = model.getValueAt(i, 1) != null ? model.getValueAt(i, 1).toString() : "";
                String jp = model.getValueAt(i, 2) != null ? model.getValueAt(i, 2).toString() : "";
                String st = model.getValueAt(i, 3) != null ? model.getValueAt(i, 3).toString() : "";

                pdfTable.addCell(new PdfPCell(new Phrase(tgl, normal)));
                pdfTable.addCell(new PdfPCell(new Phrase(jm, normal)));
                pdfTable.addCell(new PdfPCell(new Phrase(jp, normal)));
                pdfTable.addCell(new PdfPCell(new Phrase(st, normal)));

                if (st.equalsIgnoreCase("Hadir Lengkap")) {
                    lengkap++;
                } else if (st.equalsIgnoreCase("Hadir Tidak Lengkap")) {
                    tidak++;
                }
            }

            doc.add(pdfTable);
            doc.add(new Paragraph("Jumlah Hadir Lengkap : " + lengkap, normal));
            doc.add(new Paragraph("Jumlah Tidak Lengkap : " + tidak, normal));

            doc.close();
            JOptionPane.showMessageDialog(null, "PDF berhasil disimpan:\n" + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error PDF Per Guru: " + e.toString());
        }
    }

    public void cetakRekapSemuaGuru(String bulan, String tahun, JTable table) {
        try {

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Simpan Rekap Semua Guru");
            chooser.setSelectedFile(new java.io.File("Rekap_Semua_Guru_" + bulan + "_" + tahun + ".pdf"));

            int userOption = chooser.showSaveDialog(null);
            if (userOption != JFileChooser.APPROVE_OPTION) {
                return;
            }

            String fileName = chooser.getSelectedFile().getAbsolutePath();
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                fileName += ".pdf";
            }

            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, new FileOutputStream(fileName));
            doc.open();
            
            
            
            
            

            // ==================== HEADER =====================
            PdfPTable header = new PdfPTable(3);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{20, 60, 20});

// 1. LOGO KIRI
            try {
    URL imgUrl = getClass().getResource("/Image/Logo_MI.png");
    if (imgUrl != null) {
        Image logo = Image.getInstance(imgUrl);
        logo.scaleToFit(70, 70);

        PdfPCell cellLogo = new PdfPCell(logo);
        cellLogo.setBorder(Rectangle.NO_BORDER);
        cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);

        header.addCell(cellLogo);
    } else {
        // Jika gambar tidak ditemukan
        header.addCell(new PdfPCell(new Phrase("LOGO TIDAK DITEMUKAN")));
    }
} catch (Exception e) {
    e.printStackTrace();
    header.addCell(new PdfPCell(new Phrase("ERROR LOGO")));
}


// 2. TULISAN TENGAH (RATA TENGAH)
            Paragraph title = new Paragraph(
                    "DAFTAR HADIR GURU MI NURUL HUDA III\n"
                    + "TAHUN AJARAN 2025/2026\n"
                    + "BULAN " + bulan.toUpperCase() + " " + tahun,
                    new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)
            );
            PdfPCell cellTitle = new PdfPCell(title);
            cellTitle.setBorder(Rectangle.NO_BORDER);
            cellTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTitle.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.addCell(cellTitle);

// 3. KOLOM KANAN DIKOSONGI
            header.addCell(makeCell("", Element.ALIGN_CENTER, Rectangle.NO_BORDER));

            doc.add(header);

// GARIS PEMBATAS SEPERTI GAMBAR
            doc.add(new Paragraph("_____________________________________________________________\n\n"));

            
            
            
            
            // =======================================================
            Font normal = new Font(Font.FontFamily.HELVETICA, 11);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);

            TableModel model = table.getModel();
            int cols = model.getColumnCount();

            PdfPTable pdf = new PdfPTable(cols);
            pdf.setWidthPercentage(100);

            for (int c = 0; c < cols; c++) {
                PdfPCell ch = new PdfPCell(new Phrase(model.getColumnName(c), headerFont));
                ch.setHorizontalAlignment(Element.ALIGN_CENTER);
                ch.setBackgroundColor(BaseColor.LIGHT_GRAY);
                ch.setPadding(4);
                pdf.addCell(ch);
            }

            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < cols; c++) {
                    Object val = model.getValueAt(r, c);
                    pdf.addCell(new PdfPCell(new Phrase(val != null ? val.toString() : "", normal)));
                }
            }

            doc.add(pdf);
            doc.close();
            JOptionPane.showMessageDialog(null, "PDF berhasil disimpan:\n" + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error rekap semua guru: " + e.toString());
        }
    }

    private PdfPCell makeCell(String string, int ALIGN_CENTER, int NO_BORDER) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
