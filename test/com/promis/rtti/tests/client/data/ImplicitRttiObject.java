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

import com.promis.rtti.annotations.Index;
import com.promis.rtti.annotations.ReadOnly;
import com.promis.rtti.client.Persistent;
import com.promis.rtti.client.annotations.NoCompare;
import com.promis.rtti.tests.client.TestConsts;
import com.promis.rtti.tests.client.data.annotations.TestAnnotation;
import com.promis.rtti.tests.client.data.annotations.TestAnnotation2;

/**
 * Test object do not modify
 * @author SHadoW
 *
 */
@TestAnnotation(aString = "one")
@TestAnnotation2
public class ImplicitRttiObject extends Persistent
{
	//Read/write via getter/setter
	String name = TestConsts.FIELD_STRING;
	@Index(1)
	public int i1 = 1;
	@Index(2)
	public int i2 = 2;
	@Index(3)
	@TestAnnotation(aString = TestConsts.ANNOT_STRING)
	public int i3 = 3;
	//Read only via getter
	@Index(4)
	@TestAnnotation(aString = TestConsts.ANNOT_STRING)
	int i4 = 4;
	@Index(5)
	@ReadOnly
	@NoCompare
	public int i5 = 5;
	
	int notAProp;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		if (name == null) this.name = null;
		else this.name = new String(name);
	}
	
	public int getI4()
	{
		return i4;
	}

}
