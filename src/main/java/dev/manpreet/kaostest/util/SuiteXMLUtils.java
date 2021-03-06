package dev.manpreet.kaostest.util;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dev.manpreet.kaostest.KaosException;
import dev.manpreet.kaostest.dto.xml.Suite;
import dev.manpreet.kaostest.dto.xml.SuiteClass;
import dev.manpreet.kaostest.dto.xml.SuiteListener;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Holder for some utility methods required to process the suite XML and the classes defined within.
 */
@Slf4j
public class SuiteXMLUtils {

    private SuiteXMLUtils() {}

    public static Suite deserializeSuiteXML(String xmlPath) {
        if (xmlPath == null || xmlPath.isBlank()) {
            log.error("Suite XML path is null or blank");
            throw new KaosException("Suite XML path is null or blank");
        }
        File xmlFile = new File(xmlPath);
        if (!(xmlFile.exists() && xmlFile.canRead())) {
            log.error("Cannot read provided XML at: " + xmlPath);
            log.error("File exists: " + xmlFile.exists());
            log.error("File is readble: " + xmlFile.canRead());
            throw new KaosException("Cannot read provided XML at: " + xmlPath);
        }
        try {
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(xmlFile, Suite.class);
        } catch (IOException e) {
            log.error("Exception occurred while deserialising provided suite XML", e);
            throw new KaosException("Cannot deserialize provided XML at: " + xmlPath);
        }
    }

    public static boolean isSuiteValid(Suite suite) {
        log.debug(suite.toString());
        if (suite.getName() == null && suite.getTest().getName() == null) {
            log.warn("None of suite name or test name was provided");
        }
        if (suite.getTest() == null || suite.getTest().getClass_() == null || suite.getTest().getClass_().isEmpty()) {
            log.error("No tests specified");
            return false;
        }
        List<String> classCoordinates;
        if (suite.getListener() != null && !suite.getListener().isEmpty()) {
            classCoordinates = suite.getListener().stream().
                    map(SuiteListener::getClassName).
                    collect(Collectors.toList());
            if (!areAllClassesValid(classCoordinates, "listener")) {
                log.error("One of the listener classes specified is invalid");
                return false;
            }
        }
        classCoordinates = suite.getTest().getClass_().stream().
                map(SuiteClass::getName).
                collect(Collectors.toList());
        return areAllClassesValid(classCoordinates, "test");
    }

    private static boolean areAllClassesValid(List<String> classNames, String classType) {
        if (classNames.stream().anyMatch(Objects::isNull)) {
            log.error("One or more class names in " + classType + " classes is null.");
            return false;
        }
        for (String eachClassName: classNames) {
            try {
                Class.forName(eachClassName);
            } catch (ClassNotFoundException e) {
                log.error("Class with specified coordinates " + eachClassName + " not found in " + classType + " classes", e);
                return false;
            }
        }
        return true;
    }

    public static List<String> getAllTestClasses(Suite suite) {
        return suite.getTest().getClass_().stream().
                map(SuiteClass::getName).
                collect(Collectors.toList());
    }

    public static List<String> getAllListenerClasses(Suite suite) {
        return suite.getListener().stream().
                map(SuiteListener::getClassName).
                collect(Collectors.toList());
    }

    public static List<Class<?>> getClassFromName(List<String> classNames) {
        List<Class<?>> classes = new ArrayList<>();
        classNames.forEach(name -> {
            try {
                classes.add(Class.forName(name));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return classes;
    }
}
