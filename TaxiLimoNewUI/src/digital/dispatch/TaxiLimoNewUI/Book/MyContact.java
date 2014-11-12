package digital.dispatch.TaxiLimoNewUI.Book;

import java.io.InputStream;

import android.net.Uri;

public class MyContact {
	private String name, address;
	private Uri img_URI;
	private long id;
	public MyContact(Uri imgURI, String name, String addr, long id){
		this.img_URI=imgURI;
		this.name=name;
		this.address=addr;
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public Uri getImg_URI() {
		return img_URI;
	}

	public void setImg_URI(Uri img_URI) {
		this.img_URI = img_URI;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
