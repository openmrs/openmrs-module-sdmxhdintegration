package org.openmrs.module.sdmxhdintegration.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.openmrs.api.APIException;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO;

public class HibernateSDMXHDMessageDAO implements SDMXHDMessageDAO {

    private SessionFactory sessionFactory;
    
    protected final Log log = LogFactory.getLog(getClass());
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#getSDMXHDMessage(java.lang.Integer)
     * @should get the correct sdmxhd message for the given id
     */
    public SDMXHDMessage getSDMXHDMessage(Integer id) {
        return (SDMXHDMessage) sessionFactory.getCurrentSession().get(SDMXHDMessage.class, id);
    }
    
    /**
     * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#saveSDMXHDMessage(org.openmrs.module.sdmxhdintegration.SDMXHDMessage)
     * @should save the given sdmxhd message
     */
    public SDMXHDMessage saveSDMXHDMessage(SDMXHDMessage sdmxhdMessage) {
        sessionFactory.getCurrentSession().saveOrUpdate(sdmxhdMessage);
        return sdmxhdMessage;
    }
    
    /**
     * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#deleteSDMXHDMessage(org.openmrs.module.sdmxhdintegration.SDMXHDMessage)
     * @should deleve the sdmx message with the given id
     */
    public void deleteSDMXHDMessage(SDMXHDMessage sdmxhdMessage) {
        sessionFactory.getCurrentSession().delete(sdmxhdMessage);
    }

    /**
     * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#getAllSDMXHDMessages()
     * @should return all sdmx messages
     * @should return an empty list if none exist
     */
    public List<SDMXHDMessage> getAllSDMXHDMessages(Boolean includeRetired) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(SDMXHDMessage.class);
        crit.addOrder(Order.asc("name"));
        if (!includeRetired) {
        	crit.add(Expression.eq("retired", false));
        }
        
        return (List<SDMXHDMessage>) crit.list();
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#deleteKeyFamilyMapping(org.openmrs.module.sdmxhdintegration.KeyFamilyMapping)
     */
    @Override
    public void deleteKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping) {
    	sessionFactory.getCurrentSession().delete(keyFamilyMapping);
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#getAllKeyFamilyMappings()
     */
    @Override
    public List<KeyFamilyMapping> getAllKeyFamilyMappings() {
    	Session session = sessionFactory.getCurrentSession();

	    Query query = session.createQuery("from KeyFamilyMapping");
	    
	    List keyFamilyMappingList = query.list();
	    
		return keyFamilyMappingList;
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#getAllKeyFamilyMappings(org.openmrs.module.sdmxhdintegration.SDMXHDMessage, java.lang.String)
     */
    @Override
    public KeyFamilyMapping getAllKeyFamilyMappings(SDMXHDMessage sdmxhdMessage, String keyFamilyId) {
    	Criteria crit = sessionFactory.getCurrentSession().createCriteria(KeyFamilyMapping.class);
    	
    	crit.add(Expression.eq("keyFamilyId", keyFamilyId));
    	crit.add(Expression.eq("sdmxhdMessage", sdmxhdMessage));
    	
    	List list = crit.list();
    	if (list.size() > 1) {
    		throw new APIException("Multiple elements returned for this query");
    	}
    	if (list.size() < 1) {
    	return null;
    	}
    	    	
	    return (KeyFamilyMapping) list.get(0);
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#getKeyFamilyMapping(java.lang.Integer)
     */
    @Override
    public KeyFamilyMapping getKeyFamilyMapping(Integer id) {
    	return (KeyFamilyMapping) sessionFactory.getCurrentSession().get(KeyFamilyMapping.class, id);
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#saveKeyFamilyMapping(org.openmrs.module.sdmxhdintegration.KeyFamilyMapping)
     */
    @Override
    public KeyFamilyMapping saveKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping) {
    	sessionFactory.getCurrentSession().saveOrUpdate(keyFamilyMapping);
    	return keyFamilyMapping;
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#getKeyFamilyMappingByReportDefinitionId(java.lang.Integer)
     */
    @Override
    public KeyFamilyMapping getKeyFamilyMappingByReportDefinitionId(Integer reportDefinitionId) {
    	Criteria crit = sessionFactory.getCurrentSession().createCriteria(KeyFamilyMapping.class);
    	
    	crit.add(Expression.eq("reportDefinitionId", reportDefinitionId));
    	
    	List list = crit.list();
    	if (list.size() > 1) {
    		throw new APIException("Multiple elements eturned for this query");
    	}
    	    	
	    return (KeyFamilyMapping) list.get(0);
    }

	/**
     * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#getKeyFamilyMappingBySDMXHDMessage(org.openmrs.module.sdmxhdintegration.SDMXHDMessage)
     */
    @Override
    public List<KeyFamilyMapping> getKeyFamilyMappingBySDMXHDMessage(SDMXHDMessage sdmxhdMessage) {
    	Criteria crit = sessionFactory.getCurrentSession().createCriteria(KeyFamilyMapping.class);
    	
    	crit.add(Expression.eq("sdmxhdMessage", sdmxhdMessage));
    	
    	return crit.list();
    }

}
