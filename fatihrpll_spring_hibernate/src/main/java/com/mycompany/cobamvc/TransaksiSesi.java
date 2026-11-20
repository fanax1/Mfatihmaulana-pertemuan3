package com.mycompany.cobamvc;

import java.sql.Timestamp;
import javax.persistence.*;

@Entity
@Table(name = "transaksi_sesi")
public class TransaksiSesi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "kode_sesi")
    private String kodeSesi;

    @Column(name = "email")
    private String email;
    
    @Column(name = "nama_template")
    private String namaTemplate;

    @Column(name = "jumlah_cetakan")
    private int jumlahCetakan;

    @Column(name = "total_biaya")
    private int totalBiaya;

    @Column(name = "status_pembayaran")
    private String statusPembayaran;

    @Column(name = "waktu_sesi")
    private Timestamp waktuSesi;

    public TransaksiSesi() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getKodeSesi() { return kodeSesi; }
    public void setKodeSesi(String kodeSesi) { this.kodeSesi = kodeSesi; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getNamaTemplate() { return namaTemplate; }
    public void setNamaTemplate(String namaTemplate) { this.namaTemplate = namaTemplate; }

    public int getJumlahCetakan() { return jumlahCetakan; }
    public void setJumlahCetakan(int jumlahCetakan) { this.jumlahCetakan = jumlahCetakan; }

    public int getTotalBiaya() { return totalBiaya; }
    public void setTotalBiaya(int totalBiaya) { this.totalBiaya = totalBiaya; }

    public String getStatusPembayaran() { return statusPembayaran; }
    public void setStatusPembayaran(String statusPembayaran) { this.statusPembayaran = statusPembayaran; }

    public Timestamp getWaktuSesi() { return waktuSesi; }
    public void setWaktuSesi(Timestamp waktuSesi) { this.waktuSesi = waktuSesi; }
}