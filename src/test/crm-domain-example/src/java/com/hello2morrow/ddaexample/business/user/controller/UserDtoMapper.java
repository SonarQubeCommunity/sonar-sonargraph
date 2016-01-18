/**
 * 23.05.2005 17:28:18 - generated 'data-transfer-object-mapper' for 'com.hello2morrow.ddaexample.business.user.domain.User'
 */

package com.hello2morrow.ddaexample.business.user.controller;

import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class UserDtoMapper
{
	private UserDtoMapper()
	{
		//make it unaccessible
	}
	
	public static com.hello2morrow.ddaexample.business.user.service.UserDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.user.domain.User[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.user.service.UserDto[] createdDtos = new com.hello2morrow.ddaexample.business.user.service.UserDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}
	
	public static com.hello2morrow.ddaexample.business.user.service.UserDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.user.domain.User domainObject) throws TechnicalException
	{
		assert domainObject != null;
		com.hello2morrow.ddaexample.business.user.service.UserDto dto = new com.hello2morrow.ddaexample.business.user.service.UserDto();
		
		dto.setObjectId(domainObject.getObjectId());
		dto.setName(domainObject.getName());
		dto.setPassword(domainObject.getPassword());
		com.hello2morrow.dda.business.common.dsi.DomainObject[] objects = domainObject.getRoles();
		for (int i = 0; i < objects.length; ++i)
		{
			dto.addRolesReference(objects[i].getObjectId());
		} 		

		return dto;
	}
	
	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.user.service.UserDto dto, com.hello2morrow.ddaexample.business.user.domain.User domainObject, boolean resolveReferences) throws BusinessException, TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		if(dto.hasObjectId())
		{
			domainObject.updateObjectId(dto.getObjectId());
		}
	
		domainObject.setName(dto.getName());

		domainObject.setPassword(dto.getPassword());

		if(resolveReferences)
		{
			com.hello2morrow.dda.foundation.common.ObjectIdIf[] objectIds = dto.getRolesReferences();
			com.hello2morrow.ddaexample.business.user.domain.Role[] resolved = new com.hello2morrow.ddaexample.business.user.domain.Role[objectIds.length];

			for (int i = 0; i < objectIds.length; ++i)
			{
				resolved[i] = (com.hello2morrow.ddaexample.business.user.domain.Role)com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier.findByObjectId(objectIds[i]); 
			} 		
			
			domainObject.setRoles(resolved);
		}
		
	}
}