package jp.cafebabe.commons.lang;

import java.io.Serializable;

/**
 * @author Haruaki TAMADA
 */
public class Description implements NameIdentifiable, Serializable{
    private static final long serialVersionUID = 1234554645566574L;

    private String name;
    private String description;

    public Description(){
    }

    public Description(String name, String description){
        setName(name);
        setDescription(description);
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public void setName(String name){
        if(name == null){
            throw new NullPointerException("name is NULL");
        }
        this.name = name;
    }

    @Override
    public String getDescription(){
        return description;
    }

    @Override
    public void setDescription(String description){
        this.description = description;
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append(getName());
        if(getDescription() != null){
            sb.append(": ").append(getDescription());
        }

        return new String(sb);
    }
}
