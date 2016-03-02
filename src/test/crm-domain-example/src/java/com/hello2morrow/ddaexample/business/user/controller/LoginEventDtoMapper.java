/**
 * 23.05.2005 17:28:18 - generated 'data-transfer-object-mapper' for 'com.hello2morrow.ddaexample.business.user.domain.LoginEvent'
 */

package com.hello2morrow.ddaexample.business.user.controller;

import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class LoginEventDtoMapper
{
	private LoginEventDtoMapper()
	{
		//make it unaccessible
	}
	
	public static com.hello2morrow.ddaexample.business.user.service.LoginEventDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.user.domain.LoginEvent[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.user.service.LoginEventDto[] createdDtos = new com.hello2morrow.ddaexample.business.user.service.LoginEventDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}
	
	public static com.hello2morrow.ddaexample.business.user.service.LoginEventDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.user.domain.LoginEvent domainObject) throws TechnicalException
	{
		assert domainObject != null;
		com.hello2morrow.ddaexample.business.user.service.LoginEventDto dto = new com.hello2morrow.ddaexample.business.user.service.LoginEventDto();
		
		dto.setObjectId(domainObject.getObjectId());
		dto.setUserName(domainObject.getUserName());
		dto.setCreatedTimestamp(domainObject.getCreatedTimestamp());
		return dto;
	}
	
	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.user.service.LoginEventDto dto, com.hello2morrow.ddaexample.business.user.domain.LoginEvent domainObject, boolean resolveReferences) throws BusinessException, TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		if(dto.hasObjectId())
		{
			domainObject.updateObjectId(dto.getObjectId());
		}
	
	}
}