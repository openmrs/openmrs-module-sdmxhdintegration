/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

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

/**
 * Hibernate implementation of the module DAO
 */
public class HibernateSDMXHDMessageDAO implements SDMXHDMessageDAO {

	private SessionFactory sessionFactory;
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#getMessage(java.lang.Integer)
	 * @should get the correct message for the given id
	 */
	public SDMXHDMessage getMessage(Integer id) {
	    return (SDMXHDMessage)sessionFactory.getCurrentSession().get(SDMXHDMessage.class, id);
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#saveMessage(org.openmrs.module.sdmxhdintegration.SDMXHDMessage)
	 * @should save the given message
	 */
	public SDMXHDMessage saveMessage(SDMXHDMessage message) {
	    sessionFactory.getCurrentSession().saveOrUpdate(message);
	    return message;
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#deleteMessage(org.openmrs.module.sdmxhdintegration.SDMXHDMessage)
	 * @should delete the message with the given id
	 */
	public void deleteMessage(SDMXHDMessage message) {
	    sessionFactory.getCurrentSession().delete(message);
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#getAllMessages()
	 * @should return all messages
	 * @should return an empty list if none exist
	 */
	public List<SDMXHDMessage> getAllMessages(Boolean includeRetired) {
	    Criteria crit = sessionFactory.getCurrentSession().createCriteria(SDMXHDMessage.class);
	    crit.addOrder(Order.asc("name"));
	    if (!includeRetired) {
	    	crit.add(Expression.eq("retired", false));
	    }
	    
	    return (List<SDMXHDMessage>)crit.list();
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#getKeyFamilyMapping(java.lang.Integer)
	 */
	@Override
	public KeyFamilyMapping getKeyFamilyMapping(Integer id) {
		return (KeyFamilyMapping) sessionFactory.getCurrentSession().get(KeyFamilyMapping.class, id);
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#getKeyFamilyMapping(org.openmrs.module.sdmxhdintegration.SDMXHDMessage, java.lang.String)
	 */
	@Override
	public KeyFamilyMapping getKeyFamilyMapping(SDMXHDMessage message, String keyFamilyId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(KeyFamilyMapping.class);
		
		criteria.add(Expression.eq("keyFamilyId", keyFamilyId));
		criteria.add(Expression.eq("message", message));
		
		List list = criteria.list();
		if (list.size() > 1) {
			throw new APIException("Multiple elements returned for this query");
		}
		if (list.size() < 1) {
			return null;
		}
		    	
	    return (KeyFamilyMapping)list.get(0);
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
			throw new APIException("Multiple elements returned for this query");
		}
		    	
	    return (KeyFamilyMapping) list.get(0);
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#getKeyFamilyMappingsFromMessage(org.openmrs.module.sdmxhdintegration.SDMXHDMessage)
	 */
	@Override
	public List<KeyFamilyMapping> getKeyFamilyMappingsFromMessage(SDMXHDMessage message) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(KeyFamilyMapping.class);
		
		crit.add(Expression.eq("message", message));
		
		return crit.list();
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
	 * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#saveKeyFamilyMapping(org.openmrs.module.sdmxhdintegration.KeyFamilyMapping)
	 */
	@Override
	public KeyFamilyMapping saveKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping) {
		sessionFactory.getCurrentSession().saveOrUpdate(keyFamilyMapping);
		return keyFamilyMapping;
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO#deleteKeyFamilyMapping(org.openmrs.module.sdmxhdintegration.KeyFamilyMapping)
	 */
	@Override
	public void deleteKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping) {
		sessionFactory.getCurrentSession().delete(keyFamilyMapping);
	}
}
