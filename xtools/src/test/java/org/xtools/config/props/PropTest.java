package org.xtools.config.props;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class PropTest {

	static Prop prop;
	public static String filePath = "/home/x/tmp/abc.prop";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		prop = new Prop();
		prop.load(new BufferedReader(
				new InputStreamReader(
						new FileInputStream(filePath), 
						Charset.forName("utf-8"))));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testLoad() {
		assertTrue(prop.get("section1.a").equals("1"));
		assertTrue(prop.get("section1.a.b").equals("1999"));
		assertTrue(prop.get("section 2.a.b").equals("100"));
		assertTrue(prop.get("section 2.a.b.d").equals("5555555"));
		System.out.println("true");
	}
	
	@Test
	public void testStore() throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter("/home/x/tmp/ccc.prop"));
		//prop.store(writer);
		
		prop.add("section3", "abc.d", "abcccc", "some ttt");
		prop.add(null, "cc", "my name is cc", null);
		prop.store(writer);
	}

}
