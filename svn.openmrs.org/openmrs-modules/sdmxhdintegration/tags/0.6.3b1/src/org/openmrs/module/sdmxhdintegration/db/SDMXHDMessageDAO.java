package org.openmrs.module.sdmxhdintegration.db;

import java.util.List;

import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;

public interface SDMXHDMessageDAO {

    public SDMXHDMessage getSDMXHDMessage(Integer id);

    public SDMXHDMessage saveSDMXHDMessage(SDMXHDMessage sdmxhdMessage);

    public void deleteSDMXHDMessage(SDMXHDMessage sdmxhdMessage);

    public List<SDMXHDMessage> getAllSDMXHDMessages(Boolean includeRetired);

    public List<KeyFamilyMapping> getAllKeyFamilyMappings();
    
    public KeyFamilyMapping getKeyFamilyMapping(Integer id);

    public KeyFamilyMapping getAllKeyFamilyMappings(SDMXHDMessage sdmxhdMessage, String keyFamilyId);

    public void deleteKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping);

    public KeyFamilyMapping saveKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping);

    public KeyFamilyMapping getKeyFamilyMappingByReportDefinitionId(Integer reportDefinitionId);

    public List<KeyFamilyMapping> getKeyFamilyMappingBySDMXHDMessage(SDMXHDMessage sdmxhdMessage);

}
