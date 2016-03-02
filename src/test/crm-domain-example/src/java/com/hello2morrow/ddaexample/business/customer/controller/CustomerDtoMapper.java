/**
 * 23.05.2005 17:28:22 - generated 'data-transfer-object-mapper-derived' for 'com.hello2morrow.ddaexample.business.customer.domain.Customer'
 */

package com.hello2morrow.ddaexample.business.customer.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.controller.DtoManager;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class CustomerDtoMapper
{
//	Canvas canvas;
	private static Logger s_Logger = Logger.getLogger(CustomerDtoMapper.class);

    static
    {
        try
        {
            DtoManager dtoManager = DtoManager.getInstance();
            dtoManager.addDtoCtor
            (
            	com.hello2morrow.ddaexample.business.customer.domain.Customer.class, com.hello2morrow.ddaexample.business.customer.service.CustomerDto.class.getConstructor(new Class[0])
            );
            dtoManager.addDomainObjectToDtoMapper
            (
            	com.hello2morrow.ddaexample.business.customer.domain.Customer.class, 
            	CustomerDtoMapper.class.getDeclaredMethod("mapDomainObjectToDto", new Class[]{com.hello2morrow.ddaexample.business.customer.domain.Customer.class, com.hello2morrow.ddaexample.business.customer.service.CustomerDto.class})
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

	private CustomerDtoMapper()
	{
		//make it unaccessible
	}
	
	public static com.hello2morrow.ddaexample.business.customer.service.CustomerDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.customer.domain.Customer[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.customer.service.CustomerDto[] createdDtos = new com.hello2morrow.ddaexample.business.customer.service.CustomerDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}

	public static com.hello2morrow.ddaexample.business.customer.service.CustomerDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.customer.domain.Customer domainObject) throws TechnicalException
	{
		assert domainObject != null;

        DtoManager dtoManager = DtoManager.getInstance();
		Class domainObjectClass = domainObject.getClass();
		
        com.hello2morrow.ddaexample.business.customer.service.CustomerDto dto = 
        	(com.hello2morrow.ddaexample.business.customer.service.CustomerDto) dtoManager.createDto(domainObjectClass);
        
        dtoManager.mapDomainObjectToDto(domainObject, dto);

		return dto;
	}
	
	public static void mapDomainObjectToDto(com.hello2morrow.ddaexample.business.customer.domain.Customer domainObject, com.hello2morrow.ddaexample.business.customer.service.CustomerDto dto) 
		throws TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		com.hello2morrow.ddaexample.business.contact.controller.PersonDtoMapper.mapDomainObjectToDto(domainObject, dto);
		
		dto.setAge(domainObject.getAge());
	}

	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.customer.service.CustomerDto dto, com.hello2morrow.ddaexample.business.customer.domain.Customer domainObject, boolean resolveReferences)
		throws BusinessException, TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		com.hello2morrow.ddaexample.business.contact.controller.PersonDtoMapper.mapDtoToDomainObject(dto, domainObject, resolveReferences);
		
		domainObject.setAge(dto.getAge());

	}
}