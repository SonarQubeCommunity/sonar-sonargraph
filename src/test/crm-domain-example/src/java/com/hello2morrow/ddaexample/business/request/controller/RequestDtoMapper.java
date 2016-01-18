/**
 * 23.05.2005 17:28:23 - generated 'data-transfer-object-mapper (class hierarchy)' for root class 'com.hello2morrow.ddaexample.business.request.domain.Request'
 */

package com.hello2morrow.ddaexample.business.request.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.controller.DtoManager;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class RequestDtoMapper
{
    private static Logger s_Logger = Logger.getLogger(RequestDtoMapper.class);

    static
    {
        try
        {
            DtoManager dtoManager = DtoManager.getInstance();
            dtoManager.addDtoCtor
            (
            	com.hello2morrow.ddaexample.business.request.domain.Request.class, com.hello2morrow.ddaexample.business.request.service.RequestDto.class.getConstructor(new Class[0])
            );
            dtoManager.addDomainObjectToDtoMapper
            (
            	com.hello2morrow.ddaexample.business.request.domain.Request.class, 
            	RequestDtoMapper.class.getDeclaredMethod("mapDomainObjectToDto", new Class[]{com.hello2morrow.ddaexample.business.request.domain.Request.class, com.hello2morrow.ddaexample.business.request.service.RequestDto.class})
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
	
	private RequestDtoMapper()
	{
		//make it unaccessible
	}

	public static com.hello2morrow.ddaexample.business.request.service.RequestDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.request.domain.Request[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.request.service.RequestDto[] createdDtos = new com.hello2morrow.ddaexample.business.request.service.RequestDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}

	public static com.hello2morrow.ddaexample.business.request.service.RequestDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.request.domain.Request domainObject) throws TechnicalException
	{
		assert domainObject != null;

        DtoManager dtoManager = DtoManager.getInstance();
		Class domainObjectClass = domainObject.getClass();
		
        com.hello2morrow.ddaexample.business.request.service.RequestDto dto = 
        	(com.hello2morrow.ddaexample.business.request.service.RequestDto) dtoManager.createDto(domainObjectClass);
        
        dtoManager.mapDomainObjectToDto(domainObject, dto);

		return dto;
	}

	public static void mapDomainObjectToDto(com.hello2morrow.ddaexample.business.request.domain.Request domainObject, com.hello2morrow.ddaexample.business.request.service.RequestDto dto) 
		throws TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		dto.setSubject(domainObject.getSubject());

		dto.setObjectId(domainObject.getObjectId());
	}

	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.request.service.RequestDto dto, com.hello2morrow.ddaexample.business.request.domain.Request domainObject, boolean resolveReferences)
		throws TechnicalException
	{
		assert dto != null;
		assert domainObject != null;
		
		domainObject.setSubject(dto.getSubject());


		if(dto.hasObjectId())
		{
			domainObject.updateObjectId(dto.getObjectId());
		}
	}
}



