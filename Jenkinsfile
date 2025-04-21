pipeline {
    agent any

    environment {
        APP_NAME = "BECryptoBank"
        DOCKER_IMAGE = "becryptobank-app:latest"
        REPO_URL = "https://github.com/thang0401/BECryptoBank.git"
    }

    stages {
        stage("Clone repo") {
            steps {
                script {
                    def gitDirExists = fileExists(".git")
                    if (!gitDirExists) {
                        echo 'Cloning repository...'
                        sh "git clone --branch deploy --single-branch ${REPO_URL}"
                    } else {
                        echo 'Repository already cloned. Fetching new commit'
                        sh "git fetch"
                        sh "git reset --hard origin/deploy"
                    }
                }
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
