/**
 * 23.05.2005 17:28:17 - generated 'data-transfer-object-mapper' for 'com.hello2morrow.ddaexample.business.user.domain.Role'
 */

package com.hello2morrow.ddaexample.business.user.controller;

import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class RoleDtoMapper
{
	private RoleDtoMapper()
	{
		//make it unaccessible
	}
	
	public static com.hello2morrow.ddaexample.business.user.service.RoleDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.user.domain.Role[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.user.service.RoleDto[] createdDtos = new com.hello2morrow.ddaexample.business.user.service.RoleDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}
	
	public static com.hello2morrow.ddaexample.business.user.service.RoleDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.user.domain.Role domainObject) throws TechnicalException
	{
		assert domainObject != null;
		com.hello2morrow.ddaexample.business.user.service.RoleDto dto = new com.hello2morrow.ddaexample.business.user.service.RoleDto();
		
		dto.setObjectId(domainObject.getObjectId());
		dto.setName(domainObject.getName());
		com.hello2morrow.dda.business.common.dsi.DomainObject[] objects = domainObject.getServerCommands();
		for (int i = 0; i < objects.length; ++i)
		{
			dto.addServerCommandsReference(objects[i].getObjectId());
		} 		

		return dto;
	}
	
	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.user.service.RoleDto dto, com.hello2morrow.ddaexample.business.user.domain.Role domainObject, boolean resolveReferences) throws BusinessException, TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		if(dto.hasObjectId())
		{
			domainObject.updateObjectId(dto.getObjectId());
		}
	
		if(resolveReferences)
		{
			com.hello2morrow.dda.foundation.common.ObjectIdIf[] objectIds = dto.getServerCommandsReferences();
			com.hello2morrow.ddaexample.business.user.domain.ServerCommand[] resolved = new com.hello2morrow.ddaexample.business.user.domain.ServerCommand[objectIds.length];

			for (int i = 0; i < objectIds.length; ++i)
			{
				resolved[i] = (com.hello2morrow.ddaexample.business.user.domain.ServerCommand)com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier.findByObjectId(objectIds[i]); 
			} 		
			
			domainObject.setServerCommands(resolved);
		}
		
	}
}