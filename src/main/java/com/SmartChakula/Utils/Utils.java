package com.SmartChakula.Utils;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.fasterxml.uuid.Generators;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class Utils {


    public static String generateUniqueID() {
        return Generators.timeBasedGenerator().generate().toString().replace("-", "");
    }

//    public static String generateLongUniqueID() {
//        return Generators.timeBasedReorderedGenerator().generate().toString()+Generators.timeBasedReorderedGenerator().generate().toString();
//    }

    public static String deletePad() {
        return ":::" + generateUniqueID();
    }

    public static String getExceptionMessage(Exception e) {
        String msg = e.getMessage();
        if (e.getCause() != null) {
            Throwable cause = e.getCause();
            while (cause.getCause() != null && cause.getCause() != cause) {
                if (cause.getMessage() != null)
                    msg = cause.getMessage();
                cause = cause.getCause();
            }
            if (cause.getMessage() != null)
                return cause.getMessage();
        }
        return msg != null ? msg : "";
    }

    public static String camelCaseToSnakeCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return "";
        }

        Pattern pattern = Pattern.compile("([a-z])([A-Z]+)");
        Matcher matcher = pattern.matcher(camelCase);
        StringBuilder snakeCase = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(snakeCase, "$1_" + matcher.group(2).toLowerCase());
        }

        matcher.appendTail(snakeCase);

        // Handle the case where the first letter is uppercase
        if (Character.isUpperCase(camelCase.charAt(0))) {
            snakeCase.setCharAt(0, Character.toLowerCase(snakeCase.charAt(0)));
        }

        return snakeCase.toString();
    }

    public static void copyProperties(Object source, Object destination) {
        copyProperties(source, destination, getNullPropertyNames(source, null));
    }

    @SuppressWarnings("null")
    public static void copyProperties(Object source, Object destination, String[] exceptions) {
        BeanUtils.copyProperties(source, destination, getNullPropertyNames(source, exceptions));
    }

    /**
     * Returns an array of null properties of an object
     *
     * @param source
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String[] getNullPropertyNames(Object source, String[] extra) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            // check if value of this property is null then add it to the collection
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                emptyNames.add(pd.getName());
        }
        if (extra != null)
            Collections.addAll(emptyNames, extra);
        String[] result = new String[emptyNames.size()];
        return (String[]) emptyNames.toArray(result);
    }


    public static HttpServletRequest getCurrentHttpRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest).orElse(null);
    }


    public static <T> T createObjectInstance(String uid, Class<T> entityClass) {
        try {
            // Create a new instance of the entity class
            T entity = entityClass.getDeclaredConstructor().newInstance();

            Field uidField = entityClass.getDeclaredField("uid");

            uidField.setAccessible(true);

            uidField.set(entity, uid);

            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String harshMethod(String string) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(string.getBytes());

        byte[] byteData = md.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xFF) + 256, 16).substring(1));
        }
        return sb.toString();
    }

    public static boolean containsString(String[] array, String target) {
        for (String str : array) {
            if (str.equals(target)) {
                return true; // Target string found in the array
            }
        }
        return false; // Target string not found in the array
    }

    public static String getCurrentFinancialYear() {
        Integer year = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("YY")));
        return LocalDate.now().getMonthValue()<6 ? String.valueOf(year-1)+"/"+year:year+"/"+String.valueOf(year+1);
    }

}
