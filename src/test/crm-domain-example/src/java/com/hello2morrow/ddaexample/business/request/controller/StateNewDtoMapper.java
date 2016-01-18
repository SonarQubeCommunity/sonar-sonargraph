/**
 * 23.05.2005 17:28:23 - generated 'data-transfer-object-mapper-derived' for 'com.hello2morrow.ddaexample.business.request.domain.StateNew'
 */

package com.hello2morrow.ddaexample.business.request.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.controller.DtoManager;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class StateNewDtoMapper
{
    private static Logger s_Logger = Logger.getLogger(StateNewDtoMapper.class);

    static
    {
        try
        {
            DtoManager dtoManager = DtoManager.getInstance();
            dtoManager.addDtoCtor
            (
            	com.hello2morrow.ddaexample.business.request.domain.StateNew.class, com.hello2morrow.ddaexample.business.request.service.StateNewDto.class.getConstructor(new Class[0])
            );
            dtoManager.addDomainObjectToDtoMapper
            (
            	com.hello2morrow.ddaexample.business.request.domain.StateNew.class, 
            	StateNewDtoMapper.class.getDeclaredMethod("mapDomainObjectToDto", new Class[]{com.hello2morrow.ddaexample.business.request.domain.StateNew.class, com.hello2morrow.ddaexample.business.request.service.StateNewDto.class})
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

	private StateNewDtoMapper()
	{
		//make it unaccessible
	}
	
	public static com.hello2morrow.ddaexample.business.request.service.StateNewDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.request.domain.StateNew[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.request.service.StateNewDto[] createdDtos = new com.hello2morrow.ddaexample.business.request.service.StateNewDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}

	public static com.hello2morrow.ddaexample.business.request.service.StateNewDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.request.domain.StateNew domainObject) throws TechnicalException
	{
		assert domainObject != null;

        DtoManager dtoManager = DtoManager.getInstance();
		Class domainObjectClass = domainObject.getClass();
		
        com.hello2morrow.ddaexample.business.request.service.StateNewDto dto = 
        	(com.hello2morrow.ddaexample.business.request.service.StateNewDto) dtoManager.createDto(domainObjectClass);
        
        dtoManager.mapDomainObjectToDto(domainObject, dto);

		return dto;
	}
	
	public static void mapDomainObjectToDto(com.hello2morrow.ddaexample.business.request.domain.StateNew domainObject, com.hello2morrow.ddaexample.business.request.service.StateNewDto dto) 
		throws TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		com.hello2morrow.ddaexample.business.request.controller.StateDtoMapper.mapDomainObjectToDto(domainObject, dto);
		
	}

	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.request.service.StateNewDto dto, com.hello2morrow.ddaexample.business.request.domain.StateNew domainObject, boolean resolveReferences)
		throws BusinessException, TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		com.hello2morrow.ddaexample.business.request.controller.StateDtoMapper.mapDtoToDomainObject(dto, domainObject, resolveReferences);
		
	}
}