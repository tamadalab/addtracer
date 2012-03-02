package jp.cafebabe.commons.lang;

/**
 * This interface is implemented to data object contains properties.
 *
 * @author Haruaki TAMADA
 */
public interface PropertyEditable{
    /**
     * returns the property count of this object.
     */
    public int getPropertyCount();

    /**
     * set property name and its value.
     */
    public void setProperty(String name, String value);

    /**
     * returns the property value mapped by given name.
     */
    public String getProperty(String name);

    /**
     * returns the list of property names.
     */
    public String[] getPropertyNames();
}
