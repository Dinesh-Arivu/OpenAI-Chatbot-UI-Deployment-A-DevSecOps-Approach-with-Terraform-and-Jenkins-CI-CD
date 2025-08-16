pipeline{
    agent any
    stages {
        stage('Checkout from Git'){
            steps{
                git branch: 'main', url: 'https://github.com/Dinesh-Arivu/Uber-Clone-App-Deployment-A-DevSecOps-Approach-with-Terraform-and-Jenkins-CI-CD.git'
            }
        }
        stage('Terraform version'){
             steps{
                 sh 'terraform --version'
             }
        }
        stage('Terraform init'){
             steps{
                 dir('EKS_TERRAFORM') {
                      sh 'terraform init'
                   }
             }
        }
        stage('Terraform validate'){
             steps{
                 dir('EKS_TERRAFORM') {
                      sh 'terraform validate'
                   }
             }
        }
        stage('Terraform plan'){
             steps{
                 dir('EKS_TERRAFORM') {
                      sh 'terraform plan'
                   }
             }
        }
        stage('Terraform apply/destroy'){
             steps{
                 dir('EKS_TERRAFORM') {
                      sh 'terraform ${action} --auto-approve'
                   }
             }
        }
    }
}