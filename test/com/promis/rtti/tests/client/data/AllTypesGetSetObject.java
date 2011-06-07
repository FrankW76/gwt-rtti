/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is GWT RTTI library.
 *
 * The Initial Developer of the Original Code is
 * Jan "SHadoW" Rames <ramejan@gmail.com>.
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 */
package com.promis.rtti.tests.client.data;

import com.promis.rtti.annotations.GenerateMethods;
import com.promis.rtti.client.Persistent;
import com.promis.rtti.tests.client.TestConsts;
import com.promis.rtti.tests.client.data.annotations.TestAnnotation;

/**
 * Test object do not modify
 * @author SHadoW
 *
 */
@GenerateMethods
public class AllTypesGetSetObject extends Persistent
{
	String aString;
	
	boolean aBoolean;

	char aChar;

	byte aByte;
	short aShort;
	int aInt;
	long aLong;
	
	float aFloat;
	double aDouble;
	
	Object aObject = null;
	
	public AllTypesGetSetObject()
	{
	}
	
	public AllTypesGetSetObject(String someParam)
	{
		this();
	}
	
	public String getaString()
	{
		return aString;
	}
	public void setaString(String aString)
	{
		if (aString == null) this.aString = null;
		else this.aString = new String(aString);
	}
	public boolean isaBoolean()
	{
		return aBoolean;
	}
	public void setaBoolean(boolean aBoolean)
	{
		this.aBoolean = aBoolean;
	}
	public char getaChar()
	{
		return aChar;
	}
	public void setaChar(char aChar)
	{
		this.aChar = aChar;
	}
	public byte getAByte()
	{
		return aByte;
	}
	public void setAByte(byte aByte)
	{
		this.aByte = aByte;
	}
	public short getaShort()
	{
		return aShort;
	}
	public void setaShort(short aShort)
	{
		this.aShort = aShort;
	}
	public int getaInt()
	{
		return aInt;
	}
	public void setaInt(int aInt)
	{
		this.aInt = aInt;
	}
	public long getaLong()
	{
		return aLong;
	}
	public void setaLong(long aLong)
	{
		this.aLong = aLong;
	}
	public float getaFloat()
	{
		return aFloat;
	}
	public void setaFloat(float aFloat)
	{
		this.aFloat = aFloat;
	}
	public double getaDouble()
	{
		return aDouble;
	}
	public void setaDouble(double aDouble)
	{
		this.aDouble = aDouble;
	}
	public Object getaObject()
	{
		return aObject;
	}
	
	@TestAnnotation(aString = TestConsts.METHOD)
	public void setaObject(Object aObject)
	{
		this.aObject = aObject;
	}
	
	public String test(String someParam, int aInt, Integer aInteger)
	{
		return someParam + (aInt + aInteger);
	}
	
	public String test(String someParam)
	{
		return someParam;
	}
	
	public void exception(int type)
	{
		switch (type)
		{
		case 1: throw new ArithmeticException();
		default: throw new RuntimeException();
		}
	}
}
