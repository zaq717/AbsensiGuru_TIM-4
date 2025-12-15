/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sipresdik.dao;

import sipresdik.helper.Koneksi;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.File;
import javax.swing.JTable;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.net.URL;

public class RekapPresensiDao {

    private Connection conn;

    public RekapPresensiDao() {
        conn = Koneksi.konek();
    }

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

    private String getTahunAjaran(String bulan, String tahun) {
        int bulanAngka = convertBulan(bulan);
        int tahunInt = Integer.parseInt(tahun);

        if (bulanAngka >= 7) { // Juli - Desember
            return tahunInt + "/" + (tahunInt + 1);
        } else { // Januari - Juni
            return (tahunInt - 1) + "/" + tahunInt;
        }
    }

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

    public void cetakOtomatis(String guru, String bulan, String tahun, JTable tableDetail) {
        String g = guru.trim().toUpperCase();
        if (g.contains("SEMUA")) {
            // CETAK REKAP TOTAL SEMUA GURU
            cetakRekapSemuaGuruRekapTotal(bulan, tahun);
        } else {
            // CETAK DETAIL PER GURU
            cetakRekapPerGuru(guru, bulan, tahun, tableDetail);
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
                Image logo = Image.getInstance(getClass().getResource("/Image/LOGO_Rekap.jpeg"));
                logo.scaleToFit(70, 70);

                PdfPCell cellLogo = new PdfPCell(logo);
                cellLogo.setBorder(Rectangle.NO_BORDER);
                cellLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerTable.addCell(cellLogo);
            } catch (Exception e) {
                PdfPCell empty = new PdfPCell(new Phrase(""));
                empty.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(empty);
            }

            // JUDUL SAMPING LOGO
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);

            String tahunAjaran = getTahunAjaran(bulan, tahun);
            Paragraph judul = new Paragraph();
            judul.add(new Phrase("DAFTAR HADIR GURU MI NURUL HUDA III\n", titleFont));
            judul.add(new Phrase("TAHUN AJARAN " + tahunAjaran + "\n", titleFont));
            judul.add(new Phrase("BULAN " + bulan.toUpperCase() + " " + tahun, titleFont));
            judul.setAlignment(Element.ALIGN_LEFT);
            judul.setIndentationLeft(-5f);

            PdfPCell cellJudul = new PdfPCell(judul);
            cellJudul.setBorder(Rectangle.NO_BORDER);
            cellJudul.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellJudul.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellJudul.setPaddingLeft(-97f);
            headerTable.addCell(cellJudul);

            doc.add(headerTable);

            // ===== Garis bawah header =====
            Paragraph garis = new Paragraph("______________________________________________________________________________");
            garis.setSpacingBefore(-5f); // Dikurangi agar garis lebih dekat dengan tulisan di atasnya
            garis.setSpacingAfter(5f); // Dikurangi agar garis lebih dekat dengan tabel
            doc.add(garis);

