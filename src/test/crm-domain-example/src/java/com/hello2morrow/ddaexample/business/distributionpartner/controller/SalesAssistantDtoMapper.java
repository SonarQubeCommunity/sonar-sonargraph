/**
 * 23.05.2005 17:28:22 - generated 'data-transfer-object-mapper-derived' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.controller.DtoManager;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class SalesAssistantDtoMapper
{
    private static Logger s_Logger = Logger.getLogger(SalesAssistantDtoMapper.class);

    static
    {
        try
        {
            DtoManager dtoManager = DtoManager.getInstance();
            dtoManager.addDtoCtor
            (
            	com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant.class, com.hello2morrow.ddaexample.business.distributionpartner.service.SalesAssistantDto.class.getConstructor(new Class[0])
            );
            dtoManager.addDomainObjectToDtoMapper
            (
            	com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant.class, 
            	SalesAssistantDtoMapper.class.getDeclaredMethod("mapDomainObjectToDto", new Class[]{com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant.class, com.hello2morrow.ddaexample.business.distributionpartner.service.SalesAssistantDto.class})
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

	private SalesAssistantDtoMapper()
	{
		//make it unaccessible
	}
	
	public static com.hello2morrow.ddaexample.business.distributionpartner.service.SalesAssistantDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.distributionpartner.service.SalesAssistantDto[] createdDtos = new com.hello2morrow.ddaexample.business.distributionpartner.service.SalesAssistantDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}

	public static com.hello2morrow.ddaexample.business.distributionpartner.service.SalesAssistantDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant domainObject) throws TechnicalException
	{
		assert domainObject != null;

        DtoManager dtoManager = DtoManager.getInstance();
		Class domainObjectClass = domainObject.getClass();
		
        com.hello2morrow.ddaexample.business.distributionpartner.service.SalesAssistantDto dto = 
        	(com.hello2morrow.ddaexample.business.distributionpartner.service.SalesAssistantDto) dtoManager.createDto(domainObjectClass);
        
        dtoManager.mapDomainObjectToDto(domainObject, dto);

		return dto;
	}
	
	public static void mapDomainObjectToDto(com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant domainObject, com.hello2morrow.ddaexample.business.distributionpartner.service.SalesAssistantDto dto) 
		throws TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		com.hello2morrow.ddaexample.business.contact.controller.PersonDtoMapper.mapDomainObjectToDto(domainObject, dto);
		
		com.hello2morrow.dda.business.common.dsi.DomainObject[] objects = domainObject.getCustomers();
		for (int i = 0; i < objects.length; ++i)
		{
			dto.addCustomersReference(objects[i].getObjectId());
		} 		

	}

	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.distributionpartner.service.SalesAssistantDto dto, com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant domainObject, boolean resolveReferences)
		throws BusinessException, TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		com.hello2morrow.ddaexample.business.contact.controller.PersonDtoMapper.mapDtoToDomainObject(dto, domainObject, resolveReferences);
		
		if(resolveReferences)
		{
			com.hello2morrow.dda.foundation.common.ObjectIdIf[] objectIds = dto.getCustomersReferences();
			com.hello2morrow.ddaexample.business.customer.domain.Customer[] resolved = new com.hello2morrow.ddaexample.business.customer.domain.Customer[objectIds.length];

			for (int i = 0; i < objectIds.length; ++i)
			{
				resolved[i] = (com.hello2morrow.ddaexample.business.customer.domain.Customer)com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier.findByObjectId(objectIds[i]); 
			} 		
			
			domainObject.setCustomers(resolved);
		}
		
	}
}