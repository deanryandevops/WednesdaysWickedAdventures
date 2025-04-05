pipeline 
{
    agent any
    
    parameters{
        choice(name: 'Action', choices: ['deploy', 'destroy'], description: 'Select Terraform Action to run')
    }
    
    environment
    {
        AWS_ACCESS_KEY = credentials('aws-access-key')
        AWS_SECRET_KEY = credentials('aws-secret-key')
        SLACK_CHANNEL = "#terraform-infrastructure"
    }
    
    stages
    {
        stage('Clone Repository')
        {
            steps 
            {
                git branch: 'develop', url: 'https://github.com/deanryandevops/WednesdaysWickedAdventures.git'
            }
        }
        
        stage('List Directory')
        {
            steps
            {
                powershell ''' ls '''
            }
        }
        
        stage('Change Directory to Terraform Folder')
        {
            steps {
                
                dir('terraform') 
                {
                    script
                    {
                    powershell ''' terraform init '''
                    powershell ''' terraform validate '''
                    
                    if(params.Action == 'deploy')
                    {
                        powershell ''' terraform plan -out=tfplan '''
                        powershell ''' terraform apply tfplan '''
                    }
                    else
                    {
                        powershell ''' terraform destroy --auto-approve '''
                    }
                    }
                }
            }
        }
        
        stage('List Terraform Directory')
        {
            steps
            {
                powershell ''' ls '''
            }
        }
    }
    post 
    {
        success{
            echo 'Terraform deployment successful'
            slackSend channel: "{env.SLACK_CHANNEL}", color:"good", tokenCredentialId: 'slack-token', message: 'Deployment succeeded'
        }
        failure
        {
            echo 'Terraform deployment failed'
            slackSend channel: "{env.SLACK_CHANNEL}", color:"danger", tokenCredentialId: 'slack-token', message: 'Deployment failed'
        }
    }
}