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
package com.promis.rtti.tests.client;

import java.lang.annotation.Annotation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Command;
import com.promis.rtti.client.Persistent;
import com.promis.rtti.client.Rtti;
import com.promis.rtti.client.RttiClass;
import com.promis.rtti.client.RttiConst;
import com.promis.rtti.client.RttiField;
import com.promis.rtti.client.RttiIllegalArgumentException;
import com.promis.rtti.client.RttiInvocationException;
import com.promis.rtti.client.RttiMethod;
import com.promis.rtti.client.RttiObject;
import com.promis.rtti.client.RttiReadOnlyException;
import com.promis.rtti.client.UnableToAssignException;
import com.promis.rtti.client.annotations.NoCompare;
import com.promis.rtti.tests.client.data.AllTypesGetSetObject;
import com.promis.rtti.tests.client.data.AllTypesObject;
import com.promis.rtti.tests.client.data.AnnotationTestObject;
import com.promis.rtti.tests.client.data.EnumTestObject;
import com.promis.rtti.tests.client.data.EnumTestObject.Fruit;
import com.promis.rtti.tests.client.data.ExplicitRttiObject;
import com.promis.rtti.tests.client.data.ImplicitRttiObject;
import com.promis.rtti.tests.client.data.NestedDataObject;
import com.promis.rtti.tests.client.data.NoAssignCompareObject;
import com.promis.rtti.tests.client.data.NonRttiObject;
import com.promis.rtti.tests.client.data.TestEnum;
import com.promis.rtti.tests.client.data.TestInterface;
import com.promis.rtti.tests.client.data.annotations.AllTypes;
import com.promis.rtti.tests.client.data.annotations.TestAnnotation;
import com.promis.rtti.tests.client.data.annotations.TestAnnotation2;

public class RttiTest extends GWTTestCase
{
	/**
	 * Some test assertions may be different in JavaScript
	 */
	static final boolean isProd = GWT.isProdMode();
	@Override
	public String getModuleName()
	{
		return "com.promis.rtti.tests.RttiTest";
	}
	
	public void testBootup()
	{
	}

	//TODO Support for arrays
	public void testGetTypeInfo()
	{
		RttiClass res;
		res = Rtti.forName(ImplicitRttiObject.class.getName());
		assertNotNull(res);
		assertEquals(ImplicitRttiObject.class.getName(), res.getName());
		
		res = Rtti.getTypeInfo(ImplicitRttiObject.class);
		assertNotNull(res);
		assertEquals(ImplicitRttiObject.class.getName(), res.getName());
		
		res = Rtti.getTypeInfo(ExplicitRttiObject.class);
		assertNotNull(res);
		assertEquals(ExplicitRttiObject.class.getName(), res.getName());
		
		res = Rtti.getTypeInfo(NonRttiObject.class);
		assertNull(res);
		
		ImplicitRttiObject o = new ImplicitRttiObject();
		res = Rtti.getTypeInfo(o);
		assertNotNull(res);
		assertEquals(o.getClass().getName(), res.getName());
	}
	
	public void testRttiEntity()
	{
		RttiClass res;
		res = Rtti.getTypeInfo(ImplicitRttiObject.class);
		Annotation[] annots = res.getAnnotations();
		//There should be two annotations
		assertEquals(2, annots.length);
		//Assert both are correct type
		assertEquals(TestAnnotation.class, 
				res.getAnnotation(TestAnnotation.class).annotationType());
		assertEquals(TestAnnotation2.class, 
				res.getAnnotation(TestAnnotation2.class).annotationType());
		
		Annotation annot = res.getAnnotation(TestAnnotation.class);
		assertNotNull(annot);
		assertTrue(annot instanceof TestAnnotation);		
	}
	
	public void testRttiObject()
	{
		RttiClass res;
		res = Rtti.getTypeInfo(ImplicitRttiObject.class);
		assertNotNull(res);
		assertFalse(res.isAnnotation());
		assertFalse(res.isInterface());
		assertTrue(res.isClass());
		
		assertEquals(6, res.getFields().length);
		getField(res, "name");
		
		assertNull(res.getField("notAProp"));
		assertNull(res.getField("nonExistentField"));
		
		int index = 4;
		RttiField prop = res.findIndexedField(index);
		assertNotNull(prop);
		assertEquals(index, prop.getIndex());
		
		//Test RTTI inheritance support
		res = Rtti.getTypeInfo(NestedDataObject.class);
		assertNotNull(res);
		res = Rtti.getTypeInfo(res.getClazz());
		assertNotNull(res);
		assertEquals(res.getName(), NestedDataObject.class.getName());
		assertEquals(9, res.getFields().length);
		res = Rtti.getTypeInfo(res.getClazz().getSuperclass());
		assertNotNull(res);
		assertEquals(res.getName(), ImplicitRttiObject.class.getName());
		assertEquals(6, res.getFields().length);
	}
	
