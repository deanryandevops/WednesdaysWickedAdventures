pipeline {
    agent any // Use any available agent
    
    parameters {
        string(name: 'AWS_IP', defaultValue: '18.203.139.247', description: 'Enter AWS IP Address')
        string(name: 'Docker_User', defaultValue: 'l00188387', description: 'Docker Username')
        string(name: 'Docker_Pass', defaultValue: '***********', description: 'Docker password')
        string(name: 'Docker_Container_Name', defaultValue: 'backend', description: 'Docker Container Name')
    }

    environment {
        DOCKER_HUB_REPO = 'l00188387/ww-back-end'
        EC2_USER = 'ec2-user'
        APP_URL = "http://${params.AWS_IP}"
        SLACK_CHANNEL = '#deployment-backend-pipeline'
    }

    stages {
        stage('Notify Backend Slack Deployment') {
            steps {
                slackSend(
                    channel: '#deployment-backend-pipeline',
                    color: "good",
                    message: 'Deployment Started',
                    tokenCredentialId: 'slack-token'
                )
            }
        }
        stage('Login to Docker Hub') {
            steps {
                script {
                        powershell """ docker login -u ${params.Docker_User} -p ${params.Docker_Pass} """
                }
            }
        }
        stage('Clone Repository') {
            steps {
                git branch: 'develop', url: 'https://github.com/deanryandevops/WednesdaysWickedAdventures.git'
            }
        }
        stage('Build Docker Image') {
            steps {
                powershell """
                docker build -f './frontend/Dockerfile' -t ${env.DOCKER_HUB_REPO}:latest .
                """
            }
        }
        stage('Push image to docker hub') {
             steps {
            powershell """ docker push $DOCKER_HUB_REPO:latest
            """
             }
        }
        stage('Connect to AWS via SSH') {
            steps {
                script {
                    withCredentials([file(credentialsId: 'ww', variable: 'AWS_PEM')]) {
                        bat """
                        powershell -Command "Start-Process -NoNewWindow -Wait 'C:\\Program Files\\Git\\usr\\bin\\ssh.exe' -ArgumentList '-o StrictHostKeyChecking=no -i %AWS_PEM% %EC2_USER%@%AWS_IP% echo SSH Connection Successful'"
                        """
                    }
                }
            }
        }
        
        stage('Deploy Docker Container') {
            steps {
                script {
                    withCredentials([file(credentialsId: 'ww', variable: 'AWS_PEM')]) {
                        bat """
                        powershell -Command "Start-Process -NoNewWindow -Wait 'C:\\Program Files\\Git\\usr\\bin\\ssh.exe' -ArgumentList '-o StrictHostKeyChecking=no -i %AWS_PEM% %EC2_USER%@%AWS_IP% docker stop ${params.Docker_Container_Name}; docker rm ${params.Docker_Container_Name}; docker pull ${env.DOCKER_HUB_REPO}:latest; docker run -d --name ${params.Docker_Container_Name} -p 8080:8000 ${env.DOCKER_HUB_REPO}:latest'"
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline succeeded! 🎉"
            slackSend channel: "${env.SLACK_CHANNEL}",color: "good", tokenCredentialId: 'slack-token',message: "Deployment for back end succeeded! 🎉"
        }
        failure {
        echo "Pipeline failed! ❌"
        slackSend channel: "${env.SLACK_CHANNEL}",color: "danger", tokenCredentialId: 'slack-token', message: "Deployment  failed for back end! 😢"
        }
        unstable {
            echo "Pipeline is unstable! ⚠️"
        }
        
        always {
            script {
                    powershell 'docker logout' 
            }
        }
    }
}
    