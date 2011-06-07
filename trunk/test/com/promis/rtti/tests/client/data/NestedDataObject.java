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

import com.promis.rtti.client.RttiObject;

/**
 * Test object do not modify
 * @author SHadoW
 *
 */
public class NestedDataObject extends ImplicitRttiObject
{
	public RttiObject nested = new ImplicitRttiObject();
	public final int finalField = 0;
	
	public int testGetter = 0;
	
	public int getTestGetter()
	{
		return testGetter + 1;
	}
}
