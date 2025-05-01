pipeline {
    agent any

    environment {
        APP_NAME = "BECryptoBank"
        DOCKER_IMAGE = "becryptobank-app:latest"
    }

    stages {
        stage("Get commit info") {
            steps {
                sh 'chmod +x generate-git-info.sh'
                sh './generate-git-info.sh'
            }
        }
        stage("Build") {
            steps {
                sh "mvn clean package -DskipTests"
            }
        }
        stage("Docker Build Image") {
            steps {
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }
        stage("Run with Docker") {
            steps {
                sh "docker rm -f ${APP_NAME} || true"
                sh "docker run -d --name ${APP_NAME} -p 8000:8000 --env-file .env --network becryptobank ${DOCKER_IMAGE}"
            }
        }
    }
}