	public void testNewInstance()
	{
		RttiClass info = Rtti.getTypeInfo(ImplicitRttiObject.class);
		assertNotNull(info);
		Object o = info.newInstance();
		assertNotNull(o);
		assertEquals(o.getClass(), ImplicitRttiObject.class);
		
		info = Rtti.getTypeInfo(TestInterface.class);
		assertNotNull(info);
		o = info.newInstance();
		assertNull(o);
	}
	
	void testPrimitive(Class<?> clazz)
	{
		RttiClass res = Rtti.getTypeInfo(clazz);
		assertNotNull(res);
		assertTrue(res.isPrimitive());
		
		//True primitives need slightly different handling because RTTI can't support them natively
		if (clazz.isPrimitive())
		{
			assertEquals(clazz, Rtti.getPrimitive(res.getClazz()));
			//This way Rtti.getPrimitiveClass is also tested
			assertEquals(Rtti.getPrimitive(clazz), Rtti.getPrimitive(res.getClazz()));
		}
		else 
		{
			assertEquals(clazz, res.getClazz());
			assertEquals(Rtti.getPrimitive(clazz), Rtti.getPrimitive(res.getClazz()));
		}
	}
	
	public void testRttiPrimitives()
	{
		testPrimitive(boolean.class);
		testPrimitive(Boolean.class);
		testPrimitive(int.class);
		testPrimitive(Integer.class);
		testPrimitive(char.class);
		testPrimitive(Character.class);
		testPrimitive(byte.class);
		testPrimitive(Byte.class);
		testPrimitive(short.class);
		testPrimitive(Short.class);
		testPrimitive(long.class);
		testPrimitive(Long.class);
		testPrimitive(float.class);
		testPrimitive(Float.class);
		testPrimitive(double.class);
		testPrimitive(Double.class);
		testPrimitive(void.class);
		testPrimitive(Void.class);

		RttiClass res = Rtti.getTypeInfo(String.class);
		assertNotNull(res);
		assertFalse(res.isPrimitive());
		assertEquals(String.class, res.getClazz());
	}
	
	public void testGetPrimitiveClass()
	{
		//getPrimitive
		assertNull(Rtti.getPrimitive(String.class));
		assertNull(Rtti.getPrimitive(Object.class));

		assertEquals(Rtti.getPrimitive(boolean.class), boolean.class);
		assertEquals(Rtti.getPrimitive(Boolean.class), boolean.class);

		assertEquals(Rtti.getPrimitive(int.class), int.class);
		assertEquals(Rtti.getPrimitive(Integer.class), int.class);

		assertEquals(Rtti.getPrimitive(char.class), char.class);
		assertEquals(Rtti.getPrimitive(Character.class), char.class);

		assertEquals(Rtti.getPrimitive(byte.class), byte.class);
		assertEquals(Rtti.getPrimitive(Byte.class), byte.class);

		assertEquals(Rtti.getPrimitive(boolean.class), boolean.class);
		assertEquals(Rtti.getPrimitive(Boolean.class), boolean.class);

		assertEquals(Rtti.getPrimitive(short.class), short.class);
		assertEquals(Rtti.getPrimitive(Short.class), short.class);

		assertEquals(Rtti.getPrimitive(long.class), long.class);
		assertEquals(Rtti.getPrimitive(Long.class), long.class);

		assertEquals(Rtti.getPrimitive(float.class), float.class);
		assertEquals(Rtti.getPrimitive(Float.class), float.class);

		assertEquals(Rtti.getPrimitive(double.class), double.class);
		assertEquals(Rtti.getPrimitive(Double.class), double.class);

		assertEquals(Rtti.getPrimitive(void.class), void.class);
		assertEquals(Rtti.getPrimitive(Void.class), void.class);
		
		//getPrimitiveClass
		assertEquals(Rtti.getPrimitiveClass(String.class), String.class);
		assertEquals(Rtti.getPrimitiveClass(Object.class), Object.class);

		assertEquals(Rtti.getPrimitiveClass(boolean.class), Boolean.class);
		assertEquals(Rtti.getPrimitiveClass(Boolean.class), Boolean.class);

		assertEquals(Rtti.getPrimitiveClass(int.class), Integer.class);
		assertEquals(Rtti.getPrimitiveClass(Integer.class), Integer.class);

		assertEquals(Rtti.getPrimitiveClass(char.class), Character.class);
		assertEquals(Rtti.getPrimitiveClass(Character.class), Character.class);

		assertEquals(Rtti.getPrimitiveClass(byte.class), Byte.class);
		assertEquals(Rtti.getPrimitiveClass(Byte.class), Byte.class);

		assertEquals(Rtti.getPrimitiveClass(boolean.class), Boolean.class);
		assertEquals(Rtti.getPrimitiveClass(Boolean.class), Boolean.class);

		assertEquals(Rtti.getPrimitiveClass(short.class), Short.class);
		assertEquals(Rtti.getPrimitiveClass(Short.class), Short.class);

		assertEquals(Rtti.getPrimitiveClass(long.class), Long.class);
		assertEquals(Rtti.getPrimitiveClass(Long.class), Long.class);

		assertEquals(Rtti.getPrimitiveClass(float.class), Float.class);
		assertEquals(Rtti.getPrimitiveClass(Float.class), Float.class);

		assertEquals(Rtti.getPrimitiveClass(double.class), Double.class);
		assertEquals(Rtti.getPrimitiveClass(Double.class), Double.class);

		assertEquals(Rtti.getPrimitiveClass(void.class), Void.class);
		assertEquals(Rtti.getPrimitiveClass(Void.class), Void.class);
	}
	
