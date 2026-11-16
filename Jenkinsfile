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
                withCredentials([string(credentialsId: 'RAKSHALOKAM_SESSION_COOKIE', variable: 'SESSION_COOKIE')]) {
                    bat 'mvn clean test -DsuiteXmlFile=testng.xml -Dtest.session.cookie=%SESSION_COOKIE%'
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/ExtentReport*.html, test-output/**/*, target/surefire-reports/**/*', allowEmptyArchive: true
        }
    }
}
