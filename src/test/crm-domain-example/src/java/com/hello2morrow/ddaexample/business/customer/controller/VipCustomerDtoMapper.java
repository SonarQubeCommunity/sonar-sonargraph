/**
 * 23.05.2005 17:28:22 - generated 'data-transfer-object-mapper-derived' for 'com.hello2morrow.ddaexample.business.customer.domain.VipCustomer'
 */

package com.hello2morrow.ddaexample.business.customer.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.controller.DtoManager;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class VipCustomerDtoMapper
{
    private static Logger s_Logger = Logger.getLogger(VipCustomerDtoMapper.class);

    static
    {
        try
        {
            DtoManager dtoManager = DtoManager.getInstance();
            dtoManager.addDtoCtor
            (
            	com.hello2morrow.ddaexample.business.customer.domain.VipCustomer.class, com.hello2morrow.ddaexample.business.customer.service.VipCustomerDto.class.getConstructor(new Class[0])
            );
            dtoManager.addDomainObjectToDtoMapper
            (
            	com.hello2morrow.ddaexample.business.customer.domain.VipCustomer.class, 
            	VipCustomerDtoMapper.class.getDeclaredMethod("mapDomainObjectToDto", new Class[]{com.hello2morrow.ddaexample.business.customer.domain.VipCustomer.class, com.hello2morrow.ddaexample.business.customer.service.VipCustomerDto.class})
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

	private VipCustomerDtoMapper()
	{
		//make it unaccessible
	}
	
	public static com.hello2morrow.ddaexample.business.customer.service.VipCustomerDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.customer.domain.VipCustomer[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.customer.service.VipCustomerDto[] createdDtos = new com.hello2morrow.ddaexample.business.customer.service.VipCustomerDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}

	public static com.hello2morrow.ddaexample.business.customer.service.VipCustomerDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.customer.domain.VipCustomer domainObject) throws TechnicalException
	{
		assert domainObject != null;

        DtoManager dtoManager = DtoManager.getInstance();
		Class domainObjectClass = domainObject.getClass();
		
        com.hello2morrow.ddaexample.business.customer.service.VipCustomerDto dto = 
        	(com.hello2morrow.ddaexample.business.customer.service.VipCustomerDto) dtoManager.createDto(domainObjectClass);
        
        dtoManager.mapDomainObjectToDto(domainObject, dto);

		return dto;
	}
	
	public static void mapDomainObjectToDto(com.hello2morrow.ddaexample.business.customer.domain.VipCustomer domainObject, com.hello2morrow.ddaexample.business.customer.service.VipCustomerDto dto) 
		throws TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		com.hello2morrow.ddaexample.business.customer.controller.CustomerDtoMapper.mapDomainObjectToDto(domainObject, dto);
		
	}

	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.customer.service.VipCustomerDto dto, com.hello2morrow.ddaexample.business.customer.domain.VipCustomer domainObject, boolean resolveReferences)
		throws BusinessException, TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		com.hello2morrow.ddaexample.business.customer.controller.CustomerDtoMapper.mapDtoToDomainObject(dto, domainObject, resolveReferences);
		
	}
}