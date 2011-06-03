
package org.openmrs.module.sdmxhdintegration;


public class SimpleCode {
	private String value;
	private String description;
	
	/**
     * @param description the description to set
     */
    public void setDescription(String description) {
	    this.description = description;
    }
	/**
     * @return the description
     */
    public String getDescription() {
	    return description;
    }
	/**
     * @param value the value to set
     */
    public void setValue(String value) {
	    this.value = value;
    }
	/**
     * @return the value
     */
    public String getValue() {
	    return value;
    }

}