	public void testRttiAnnotation()
	{
		RttiClass res;
		res = Rtti.getTypeInfo(ImplicitRttiObject.class);
		assertNotNull(res);
		
		Annotation annot = res.getAnnotation(TestAnnotation2.class);
		assertNotNull(annot);
		assertEquals(TestAnnotation2.class.getName(), annot.annotationType().getName());
		
		//Test nested annotations
		Class<?> annotType = annot.annotationType();
		assertEquals(annotType, TestAnnotation2.class);
		RttiClass annotClass =  Rtti.getTypeInfo(annotType);
		assertNotNull(annotClass);
		Annotation[] annots2 = annotClass.getAnnotations();
		assertEquals(1, annots2.length);
		assertEquals(TestAnnotation.class, annots2[0].annotationType());
		assertEquals(TestConsts.TWO, ((TestAnnotation)annots2[0]).aString());
		assertNotNull(annotClass.getAnnotation(TestAnnotation.class));
		assertTrue(annotClass.isAnnotation());
		assertTrue(annotClass.isInterface());
		assertFalse(annotClass.isClass());
		
		//Test all primitives
		res = Rtti.getTypeInfo(AnnotationTestObject.class);
		assertNotNull(res);
		AllTypes a = res.getAnnotation(AllTypes.class);
		assertNotNull(a);
		assertEquals(false, a.aBoolean());
		assertEquals(0, a.aByte());
		assertEquals((double)0, a.aDouble());
		assertEquals('a', a.aChar());
		assertEquals((float)0, a.aFloat());
		assertEquals(0, a.aInt());
		assertEquals(0, a.aLong());
		assertEquals(0, a.aShort());
		assertEquals(TestConsts.ONE, a.aString());
	}
	
	private RttiField getField(RttiClass info, String name)
	{
		RttiField prop = info.getField(name);
		assertNotNull(prop);
		assertEquals(name, prop.getName());
		return prop;
	}
	
	private <E> E getPropValue(RttiObject o, String name, Class<E> clazz)
	{
		RttiClass info = Rtti.getTypeInfo(o);
		assertNotNull(info);
		
		RttiField prop = getField(info, name);
		return prop.get(o, clazz);
	}

	private <E> void setPropValue(RttiObject o, String name, E value)
	{
		RttiClass info = Rtti.getTypeInfo(o);
		assertNotNull(info);
		
		RttiField prop = getField(info, name);
		prop.set(o, value);
	}
	
	private <E> void testWriteException(RttiObject o, RttiField prop,
			E value)
	{
		boolean hasException = false;
		try
		{
			prop.set(o, value);
		}
		catch (RttiReadOnlyException e) 
		{
			hasException = true;
		}
		catch (ClassCastException e) 
		{
			hasException = true;
		}
		
		assertTrue(hasException);
	}
	
	private <E> void testReadException(RttiObject o, RttiField prop,
			Class<E> clazz)
	{
		boolean hasException = false;
		try
		{
			prop.get(o, clazz);
		}
		catch (ClassCastException e) 
		{
			hasException = true;
		}
		
		assertTrue(hasException);
	}
	
