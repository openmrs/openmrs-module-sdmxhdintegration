
package org.openmrs.module.sdmxhdintegration.reporting.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;


/**
 * Represents a SDMX-HD CohortIndicatorDataSetDefinition with various maps to store information
 * about how a SDMX-HD Indicator and Dimensions are mapped to OMRS Indicators and Dimensions
 */
public class SDMXHDCohortIndicatorDataSetDefinition extends CohortIndicatorDataSetDefinition {
	
	// SDMX-HD Message ID
	private Integer SDMXHDMessageId;
	
	/* Indicator and Dimension Mapping */
	// SDMX-HD Indicator -> OMRS Indicator
	private Map<String, Integer> mappedIndicators = new HashMap<String, Integer>();
	// SDMX-HD Dimension -> OMRS Dimension
	private Map<String, Integer> mappedDimensions = new HashMap<String, Integer>();
	// SDMX-HD Dimension -> SDMX-HD Dimension option -> OMRS Dimension option
	private Map<String, Map<String, String>> mappedDimensionOptions = new HashMap<String, Map<String, String>>();
	
	/* Fixed values for Dimensions */
	// SDMX-HD Dimenison -> FixedValue
	private Map<String, String> fixedDimensionValues = new HashMap<String, String>();
	// Column name -> SDMX-HD Dimension with known fixed value
	private Map<String, List<String>> mappedFixedDimensions = new HashMap<String, List<String>>();
	
	/* Column to SDMX-HD indicator mapping */
	// SDMX-HD Indicator -> Column Name
	private Map<String, List<String>> indicatorColumnMapping = new HashMap<String, List<String>>();
	
	/* Attribute Value Mappings */
	// SDMX-HD Attribute -> Attribute value
	private Map<String, String> dataSetAttachedAttributes = new HashMap<String, String>();
	// Column name -> SDMX-HD Attribute -> attribute value
	// TODO figure out groups once consensus is reached in how to use them in SDMX-HD
	//private Map<String, Map<String, String>> groupAttachedAttributes = new HashMap<String, Map<String, String>>();
	// Column name -> SDMX-HD Attribute -> attribute value
	private Map<String, Map<String, String>> seriesAttachedAttributes = new HashMap<String, Map<String, String>>();
	// Column name -> SDMX-HD Attribute -> attribute value
	private Map<String, Map<String, String>> obsAttachedAttributes = new HashMap<String, Map<String, String>>();
	
	public void mapIndicator(String sdmxhdIndicatorName, Integer omrsIndicator) {
		mappedIndicators.put(sdmxhdIndicatorName, omrsIndicator);
	}
	
	public void mapDimension(String smdxhdDimension, Integer omrsDimension, Map<String, String> mappedDimensionOptions) {
		mappedDimensions.put(smdxhdDimension, omrsDimension);
		this.mappedDimensionOptions.put(smdxhdDimension, mappedDimensionOptions);
	}
	
	public Integer getOMRSMappedIndicator(String sdmxhdIndicator) {
		return mappedIndicators.get(sdmxhdIndicator);
	}
	
	public String getSDMXHDMappedIndicator(Integer omrsIndicatorId) {
		for (String sdmxhdIndicatorName : mappedIndicators.keySet()) {
			Integer omrsIndicatorIdTemp = mappedIndicators.get(sdmxhdIndicatorName);
			if (omrsIndicatorId.equals(omrsIndicatorIdTemp)) {
				return sdmxhdIndicatorName;
			}
		}
		return null;
	}
	
	public Integer getOMRSMappedDimension(String sdmxhdDimension) {
		return mappedDimensions.get(sdmxhdDimension);
	}
	
	public String getORMSMappedDimensionOption(String sdmxhdDimension, String sdmxhdDimensionOption) {
		Map<String, String> mappedDimensionOption = mappedDimensionOptions.get(sdmxhdDimension);
		if (mappedDimensionOption == null) {
			return null;
		}
		return mappedDimensionOption.get(sdmxhdDimensionOption);
	}
	
	public Map<String, Integer> getOMRSMappedIndicators() {
		return mappedIndicators;
	}
	
	public Map<String, Integer> getOMRSMappedDimensions() {
		return mappedDimensions;
	}
	
	public Map<String, Map<String, String>> getOMRSMappedDimensionOptions() {
		return mappedDimensionOptions;
	}

    public Map<String, String> getOMRSMappedDimensionOptions(String sdmxhdDimension) {
    	return mappedDimensionOptions.get(sdmxhdDimension);
    }

    public Map<String, String> getDataSetAttachedAttributes() {
    	return dataSetAttachedAttributes;
    }

    public void setDataSetAttachedAttributes(Map<String, String> dataSetAttachedAttributes) {
    	this.dataSetAttachedAttributes = dataSetAttachedAttributes;
    }

    public Map<String, Map<String, String>> getSeriesAttachedAttributes() {
    	return seriesAttachedAttributes;
    }
	
    public void setSeriesAttachedAttributes(Map<String, Map<String, String>> seriesAttachedAttributes) {
    	this.seriesAttachedAttributes = seriesAttachedAttributes;
    }
	
