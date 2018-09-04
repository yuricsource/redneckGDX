package ru.m210projects.Redneck.Types;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Date {

	private DateFormat dateFormat;
	private long launchDate;
	
	public Date(String format)
	{
		dateFormat = new SimpleDateFormat(format, Locale.US);
		launchDate = getCurrentDate();
	}
	
	public String getDate(long time)
	{
		return dateFormat.format(time);
	}
	
	public String getLaunchDate()
	{
		return dateFormat.format(launchDate);
	}
	
	public long getCurrentDate()
	{
		return System.currentTimeMillis();
	}
}