	private void testObject(RttiObject o)
	{
		RttiField prop;
		
		RttiClass info = Rtti.getTypeInfo(o);
		assertNotNull(info);
		
		prop = getField(info, "name");
		assertEquals(RttiConst.DEFAULT_INDEX, prop.getIndex());				
		assertEquals(0, prop.getAnnotations().length);
		assertEquals(String.class, prop.getType());
		assertFalse(prop.isReadonly());
		testWriteException(o, prop, 0);
		testReadException(o, prop, Integer.class);
		
		prop = getField(info, "i1");
		assertEquals(1, prop.getIndex());				
		assertEquals(0, prop.getAnnotations().length);
		assertEquals(Integer.class, prop.getType());
		assertFalse(prop.isReadonly());
		testWriteException(o, prop, "");
		testReadException(o, prop, String.class);
		
		prop = getField(info, "i2");
		assertEquals(2, prop.getIndex());				
		assertEquals(0, prop.getAnnotations().length);
		assertEquals(Integer.class, prop.getType());
		assertFalse(prop.isReadonly());

		prop = getField(info, "i3");
		assertEquals(3, prop.getIndex());				
		assertEquals(1, prop.getAnnotations().length);
		Annotation annot = prop.getAnnotation(TestAnnotation.class);
		assertNotNull(annot);
		assertEquals(TestConsts.ANNOT_STRING, ((TestAnnotation)annot).aString());
		assertEquals(Integer.class, prop.getType());
		assertFalse(prop.isReadonly());

		prop = getField(info, "i4");
		assertEquals(4, prop.getIndex());				
		assertEquals(1, prop.getAnnotations().length);
		annot = prop.getAnnotation(TestAnnotation.class);
		assertNotNull(annot);
		assertEquals(TestConsts.ANNOT_STRING, ((TestAnnotation)annot).aString());
		assertEquals(Integer.class, prop.getType());
		assertTrue(prop.isReadonly());
		testWriteException(o, prop, 0);

		prop = getField(info, "i5");
		assertEquals(5, prop.getIndex());				
		assertEquals(1, prop.getAnnotations().length);
		annot = prop.getAnnotation(NoCompare.class);
		assertNotNull(annot);
		assertEquals(Integer.class, prop.getType());
		assertTrue(prop.isReadonly());
		testWriteException(o, prop, 0);
	}
	
	public void testRttiField()
	{
		//This test field structure value assignment tested later
		RttiObject o = new NestedDataObject();
		
		testObject(o);
		
		RttiField prop;
		
		RttiClass info = Rtti.getTypeInfo(o);
		assertNotNull(info);
		
		prop = getField(info, "testGetter");
		assertEquals(RttiConst.DEFAULT_INDEX, prop.getIndex());				
		assertEquals(0, prop.getAnnotations().length);
		assertEquals(Integer.class, prop.getType());
		assertFalse(prop.isReadonly());	//RO by final
		int i = (int)(Math.random() * 1000);
		prop.set(o, i);
		assertEquals(i + 1, (int)prop.get(o, int.class));

		prop = getField(info, "nested");
		assertEquals(RttiConst.DEFAULT_INDEX, prop.getIndex());				
		assertEquals(0, prop.getAnnotations().length);
		assertEquals(RttiObject.class, prop.getType());
		assertFalse(prop.isReadonly());
		testWriteException(o, prop, "bad object class");
		
		o = prop.get(o, ImplicitRttiObject.class);
		assertNotNull(o);
		assertTrue(o instanceof ImplicitRttiObject);
		testObject(o);

		prop = getField(info, "finalField");
		assertEquals(RttiConst.DEFAULT_INDEX, prop.getIndex());				
		assertEquals(0, prop.getAnnotations().length);
		assertEquals(Integer.class, prop.getType());
		assertTrue(prop.isReadonly());	//RO by final
}
	
	private <E> void testRttiFieldValue(RttiObject o, String name, E test)
	{
		RttiClass info = Rtti.getTypeInfo(o);
		assertNotNull(info);
		
		RttiField prop = info.getField(name);
		assertNotNull(prop);
		assertEquals(name, prop.getName());

		prop.set(o, test);
		Object val = prop.get(o);
		if (val != null) assertTrue(val.getClass().equals(test.getClass()));
		assertEquals(test, val);	
	}
	
