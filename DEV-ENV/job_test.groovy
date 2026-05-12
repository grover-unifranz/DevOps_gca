pipeline {
    agent {
        label 'slave1'
    }
    tools{
        jdk 'java21_slave1'
        maven 'maven-399'
    }
    stages {
        stage("Limpiar Workspace"){
            steps{
                cleanWs()
            }
        }
        stage("Descargar proyecto") {
            steps {
                git credentialsId: 'git_cred', branch: 'devrsr', url: "https://github.com/grover-unifranz/DevOps_gca.git"
            }
        }
        stage("Realizar Build") {
            steps {
                sh "mvn -v"
                sh "pwd"
                sh "mvn clean compile package"
                }
            }
        }
    }

