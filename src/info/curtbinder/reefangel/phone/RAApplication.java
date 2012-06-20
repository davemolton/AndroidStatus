package info.curtbinder.reefangel.phone;

/*
 * Copyright (c) 2011-12 by Curt Binder (http://curtbinder.info)
 *
 * This work is made available under the terms of the 
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

import android.app.Application;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class RAApplication extends Application {

	private static final String TAG = RAApplication.class.getSimpleName();
	private static final String NUMBER_PATTERN = "\\d+";
	private static final String HOST_PATTERN =
			"^(?i:[[0-9][a-z]]+)(?i:[\\w\\.\\-]*)(?i:[[0-9][a-z]]+)$";
	private static final String USERID_PATTERN = "[\\w\\-\\.]+";
	private SharedPreferences prefs;
	// Error code stuff
	private String[] errorCodes;
	private String[] errorCodesStrings;
	public int errorCode;
	// Devices stuff
	private String[] devicesArray;

	// Controller Data
	public RAData data;

	// Relay labels
	private int[][] relayLabels;
	private int[] relayDefaultLabels;

	// Service Stuff
	public boolean isServiceRunning;

	@Override
	public void onCreate ( ) {
		prefs = PreferenceManager.getDefaultSharedPreferences( this );
		errorCodes = getResources().getStringArray( R.array.errorCodes );
		errorCodesStrings =
				getResources().getStringArray( R.array.errorCodesStrings );
		errorCode = 0; // set to no error initially
		data = new RAData( this );
		devicesArray = getResources().getStringArray( R.array.devicesValues );
		isServiceRunning = false;

		fillRelayLabels();

		checkServiceRunning();

	}

	@Override
	public void onTerminate ( ) {
		super.onTerminate();
		data.close();

		if ( isServiceRunning )
			stopService( new Intent( this, ControllerService.class ) );
	}

	// Data handling
	public void insertData ( Intent i ) {
		ContentValues v = new ContentValues();
		v.put( RAData.PCOL_T1, i.getStringExtra( RAData.PCOL_T1 ) );
		v.put( RAData.PCOL_T2, i.getStringExtra( RAData.PCOL_T2 ) );
		v.put( RAData.PCOL_T3, i.getStringExtra( RAData.PCOL_T3 ) );
		v.put( RAData.PCOL_PH, i.getStringExtra( RAData.PCOL_PH ) );
		v.put( RAData.PCOL_DP, i.getStringExtra( RAData.PCOL_DP ) );
		v.put( RAData.PCOL_AP, i.getStringExtra( RAData.PCOL_AP ) );
		v.put( RAData.PCOL_SAL, i.getStringExtra( RAData.PCOL_SAL ) );
		v.put( RAData.PCOL_ORP, i.getStringExtra( RAData.PCOL_ORP ) );
		v.put( RAData.PCOL_ATOHI, i.getBooleanExtra( RAData.PCOL_ATOHI, false ) );
		v.put( RAData.PCOL_ATOLO, i.getBooleanExtra( RAData.PCOL_ATOLO, false ) );
		v.put( RAData.PCOL_LOGDATE, i.getStringExtra( RAData.PCOL_LOGDATE ) );
		v.put(	RAData.PCOL_RDATA,
				i.getShortExtra( RAData.PCOL_RDATA, (short) 0 ) );
		v.put(	RAData.PCOL_RONMASK,
				i.getShortExtra( RAData.PCOL_RONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_ROFFMASK,
				i.getShortExtra( RAData.PCOL_ROFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R1DATA,
				i.getShortExtra( RAData.PCOL_R1DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R1ONMASK,
				i.getShortExtra( RAData.PCOL_R1ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R1OFFMASK,
				i.getShortExtra( RAData.PCOL_R1OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R2DATA,
				i.getShortExtra( RAData.PCOL_R2DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R2ONMASK,
				i.getShortExtra( RAData.PCOL_R2ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R2OFFMASK,
				i.getShortExtra( RAData.PCOL_R2OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R3DATA,
				i.getShortExtra( RAData.PCOL_R3DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R3ONMASK,
				i.getShortExtra( RAData.PCOL_R3ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R3OFFMASK,
				i.getShortExtra( RAData.PCOL_R3OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R4DATA,
				i.getShortExtra( RAData.PCOL_R4DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R4ONMASK,
				i.getShortExtra( RAData.PCOL_R4ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R4OFFMASK,
				i.getShortExtra( RAData.PCOL_R4OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R5DATA,
				i.getShortExtra( RAData.PCOL_R5DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R5ONMASK,
				i.getShortExtra( RAData.PCOL_R5ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R5OFFMASK,
				i.getShortExtra( RAData.PCOL_R5OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R6DATA,
				i.getShortExtra( RAData.PCOL_R6DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R6ONMASK,
				i.getShortExtra( RAData.PCOL_R6ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R6OFFMASK,
				i.getShortExtra( RAData.PCOL_R6OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R7DATA,
				i.getShortExtra( RAData.PCOL_R7DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R7ONMASK,
				i.getShortExtra( RAData.PCOL_R7ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R7OFFMASK,
				i.getShortExtra( RAData.PCOL_R7OFFMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R8DATA,
				i.getShortExtra( RAData.PCOL_R8DATA, (short) 0 ) );
		v.put(	RAData.PCOL_R8ONMASK,
				i.getShortExtra( RAData.PCOL_R8ONMASK, (short) 0 ) );
		v.put(	RAData.PCOL_R8OFFMASK,
				i.getShortExtra( RAData.PCOL_R8OFFMASK, (short) 0 ) );
		v.put( RAData.PCOL_PWME0, i.getStringExtra( RAData.PCOL_PWME0 ) );
		v.put( RAData.PCOL_PWME1, i.getStringExtra( RAData.PCOL_PWME1 ) );
		v.put( RAData.PCOL_PWME2, i.getStringExtra( RAData.PCOL_PWME2 ) );
		v.put( RAData.PCOL_PWME3, i.getStringExtra( RAData.PCOL_PWME3 ) );
		v.put( RAData.PCOL_PWME4, i.getStringExtra( RAData.PCOL_PWME4 ) );
		v.put( RAData.PCOL_PWME5, i.getStringExtra( RAData.PCOL_PWME5 ) );
		v.put( RAData.PCOL_AIW, i.getByteExtra( RAData.PCOL_AIW, (byte) 0 ) );
		v.put( RAData.PCOL_AIB, i.getByteExtra( RAData.PCOL_AIB, (byte) 0 ) );
		v.put( RAData.PCOL_AIRB, i.getByteExtra( RAData.PCOL_AIRB, (byte) 0 ) );
		v.put( RAData.PCOL_RFM, i.getByteExtra( RAData.PCOL_RFM, (byte) 0 ) );
		v.put( RAData.PCOL_RFS, i.getByteExtra( RAData.PCOL_RFS, (byte) 0 ) );
		v.put( RAData.PCOL_RFD, i.getByteExtra( RAData.PCOL_RFD, (byte) 0 ) );
		v.put( RAData.PCOL_RFW, i.getByteExtra( RAData.PCOL_RFW, (byte) 0 ) );
		v.put( RAData.PCOL_RFRB, i.getByteExtra( RAData.PCOL_RFRB, (byte) 0 ) );
		v.put( RAData.PCOL_RFR, i.getByteExtra( RAData.PCOL_RFR, (byte) 0 ) );
		v.put( RAData.PCOL_RFG, i.getByteExtra( RAData.PCOL_RFG, (byte) 0 ) );
		v.put( RAData.PCOL_RFB, i.getByteExtra( RAData.PCOL_RFB, (byte) 0 ) );
		v.put( RAData.PCOL_RFI, i.getByteExtra( RAData.PCOL_RFI, (byte) 0 ) );
		v.put( RAData.PCOL_IO, i.getByteExtra( RAData.PCOL_IO, (byte) 0 ) );
		v.put( RAData.PCOL_C0, i.getByteExtra( RAData.PCOL_C0, (byte) 0 ) );
		v.put( RAData.PCOL_C1, i.getByteExtra( RAData.PCOL_C1, (byte) 0 ) );
		v.put( RAData.PCOL_C2, i.getByteExtra( RAData.PCOL_C2, (byte) 0 ) );
		v.put( RAData.PCOL_C3, i.getByteExtra( RAData.PCOL_C3, (byte) 0 ) );
		v.put( RAData.PCOL_C4, i.getByteExtra( RAData.PCOL_C4, (byte) 0 ) );
		v.put( RAData.PCOL_C5, i.getByteExtra( RAData.PCOL_C5, (byte) 0 ) );
		v.put( RAData.PCOL_C6, i.getByteExtra( RAData.PCOL_C6, (byte) 0 ) );
		v.put( RAData.PCOL_C7, i.getByteExtra( RAData.PCOL_C7, (byte) 0 ) );
		v.put( RAData.PCOL_EM, i.getByteExtra( RAData.PCOL_EM, (byte) 0 ) );
		v.put( RAData.PCOL_REM, i.getByteExtra( RAData.PCOL_REM, (byte) 0 ) );
		data.insert( v );
	}

	// Error Logging
	public void error ( int errorCodeIndex, Throwable t, String msg ) {
		errorCode = Integer.parseInt( errorCodes[errorCodeIndex] );
		Log.e( TAG, msg, t );
	}

	public String getErrorMessage ( ) {
		String s =
				(String) getResources().getText( R.string.messageUnknownError );
		// loop through array of error codes and match with the current code
		for ( int i = 0; i < errorCodes.length; i++ ) {
			if ( Integer.parseInt( errorCodes[i] ) == errorCode ) {
				// found code
				s =
						String.format(	"%s %d: %s",
										getResources()
												.getText( R.string.messageError ),
										errorCode, errorCodesStrings[i] );
				break;
			}
		}
		return s;
	}

	private boolean isNumber ( Object value ) {
		if ( (!value.toString().equals( "" ))
				&& (value.toString().matches( NUMBER_PATTERN )) ) {
			return true;
		}
		return false;
	}

	public boolean validateHost ( Object host ) {
		// host validation here
		Log.d( TAG, "Validate entered host" );
		String h = host.toString();

		// Hosts must:
		// - not start with 'http://'
		// - only contain: alpha, number, _, -, .
		// - end with: alpha or number

		if ( !h.matches( HOST_PATTERN ) ) {
			// invalid host
			Log.d( TAG, "Invalid host" );
			Toast.makeText( this,
							this.getString( R.string.prefHostInvalidHost )
									+ ": " + host.toString(),
							Toast.LENGTH_SHORT ).show();
			return false;
		}
		return true;
	}

	public boolean validatePort ( Object port ) {
		Log.d( TAG, "Validate entered port" );
		if ( !isNumber( port ) ) {
			// not a number
			Log.d( TAG, "Invalid port" );
			Toast.makeText( this,
							getString( R.string.messageNotNumber ) + ": "
									+ port.toString(), Toast.LENGTH_SHORT )
					.show();
			return false;
		} else {
			// it's a number, verify it's within range
			int min = Integer.parseInt( getString( R.string.prefPortMin ) );
			int max = Integer.parseInt( getString( R.string.prefPortMax ) );
			int v = Integer.parseInt( (String) port.toString() );

			// check if it's less than the min value or if it's greater than
			// the max value
			if ( (v < min) || (v > max) ) {
				Log.d( TAG, "Invalid port range" );
				Toast.makeText( this,
								getString( R.string.prefPortInvalidPort )
										+ ": " + port.toString(),
								Toast.LENGTH_SHORT ).show();
				return false;
			}
		}
		return true;
	}

	public boolean validateUser ( Object user ) {
		String u = user.toString();
		if ( !u.matches( USERID_PATTERN ) ) {
			// invalid userid
			Log.d( TAG, "Invalid userid" );
			Toast.makeText( this,
							getString( R.string.prefUserIdInvalid ) + ": "
									+ user.toString(), Toast.LENGTH_SHORT )
					.show();
			return false;
		}
		return true;
	}

	// Preferences
	protected void fillRelayLabels ( ) {
		relayLabels =
				new int[][] {	{	R.string.prefMainPort1LabelKey,
									R.string.prefMainPort2LabelKey,
									R.string.prefMainPort3LabelKey,
									R.string.prefMainPort4LabelKey,
									R.string.prefMainPort5LabelKey,
									R.string.prefMainPort6LabelKey,
									R.string.prefMainPort7LabelKey,
									R.string.prefMainPort8LabelKey },
								{	R.string.prefExp1Port1LabelKey,
									R.string.prefExp1Port2LabelKey,
									R.string.prefExp1Port3LabelKey,
									R.string.prefExp1Port4LabelKey,
									R.string.prefExp1Port5LabelKey,
									R.string.prefExp1Port6LabelKey,
									R.string.prefExp1Port7LabelKey,
									R.string.prefExp1Port8LabelKey },
								{	R.string.prefExp2Port1LabelKey,
									R.string.prefExp2Port2LabelKey,
									R.string.prefExp2Port3LabelKey,
									R.string.prefExp2Port4LabelKey,
									R.string.prefExp2Port5LabelKey,
									R.string.prefExp2Port6LabelKey,
									R.string.prefExp2Port7LabelKey,
									R.string.prefExp2Port8LabelKey },
								{	R.string.prefExp3Port1LabelKey,
									R.string.prefExp3Port2LabelKey,
									R.string.prefExp3Port3LabelKey,
									R.string.prefExp3Port4LabelKey,
									R.string.prefExp3Port5LabelKey,
									R.string.prefExp3Port6LabelKey,
									R.string.prefExp3Port7LabelKey,
									R.string.prefExp3Port8LabelKey },
								{	R.string.prefExp4Port1LabelKey,
									R.string.prefExp4Port2LabelKey,
									R.string.prefExp4Port3LabelKey,
									R.string.prefExp4Port4LabelKey,
									R.string.prefExp4Port5LabelKey,
									R.string.prefExp4Port6LabelKey,
									R.string.prefExp4Port7LabelKey,
									R.string.prefExp4Port8LabelKey },
								{	R.string.prefExp5Port1LabelKey,
									R.string.prefExp5Port2LabelKey,
									R.string.prefExp5Port3LabelKey,
									R.string.prefExp5Port4LabelKey,
									R.string.prefExp5Port5LabelKey,
									R.string.prefExp5Port6LabelKey,
									R.string.prefExp5Port7LabelKey,
									R.string.prefExp5Port8LabelKey },
								{	R.string.prefExp6Port1LabelKey,
									R.string.prefExp6Port2LabelKey,
									R.string.prefExp6Port3LabelKey,
									R.string.prefExp6Port4LabelKey,
									R.string.prefExp6Port5LabelKey,
									R.string.prefExp6Port6LabelKey,
									R.string.prefExp6Port7LabelKey,
									R.string.prefExp6Port8LabelKey },
								{	R.string.prefExp7Port1LabelKey,
									R.string.prefExp7Port2LabelKey,
									R.string.prefExp7Port3LabelKey,
									R.string.prefExp7Port4LabelKey,
									R.string.prefExp7Port5LabelKey,
									R.string.prefExp7Port6LabelKey,
									R.string.prefExp7Port7LabelKey,
									R.string.prefExp7Port8LabelKey },
								{	R.string.prefExp8Port1LabelKey,
									R.string.prefExp8Port2LabelKey,
									R.string.prefExp8Port3LabelKey,
									R.string.prefExp8Port4LabelKey,
									R.string.prefExp8Port5LabelKey,
									R.string.prefExp8Port6LabelKey,
									R.string.prefExp8Port7LabelKey,
									R.string.prefExp8Port8LabelKey } };
		relayDefaultLabels =
				new int[] { R.string.labelPort1,
							R.string.labelPort2,
							R.string.labelPort3,
							R.string.labelPort4,
							R.string.labelPort5,
							R.string.labelPort6,
							R.string.labelPort7,
							R.string.labelPort8 };
	}

	public boolean isCommunicateController ( ) {
		boolean b = false;
		if ( getPrefDevice().equals( devicesArray[0] ) ) {
			b = true;
		}
		return b;
	}

	public boolean useOld085xExpansionRelays ( ) {
		return prefs.getBoolean( getString( R.string.prefExp085xKey ), false );
	}
	
	public boolean useOldPre099MemoryLocations ( ) {
		return prefs.getBoolean( getString( R.string.prefPre099MemoryKey ), true );
	}

	public boolean isFirstRun ( ) {
		// First run will be determined by:
		// if the first run key is NOT set AND
		// if the host key is NOT set OR if it's the same as the default
		boolean fFirst =
				prefs.getBoolean( getString( R.string.prefFirstRunKey ), true );
		// if it's already set, no need to compare the hosts
		if ( !fFirst ) {
			Log.w( TAG, "First run already set" );
			return false;
		}

		// if it's not set (as in existing installations), check the host
		// the host should be set and it should not be the same as the default
		boolean fHost = true;
		String host = prefs.getString( getString( R.string.prefHostKey ), "" );
		if ( host.equals( "" ) )
			fHost = false;
		Log.w( TAG, "Host:  '" + host + "',  host set: " + fHost );
		if ( !fHost )
			return true;

		// if we have made it here, then it's an existing install where the user
		// has the host set to something other than the default
		// so we will go ahead and clear the first run prompt for them
		disableFirstRun();
		return false;
	}

	public void disableFirstRun ( ) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean( getString( R.string.prefFirstRunKey ), false );
		editor.commit();
	}

	protected void clearFirstRun ( ) {
		// TODO remove this function, not needed to clear first run key
		deletePref( R.string.prefFirstRunKey );
	}

	public int getSelectedProfile ( ) {
		return Integer.parseInt( prefs
				.getString( getString( R.string.prefProfileSelectedKey ),
							getString( R.string.prefProfileSelectedDefault ) ) );
	}

	public void setSelectedProfile ( int profile ) {
		if ( profile > 1 )
			return;
		String s = "" + profile;
		Log.d( TAG, "Changed Profile: " + s );
		setPref( R.string.prefProfileSelectedKey, s );
	}

	public boolean isAwayProfileEnabled ( ) {
		// get away host, compare to empty host
		// if host is set, then the profile is enabled
		// if port is not set, that implies default port
		String host = getPrefAwayHost();
		Log.d( TAG, "isAwayProfileEnabled: " + host );
		if ( host.equals( getString( R.string.prefHostAwayDefault ) ) ) {
			return false;
		}
		return true;
	}

	public String getPrefHost ( ) {
		int profile = getSelectedProfile();
		if ( profile == 1 ) {
			// Away profile
			if ( isAwayProfileEnabled() ) {
				// away profile is filled in and enabled
				// return away profile
				return getPrefAwayHost();
			}
		}
		return getPrefHomeHost();
	}

	public String getPrefPort ( ) {
		int profile = getSelectedProfile();
		if ( profile == 1 ) {
			// Away profile
			if ( isAwayProfileEnabled() ) {
				// away profile is filled in and enabled
				// return away profile
				return getPrefAwayPort();
			}
		}
		return getPrefHomePort();
	}

	public String getPrefHomeHost ( ) {
		return prefs.getString( getString( R.string.prefHostKey ),
								getString( R.string.prefHostHomeDefault ) );
	}

	public String getPrefHomePort ( ) {
		return prefs.getString( getString( R.string.prefPortKey ),
								getString( R.string.prefPortDefault ) );
	}

	public String getPrefAwayHost ( ) {
		return prefs.getString( getString( R.string.prefHostAwayKey ),
								getString( R.string.prefHostAwayDefault ) );
	}

	public String getPrefAwayPort ( ) {
		return prefs.getString( getString( R.string.prefPortAwayKey ),
								getString( R.string.prefPortDefault ) );
	}

	public boolean getPrefT2Visibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefT2VisibilityKey ),
									true );
	}

	public boolean getPrefT3Visibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefT3VisibilityKey ),
									true );
	}

	public boolean getPrefDPVisibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefDPVisibilityKey ),
									true );
	}

	public boolean getPrefAPVisibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefAPVisibilityKey ),
									true );
	}

	public boolean getPrefPHVisibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefPHVisibilityKey ),
									true );
	}

	public boolean getPrefSalinityVisibility ( ) {
		return prefs
				.getBoolean(	getString( R.string.prefSalinityVisibilityKey ),
								false );
	}

	public boolean getPrefORPVisibility ( ) {
		return prefs.getBoolean(	getString( R.string.prefORPVisibilityKey ),
									false );
	}

	public String getPrefT1Label ( ) {
		return prefs.getString( getString( R.string.prefT1LabelKey ),
								getString( R.string.labelTemp1 ) );
	}

	public String getPrefT2Label ( ) {
		return prefs.getString( getString( R.string.prefT2LabelKey ),
								getString( R.string.labelTemp2 ) );
	}

	public String getPrefT3Label ( ) {
		return prefs.getString( getString( R.string.prefT3LabelKey ),
								getString( R.string.labelTemp3 ) );
	}

	public String getPrefPHLabel ( ) {
		return prefs.getString( getString( R.string.prefPHLabelKey ),
								getString( R.string.labelPH ) );
	}

	public String getPrefDPLabel ( ) {
		return prefs.getString( getString( R.string.prefDPLabelKey ),
								getString( R.string.labelDP ) );
	}

	public String getPrefAPLabel ( ) {
		return prefs.getString( getString( R.string.prefAPLabelKey ),
								getString( R.string.labelAP ) );
	}

	public String getPrefSalinityLabel ( ) {
		return prefs.getString( getString( R.string.prefSalinityLabelKey ),
								getString( R.string.labelSalinity ) );
	}

	public String getPrefORPLabel ( ) {
		return prefs.getString( getString( R.string.prefORPLabelKey ),
								getString( R.string.labelORP ) );
	}

	public String getPrefMainRelayLabel ( int port ) {
		return getPrefRelayLabel( 0, port );
	}

	public String getPrefRelayLabel ( int relay, int port ) {
		return prefs.getString( getString( relayLabels[relay][port] ),
								getString( relayDefaultLabels[port] ) );
	}

	public void setPrefRelayLabel ( int relay, int port, String label ) {
		setPref( getString( relayLabels[relay][port] ), label );
	}

	public int getPrefRelayKey ( int relay, int port ) {
		return relayLabels[relay][port];
	}

	public void setPref ( String key, String value ) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString( key, value );
		editor.commit();
	}

	public void setPref ( int keyid, String value ) {
		setPref( getString( keyid ), value );
	}

	public void deletePref ( int keyid ) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove( getString( keyid ) );
		editor.commit();
	}

	public String getPrefDevice ( ) {
		return prefs.getString( getString( R.string.prefDeviceKey ),
								getString( R.string.prefDeviceDefault ) );
	}

	public String getPrefUserId ( ) {
		return prefs.getString( getString( R.string.prefUserIdKey ),
								getString( R.string.prefUserIdDefault ) );
	}

	public int getPrefExpansionRelayQuantity ( ) {
		return Integer.parseInt( prefs
				.getString( getString( R.string.prefExpQtyKey ), "0" ) );
	}

	public void checkServiceRunning ( ) {
		// Check if the service is running, if not start it
		if ( !isServiceRunning && !isFirstRun() )
			startService( new Intent( this, ControllerService.class ) );
	}
}
