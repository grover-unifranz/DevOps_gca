pipeline {
    agent {
        label 'slave1'
    }
    stages {
        stage("TEST INICIAL") {
            steps {
                sh "echo 'HOLA MUNDO GROVER CONDORI'"
            }
        }
        stage("Esperando") {
            steps {
                script {
                    echo 'espera 15 segundos'
                    sleep(15)
                }
            }
        }
    }
}