	private void testRttiFieldAssignment(RttiObject o)
	{
		testRttiFieldValue(o, "aString", TestConsts.FIELD_STRING);
		testRttiFieldValue(o, "aString", (String)null);
		testRttiFieldValue(o, "aBoolean", true);
		testRttiFieldValue(o, "aBoolean", false);
		testRttiFieldValue(o, "aChar", 'W');
		testRttiFieldValue(o, "aByte", (byte)120);
		testRttiFieldValue(o, "aShort", (short)65535);
		testRttiFieldValue(o, "aInt", (int)45645645);
		testRttiFieldValue(o, "aLong", 0x100000000L);
		testRttiFieldValue(o, "aFloat", 0.001f);
		testRttiFieldValue(o, "aDouble", (double)1.598e10);
		testRttiFieldValue(o, "aObject", new String(TestConsts.FIELD_STRING));
	}
	
	public void testRttiFieldDirectAssignment()
	{
		AllTypesObject o = new AllTypesObject();
		testRttiFieldAssignment(o);
	}
	
	public void testRttiFieldGetSetAssignment()
	{
		AllTypesGetSetObject o = new AllTypesGetSetObject();
		testRttiFieldAssignment(o);
	}
	
	public void testAssignException(Command callback)
	{
		boolean hasException = false;
		try
		{
			callback.execute();
		}
		catch (UnableToAssignException e)
		{
			hasException = true;
		}
		assertTrue(hasException);	
	}
	
	public void testAssign()
	{
		final NestedDataObject o1 = new NestedDataObject();
		ImplicitRttiObject o2 = new ImplicitRttiObject();
		NestedDataObject o3 = new NestedDataObject();
		
		//Both objects should be assignable to one another
		o1.assign(o2);
		o2.assign(o1);
		
		o1.assign(o1);
		
		//Test simple value assignments
		//These should NOT be same
		o3.i5 = 654564;
		o1.i5 = 564742; 
		//Rest should
		o1.setName("some random name " + (Math.random() * 65535));
		o1.i1 = (int)(Math.random() * 65535);
		o1.i2 = (int)(Math.random() * 65535);
		o1.i3 = (int)(Math.random() * 65535);
		
		o3.assign(o1);
		if (! isProd)
		{
			assertNotSame(o1.getName(), o3.getName());	//Name object is duplicated (see setter)
		}
		assertEquals(o1.getName(), o3.getName());
		assertEquals(o1.i1, o3.i1);
		assertEquals(o1.i2, o3.i2);
		assertEquals(o1.i3, o3.i3);
		assertSame(o1.nested, o3.nested);
		//Rest are read-only
		assertFalse(o1.i5 == o3.i5);
		
		//Test that descendant's properties wont'change
		o3.assign(o2);
		if (! isProd)
		{
			assertNotSame(o2.getName(), o3.getName());	//Name object is duplicated (see setter)
		}
		assertEquals(o2.getName(), o3.getName());
		assertEquals(o2.i1, o3.i1);
		assertEquals(o2.i2, o3.i2);
		assertEquals(o2.i3, o3.i3);
		assertSame(o1.nested, o3.nested);	//This must remain unchanged
		
		//Test unassignable objects
		testAssignException(new Command()
		{
			@Override
			public void execute()
			{
				Persistent p = new Persistent(){};
				p.assign(o1);
			}
		});			
		testAssignException(new Command()
		{
			@Override
			public void execute()
			{
				ExplicitRttiObject p = new ExplicitRttiObject();
				o1.assign(p);
			}
		});
		testAssignException(new Command()
		{
			@Override
			public void execute()
			{
				NonRttiObject p = new NonRttiObject(){};
				o1.assign(p);
			}
		});			

		
		//Test that all supported types can be assigned
	}
	
	private void prepRttiAssign(RttiObject o)
	{
		setPropValue(o, "aString", TestConsts.FIELD_STRING);
		/*setPropValue(o, "aString", (String)null);
		setPropValue(o, "aBoolean", true);*/
		setPropValue(o, "aBoolean", false);
		setPropValue(o, "aChar", 'K');
		setPropValue(o, "aByte", (byte)2);
		setPropValue(o, "aShort", (short)5558);
		setPropValue(o, "aInt", (int)789979);
		setPropValue(o, "aLong", 0x1AAAAAAAAL);
		setPropValue(o, "aFloat", 0.0001f);
		setPropValue(o, "aDouble", (double)1.899e-8);
		setPropValue(o, "aObject", new String(TestConsts.FIELD_STRING));
	}
	
	private <E> void testRttiAssignValue(RttiObject o1, RttiObject o2, 
			String name, Class<E> clazz)
	{
		assertEquals(getPropValue(o1, name, clazz), getPropValue(o2, name, clazz));
	}
	