            // ===========================================================
            Font normal = new Font(Font.FontFamily.HELVETICA, 11);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);

            // NAMA GURU
            Paragraph namaGuruParagraph = new Paragraph("Nama Guru: " + guru + "\n", normal);
            namaGuruParagraph.setSpacingAfter(5f); // Jarak sebelum tabel
            doc.add(namaGuruParagraph);

            // Tabel dengan 4 kolom: TANGGAL, JAM MASUK, JAM PULANG, STATUS
            PdfPTable pdfTable = new PdfPTable(4);
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{15, 25, 25, 25}); // Proporsi kolom sama rata

            pdfTable.setSpacingBefore(0f);// Mendekatkan tabel ke garis

            // Header tabel
            String[] headers = {"Tanggal", "Jam Masuk", "Jam Pulang", "Status"};
            for (String h : headers) {
                PdfPCell c = new PdfPCell(new Phrase(h, headerFont));
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                c.setVerticalAlignment(Element.ALIGN_MIDDLE);
                c.setPadding(5);
                c.setBackgroundColor(BaseColor.LIGHT_GRAY);
                pdfTable.addCell(c);
            }

            int lengkap = 0, tidak = 0;

            // Data tabel
            TableModel model = table.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                // Kolom: 0=Tanggal, 1=Nama Guru, 2=Jam Masuk, 3=Jam Pulang, 4=Status
                String tgl = model.getValueAt(i, 0) != null ? model.getValueAt(i, 0).toString() : "";
                String jm = model.getValueAt(i, 2) != null ? model.getValueAt(i, 2).toString() : "";
                String jp = model.getValueAt(i, 3) != null ? model.getValueAt(i, 3).toString() : "";
                String st = model.getValueAt(i, 4) != null ? model.getValueAt(i, 4).toString() : "";

                // Tanggal - CENTER
                PdfPCell cellTgl = new PdfPCell(new Phrase(tgl, normal));
                cellTgl.setPadding(5);
                cellTgl.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellTgl.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfTable.addCell(cellTgl);

                // Jam Masuk - CENTER
                PdfPCell cellJM = new PdfPCell(new Phrase(jm, normal));
                cellJM.setPadding(5);
                cellJM.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellJM.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfTable.addCell(cellJM);

                // Jam Pulang - CENTER
                PdfPCell cellJP = new PdfPCell(new Phrase(jp, normal));
                cellJP.setPadding(5);
                cellJP.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellJP.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfTable.addCell(cellJP);

                // Status - CENTER
                PdfPCell cellSt = new PdfPCell(new Phrase(st, normal));
                cellSt.setPadding(5);
                cellSt.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellSt.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfTable.addCell(cellSt);

                // Hitung status
                if (st.equalsIgnoreCase("Hadir Lengkap")) {
                    lengkap++;
                } else if (st.equalsIgnoreCase("Hadir Tidak Lengkap")) {
                    tidak++;
                }
            }

            doc.add(pdfTable);

            // Ringkasan
            doc.add(new Paragraph("\nJumlah Hadir Lengkap: " + lengkap, normal));
            doc.add(new Paragraph("Jumlah Hadir Tidak Lengkap: " + tidak, normal));

            doc.close();
            JOptionPane.showMessageDialog(null, "PDF berhasil disimpan:\n" + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error PDF Per Guru: " + e.toString());
        }
    }

    public void cetakRekapSemuaGuruRekapTotal(String bulan, String tahun) {
        try {

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Simpan Rekap Semua Guru");
            chooser.setSelectedFile(
                    new File("Rekap_Semua_Guru_" + bulan + "_" + tahun + ".pdf")
            );

            if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            String fileName = chooser.getSelectedFile().getAbsolutePath();
            if (!fileName.endsWith(".pdf")) {
                fileName += ".pdf";
            }

            Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(doc, new FileOutputStream(fileName));
            doc.open();
            //header
            PdfPTable header = new PdfPTable(3);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{20, 60, 20});
            try {
                Image logo = Image.getInstance(getClass().getResource("/Image/LOGO_Rekap.jpeg"));
                logo.scaleToFit(70, 70);
                PdfPCell cellLogo = new PdfPCell(logo);
                cellLogo.setBorder(Rectangle.NO_BORDER);
                header.addCell(cellLogo);
            } catch (Exception e) {
                header.addCell(new PdfPCell(new Phrase("")));
            }
            String tahunAjaran = getTahunAjaran(bulan, tahun);
            Paragraph title = new Paragraph(
                    "DAFTAR HADIR GURU MI NURUL HUDA III\n"
                    + "TAHUN AJARAN " + tahunAjaran + "\n"
                    + "BULAN " + bulan.toUpperCase() + " " + tahun,
                    new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)
            );
            PdfPCell titleCell = new PdfPCell(title);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.addCell(titleCell);
            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setBorder(Rectangle.NO_BORDER);
            header.addCell(emptyCell);
            doc.add(header);
            doc.add(new Paragraph("___________________________________________________________________________________________________________________\n\n"));
            DefaultTableModel model = getRekapTotal(bulan, tahun);
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 10, 10, 10});
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 11);
            String[] headers = {
                "NO",
                "NAMA GURU",
                "JUMLAH HADIR LENGKAP",
                "JUMLAH HADIR TIDAK LENGKAP"
            };

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(6);
                table.addCell(cell);
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                table.addCell(makeCell(model.getValueAt(i, 0).toString(), Element.ALIGN_CENTER));
                table.addCell(makeCell(model.getValueAt(i, 1).toString(), Element.ALIGN_LEFT));
                table.addCell(makeCell(model.getValueAt(i, 2).toString(), Element.ALIGN_CENTER));
                table.addCell(makeCell(model.getValueAt(i, 3).toString(), Element.ALIGN_CENTER));
            }

            doc.add(table);
            doc.close();
            JOptionPane.showMessageDialog(null, "PDF Rekap Semua Guru berhasil dibuat");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private PdfPCell makeCell(String text, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(align);
        cell.setPadding(5);
        return cell;
    }

}
