pipeline {
    agent any
    tools {
        jdk 'jdk-21'
    }

    environment {
        REMOTE_USER = "ubuntu"
        REMOTE_HOST = "192.168.1.22"
        REMOTE_APP_DIR = "/home/ubuntu/action-plan-backend"
        REMOTE_APP_DIR_RCT = "/home/ubuntu/action-plan-backend-rct"
    }

    stages {
        stage('Build') {
            steps {
                sh '''
                    chmod +x ./mvnw
                    ./mvnw -v
                    ./mvnw clean install -DskipTests
                '''
            }
        }

        stage('Push and run') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'rct'
                }
            }
            steps {
                script {
                    def remoteDir = env.BRANCH_NAME == 'dev' ? env.REMOTE_APP_DIR : env.REMOTE_APP_DIR_RCT
                    def serviceName = env.BRANCH_NAME == 'dev' ? 'action-plan-backend' : 'action-plan-backend-rct'
                    sshagent(['action']) {
                        sh """ rsync -a -v --delete -e 'ssh -o StrictHostKeyChecking=no' target/*.jar ${REMOTE_USER}@${REMOTE_HOST}:${remoteDir}"""
                        sh """ ssh ${REMOTE_USER}@${REMOTE_HOST} "sudo systemctl restart ${serviceName}" """
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