	private void testRttiAssign(RttiObject o1, Persistent o2)
	{
		prepRttiAssign(o1);
		
		o2.assign(o1);
		
		testRttiAssignValue(o1, o2, "aString", String.class);
		testRttiAssignValue(o1, o2, "aBoolean", boolean.class);
		testRttiAssignValue(o1, o2, "aChar", char.class);
		testRttiAssignValue(o1, o2, "aByte", byte.class);
		testRttiAssignValue(o1, o2, "aShort", short.class);
		testRttiAssignValue(o1, o2, "aInt", int.class);
		testRttiAssignValue(o1, o2, "aLong", long.class);
		testRttiAssignValue(o1, o2, "aFloat", float.class);
		testRttiAssignValue(o1, o2, "aDouble", double.class);
		testRttiAssignValue(o1, o2, "aObject", Object.class);
	}

	public void testRttiAssignDirectAssignment()
	{
		AllTypesObject o1 = new AllTypesObject();
		AllTypesObject o2 = new AllTypesObject();
		testRttiAssign(o1, o2);
		assertSame(o1.aString, o2.aString);
		o1.aString = null;
		o2.assign(o1);
		assertSame(o1.aString, o2.aString);
	}
	
	public void testRttiAssignGetSetAssignment()
	{
		AllTypesGetSetObject o1 = new AllTypesGetSetObject();
		AllTypesGetSetObject o2 = new AllTypesGetSetObject();
		testRttiAssign(o1, o2);
		if (! isProd)
		{
			assertNotSame(o1.getaString(), o2.getaString());
		}
		o1.setaString(null);
		o2.assign(o1);
		assertSame(o1.getaString(), o2.getaString());
	}
	
	public void testCompare()
	{
		NestedDataObject o1 = new NestedDataObject();
		NestedDataObject o2 = new NestedDataObject();
		ImplicitRttiObject o3 = new ImplicitRttiObject();
		
		//Some basic functionality (including nested comparing)
		assertTrue(o1.compare(o1));
		assertTrue(o2.compare(o1));
		assertFalse(o3.compare(o1));
		assertFalse(o1.compare(o3));

		//Test NoCompare
		o1.i5 = o2.i5 + 10;
		assertTrue(o1.compare(o1));
		assertTrue(o2.compare(o1));

		//Comparing of null strings
		o1.setName(null);
		assertFalse(o1.compare(o2));
		assertFalse(o2.compare(o1));
		o2.setName(null);
		assertTrue(o1.compare(o2));
		
		//Comparing of null and distinct objects
		o1.nested = null;
		assertFalse(o1.compare(o2));
		assertFalse(o2.compare(o1));
		
		//Comparing of distinct objects
		o1.nested = new AllTypesGetSetObject();
		prepRttiAssign(o1.nested);
		assertFalse(o1.compare(o2));
		assertFalse(o2.compare(o1));
		o2.nested = new AllTypesObject();
		prepRttiAssign(o2.nested);
		assertFalse(o1.compare(o2));
		assertFalse(o2.compare(o1));
		
		//Comparing of complex nested object
		o2.nested = new AllTypesGetSetObject();
		prepRttiAssign(o2.nested);
		assertTrue(o1.compare(o2));
		assertTrue(o2.compare(o1));
	}
	
	public void testDirectCompare()
	{
		AllTypesObject o1 = new AllTypesObject();
		AllTypesObject o2 = new AllTypesObject();
		
		assertTrue(o1.compare(o2));
		
		prepRttiAssign(o1);
		assertFalse(o1.compare(o2));
		assertFalse(o2.compare(o1));
		o2.assign(o1);
		assertTrue(o1.compare(o2));
		assertTrue(o2.compare(o1));
		
		o1.aObject = new NestedDataObject();
		assertFalse(o1.compare(o2));
		assertFalse(o2.compare(o1));
		o2.aObject = new NestedDataObject();
		assertTrue(o1.compare(o2));
		assertTrue(o2.compare(o1));
	}
	
	public void testGetSetCompare()
	{
		AllTypesGetSetObject o1 = new AllTypesGetSetObject();
		AllTypesGetSetObject o2 = new AllTypesGetSetObject();
		
		assertTrue(o1.compare(o2));
		
		prepRttiAssign(o1);
		assertFalse(o1.compare(o2));
		assertFalse(o2.compare(o1));
		o2.assign(o1);
		assertTrue(o1.compare(o2));
		assertTrue(o2.compare(o1));
		
		o1.setaObject(new NestedDataObject());
		assertFalse(o1.compare(o2));
		assertFalse(o2.compare(o1));
		o2.setaObject(new NestedDataObject());
		assertTrue(o1.compare(o2));
		assertTrue(o2.compare(o1));
	}
	
