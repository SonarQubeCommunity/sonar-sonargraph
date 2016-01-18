/**
 * 23.05.2005 17:28:22 - generated 'data-transfer-object-mapper (class hierarchy)' for root class 'com.hello2morrow.ddaexample.business.contact.domain.Person'
 */

package com.hello2morrow.ddaexample.business.contact.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.controller.DtoManager;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class PersonDtoMapper
{
    private static Logger s_Logger = Logger.getLogger(PersonDtoMapper.class);

    static
    {
        try
        {
            DtoManager dtoManager = DtoManager.getInstance();
            dtoManager.addDtoCtor
            (
            	com.hello2morrow.ddaexample.business.contact.domain.Person.class, com.hello2morrow.ddaexample.business.contact.service.PersonDto.class.getConstructor(new Class[0])
            );
            dtoManager.addDomainObjectToDtoMapper
            (
            	com.hello2morrow.ddaexample.business.contact.domain.Person.class, 
            	PersonDtoMapper.class.getDeclaredMethod("mapDomainObjectToDto", new Class[]{com.hello2morrow.ddaexample.business.contact.domain.Person.class, com.hello2morrow.ddaexample.business.contact.service.PersonDto.class})
            );
        }
        catch (SecurityException e)
        {
            s_Logger.error(ExceptionUtility.collectAll(e));
            assert false : ExceptionUtility.collectAll(e);
        }
        catch (NoSuchMethodException e)
        {
            s_Logger.error(ExceptionUtility.collectAll(e));
            assert false : ExceptionUtility.collectAll(e);
        }
    }
	
	private PersonDtoMapper()
	{
		//make it unaccessible
	}

	public static com.hello2morrow.ddaexample.business.contact.service.PersonDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.contact.domain.Person[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.contact.service.PersonDto[] createdDtos = new com.hello2morrow.ddaexample.business.contact.service.PersonDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}

	public static com.hello2morrow.ddaexample.business.contact.service.PersonDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.contact.domain.Person domainObject) throws TechnicalException
	{
		assert domainObject != null;

        DtoManager dtoManager = DtoManager.getInstance();
		Class domainObjectClass = domainObject.getClass();
		
        com.hello2morrow.ddaexample.business.contact.service.PersonDto dto = 
        	(com.hello2morrow.ddaexample.business.contact.service.PersonDto) dtoManager.createDto(domainObjectClass);
        
        dtoManager.mapDomainObjectToDto(domainObject, dto);

		return dto;
	}

	public static void mapDomainObjectToDto(com.hello2morrow.ddaexample.business.contact.domain.Person domainObject, com.hello2morrow.ddaexample.business.contact.service.PersonDto dto) 
		throws TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		dto.setFirstName(domainObject.getFirstName());
		dto.setLastName(domainObject.getLastName());

		com.hello2morrow.dda.business.common.dsi.DomainObject object = domainObject.getAddress();
		dto.setAddressReference(object.getObjectId());

		dto.setObjectId(domainObject.getObjectId());
	}

	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.contact.service.PersonDto dto, com.hello2morrow.ddaexample.business.contact.domain.Person domainObject, boolean resolveReferences)
		throws TechnicalException
	{
		assert dto != null;
		assert domainObject != null;
		
		domainObject.setFirstName(dto.getFirstName());

		domainObject.setLastName(dto.getLastName());

		if(resolveReferences)
		{
			com.hello2morrow.dda.foundation.common.ObjectIdIf objectId = dto.getAddressReference();
			com.hello2morrow.ddaexample.business.contact.domain.Address resolved = (com.hello2morrow.ddaexample.business.contact.domain.Address)com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier.findByObjectId(objectId);
			domainObject.setAddress(resolved);
		}

		if(dto.hasObjectId())
		{
			domainObject.updateObjectId(dto.getObjectId());
		}
	}
}



