package jp.cafebabe.commons.lang;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.Serializable;

/**
 * <p>
 * This class represents the organization information for byte code processor.
 * </p>
 *
 * @author Haruaki TAMADA
 */
public class Organization implements Serializable{
    private static final long serialVersionUID = 98743509782038725L;

    public static final Organization UNKNOWN = new Organization("unknown", null);
    private String name;
    private URL url;

    public Organization(){
    }

    public Organization(String name, URL url){
        setName(name);
        setUrl(url);
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

    public void setUrl(String url) throws MalformedURLException{
        setUrl(new URL(url));
    }

    public void setUrl(URL url){
        this.url = url;
    }

    public URL getUrl(){
        return url;
    }

    @Override
    public int hashCode(){
        return name.hashCode() + url.hashCode();
    }

    @Override
    public boolean equals(Object object){
        if(object instanceof Organization){
            Organization o = (Organization)object;

            return getName().equals(o.getName()) && checkUrl(getUrl(), o.getUrl());
        }

        return false;
    }

    @Override
    public String toString(){
        return getInformation();
    }

    public String getInformation(){
        StringBuffer sb = new StringBuffer(getName());
        if(getUrl() != null){
            sb.append(" <").append(getUrl()).append(">");
        }
        return new String(sb);
    }

    private boolean checkUrl(URL url1, URL url2){
        if(url1 == null && url2 == null){
            return true;
        }
        else if(url1 != null && url1.equals(url2)){
            return true;
        }
        return false;
    }
}
