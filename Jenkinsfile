pipeline {

    agent any

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }

    environment {
        IMAGE = "dockerhubuser/springboot-demo:${BUILD_NUMBER}"
        SONAR = "SonarQube"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/user/repo.git',
                    credentialsId: 'github-token'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                    mvn sonar:sonar \
                    -Dsonar.projectKey=springboot-demo
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Upload to Artifactory') {
            steps {
                sh '''
                curl -u user:token \
                -T target/*.jar \
                https://artifactory-url/artifactory/libs-release-local/
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t $IMAGE .'
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub',
                    usernameVariable: 'USER',
                    passwordVariable: 'PASS'
                )]) {
                    sh 'echo $PASS | docker login -u $USER --password-stdin'
                }
            }
        }

        stage('Push Image') {
            steps {
                sh 'docker push $IMAGE'
            }
        }

        stage('Deploy') {
            steps {
                sshagent(['ec2-key']) {
                    sh '''
                    ssh -o StrictHostKeyChecking=no ubuntu@DEPLOYMENT-IP << EOF

                    docker pull $IMAGE

                    docker stop springboot || true

                    docker rm springboot || true

                    docker run -d \
                    --name springboot \
                    -p 8080:8080 \
                    $IMAGE

                    EOF
                    '''
                }
            }
        }
    }

    post {

        success {

            emailext(
                subject: "SUCCESS: ${JOB_NAME}",
                body: "Build ${BUILD_NUMBER} deployed successfully.",
                to: "yourmail@gmail.com"
            )

        }

        failure {

            emailext(
                subject: "FAILED: ${JOB_NAME}",
                body: "Build ${BUILD_NUMBER} failed.",
                to: "yourmail@gmail.com"
            )

        }

    }

}
