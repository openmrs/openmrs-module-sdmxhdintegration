
package org.openmrs.module.sdmxhdintegration;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.reporting.report.definition.ReportDefinition;


/**
 *
 */
public class KeyFamilyMapping extends BaseOpenmrsObject {
	
	private Integer id;
	private SDMXHDMessage sdmxhdMessage;
	private String keyFamilyId;
	private Integer reportDefinitionId;

	
    /**
     * @return the sdmxhdMessage
     */
    public SDMXHDMessage getSdmxhdMessage() {
    	return sdmxhdMessage;
    }

	
    /**
     * @param sdmxhdMessage the sdmxhdMessage to set
     */
    public void setSdmxhdMessage(SDMXHDMessage sdmxhdMessage) {
    	this.sdmxhdMessage = sdmxhdMessage;
    }

	
    /**
     * @return the keyFamilyId
     */
    public String getKeyFamilyId() {
    	return keyFamilyId;
    }

	
    /**
     * @param keyFamilyId the keyFamilyId to set
     */
    public void setKeyFamilyId(String keyFamilyId) {
    	this.keyFamilyId = keyFamilyId;
    }

	
    /**
     * @return the reportDefinition
     */
    public Integer getReportDefinitionId() {
    	return reportDefinitionId;
    }

	
    /**
     * @param reportDefinition the reportDefinition to set
     */
    public void setReportDefinitionId(Integer reportDefinitionId) {
    	this.reportDefinitionId = reportDefinitionId;
    }

	/**
     * @see org.openmrs.OpenmrsObject#getId()
     */
    @Override
    public Integer getId() {
	    return id;
    }

	/**
     * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
     */
    @Override
    public void setId(Integer id) {
	    this.id = id;
    }

}
