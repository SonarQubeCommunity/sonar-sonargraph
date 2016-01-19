package com.hello2morrow.sonarplugin.foundation;

public class SonargraphUtilities {
  public static final String DEFAULT_BUILD_UNIT = "(Default Build Unit)";
  private static final String UNKNOWN = "<UNKNOWN>";
  private static final String PROJECT_BUILDUNIT_SEPARATOR = "::";

  private SonargraphUtilities() {
    // do not instantiate
  }

  public static String getBuildUnitName(String fqName) {
    if (fqName == null) {
      return UNKNOWN;
    }

    int projectSeparatorPos = fqName.indexOf(PROJECT_BUILDUNIT_SEPARATOR);
    if (projectSeparatorPos == -1) {
      return UNKNOWN;
    }

    String buName = fqName.substring(projectSeparatorPos + 2);

    if (DEFAULT_BUILD_UNIT.equals(buName)) {
      // Compatibility with old SonarJ versions
      buName = fqName.substring(0, projectSeparatorPos);
    }
    return buName;
  }
}
