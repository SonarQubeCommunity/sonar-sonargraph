/**
 * 23.05.2005 17:28:21 - generated 'data-transfer-object-mapper' for 'com.hello2morrow.ddaexample.business.contact.domain.Address'
 */

package com.hello2morrow.ddaexample.business.contact.controller;

import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class AddressDtoMapper
{
	private AddressDtoMapper()
	{
		//make it unaccessible
	}
	
	public static com.hello2morrow.ddaexample.business.contact.service.AddressDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.contact.domain.Address[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.contact.service.AddressDto[] createdDtos = new com.hello2morrow.ddaexample.business.contact.service.AddressDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}
	
	public static com.hello2morrow.ddaexample.business.contact.service.AddressDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.contact.domain.Address domainObject) throws TechnicalException
	{
		assert domainObject != null;
		com.hello2morrow.ddaexample.business.contact.service.AddressDto dto = new com.hello2morrow.ddaexample.business.contact.service.AddressDto();
		
		dto.setObjectId(domainObject.getObjectId());
		dto.setStreet(domainObject.getStreet());
		dto.setCity(domainObject.getCity());
		dto.setZipCode(domainObject.getZipCode());
		return dto;
	}
	
	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.contact.service.AddressDto dto, com.hello2morrow.ddaexample.business.contact.domain.Address domainObject, boolean resolveReferences) throws BusinessException, TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		if(dto.hasObjectId())
		{
			domainObject.updateObjectId(dto.getObjectId());
		}
	
		domainObject.setStreet(dto.getStreet());

		domainObject.setCity(dto.getCity());

		domainObject.setZipCode(dto.getZipCode());

	}
}