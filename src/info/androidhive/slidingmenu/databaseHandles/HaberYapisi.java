package info.androidhive.slidingmenu.databaseHandles;


public class HaberYapisi {
	
	private String link;
	private String baslik;
	private String kategori;
	private String bildirim_zamani;
	
	public HaberYapisi() {
	}
	
	public HaberYapisi(String _link, String _baslik, String _kategori, String _bildirimZamani) {
		super();
		this.link=_link;
		this.baslik=_baslik;
		this.kategori=_kategori;
		this.bildirim_zamani=_bildirimZamani;
	}

	public String getlink() {
		return link;
	}

	public void setlink(String link) {
		this.link = link;
	}

	public String getbaslik() {
		return baslik;
	}

	public void setbaslik(String baslik) {
		this.baslik = baslik;
	}

	public String getkategori() {
		return kategori;
	}

	public void setkategori(String kategori) {
		this.kategori = kategori;
	}

	public String getbildirim_zamani() {
		return bildirim_zamani;
	}

	public void setbildirim_zamani(String bildirim_zamani) {
		this.bildirim_zamani = bildirim_zamani;
	}


}
