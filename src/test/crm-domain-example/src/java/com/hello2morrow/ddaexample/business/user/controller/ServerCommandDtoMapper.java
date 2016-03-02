/**
 * 23.05.2005 17:28:16 - generated 'data-transfer-object-mapper' for 'com.hello2morrow.ddaexample.business.user.domain.ServerCommand'
 */

package com.hello2morrow.ddaexample.business.user.controller;

import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class ServerCommandDtoMapper
{
	private ServerCommandDtoMapper()
	{
		//make it unaccessible
	}
	
	public static com.hello2morrow.ddaexample.business.user.service.ServerCommandDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.user.domain.ServerCommand[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.user.service.ServerCommandDto[] createdDtos = new com.hello2morrow.ddaexample.business.user.service.ServerCommandDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}
	
	public static com.hello2morrow.ddaexample.business.user.service.ServerCommandDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.user.domain.ServerCommand domainObject) throws TechnicalException
	{
		assert domainObject != null;
		com.hello2morrow.ddaexample.business.user.service.ServerCommandDto dto = new com.hello2morrow.ddaexample.business.user.service.ServerCommandDto();
		
		dto.setObjectId(domainObject.getObjectId());
		dto.setName(domainObject.getName());
		return dto;
	}
	
	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.user.service.ServerCommandDto dto, com.hello2morrow.ddaexample.business.user.domain.ServerCommand domainObject, boolean resolveReferences) throws BusinessException, TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		if(dto.hasObjectId())
		{
			domainObject.updateObjectId(dto.getObjectId());
		}
	
	}
}