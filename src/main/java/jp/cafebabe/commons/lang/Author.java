package jp.cafebabe.commons.lang;

import java.io.Serializable;

/**
 * <p>
 * This class represents the author information of byte code processor.
 * </p>
 *
 * @author Haruaki TAMADA
 */
public class Author implements Serializable{
    private static final long serialVersionUID = 23487892348723054L;

    private String name;
    private String email;
    private Organization organization = Organization.UNKNOWN;

    public Author(){
    }

    public Author(String name, String email){
        setName(name);
        setEmail(email);
    }

    public Author(String name, String email, Organization org){
        this(name, email);
        this.organization = org;
    }

    public void setOrganization(Organization org){
        this.organization = org;
    }

    public Organization getOrganization(){
        return organization;
    }

    public void setName(String name){
        if(name == null){
            throw new NullPointerException();
        }
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    @Override
    public String toString(){
        return getInformation();
    }

    public String getInformation(){
        StringBuffer sb = new StringBuffer(getName());
        // sb.append(" <").append(getEmail()).append(">");

        if(getOrganization() != null && !getOrganization().equals(Organization.UNKNOWN)){
            sb.append(" [").append(organization).append("]");
        }

        return new String(sb);
    }
}
