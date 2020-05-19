
package com.cyberbot.bomberman.core.models.tiles.loader;

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
 *         &lt;element ref="{}tileset"/>
 *         &lt;element ref="{}layer" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="height" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="infinite" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="nextlayerid" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="nextobjectid" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="orientation" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="renderorder" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="tiledversion" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="tileheight" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="tilewidth" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="width" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "tileset",
    "layer"
})
@XmlRootElement(name = "map")
public class Map {

    @XmlElement(required = true)
    protected Tileset tileset;
    @XmlElement(required = true)
    protected List<Layer> layer;
    @XmlAttribute(name = "height", required = true)
    protected BigInteger height;
    @XmlAttribute(name = "infinite", required = true)
    protected BigInteger infinite;
    @XmlAttribute(name = "nextlayerid", required = true)
    protected BigInteger nextlayerid;
    @XmlAttribute(name = "nextobjectid", required = true)
    protected BigInteger nextobjectid;
    @XmlAttribute(name = "orientation", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String orientation;
    @XmlAttribute(name = "renderorder", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String renderorder;
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
    @XmlAttribute(name = "width", required = true)
    protected BigInteger width;

    /**
     * Gets the value of the tileset property.
     * 
     * @return
     *     possible object is
     *     {@link Tileset }
     *     
     */
    public Tileset getTileset() {
        return tileset;
    }

    /**
     * Sets the value of the tileset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tileset }
     *     
     */
    public void setTileset(Tileset value) {
        this.tileset = value;
    }

    /**
     * Gets the value of the layer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the layer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLayer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Layer }
     * 
     * 
     */
    public List<Layer> getLayer() {
        if (layer == null) {
            layer = new ArrayList<Layer>();
        }
        return this.layer;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setHeight(BigInteger value) {
        this.height = value;
    }

    /**
     * Gets the value of the infinite property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getInfinite() {
        return infinite;
    }

    /**
     * Sets the value of the infinite property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setInfinite(BigInteger value) {
        this.infinite = value;
    }

    /**
     * Gets the value of the nextlayerid property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNextlayerid() {
        return nextlayerid;
    }

    /**
     * Sets the value of the nextlayerid property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNextlayerid(BigInteger value) {
        this.nextlayerid = value;
    }

    /**
     * Gets the value of the nextobjectid property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNextobjectid() {
        return nextobjectid;
    }

    /**
     * Sets the value of the nextobjectid property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNextobjectid(BigInteger value) {
        this.nextobjectid = value;
    }

    /**
     * Gets the value of the orientation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Sets the value of the orientation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrientation(String value) {
        this.orientation = value;
    }

    /**
     * Gets the value of the renderorder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRenderorder() {
        return renderorder;
    }

    /**
     * Sets the value of the renderorder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRenderorder(String value) {
        this.renderorder = value;
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

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setWidth(BigInteger value) {
        this.width = value;
    }

}
