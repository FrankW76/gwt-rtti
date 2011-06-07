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
package com.promis.rtti.client;

import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.promis.logging.client.Log;
import com.promis.rtti.annotations.RttiPackage;

/**
 * Gives access to RTTI class parameters.
 * Any class inside {@link RttiPackage} can have RTTI assigned 
 * (see description of {@link RttiPackage} for how-to).
 * Also all <b>primitives</b> (both simple and class representation) 
 * and {@link String} have RTTI generated by default. But primitives work
 * slightly different than in pure JAVA because only classes can be 
 * used in RTTI and so only java.lang.* is returned but 
 * <code>isPrimitive</code> returns <code>true</code>. 
 * (ie. both Boolean and boolean look like primitives)<br>
 * Note that <b>assigning</b> and <b>comparing</b> of primitives is 
 * <b>not supported</b> by RTTI (they will be supported in fields 
 * but not directly).  
 * @author SHadoW
 *
 */
public final class Rtti
{
	private Rtti() {}
	
	private static Map<String, RttiClass> typeMap =
		new TreeMap<String, RttiClass>();
	
	private static RttiAdapter adapter = null;
	
	/**
	 * Returns the {@link RttiClass} interface associated with the class 
	 * or interface with the given string name.
	 * @param className
	 * @return
	 */
	public static RttiClass forName(final String className)
	{
		if (adapter == null) adapter = GWT.create(RttiAdapter.class);
		
		RttiClass result = typeMap.get(className);
		if (result == null)
		{
			//Object in cache and doesn't have RTTI info
			if (typeMap.containsKey(className))
				return null;
			Log.info("Adding RTTI for " + className);
			result = adapter.find(className);
			typeMap.put(className, result);
		}
		
		return result;
	}
	
	/**
	 * Retrieve type information for given class type
	 * @param instance
	 * @return
	 */
	public static RttiClass getTypeInfo(Class<?> classType)
	{
		return forName(classType.getName());
	}

	/**
	 * Retrieve type information for given object
	 * @param instance
	 * @return
	 */
	public static RttiClass getTypeInfo(Object instance)
	{		
		return getTypeInfo(instance.getClass());
	}
	
	/**
	 * Returns primitive counterpart of possibly primitive class
	 * or <code>null</code> if the class doesn't have one.
	 * @param clazz
	 * @return
	 */
	public static Class<?> getPrimitive(Class<?> clazz)
	{
		if (clazz.isPrimitive()) return clazz;
		if (clazz == Boolean.class) return boolean.class;
		if (clazz == Character.class) return char.class;
		if (clazz == Byte.class) return byte.class;
		if (clazz == Short.class) return short.class;
		if (clazz == Integer.class) return int.class;
		if (clazz == Long.class) return long.class;
		if (clazz == Float.class) return float.class;
		if (clazz == Double.class) return double.class;
		if (clazz == Void.class) return void.class;
		
		return null;
	}

	/**
	 * Returns primitive class counterpart of possibly primitive type.
	 * @param clazz
	 * @return
	 */
	public static Class<?> getPrimitiveClass(Class<?> clazz)
	{
		if (clazz == boolean.class) return Boolean.class;
		if (clazz == char.class) return Character.class;
		if (clazz == byte.class) return Byte.class;
		if (clazz == short.class) return Short.class;
		if (clazz == int.class) return Integer.class;
		if (clazz == long.class) return Long.class;
		if (clazz == float.class) return Float.class;
		if (clazz == double.class) return Double.class;
		if (clazz == void.class) return Void.class;
		
		return clazz;
	}
}
