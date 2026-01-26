package com.SmartChakula.Utils;

import com.fasterxml.uuid.Generators;
import org.springframework.core.env.Environment;

import java.util.Random;
import java.util.UUID;

public class CustomGeneratedData {

	public static String GenerateUniqueID() {
		Environment env = SpringContext.getBean(Environment.class);
		UUID uuid = Generators.timeBasedGenerator().generate();
		String uuidStr = uuid.toString().replace("-", "");
		return env != null ? env.getProperty("lsms.instance") + uuidStr : uuidStr;
	}

	public static boolean isObjectHashMap(Object source) {
		try {
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}

	public static int generateOtp() {
		Random random = new Random();
		return random.nextInt(89999999) + 10000001;
	}


	public static int randInt(int min, int max) {
		return (int)(Math.random()*(max-min+1) + min);
	}

}
