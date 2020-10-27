
package dtos;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dtos package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Categories_QNAME = new QName("http://xml.netbeans.org/schema/categories", "categories");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dtos
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ListCategories }
     * 
     */
    public ListCategories createListCategories() {
        return new ListCategories();
    }

    /**
     * Create an instance of {@link Category }
     * 
     */
    public Category createCategory() {
        return new Category();
    }

    /**
     * Create an instance of {@link SubCategory }
     * 
     */
    public SubCategory createSubCategory() {
        return new SubCategory();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListCategories }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://xml.netbeans.org/schema/categories", name = "categories")
    public JAXBElement<ListCategories> createCategories(ListCategories value) {
        return new JAXBElement<ListCategories>(_Categories_QNAME, ListCategories.class, null, value);
    }

}
