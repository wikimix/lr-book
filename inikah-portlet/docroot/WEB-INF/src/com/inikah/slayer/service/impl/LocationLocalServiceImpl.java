/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.inikah.slayer.service.impl;

import java.util.List;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.RefinedSoundex;

import com.inikah.slayer.model.Location;
import com.inikah.slayer.service.base.LocationLocalServiceBaseImpl;
import com.inikah.util.IConstants;
import com.inikah.util.NotifyUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Country;
import com.liferay.portal.service.CountryServiceUtil;

/**
 * The implementation of the location local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.inikah.slayer.service.LocationLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Ahmed Hasan
 * @see com.inikah.slayer.service.base.LocationLocalServiceBaseImpl
 * @see com.inikah.slayer.service.LocationLocalServiceUtil
 */
public class LocationLocalServiceImpl extends LocationLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.inikah.slayer.service.LocationLocalServiceUtil} to access the location local service.
	 */
	
	public Location getLocation(long parentId, String name, int locType, long userId) {
		Location location = null;
		try {
			location = locationPersistence.fetchByParentId_Name(parentId, name, locType);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(location)) return location;
		
		long locationId = 0l;
		try {
			locationId = counterLocalService.increment(Location.class.getName());
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		location = locationPersistence.create(locationId);
		
		location.setParentId(parentId);
		location.setName(name);
		location.setLocType(locType);
		
		try {
			location = locationPersistence.update(location);
			NotifyUtil.newCityCreated(location);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return location;		
	}
	
	public Location getLocation(long parentId, String code, String name, long userId) {
		
		Location location = null;
		try {
			location = locationPersistence.fetchByParentId_Code(parentId, code, IConstants.LOC_TYPE_REGION);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(location)) return location;
		
		long locationId = 0l;
		try {
			locationId = counterLocalService.increment(Location.class.getName());
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		location = locationPersistence.create(locationId);
		
		location.setParentId(parentId);
		location.setCode(code);
		location.setLocType(IConstants.LOC_TYPE_REGION);
		location.setUserId(userId);
		location.setName(name);
		
		if (name.startsWith("State Of")) {
			name = StringUtil.replace(name, "State Of ", StringPool.BLANK);
		}

		try {
			location = locationPersistence.update(location);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return location;
	}
	
	public List<Location> getLocations(long parentId, int locType) {
		List<Location> locations = null;
		try {
			locations = locationPersistence.findByParentId_LocType(parentId, locType);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return locations;
	}
	
	public String getDisplayInfo(long cityId) {
		StringBuilder sb = new StringBuilder();

		try {
			Location location = locationPersistence.fetchByPrimaryKey(cityId);
			sb.append(location.getName()).append(StringPool.COMMA).append(StringPool.SPACE);
			
			location = locationPersistence.fetchByPrimaryKey(location.getParentId());
			sb.append(location.getName()).append(StringPool.COMMA).append(StringPool.SPACE);
			
			Country country = CountryServiceUtil.fetchCountry(location.getParentId());
			sb.append(country.getName());
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	public long insertCity(long regionId, String name, long userId) {
		
		long cityId = 0l;
		
		List<Location> cities = null;
		try {
			cities = locationPersistence.findByParentId_LocType(regionId, IConstants.LOC_TYPE_CITY);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		// check for soundex
		
		for (Location city: cities) {
			int diff = 0;
			try {
				diff = RefinedSoundex.US_ENGLISH.difference(city.getName(), name);
			} catch (EncoderException e) {
				e.printStackTrace();
			}
			
			if (diff >= 3) {
				cityId = city.getLocationId();
				break;
			}
		}
		
		if (cityId == 0l) {
			try {
				cityId = counterLocalService.increment(Location.class.getName());
			} catch (SystemException e) {
				e.printStackTrace();
			}
			Location city = createLocation(cityId);
			city.setName(name);
			city.setUserId(userId);
			city.setLocType(IConstants.LOC_TYPE_CITY);
			city.setParentId(regionId);
			city.setActive_(false);
			
			try {
				addLocation(city);
				NotifyUtil.newCityCreated(city);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		
		return cityId;
	}
}