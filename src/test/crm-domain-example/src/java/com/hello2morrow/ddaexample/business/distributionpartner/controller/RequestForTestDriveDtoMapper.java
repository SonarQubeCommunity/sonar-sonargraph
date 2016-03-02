/**
 * 23.05.2005 17:28:23 - generated 'data-transfer-object-mapper-derived' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForTestDrive'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.controller.DtoManager;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class RequestForTestDriveDtoMapper
{
    private static Logger s_Logger = Logger.getLogger(RequestForTestDriveDtoMapper.class);

    static
    {
        try
        {
            DtoManager dtoManager = DtoManager.getInstance();
            dtoManager.addDtoCtor
            (
            	com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForTestDrive.class, com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForTestDriveDto.class.getConstructor(new Class[0])
            );
            dtoManager.addDomainObjectToDtoMapper
            (
            	com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForTestDrive.class, 
            	RequestForTestDriveDtoMapper.class.getDeclaredMethod("mapDomainObjectToDto", new Class[]{com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForTestDrive.class, com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForTestDriveDto.class})
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

	private RequestForTestDriveDtoMapper()
	{
		//make it unaccessible
	}
	
	public static com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForTestDriveDto[] createDtosFromDomainObjects(com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForTestDrive[] domainObjects) throws TechnicalException
	{
		assert AssertionUtility.checkArray(domainObjects);
		com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForTestDriveDto[] createdDtos = new com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForTestDriveDto[domainObjects.length];
		 
        for (int i = 0; i < domainObjects.length; i++)
        {
        	createdDtos[i] = createDtoFromDomainObject(domainObjects[i]);
        }
        
        return createdDtos;
	}

	public static com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForTestDriveDto createDtoFromDomainObject(com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForTestDrive domainObject) throws TechnicalException
	{
		assert domainObject != null;

        DtoManager dtoManager = DtoManager.getInstance();
		Class domainObjectClass = domainObject.getClass();
		
        com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForTestDriveDto dto = 
        	(com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForTestDriveDto) dtoManager.createDto(domainObjectClass);
        
        dtoManager.mapDomainObjectToDto(domainObject, dto);

		return dto;
	}
	
	public static void mapDomainObjectToDto(com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForTestDrive domainObject, com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForTestDriveDto dto) 
		throws TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		com.hello2morrow.ddaexample.business.request.controller.RequestDtoMapper.mapDomainObjectToDto(domainObject, dto);
		
		dto.setTargetDate(domainObject.getTargetDate());
	}

	public static void mapDtoToDomainObject(com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForTestDriveDto dto, com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForTestDrive domainObject, boolean resolveReferences)
		throws BusinessException, TechnicalException
	{
		assert dto != null;
		assert domainObject != null;

		com.hello2morrow.ddaexample.business.request.controller.RequestDtoMapper.mapDtoToDomainObject(dto, domainObject, resolveReferences);
		
		domainObject.setTargetDate(dto.getTargetDate());

	}
}