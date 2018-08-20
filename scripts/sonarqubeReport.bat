cd ..
gradlew clean test sonarqube
          -Dsonar.host.url=http://[localhost]:9000
          --info --stacktrace