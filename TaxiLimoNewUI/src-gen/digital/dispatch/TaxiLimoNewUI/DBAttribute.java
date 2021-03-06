package digital.dispatch.TaxiLimoNewUI;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table DBATTRIBUTE.
 */
public class DBAttribute {

    private Long id;
    private String attributeId;
    private String name;
    private String iconId;

    public DBAttribute() {
    }

    public DBAttribute(Long id) {
        this.id = id;
    }

    public DBAttribute(Long id, String attributeId, String name, String iconId) {
        this.id = id;
        this.attributeId = attributeId;
        this.name = name;
        this.iconId = iconId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

}
