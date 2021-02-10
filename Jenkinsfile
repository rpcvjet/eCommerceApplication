pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                withMaven(maven: 'maven_3_6_3') {
                    sh 'maven clean compile'
                }
                echo 'Building..'
            }
        }
        stage('Test') {
            steps {
            withMaven(maven: 'maven_3_6_3') {
                                sh 'maven test'
                            }
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
            withMaven(maven: 'maven_3_6_3') {
                                sh 'maven deploy'
                            }
                echo 'Deploying....'
            }
        }
    }
}