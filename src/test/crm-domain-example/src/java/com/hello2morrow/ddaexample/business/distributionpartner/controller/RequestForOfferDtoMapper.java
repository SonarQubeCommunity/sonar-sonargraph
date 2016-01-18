/**
 * 23.05.2005 17:28:23 - generated 'data-transfer-object-mapper-derived' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForOffer'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.controller.DtoManager;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class RequestForOfferDtoMapper
{
    private static Logger s_Logger = Logger.getLogger(RequestForOfferDtoMapper.class);

    static
    {
        try
        {
            DtoManager dtoManager = DtoManager.getInstance();
            dtoManager.addDtoCtor
            (
            	com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForOffer.class, com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForOfferDto.class.getConstructor(new Class[0])
            );
            dtoManager.addDomainObjectToDtoMapper
            (
            	com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForOffer.class, 
            	RequestForOfferDtoMapper.class.getDeclaredMethod("mapDomainObjectToDto", new Class[]{com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForOffer.class, com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForOfferDto.class})
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

	private RequestForOfferDtoMapper()
	{
		//make it unaccessible
	}
	
	public static com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForOfferDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForOffer[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForOfferDto[] createdDtos = new com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForOfferDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}

	public static com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForOfferDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForOffer domainObject) throws TechnicalException
	{
		assert domainObject != null;

        DtoManager dtoManager = DtoManager.getInstance();
		Class domainObjectClass = domainObject.getClass();
		
        com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForOfferDto dto = 
        	(com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForOfferDto) dtoManager.createDto(domainObjectClass);
        
        dtoManager.mapDomainObjectToDto(domainObject, dto);

		return dto;
	}
	
	public static void mapDomainObjectToDto(com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForOffer domainObject, com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForOfferDto dto) 
		throws TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		com.hello2morrow.ddaexample.business.request.controller.RequestDtoMapper.mapDomainObjectToDto(domainObject, dto);
		
	}

	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForOfferDto dto, com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForOffer domainObject, boolean resolveReferences)
		throws BusinessException, TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		com.hello2morrow.ddaexample.business.request.controller.RequestDtoMapper.mapDtoToDomainObject(dto, domainObject, resolveReferences);
		
	}
}