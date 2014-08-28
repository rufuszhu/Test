package com.digital.dispatch.TaxiLimoSoap.responses;

public class AttributeItem {
	private String id;
	private String name;
	private String appIcon;
	
	public AttributeItem() {
		setId("");
		setName("");
		setAppIcon("");
	}
	
	public AttributeItem(String id, String name, String appIcon) {
		this.setId(id);
		this.setName(name);
		this.setAppIcon(appIcon);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

	}
	public String getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}
}
