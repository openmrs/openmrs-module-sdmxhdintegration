package org.openmrs.module.sdmxhdintegration.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipFile;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.parser.SDMXHDParser;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO;

public class SDMXHDModuleServiceImpl extends BaseOpenmrsService implements SDMXHDService {
    
    private SDMXHDMessageDAO dao;

    public void setDao(SDMXHDMessageDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<SDMXHDMessage> getAllSDMXHDMessages(Boolean includeRetired) {
    	return dao.getAllSDMXHDMessages(includeRetired);
    }

    @Override
    public SDMXHDMessage getSDMXHDMessage(Integer id) {
    	return dao.getSDMXHDMessage(id);
    }

    @Override
    public void purgeSDMXHDMessage(SDMXHDMessage sdmxhdMessage) {
    	dao.deleteSDMXHDMessage(sdmxhdMessage);
    }

    @Override
    public SDMXHDMessage saveSDMXHDMessage(SDMXHDMessage sdmxhdMessage) {
        if (sdmxhdMessage.getCreator() == null) {
        	sdmxhdMessage.setCreator(Context.getAuthenticatedUser());
        }
        if (sdmxhdMessage.getDateCreated() == null) {
        	sdmxhdMessage.setDateCreated(new Date());
        }
        if (sdmxhdMessage.getId() != null) {
            if (sdmxhdMessage.getChangedBy() == null) {
            	sdmxhdMessage.setChangedBy(Context.getAuthenticatedUser());
            }
            if (sdmxhdMessage.getDateChanged() == null) {
            	sdmxhdMessage.setDateChanged(new Date());
            }
        }
        if (sdmxhdMessage.getRetired() == null) {
        	sdmxhdMessage.setRetired(false);
        }

        return dao.saveSDMXHDMessage(sdmxhdMessage);
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getAllKeyFamilyMappings()
     */
    @Override
    public List<KeyFamilyMapping> getAllKeyFamilyMappings() {
	    return dao.getAllKeyFamilyMappings();
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getKeyFamilyMapping(java.lang.Integer)
     */
    @Override
    public KeyFamilyMapping getKeyFamilyMapping(Integer id) {
	    return dao.getKeyFamilyMapping(id);
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getKeyFamilyMapping(org.openmrs.module.sdmxhdintegration.SDMXHDMessage, java.lang.String)
     */
    @Override
    public KeyFamilyMapping getKeyFamilyMapping(SDMXHDMessage sdmxhdMessage, String keyFamilyId) {
	    return dao.getAllKeyFamilyMappings(sdmxhdMessage, keyFamilyId);
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#purgeKeyFamilyMapping(org.openmrs.module.sdmxhdintegration.KeyFamilyMapping)
     */
    @Override
    public void purgeKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping) {
	    dao.deleteKeyFamilyMapping(keyFamilyMapping);
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#saveKeyFamilyMapping(org.openmrs.module.sdmxhdintegration.KeyFamilyMapping)
     */
    @Override
    public KeyFamilyMapping saveKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping) {
    	return dao.saveKeyFamilyMapping(keyFamilyMapping);
    }

	/**
     * @throws IOException 
	 * @throws SchemaValidationException 
	 * @throws ExternalRefrenceNotFoundException 
	 * @throws XMLStreamException 
	 * @throws ValidationException 
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getSDMXHDDataSetDefinition(org.openmrs.module.sdmxhdintegration.SDMXHDMessage)
     */
    @Override
    public DSD getSDMXHDDataSetDefinition(SDMXHDMessage sdmxhdMessage) throws IOException, ValidationException, XMLStreamException, ExternalRefrenceNotFoundException, SchemaValidationException {
    	String path = Context.getAdministrationService().getGlobalProperty("sdmxhdintegration.messageUploadDir");
    	ZipFile zf = new ZipFile(path + File.separator + sdmxhdMessage.getSdmxhdZipFileName());
    	SDMXHDParser parser = new SDMXHDParser();
    	org.jembi.sdmxhd.SDMXHDMessage sdmxhdData = parser.parse(zf);
    	return sdmxhdData.getDsd();
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getKeyFamilyMappingByReportDefinitionId(java.lang.Integer)
     */
    @Override
    public KeyFamilyMapping getKeyFamilyMappingByReportDefinitionId(Integer reportDefinitionId) {
    	return dao.getKeyFamilyMappingByReportDefinitionId(reportDefinitionId);
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getKeyFamilyMappingBySDMXHDMessage(org.openmrs.module.sdmxhdintegration.SDMXHDMessage)
     */
    @Override
    public List<KeyFamilyMapping> getKeyFamilyMappingBySDMXHDMessage(SDMXHDMessage sdmxhdMessage) {
	    return dao.getKeyFamilyMappingBySDMXHDMessage(sdmxhdMessage);
    }

}