    public Map<String, Map<String, String>> getObsAttachedAttributes() {
    	return obsAttachedAttributes;
    }
	
    public void setObsAttachedAttributes(Map<String, Map<String, String>> obsAttachedAttributes) {
    	this.obsAttachedAttributes = obsAttachedAttributes;
    }
    
    public void addDataSetAttribute(String SDMXHDAttribute, String value) {
    	dataSetAttachedAttributes.put(SDMXHDAttribute, value);
    }
    
    public void addSeriesAttributesToColumn(String columnKey, Map<String, String> attributes) {
    	seriesAttachedAttributes.put(columnKey, attributes);
    }
    
    public void addObsAttributesToColumn(String columnKey, Map<String, String> attributes) {
    	obsAttachedAttributes.put(columnKey, attributes);
    }

	/**
     * Auto generated method comment
     * 
     * @param columnKey
     * @param key
     * @param attributeValue
     */
    public void addSeriesAttributesToColumn(String columnKey, String key, String attributeValue) {
	    Map<String, String> map = seriesAttachedAttributes.get(columnKey);
	    if (map != null) {
	    	map.put(key, attributeValue);
	    } else {
	    	map = new HashMap<String, String>();
	    	map.put(key, attributeValue);
	    	seriesAttachedAttributes.put(columnKey, map);
	    }
    }

	/**
     * Auto generated method comment
     * 
     * @param columnKey
     * @param key
     * @param attributeValue
     */
    public void addObsAttributesToCoulmn(String columnKey, String key, String attributeValue) {
    	Map<String, String> map = obsAttachedAttributes.get(columnKey);
	    if (map != null) {
	    	map.put(key, attributeValue);
	    } else {
	    	map = new HashMap<String, String>();
	    	map.put(key, attributeValue);
	    	obsAttachedAttributes.put(columnKey, map);
	    }
    }
	
    /**
     * @return the sDMXHDMessageId
     */
    public Integer getSDMXHDMessageId() {
    	return SDMXHDMessageId;
    }
	
    /**
     * @param sDMXHDMessageId the sDMXHDMessageId to set
     */
    public void setSDMXHDMessageId(Integer sDMXHDMessageId) {
    	SDMXHDMessageId = sDMXHDMessageId;
    }

    /**
     * @return the fixedDimensionValues
     */
    public Map<String, String> getFixedDimensionValues() {
    	return fixedDimensionValues;
    }

    /**
     * @param fixedDimensionValues the fixedDimensionValues to set
     */
    public void setFixedDimensionValues(Map<String, String> fixedDimensionValues) {
    	this.fixedDimensionValues = fixedDimensionValues;
    }
    
    public void addFixedDimensionValues(String sdmxhdDimension, String value) {
    	fixedDimensionValues.put(sdmxhdDimension, value);
    }
    
    public String getFixedDimensionValues(String sdmxhdDimension) {
    	return fixedDimensionValues.get(sdmxhdDimension);
    }
    
    /**
     * @return the mappedFixedDimensions
     */
    public Map<String, List<String>> getMappedFixedDimensions() {
    	return mappedFixedDimensions;
    }
	
    /**
     * @param mappedFixedDimensions the mappedFixedDimensions to set
     */
    public void setMappedFixedDimensions(Map<String, List<String>> mappedFixedDimensions) {
    	this.mappedFixedDimensions = mappedFixedDimensions;
    }

	public void mapFixedDimension(String columnName, String sdmxhdDimension) {
    	if (mappedFixedDimensions.get(columnName) == null) {
    		mappedFixedDimensions.put(columnName, new ArrayList<String>());
    	}
    	List<String> list = mappedFixedDimensions.get(columnName);
    	list.add(sdmxhdDimension);
    }
    
    public List<String> getMappedFixedDimension(String columnName) {
    	if (mappedFixedDimensions.get(columnName) == null) {
    		return new ArrayList<String>();
    	} else {
    		return mappedFixedDimensions.get(columnName);
    	}
    	
    }
    
    /**
     * @return the indicatorColumnMapping
     */
    public Map<String, List<String>> getIndicatorColumnMapping() {
    	return indicatorColumnMapping;
    }
	
    /**
     * @param indicatorColumnMapping the indicatorColumnMapping to set
     */
    public void setIndicatorColumnMapping(Map<String, List<String>> indicatorColumnMapping) {
    	this.indicatorColumnMapping = indicatorColumnMapping;
    }

    public void addIndicatorColumnMapping(String sdmxhdIndicator, String columnName) {
    	List<String> list = indicatorColumnMapping.get(sdmxhdIndicator);
    	if (list == null) {
    		list = new ArrayList<String>();
    		list.add(columnName);
    		indicatorColumnMapping.put(sdmxhdIndicator, list);
    	} else {
    		list.add(columnName);
    	}
    }
    
    public void removeIndicatorColumnMappings(String sdmxhdIndicator) {
    	indicatorColumnMapping.remove(sdmxhdIndicator);
    }
    
    public List<String> getIndicatorColumnMapping(String sdmxhdIndicator) {
    	return indicatorColumnMapping.get(sdmxhdIndicator);
    }
}
