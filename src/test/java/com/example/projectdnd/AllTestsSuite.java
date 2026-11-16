package com.example.projectdnd;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * JUnit 5 Test Suite runner that aggregates and executes all core unit and web-layer tests
 * for Project DnD ({@link DiceServiceTest}, {@link GameServiceTest}, and {@link GameControllerTest}).
 */
@Suite
@SuiteDisplayName("Project DnD - Complete Test Suite")
@SelectClasses({
        DiceServiceTest.class,
        GameServiceTest.class,
        GameControllerTest.class
})
public class AllTestsSuite {
}