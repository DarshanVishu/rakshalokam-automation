pipeline {
    agent any

    tools {
        jdk 'JDK-17'
        maven 'Maven-3'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/DarshanVishu/rakshalokam-automation.git'
            }
        }

        stage('Run Automation Suite') {
            steps {
                bat 'mvn clean test -DsuiteXmlFile=testng.xml'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/ExtentReport*.html, test-output/**/*', allowEmptyArchive: true
        }
    }
}