	public void testNoAssignCompare()
	{
		final NoAssignCompareObject o = new NoAssignCompareObject();
		
		testAssignException(new Command()
		{
			@Override
			public void execute()
			{
				o.assign(o);
			}
		});
		assertFalse(o.compare(o));
	}
	
	RttiMethod getMethod(RttiClass info, String name, Class<?>... params)
	{
		RttiMethod method = info.getMethod(name, params);
		assertNotNull(method);
		assertEquals(name, method.getName());
		assertEquals(params.length, method.getParameterTypes().length);
		for (int i = 0; i < params.length; i++)
		{
			assertEquals(params[i], method.getParameterTypes()[i]);
		}
		
		return method;
		
	}
	
	void testInvocationException(Class<? extends Throwable> excClass, 
			RttiMethod m, Object instance, Object... params) throws RttiIllegalArgumentException
	{
		boolean hasException = false;
		
		try
		{
			m.invoke(instance, params);
		}
		catch (RttiInvocationException e)
		{
			if (e.getCause().getClass() == excClass)
				hasException = true;
			else if (isProd && (excClass == NullPointerException.class) && 
					(e.getCause().getClass() == JavaScriptException.class))
			{
				//NullPointerException (and possibly others) are sometime manifested
				//as JavaScriptException in production
				//See: http://code.google.com/p/google-web-toolkit/issues/detail?id=3069#c12
				//Or   http://code.google.com/intl/cs/webtoolkit/doc/latest/DevGuideCodingBasicsCompatibility.html#l
				hasException = true;
			}
			else assertTrue("Invalid exception caught " + e.getCause().toString(), false);
		}
		
		assertTrue("RttiInvocationException should be raised", hasException);		
	}
	
	void testArgumentException(RttiMethod m, Object instance, Object... params) throws RttiInvocationException
	{
		boolean hasException = false;
		
		try
		{
			m.invoke(instance, params);
		}
		catch (RttiIllegalArgumentException e)
		{
			hasException = true;
		}
		
		assertTrue("RttiIllegalArgumentException should be raised", hasException);		
	}
	
	private void testMethodArgs(RttiClass info, String name, Class<?> clazz)
	{
		String s = "get" + name;
		RttiMethod method = info.getMethod(s);
		if ((method == null) && (clazz == boolean.class))
		{
			s = "is" + name;
			method = info.getMethod(s);
		}
		assertNotNull(method);
		assertEquals(s, method.getName());
		assertEquals(0, method.getParameterTypes().length);
		assertEquals(clazz, method.getReturnType());
		
		s = "set" + name;
		method = info.getMethod(s, clazz);
		assertNotNull(method);
		assertEquals(s, method.getName());
		assertEquals(1, method.getParameterTypes().length);
		assertEquals(clazz, method.getParameterTypes()[0]);
		assertEquals(void.class, method.getReturnType());
	}
	
