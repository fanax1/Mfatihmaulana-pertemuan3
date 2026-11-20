package com.mycompany.cobamvc;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class MenuUtama extends JFrame {

    // Komponen Data Template
    private final JTextField txtKodeTpl = new JTextField();
    private final JTextField txtNamaTpl = new JTextField();
    private final JTextField txtHargaTpl = new JTextField();
    private final JTextField txtStokKertas = new JTextField();
    private final DefaultTableModel modelTemplate;
    private final JTable tableTemplate;
    private int selectedTemplateId = -1;

    // Komponen Transaksi Sesi
    private final JTextField txtKodeSesi = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JComboBox<String> cmbTemplate = new JComboBox<>();
    private final JTextField txtJumlahCetak = new JTextField("1");
    private final JComboBox<String> cmbStatusBayar = new JComboBox<>(new String[]{"Sudah Dibayar", "Belum Dibayar"});
    private final DefaultTableModel modelTransaksi;
    private final JTable tableTransaksi;
    private int selectedTransaksiId = -1;

    // Komponen Laporan (Model Terpisah 6 Kolom)
    private final DefaultTableModel modelLaporan;
    private final JTable tableLaporan;

    // Label Dashboard & Laporan
    private final JLabel lblStatSesi = new JLabel("Total Sesi: 0");
    private final JLabel lblStatPendapatan = new JLabel("Total Pendapatan (Sudah Dibayar): Rp 0");

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    public MenuUtama() {
        setTitle("Admin Self-Service Photobooth");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1020, 580);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ================= SIDEBAR KIRI =================
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(33, 37, 41));
        sidebar.setPreferredSize(new Dimension(210, 580));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));

        JLabel lblBrand = new JLabel("PHOTOBOOTH ADMIN");
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblBrand);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton btnDashboard = createSidebarButton("Dashboard");
        JButton btnTemplate = createSidebarButton("Data Template");
        JButton btnTransaksi = createSidebarButton("Transaksi");
        JButton btnLaporan = createSidebarButton("Laporan");
        JButton btnLogout = createSidebarButton("Logout");

        sidebar.add(btnDashboard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
        sidebar.add(btnTemplate);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
        sidebar.add(btnTransaksi);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
        sidebar.add(btnLaporan);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // ================= 1. PANEL DATA TEMPLATE =================
        JPanel panelTemplate = new JPanel(new BorderLayout(10, 15));
        panelTemplate.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelTemplate.setBackground(new Color(240, 240, 240));

        JPanel topTpl = new JPanel(new BorderLayout(10, 10));
        topTpl.setOpaque(false);

        JPanel gridTpl = new JPanel(new GridLayout(2, 4, 12, 4));
        gridTpl.setOpaque(false);
        gridTpl.add(new JLabel("Kode Template"));
        gridTpl.add(new JLabel("Nama Template Bingkai"));
        gridTpl.add(new JLabel("Harga / Cetak (Rp)"));
        gridTpl.add(new JLabel("Stok Kertas"));
        gridTpl.add(txtKodeTpl);
        gridTpl.add(txtNamaTpl);
        gridTpl.add(txtHargaTpl);
        gridTpl.add(txtStokKertas);

        JPanel btnRowTpl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        btnRowTpl.setOpaque(false);
        JButton btnTambahTpl = new JButton("Tambah");
        JButton btnUbahTpl = new JButton("Ubah");
        JButton btnHapusTpl = new JButton("Hapus");
        JButton btnBersihTpl = new JButton("Bersihkan");
        btnRowTpl.add(btnTambahTpl);
        btnRowTpl.add(btnUbahTpl);
        btnRowTpl.add(btnHapusTpl);
        btnRowTpl.add(btnBersihTpl);

        topTpl.add(gridTpl, BorderLayout.NORTH);
        topTpl.add(btnRowTpl, BorderLayout.CENTER);
        panelTemplate.add(topTpl, BorderLayout.NORTH);

        String[] colTpl = {"ID", "Kode", "Nama Template", "Harga", "Stok"};
        modelTemplate = new DefaultTableModel(colTpl, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableTemplate = new JTable(modelTemplate);
        tableTemplate.setRowHeight(24);
        panelTemplate.add(new JScrollPane(tableTemplate), BorderLayout.CENTER);

        // ================= 2. PANEL TRANSAKSI SESI =================
        JPanel panelTransaksi = new JPanel(new BorderLayout(10, 15));
        panelTransaksi.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelTransaksi.setBackground(new Color(240, 240, 240));

        JPanel topTrx = new JPanel(new BorderLayout(10, 10));
        topTrx.setOpaque(false);

        JPanel gridTrx = new JPanel(new GridLayout(2, 5, 10, 4));
        gridTrx.setOpaque(false);
        gridTrx.add(new JLabel("Kode Sesi"));
        gridTrx.add(new JLabel("Email Pelanggan"));
        gridTrx.add(new JLabel("Pilih Template"));
        gridTrx.add(new JLabel("Jml Cetakan (1-10)"));
        gridTrx.add(new JLabel("Status Pembayaran"));
        gridTrx.add(txtKodeSesi);
        gridTrx.add(txtEmail);
        gridTrx.add(cmbTemplate);
        gridTrx.add(txtJumlahCetak);
        gridTrx.add(cmbStatusBayar);

        JPanel btnRowTrx = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        btnRowTrx.setOpaque(false);
        JButton btnSimpanTrx = new JButton("Catat Transaksi");
        JButton btnUpdateStatus = new JButton("Ubah Status Bayar");
        JButton btnHapusTrx = new JButton("Hapus");
        JButton btnResetTrx = new JButton("Sesi Baru");
        btnRowTrx.add(btnSimpanTrx);
        btnRowTrx.add(btnUpdateStatus);
        btnRowTrx.add(btnHapusTrx);
        btnRowTrx.add(btnResetTrx);

        topTrx.add(gridTrx, BorderLayout.NORTH);
        topTrx.add(btnRowTrx, BorderLayout.CENTER);
        panelTransaksi.add(topTrx, BorderLayout.NORTH);

        String[] colTrx = {"ID", "Kode Sesi", "Template Bingkai", "Jml Cetakan", "Total Biaya (Rp)", "Status Pembayaran"};
        modelTransaksi = new DefaultTableModel(colTrx, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableTransaksi = new JTable(modelTransaksi);
        tableTransaksi.setRowHeight(24);
        panelTransaksi.add(new JScrollPane(tableTransaksi), BorderLayout.CENTER);

        // ================= 3. PANEL DASHBOARD =================
        JPanel panelDashboard = new JPanel(new GridBagLayout());
        panelDashboard.setBackground(new Color(245, 245, 245));
        JPanel boxInfo = new JPanel(new GridLayout(4, 1, 10, 10));
        boxInfo.setOpaque(false);
        JLabel lblJudulDash = new JLabel("DASHBOARD ADMINISTRATOR - SELF-SERVICE PHOTOBOOTH");
        lblJudulDash.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblStatSesi.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblStatPendapatan.setFont(new Font("SansSerif", Font.BOLD, 15));
        boxInfo.add(lblJudulDash);
        boxInfo.add(new JLabel("Status Backend: Spring Core + Hibernate ORM + MySQL Aktif"));
        boxInfo.add(lblStatSesi);
        boxInfo.add(lblStatPendapatan);
        panelDashboard.add(boxInfo);

        // ================= 4. PANEL LAPORAN (6 KOLOM + BUTTON CETAK) =================
        JPanel panelLaporan = new JPanel(new BorderLayout(10, 15));
        panelLaporan.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelLaporan.setBackground(new Color(240, 240, 240));

        JPanel topLap = new JPanel(new BorderLayout());
        topLap.setOpaque(false);
        JLabel lblLap = new JLabel("RIWAYAT & LAPORAN TRANSAKSI SESI PHOTOBOOTH", SwingConstants.LEFT);
        lblLap.setFont(new Font("SansSerif", Font.BOLD, 15));

        JButton btnCetakJasper = new JButton("Cetak Laporan");
        btnCetakJasper.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCetakJasper.setBackground(new Color(44, 62, 80));
        btnCetakJasper.setForeground(Color.WHITE);
        btnCetakJasper.setFocusPainted(false);

        topLap.add(lblLap, BorderLayout.WEST);
        topLap.add(btnCetakJasper, BorderLayout.EAST);
        panelLaporan.add(topLap, BorderLayout.NORTH);

        String[] colLap = {"Kode Sesi", "Email", "Template Bingkai", "Jml Cetakan", "Waktu Sesi", "Total Biaya (Rp)"};
        modelLaporan = new DefaultTableModel(colLap, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableLaporan = new JTable(modelLaporan);
        tableLaporan.setRowHeight(25);

        tableLaporan.getColumnModel().getColumn(0).setPreferredWidth(95);  // Kode Sesi
        tableLaporan.getColumnModel().getColumn(1).setPreferredWidth(155); // Email
        tableLaporan.getColumnModel().getColumn(2).setPreferredWidth(160); // Template Bingkai
        tableLaporan.getColumnModel().getColumn(3).setPreferredWidth(85);  // Jml Cetakan
        tableLaporan.getColumnModel().getColumn(4).setPreferredWidth(120); // Waktu Sesi
        tableLaporan.getColumnModel().getColumn(5).setPreferredWidth(110); // Total Biaya (Rp)

        panelLaporan.add(new JScrollPane(tableLaporan), BorderLayout.CENTER);

        contentPanel.add(panelTemplate, "TEMPLATE");
        contentPanel.add(panelTransaksi, "TRANSAKSI");
        contentPanel.add(panelDashboard, "DASHBOARD");
        contentPanel.add(panelLaporan, "LAPORAN");
        add(contentPanel, BorderLayout.CENTER);

        // ================= EVENT NAVIGATION =================
        btnDashboard.addActionListener(e -> {
            updateStatistik();
            cardLayout.show(contentPanel, "DASHBOARD");
        });
        btnTemplate.addActionListener(e -> {
            loadDataTemplate();
            cardLayout.show(contentPanel, "TEMPLATE");
        });
        btnTransaksi.addActionListener(e -> {
            loadDataTemplate();
            loadDataTransaksi();
            generateKodeSesi();
            cardLayout.show(contentPanel, "TRANSAKSI");
        });
        btnLaporan.addActionListener(e -> {
            loadDataLaporan();
            cardLayout.show(contentPanel, "LAPORAN");
        });
        btnLogout.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this, "Keluar dari halaman admin?", "Logout", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                new LoginForm().setVisible(true);
                this.dispose();
            }
        });

        // ================= EVENT CRUD TEMPLATE =================
        tableTemplate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = tableTemplate.getSelectedRow();
                if (r != -1) {
                    selectedTemplateId = Integer.parseInt(modelTemplate.getValueAt(r, 0).toString());
                    txtKodeTpl.setText(modelTemplate.getValueAt(r, 1).toString());
                    txtNamaTpl.setText(modelTemplate.getValueAt(r, 2).toString());
                    txtHargaTpl.setText(modelTemplate.getValueAt(r, 3).toString());
                    txtStokKertas.setText(modelTemplate.getValueAt(r, 4).toString());
                }
            }
        });

        btnTambahTpl.addActionListener(e -> tambahTemplate());
        btnUbahTpl.addActionListener(e -> ubahTemplate());
        btnHapusTpl.addActionListener(e -> hapusTemplate());
        btnBersihTpl.addActionListener(e -> bersihTemplate());

        // ================= EVENT CRUD TRANSAKSI =================
        tableTransaksi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = tableTransaksi.getSelectedRow();
                if (r != -1) {
                    selectedTransaksiId = Integer.parseInt(modelTransaksi.getValueAt(r, 0).toString());
                    txtKodeSesi.setText(modelTransaksi.getValueAt(r, 1).toString());
                    cmbStatusBayar.setSelectedItem(modelTransaksi.getValueAt(r, 5).toString());
                }
            }
        });

        btnSimpanTrx.addActionListener(e -> simpanTransaksi());
        btnUpdateStatus.addActionListener(e -> ubahStatusTransaksi());
        btnHapusTrx.addActionListener(e -> hapusTransaksi());
        btnResetTrx.addActionListener(e -> generateKodeSesi());
        btnCetakJasper.addActionListener(e -> cetakLaporanJasper());

        // Muat data awal
        loadDataTemplate();
        loadDataTransaksi();
        loadDataLaporan();
        generateKodeSesi();
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 48));
        btn.setPreferredSize(new Dimension(180, 48));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        return btn;
    }

    private void generateKodeSesi() {
        selectedTransaksiId = -1;
        txtKodeSesi.setText("SESI-" + (System.currentTimeMillis() % 100000));
        txtEmail.setText("");
        txtJumlahCetak.setText("1");
    }

    private void loadDataTemplate() {
        modelTemplate.setRowCount(0);
        cmbTemplate.removeAllItems();
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            List<TemplateFoto> list = session.createQuery("FROM TemplateFoto", TemplateFoto.class).list();
            for (TemplateFoto t : list) {
                modelTemplate.addRow(new Object[]{
                    t.getId(), t.getKodeTemplate(), t.getNamaTemplate(), t.getHargaPerCetak(), t.getStokKertas()
                });
                cmbTemplate.addItem(t.getNamaTemplate() + " - Rp " + t.getHargaPerCetak());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat template: " + ex.getMessage());
        }
    }

    private void tambahTemplate() {
        if (txtKodeTpl.getText().trim().isEmpty() || txtNamaTpl.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode dan Nama Template wajib diisi!");
            return;
        }
        Transaction tx = null;
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            TemplateFoto t = new TemplateFoto();
            t.setKodeTemplate(txtKodeTpl.getText().trim());
            t.setNamaTemplate(txtNamaTpl.getText().trim());
            t.setHargaPerCetak(Integer.parseInt(txtHargaTpl.getText().trim()));
            t.setStokKertas(Integer.parseInt(txtStokKertas.getText().trim()));
            session.save(t);
            tx.commit();
            JOptionPane.showMessageDialog(this, "Template bingkai berhasil ditambahkan!");
            bersihTemplate();
            loadDataTemplate();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            JOptionPane.showMessageDialog(this, "Input tidak valid: " + ex.getMessage());
        }
    }

    private void ubahTemplate() {
        if (selectedTemplateId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris template di tabel terlebih dahulu!");
            return;
        }
        Transaction tx = null;
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            TemplateFoto t = session.get(TemplateFoto.class, selectedTemplateId);
            if (t != null) {
                t.setKodeTemplate(txtKodeTpl.getText().trim());
                t.setNamaTemplate(txtNamaTpl.getText().trim());
                t.setHargaPerCetak(Integer.parseInt(txtHargaTpl.getText().trim()));
                t.setStokKertas(Integer.parseInt(txtStokKertas.getText().trim()));
                session.update(t);
            }
            tx.commit();
            JOptionPane.showMessageDialog(this, "Template berhasil diperbarui!");
            bersihTemplate();
            loadDataTemplate();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            JOptionPane.showMessageDialog(this, "Gagal ubah data: " + ex.getMessage());
        }
    }

    private void hapusTemplate() {
        if (selectedTemplateId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris template yang akan dihapus!");
            return;
        }
        Transaction tx = null;
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            TemplateFoto t = session.get(TemplateFoto.class, selectedTemplateId);
            if (t != null) session.delete(t);
            tx.commit();
            JOptionPane.showMessageDialog(this, "Template berhasil dihapus!");
            bersihTemplate();
            loadDataTemplate();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            JOptionPane.showMessageDialog(this, "Gagal hapus data: " + ex.getMessage());
        }
    }

    private void bersihTemplate() {
        selectedTemplateId = -1;
        txtKodeTpl.setText("");
        txtNamaTpl.setText("");
        txtHargaTpl.setText("");
        txtStokKertas.setText("");
        tableTemplate.clearSelection();
    }

    private void loadDataTransaksi() {
        modelTransaksi.setRowCount(0);
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            List<TransaksiSesi> list = session.createQuery("FROM TransaksiSesi", TransaksiSesi.class).list();
            for (TransaksiSesi tr : list) {
                modelTransaksi.addRow(new Object[]{
                    tr.getId(), tr.getKodeSesi(), tr.getNamaTemplate(),
                    tr.getJumlahCetakan(), tr.getTotalBiaya(), tr.getStatusPembayaran()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat transaksi: " + ex.getMessage());
        }
    }

    private void loadDataLaporan() {
        modelLaporan.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            List<TransaksiSesi> list = session.createQuery("FROM TransaksiSesi ORDER BY id DESC", TransaksiSesi.class).list();
            for (TransaksiSesi tr : list) {
                String emailTampil = (tr.getEmail() != null && !tr.getEmail().isEmpty()) ? tr.getEmail() : "-";
                String waktuTampil = (tr.getWaktuSesi() != null) ? sdf.format(tr.getWaktuSesi()) : "-";

                modelLaporan.addRow(new Object[]{
                    tr.getKodeSesi(),
                    emailTampil,
                    tr.getNamaTemplate(),
                    tr.getJumlahCetakan(),
                    waktuTampil,
                    tr.getTotalBiaya()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat laporan: " + ex.getMessage());
        }
    }

    private void simpanTransaksi() {
        if (cmbTemplate.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Belum ada template foto tersedia!");
            return;
        }
        try {
            int jml = Integer.parseInt(txtJumlahCetak.getText().trim());
            if (jml < 1 || jml > 10) {
                JOptionPane.showMessageDialog(this, "Jumlah cetakan harus antara 1 sampai 10 lembar!");
                return;
            }
            String pilihan = cmbTemplate.getSelectedItem().toString();
            String[] bagian = pilihan.split(" - Rp ");
            String namaTpl = bagian[0];
            int hargaSatuan = Integer.parseInt(bagian[1]);
            int totalBiaya = hargaSatuan * jml;

            Transaction tx = null;
            try (Session session = DBConnection.getSessionFactory().openSession()) {
                tx = session.beginTransaction();
                TransaksiSesi tr = new TransaksiSesi();
                tr.setKodeSesi(txtKodeSesi.getText().trim());
                tr.setEmail(txtEmail.getText().trim());
                tr.setNamaTemplate(namaTpl);
                tr.setJumlahCetakan(jml);
                tr.setTotalBiaya(totalBiaya);
                tr.setStatusPembayaran(cmbStatusBayar.getSelectedItem().toString());
                tr.setWaktuSesi(new Timestamp(System.currentTimeMillis()));
                session.save(tr);
                tx.commit();
                JOptionPane.showMessageDialog(this, "Transaksi Sesi Berhasil!\nTotal Biaya: Rp " + totalBiaya);
                loadDataTransaksi();
                loadDataLaporan();
                generateKodeSesi();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah cetakan harus berupa angka!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi: " + ex.getMessage());
        }
    }

    private void ubahStatusTransaksi() {
        if (selectedTransaksiId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris transaksi di tabel terlebih dahulu!");
            return;
        }
        Transaction tx = null;
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            TransaksiSesi tr = session.get(TransaksiSesi.class, selectedTransaksiId);
            if (tr != null) {
                tr.setStatusPembayaran(cmbStatusBayar.getSelectedItem().toString());
                session.update(tr);
            }
            tx.commit();
            JOptionPane.showMessageDialog(this, "Status pembayaran berhasil diperbarui!");
            loadDataTransaksi();
            loadDataLaporan();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            JOptionPane.showMessageDialog(this, "Gagal memperbarui status: " + ex.getMessage());
        }
    }

    private void hapusTransaksi() {
        if (selectedTransaksiId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris transaksi yang akan dihapus!");
            return;
        }
        Transaction tx = null;
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            TransaksiSesi tr = session.get(TransaksiSesi.class, selectedTransaksiId);
            if (tr != null) session.delete(tr);
            tx.commit();
            JOptionPane.showMessageDialog(this, "Riwayat transaksi dihapus!");
            loadDataTransaksi();
            loadDataLaporan();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            JOptionPane.showMessageDialog(this, "Gagal menghapus transaksi: " + ex.getMessage());
        }
    }

    private void updateStatistik() {
        loadDataTransaksi();
        int totalSesi = modelTransaksi.getRowCount();
        int totalPendapatan = 0;
        for (int i = 0; i < totalSesi; i++) {
            String status = modelTransaksi.getValueAt(i, 5).toString();
            if ("Sudah Dibayar".equalsIgnoreCase(status)) {
                totalPendapatan += Integer.parseInt(modelTransaksi.getValueAt(i, 4).toString());
            }
        }
        lblStatSesi.setText("Total Sesi Photobooth Tercatat: " + totalSesi + " Sesi");
        lblStatPendapatan.setText("Total Pendapatan (Sudah Dibayar): Rp " + totalPendapatan);
    }

    private void cetakLaporanJasper() {
        try {
            // Ganti nama_database_anda dengan nama database MySQL Anda (contoh: photobooth_db atau cobamvc)
            String url = "jdbc:mysql://localhost:3306/login";
            String user = "root";
            String pass = "";

            Connection conn = DriverManager.getConnection(url, user, pass);

            InputStream reportStream = getClass().getResourceAsStream("/com/mycompany/cobamvc/LaporanPhotobooth.jrxml");
            if (reportStream == null) {
                JOptionPane.showMessageDialog(this, "File LaporanPhotobooth.jrxml tidak ditemukan di package com.mycompany.cobamvc!");
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), conn);

            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Laporan Transaksi Self-Photo Booth");
            viewer.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mencetak laporan: " + ex.getMessage());
        }
    }
}