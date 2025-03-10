pipeline {
    agent any // Use any available agent
    
    parameters {
        string(name: 'AWS_IP', defaultValue: '', description: 'Enter AWS IP Address')
        string(name: 'Docker_User', defaultValue: '', description: 'Docker Username')
        string(name: 'Docker_Pass', defaultValue: '', description: 'Docker password')
        string(name: 'Docker_Container_Name', defaultValue: '', description: 'Docker Container Name')
    }

    environment {
        DOCKER_HUB_REPO = 'l00188387/wednesdayswickedadventures'
        EC2_USER = 'ubuntu'
        APP_URL = "http://${params.AWS_IP}:8000"
    }

    stages {
        
        stage('AWS_IP') {
            steps {
                echo "Hello IP, ${params.AWS_IP}!"
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
                git branch: 'main', url: 'https://github.com/deanryandevops/WednesdaysWickedAdventures.git'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                powershell """
                docker build -t ${env.DOCKER_HUB_REPO}:latest .
                """
            }
        }
        stage('Push image to docker hub')
        {
             steps {
            powershell """ docker push $DOCKER_HUB_REPO:latest
            """
             }
        }
        
        stage('Deploy to EC2') 
        {
            steps 
            {
                sshagent(['ec2-ssh-key']) 
                {
                    powershell """
                    ssh -o StrictHostKeyChecking=no ${env.EC2_USER}@${params.AWS_IP} "
                    docker stop ${params.Docker_Container_Name};
                    docker rm ${params.Docker_Container_Name};
                    docker pull ${env.DOCKER_HUB_REPO}:latest;
                    docker run -d --name ${params.Docker_Container_Name} -p 8080:8000 ${env.DOCKER_HUB_REPO}:latest;
                    "
                """
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline succeeded! 🎉"
            // Perform actions for success (e.g., send a notification)
        }
        failure {
            echo "Pipeline failed! ❌"
            // Perform actions for failure (e.g., send an alert)
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