	public void testMethods() throws RttiInvocationException, RttiIllegalArgumentException
	{
		//Make sure that info is not generated on non GenerateMethods objects
		RttiClass info = Rtti.getTypeInfo(NestedDataObject.class);
		assertNotNull(info);
		assertNull(info.getMethods());
		assertNull(info.getMethod("anyMethod"));
		
		//Few simple tests
		AllTypesGetSetObject o = new AllTypesGetSetObject();
		info = Rtti.getTypeInfo(o);
		assertNotNull(info);
		
		o.setaString(TestConsts.METHOD);
		RttiMethod method = getMethod(info, "getaString");
		String s = (String) method.invoke(o);
		try
		{
			@SuppressWarnings("unused")
			Integer i = (Integer) method.invoke(o);
			assertFalse(true);
		}
		catch (ClassCastException e)
		{
		}
		assertEquals(o.getaString(), s);
		
		assertEquals(25, info.getMethods().length);
		
		int i = 1234;
		method = getMethod(info, "setaObject", Object.class);
		testArgumentException(method, o);
		testArgumentException(method, null);
		testArgumentException(method, new String());
		testArgumentException(method, new String(), (Object)null);
		
		assertNull(method.invoke(o, (Object)null));	//Objects can be null
		assertNull(o.getaObject());

		assertNull(method.invoke(o, i));
		assertEquals(i, o.getaObject());
		s = new String("some testing text");
		assertNull(method.invoke(o, s));
		assertSame(s, o.getaObject());	
		
		//Check annotations
		assertEquals(1, method.getAnnotations().length);
		TestAnnotation annot = method.getAnnotation(TestAnnotation.class);
		assertNotNull(annot);
		assertEquals(TestConsts.METHOD, annot.aString());
		
		//Check invocation exception
		method = getMethod(info, "exception", int.class);
		testInvocationException(RuntimeException.class, method, o, 0);
		testInvocationException(ArithmeticException.class, method, o, 1);
		Integer I = 567;
		method = getMethod(info, "test", s.getClass(), int.class, I.getClass());
		testInvocationException(NullPointerException.class, method, o, "ABCD", 1234, null);	//This should cause error since arithmetic operation with null is impossible 
		
		//Check multiple parameters
		testArgumentException(method, o, "", null, null);	//Primitives cannot be null
		testArgumentException(method, o, i, TestConsts.ONE);
		testArgumentException(method, o, i, TestConsts.ONE, TestConsts.TWO);
		assertEquals(o.test(TestConsts.ONE, i, I), method.invoke(o, TestConsts.ONE, i, I));

		method = getMethod(info, "test", s.getClass());
		testArgumentException(method, o, i, TestConsts.ONE);
		testArgumentException(method, o, i, TestConsts.ONE, TestConsts.TWO);
		assertEquals(o.test(TestConsts.TWO), method.invoke(o, TestConsts.TWO));
		
		//Check interfaces
		info = Rtti.getTypeInfo(TestInterface.class);
		assertNotNull(info);
		TestInterface intf = new TestInterface()
		{
			@Override
			public boolean someMethod(String str, int anInt, Integer integer)
			{
				return anInt == integer;
			}
		};
		method = info.getMethods()[0];
		assertEquals(intf.someMethod(null, i, I), 
				(boolean)(Boolean)method.invoke(intf, null, I, i));
		
		//Check return and parameter types
		info = Rtti.getTypeInfo(AllTypesGetSetObject.class);
		//This threw an exception it shouldn't, it might have happened 
		//if annotation type is not RTTI enabled
		assertNotNull(info);
		info.getMethods()[5].toString();	
		testMethodArgs(info, "aString", String.class);
		testMethodArgs(info, "aBoolean", boolean.class);
		testMethodArgs(info, "aChar", char.class);
		testMethodArgs(info, "AByte", byte.class);
		testMethodArgs(info, "aShort", short.class);
		testMethodArgs(info, "aInt", int.class);
		testMethodArgs(info, "aLong", long.class);
		testMethodArgs(info, "aFloat", float.class);
		testMethodArgs(info, "aDouble", double.class);
		testMethodArgs(info, "aObject", Object.class);
	}
	
	//Also testing nested types
	public void testEnum()
	{
		EnumTestObject o1 = new EnumTestObject();
		
		RttiClass info = Rtti.getTypeInfo(o1);
		assertNotNull(info);
		
		assertEquals(2, info.getFields().length);
		assertNotNull(info.getField("aEnum"));
		assertNotNull(info.getField("nestedEnum"));
		
		o1.aEnum = TestEnum.TWO;
		assertTrue(TestEnum.TWO == info.getField("aEnum").get(o1, TestEnum.class));
		info.getField("aEnum").set(o1, TestEnum.THREE);
		assertTrue(TestEnum.THREE == o1.aEnum);
		o1.nestedEnum = Fruit.ORANGE;
		
		EnumTestObject o2 = new EnumTestObject();
		o2.assign(o1);
		assertTrue(TestEnum.THREE == o2.aEnum);
		assertTrue(EnumTestObject.Fruit.ORANGE == o2.nestedEnum);
		assertTrue(o1.compare(o2));
		
		info = Rtti.getTypeInfo(EnumTestObject.Fruit.class);
		assertNotNull(info);
		assertEquals(EnumTestObject.Fruit.class.getName(), info.getName());
		assertTrue(info.isClass());
		assertTrue(info.isEnum());
		EnumTestObject.Fruit[] fruits = info.getEnumConstants();
		assertNotNull(fruits);
		fruits = info.getEnumConstants(EnumTestObject.Fruit.class);
		assertNotNull(fruits);
		
		boolean gotException = false;
		try
		{
			TestEnum[] bad = info.getEnumConstants(TestEnum.class);
			assertNull(bad);	//Should be unreachable
		}
		catch (ClassCastException e) 
		{
			gotException = true;
		}
		assertTrue(gotException);
		
		assertEquals(3, fruits.length);
		assertEquals("APPLE", fruits[0].toString());
		assertEquals("ORANGE", fruits[1].toString());
		assertEquals("BANANA", fruits[2].toString());
	}
}
