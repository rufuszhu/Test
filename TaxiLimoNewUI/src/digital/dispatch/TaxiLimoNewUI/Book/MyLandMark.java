package digital.dispatch.TaxiLimoNewUI.Book;

import java.io.InputStream;

import android.net.Uri;

public class MyLandMark {
	private String name, address;
	private String type;
	private long id;
	public MyLandMark(String type, String name, String addr, long id){
		this.type=type;
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
