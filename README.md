News app sample

This is an Android project designed using the MVVM pattern and uses most of the libraries recommended by Google

The application is hitting the New York Times API and retrieves a list of most popular articles. By selecting 
one of the articles from the list you will be redirected to the article page via one of your browser applications.

Getting Started
---------------
First of all you have to go to https://developer.nytimes.com/signup and generate an API Key. Put anything you
want in the Website field and in the API section select 'Most Popular API'

After you receive the API Key via e-mail, you need to add it to the `gradle.properties` from your home directory, 
in the following format: `NewsApp_ApiKey="YOUR API KEY"`
You can find additional instructions in the following link: https://medium.com/code-better/hiding-api-keys-from-your-android-repository-b23f5598b906

In order to use the scripts, navigate to `YourProjectLocation\NewsApp\scripts\` You will find there some batch files for Windows users.
If you are running Linux, ignore the batch files and just go in the project root folder where you have a `gradlew` file

You should have a device connected or an emulator running before running the scripts

* Build and run debug apk
  * For Windows: Run `build.bat` (run it from a command prompt if you don't want the terminal to close when finished)
  * For Linux: Run `./gradlew cleanInstallDebug`
  
  The application should now be installed on your device. It's labeled `News` so go ahead and search for it. 

* Run tests and generate code coverage reports
  * For Windows: Run `testCoverage.bat` (run it from a command prompt if you don't want the terminal to close when finished)
  * For Linux: Run `./gradlew fullCoverageReport`

  * The instrumented test reports can be found in app\build\reports\androidTests\connected\.
  * The unit test reports can be found in $module\build\reports\tests.
  * The code coverage reports can be found in \build\reports\javacoco.

* Run lint checks
  * For Windows: Run `lint.bat` (run it from a command prompt if you don't want the terminal to close when finished)
  * For Linux: Run `.gradlew checkLint`

  All reports are saved in build\reports\lint
  
* Generate SonarQube report

    1. Download SonarQube from https://www.sonarqube.org/downloads/
    2. Downloaded version will come in Zip, just unzip it
    3. Go to /sonarcube/bin/<platform-folder>
    4. Hit startsonar.bat to start running SonarQube on server
    5. Now open http://localhost:9000 to see admin panel of SonarQube
    6. Login to user panel using `admin` for username and password
  
    * For Windows: Run `sonarqubeReport.bat` (run it from a command prompt if you don't want the terminal to close when finished)
    * For Linux: `./gradlew clean test sonarqube
                  -Dsonar.host.url=http://[localhost]:9000
                  --info --stacktrace`
				  
    Note: If you have SonarQube running on some server, you can change `sonar.host.url` accordingly. For Windows, modify the command inside the batch file.
	
    Reports can be seen by accessing http://localhost:9000 or your SonarQube server address 

	
Used libraries
--------------

* Dependency Injection
  * Dagger

* Model layer
  * Retrofit
  * OkHttp
  * Gson

* ViewModel layer
  * ViewModel
  * LiveData

* View layer
  * ConstraintLayout
  * RecyclerView
  * CircleImageView
  * Picasso (loading images from url)
  
* Testing
  * MockWebServer
  * Mockito
  * Espresso
  * JUnit
  * Architecture Components
