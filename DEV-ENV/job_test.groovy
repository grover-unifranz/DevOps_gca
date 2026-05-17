def url_repo = "https://github.com/andresmerida/academic-management.git"
pipeline {
    agent {
        label 'slave1'
    }
    tools{
        jdk 'java21_slave1'
        maven 'maven-399'
    }
    parameters{
        string defaultValue: 'dev', description: 'Colocar el branch a ejecutar', name: 'BRANCH', trim: false
        choice(name: 'SCAN_GRYPE', choice: ['YES','NO'], description:'Seleccione SI o NO')
    }
    stages {
        stage("Limpiar Workspace"){
            steps{
                cleanWs()
            }
        }
        stage("Colocar nombre de Build")
        {
            steps{
                script{
                    currentBuild.displayName="Services-deploy_back-"+ currentBuild.number
                }
            }
        }
        stage("Descargar proyecto") {
            steps {
                git credentialsId: 'git_cred', branch: "${params.BRANCH}", url: "${url_repo}"
            }
        }
        stage("Realizar Build") {
            steps {
                sh "mvn -v"
                sh "pwd"
                sh "mvn clean compile package"
                }
            }
            stage("Archivar artefacto")
            {
                steps{
                    sh "mv am-core-web-service/target/*.jar am-core-web-service/target/app.jar"
                    stash includes:'am-core-web-service/target/app.jar', name: 'backartifact'
                    archiveArtifacts artifacts: 'am-core-web-service/target/app.jar', onlyIfSuccessful: true
                }
            }
            stage("test de vulnerabilidadees de seguridad"){
                when {equals expected: 'YES', actual: SCAN_GRYPE}
                agent { label 'grype_test'}
                steps{
                    unstash 'backartifact'
                    sh "/grype /home/workspace/DEV/BACKEND/job_test1/am-core-web-service/target/app.jar > Informe-scan.txt"
                    archiveArtifacts artifacts: 'Informe-scan.txt', onlyIfSuccessful: true 
                }
            }
        }
    }

