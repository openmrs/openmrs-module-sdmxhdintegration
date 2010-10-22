package org.openmrs.module.sdmxhdintegration;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

public interface SDMXHDService extends OpenmrsService {

    @Transactional(readOnly=true)
    public SDMXHDMessage getSDMXHDMessage(Integer id);

    @Transactional(readOnly=true)
    public List<SDMXHDMessage> getAllSDMXHDMessages(Boolean includeRetired);
    
    @Transactional
    public SDMXHDMessage saveSDMXHDMessage(SDMXHDMessage sdmxhdMessage);

    @Transactional
    public void purgeSDMXHDMessage(SDMXHDMessage sdmxhdMessage);
    
    @Transactional(readOnly=true)
    public KeyFamilyMapping getKeyFamilyMapping(Integer id);
    
    @Transactional(readOnly=true)
    public List<KeyFamilyMapping> getAllKeyFamilyMappings();
    
    @Transactional
    public KeyFamilyMapping saveKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping);
    
    @Transactional
    public void purgeKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping);
    
    @Transactional(readOnly=true)
    public KeyFamilyMapping getKeyFamilyMapping(SDMXHDMessage sdmxhdMessage, String keyFamilyId);
    
    public DSD getSDMXHDDataSetDefinition(SDMXHDMessage sdmxhdMessage) throws IOException, ValidationException, XMLStreamException, ExternalRefrenceNotFoundException, SchemaValidationException;

    @Transactional(readOnly=true)
    public KeyFamilyMapping getKeyFamilyMappingByReportDefinitionId(Integer reportDefinitionId);

    @Transactional(readOnly=true)
    public List<KeyFamilyMapping> getKeyFamilyMappingBySDMXHDMessage(SDMXHDMessage sdmxhdMessage);
    
}
