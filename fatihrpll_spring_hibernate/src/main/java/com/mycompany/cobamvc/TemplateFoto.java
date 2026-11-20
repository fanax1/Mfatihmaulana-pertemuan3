package com.mycompany.cobamvc;

import javax.persistence.*;

@Entity
@Table(name = "template_foto")
public class TemplateFoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "kode_template")
    private String kodeTemplate;

    @Column(name = "nama_template")
    private String namaTemplate;

    @Column(name = "harga_per_cetak")
    private int hargaPerCetak;

    @Column(name = "stok_kertas")
    private int stokKertas;

    public TemplateFoto() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getKodeTemplate() { return kodeTemplate; }
    public void setKodeTemplate(String kodeTemplate) { this.kodeTemplate = kodeTemplate; }

    public String getNamaTemplate() { return namaTemplate; }
    public void setNamaTemplate(String namaTemplate) { this.namaTemplate = namaTemplate; }

    public int getHargaPerCetak() { return hargaPerCetak; }
    public void setHargaPerCetak(int hargaPerCetak) { this.hargaPerCetak = hargaPerCetak; }

    public int getStokKertas() { return stokKertas; }
    public void setStokKertas(int stokKertas) { this.stokKertas = stokKertas; }
}