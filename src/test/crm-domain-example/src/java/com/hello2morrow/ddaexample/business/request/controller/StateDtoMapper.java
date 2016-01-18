/**
 * 23.05.2005 17:28:23 - generated 'data-transfer-object-mapper (class hierarchy)' for root class 'com.hello2morrow.ddaexample.business.request.domain.State'
 */

package com.hello2morrow.ddaexample.business.request.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.controller.DtoManager;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class StateDtoMapper
{
    private static Logger s_Logger = Logger.getLogger(StateDtoMapper.class);

    static
    {
        try
        {
            DtoManager dtoManager = DtoManager.getInstance();
            dtoManager.addDtoCtor
            (
            	com.hello2morrow.ddaexample.business.request.domain.State.class, com.hello2morrow.ddaexample.business.request.service.StateDto.class.getConstructor(new Class[0])
            );
            dtoManager.addDomainObjectToDtoMapper
            (
            	com.hello2morrow.ddaexample.business.request.domain.State.class, 
            	StateDtoMapper.class.getDeclaredMethod("mapDomainObjectToDto", new Class[]{com.hello2morrow.ddaexample.business.request.domain.State.class, com.hello2morrow.ddaexample.business.request.service.StateDto.class})
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
	
	private StateDtoMapper()
	{
		//make it unaccessible
	}

	public static com.hello2morrow.ddaexample.business.request.service.StateDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.request.domain.State[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.request.service.StateDto[] createdDtos = new com.hello2morrow.ddaexample.business.request.service.StateDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}

	public static com.hello2morrow.ddaexample.business.request.service.StateDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.request.domain.State domainObject) throws TechnicalException
	{
		assert domainObject != null;

        DtoManager dtoManager = DtoManager.getInstance();
		Class domainObjectClass = domainObject.getClass();
		
        com.hello2morrow.ddaexample.business.request.service.StateDto dto = 
        	(com.hello2morrow.ddaexample.business.request.service.StateDto) dtoManager.createDto(domainObjectClass);
        
        dtoManager.mapDomainObjectToDto(domainObject, dto);

		return dto;
	}

	public static void mapDomainObjectToDto(com.hello2morrow.ddaexample.business.request.domain.State domainObject, com.hello2morrow.ddaexample.business.request.service.StateDto dto) 
		throws TechnicalException
	{
		assert dto != null;
		assert domainObject != null;


		dto.setObjectId(domainObject.getObjectId());
	}

	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.request.service.StateDto dto, com.hello2morrow.ddaexample.business.request.domain.State domainObject, boolean resolveReferences)
		throws TechnicalException
	{
		assert dto != null;
		assert domainObject != null;
		

		if(dto.hasObjectId())
		{
			domainObject.updateObjectId(dto.getObjectId());
		}
	}
}

