pipeline {
    agent any

    environment {
        APP_NAME = "BECryptoBank"
        DOCKER_IMAGE = "becryptobank-app:latest"
        REPO_URL = "https://github.com/thang0401/BECryptoBank.git"
        WORKDIR = "BECryptoBank"
	    ENV_FILE = "/var/lib/jenkins/shared/.env"
    }

    stages {
        stage("Clone repo") {
            steps {
                script {
                    def gitDirExists = fileExists("${WORKDIR}/.git")
                    if (!gitDirExists) {
                        echo 'Cloning repository...'
                        sh "git clone --branch deploy --single-branch ${REPO_URL}"
                    } else {
                        echo 'Repository already cloned. Fetching new commit'
                        dir("${WORKDIR}") {
                            sh "git fetch"
                            sh "git reset --hard origin/deploy"
                        }
                    }
                }
            }
        }
        stage("Build") {
            steps {
                dir("${WORKDIR}") {
                    sh "mvn clean package -DskipTests"
                }
            }
        }
        stage("Docker Build Image") {
            steps {
                dir("${WORKDIR}") {
                    sh "docker build -t ${DOCKER_IMAGE} ."
                }
            }
        }
        stage("Run with Docker") {
            steps {
                dir("${WORKDIR}") {
                    sh "docker rm -f ${WORKDIR} || true"
                    sh "docker run -d --name ${WORKDIR} -p 8000:8000 --env-file .env --network becryptobank ${DOCKER_IMAGE}"
                }
            }
        }
    }
}
