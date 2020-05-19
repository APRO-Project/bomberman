
package com.cyberbot.bomberman.core.models.tiles.loader.tileset;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}image"/>
 *         &lt;element ref="{}tile" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="columns" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="tilecount" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="tiledversion" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="tileheight" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="tilewidth" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "image",
    "tile"
})
@XmlRootElement(name = "tileset")
public class Tileset {

    @XmlElement(required = true)
    protected Image image;
    @XmlElement(required = true)
    protected List<Tile> tile;
    @XmlAttribute(name = "columns", required = true)
    protected BigInteger columns;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(name = "tilecount", required = true)
    protected BigInteger tilecount;
    @XmlAttribute(name = "tiledversion", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String tiledversion;
    @XmlAttribute(name = "tileheight", required = true)
    protected BigInteger tileheight;
    @XmlAttribute(name = "tilewidth", required = true)
    protected BigInteger tilewidth;
    @XmlAttribute(name = "version", required = true)
    protected BigDecimal version;

    /**
     * Gets the value of the image property.
     * 
     * @return
     *     possible object is
     *     {@link Image }
     *     
     */
    public Image getImage() {
        return image;
    }

    /**
     * Sets the value of the image property.
     * 
     * @param value
     *     allowed object is
     *     {@link Image }
     *     
     */
    public void setImage(Image value) {
        this.image = value;
    }

    /**
     * Gets the value of the tile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tile }
     * 
     * 
     */
    public List<Tile> getTile() {
        if (tile == null) {
            tile = new ArrayList<Tile>();
        }
        return this.tile;
    }

    /**
     * Gets the value of the columns property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getColumns() {
        return columns;
    }

    /**
     * Sets the value of the columns property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setColumns(BigInteger value) {
        this.columns = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the tilecount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTilecount() {
        return tilecount;
    }

    /**
     * Sets the value of the tilecount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTilecount(BigInteger value) {
        this.tilecount = value;
    }

    /**
     * Gets the value of the tiledversion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTiledversion() {
        return tiledversion;
    }

    /**
     * Sets the value of the tiledversion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTiledversion(String value) {
        this.tiledversion = value;
    }

    /**
     * Gets the value of the tileheight property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTileheight() {
        return tileheight;
    }

    /**
     * Sets the value of the tileheight property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTileheight(BigInteger value) {
        this.tileheight = value;
    }

    /**
     * Gets the value of the tilewidth property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTilewidth() {
        return tilewidth;
    }

    /**
     * Sets the value of the tilewidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTilewidth(BigInteger value) {
        this.tilewidth = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVersion(BigDecimal value) {
        this.version = value;
    }

}
