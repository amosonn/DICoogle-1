/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.dicoogle.webservices.elements;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author samuelcampos
 */
@XmlRootElement(name="List")
public class JaxbStrList{
    @XmlElement(name="Item")
    public List<String> list;

    public JaxbStrList(){}
    
    public JaxbStrList(List<String> list){
        this.list=list;
    }
}

