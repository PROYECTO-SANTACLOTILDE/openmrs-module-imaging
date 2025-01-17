package org.openmrs.module.imaging;

import java.util.UUID;

public class ImagingConstants {
	
	public static final String MODULE_ID = "imaging";
	
	public static final String APP_IMAGING = MODULE_ID + ".configure";
	
	public static final String GP_SERVER_UUID = MODULE_ID + ".serverUuid";
	
	public static final String GP_SERVER_URL = MODULE_ID + ".serverUrl";
	
	public static final String GP_SERVER_username = MODULE_ID + ".userName";
	
	public static final String GP_SERVER_password = MODULE_ID + ".userPassword";
	
	public static final String GP_VALIDATION_TYPE = MODULE_ID + ".validationType";
	
	public static final String GP_IMAGING_AT_STARTUP_PATH = MODULE_ID + ".imagingLoadAtStartupPath";
	
	public static final UUID OPEN_IMAGING_NAMESPACE_UUID = UUID.fromString("672c6cb2-4f47-4cb0-9fca-b5f6116cd33a");
	
	/**
	 * PRIVILEGES
	 */
	public static final String VIEW_STUDIES = "View studies";
	
	public static final String STUDIES_PAGE_INCLUDE_FRAGMENT_EXTENSION_POINT = "studiesList.includeFragments";
	
	public static final String SERIES_PAGE_INCLUDE_FRAGMENT_EXTENSION_POINT = "seriesList.includeFragments";
}
