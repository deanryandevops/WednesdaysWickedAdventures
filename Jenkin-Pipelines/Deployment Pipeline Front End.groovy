pipeline {
    agent any // Use any available agent
    
    parameters {
        string(name: 'AWS_IP', defaultValue: '18.203.139.247', description: 'Enter AWS IP Address')
        string(name: 'Docker_User', defaultValue: 'l00188387', description: 'Docker Username')
        string(name: 'Docker_Pass', defaultValue: '*********', description: 'Docker password')
        string(name: 'Docker_Container_Name', defaultValue: 'frontend', description: 'Docker Container Name')
    }

    environment {
        DOCKER_HUB_REPO = 'l00188387/ww-front-end'
        EC2_USER = 'ec2-user'
        APP_URL = "http://${params.AWS_IP}"
        SLACK_CHANNEL = '#deployment-front-end-pipeline'
    }

    stages {
        stage('AWS_IP') {
            steps {
                echo "Hello IP, ${params.AWS_IP}!"
            }
        }
        stage('Notify Slack Deployment') {
            steps {
                slackSend(
                    channel: '#deployment-front-end-pipeline',
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
        stage('Install Docker on EC2') {
            steps {
                script {
                    withCredentials([file(credentialsId: 'ww', variable: 'AWS_PEM')]) {
                        bat """
                        powershell -Command "Start-Process -NoNewWindow -Wait 'C:\\Program Files\\Git\\usr\\bin\\ssh.exe' -ArgumentList '-o StrictHostKeyChecking=no -i %AWS_PEM% %EC2_USER%@%AWS_IP% sudo apt-get update && sudo apt-get install -y docker.io && sudo systemctl start docker && sudo systemctl enable docker'"
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
        
        stage('Print Website Link') {
    steps {
        script {
            echo "Application is running at: http://${AWS_IP}:8080"
        }
    }
        }
    }

    post {
        success {
            echo "Pipeline succeeded! 🎉"
            slackSend channel: "${env.SLACK_CHANNEL}",color: "good", tokenCredentialId: 'slack-token',message: "Deployment front end succeeded! 🎉\nApplication is live at: http://${AWS_IP}:8080"
        }
        failure {
        echo "Pipeline failed! ❌"
        slackSend channel: "${env.SLACK_CHANNEL}",color: "danger", tokenCredentialId: 'slack-token', message: "Deployment front end failed! 😢"
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
    