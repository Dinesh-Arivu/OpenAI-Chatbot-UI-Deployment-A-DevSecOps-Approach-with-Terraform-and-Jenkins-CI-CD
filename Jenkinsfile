pipeline {
    agent any

    tools {
        jdk 'jdk17'
        nodejs 'node16'
    }

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
    }

    stages {
        stage('Checkout from Git') {
            steps {
                git branch: 'legacy', url: 'https://github.com/Dinesh-Arivu/OpenAI-Chatbot-UI-Deployment-A-DevSecOps-Approach-with-Terraform-and-Jenkins-CI-CD.git'
            }
        }

        stage('Install Dependencies') {
            steps {
                sh 'npm install'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh '''${SCANNER_HOME}/bin/sonar-scanner \
                        -Dsonar.projectName=Chatbot \
                        -Dsonar.projectKey=Chatbot'''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('OWASP Dependency Check') {
            steps {
                dependencyCheck additionalArguments: '--scan ./ --disableYarnAudit --disableNodeAudit', odcInstallation: 'DP-Check'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
            }
        }

        stage('Trivy File System Scan') {
            steps {
                sh 'trivy fs . --format json --output trivyfs.json'
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'docker', toolName: 'docker') {
                        sh 'docker build -t chatbotui .'
                        sh 'docker tag chatbotui dinesh1097/chatbotui:latest'
                        sh 'docker push dinesh1097/chatbotui:latest'
                    }
                }
            }
        }

        stage('Trivy Image Scan') {
            steps {
                sh 'trivy image dinesh1097/chatbotui:latest --format json --output trivy.json'
            }
        }

        stage('Cleanup Docker Container') {
            steps {
                sh 'docker stop chatbotui || true'
                sh 'docker rm chatbotui || true'
            }
        }

        stage('Deploy Docker Container') {
            steps {
                sh 'docker run -d --name chatbotui -p 3000:3000 dinesh1097/chatbotui:latest'
            }
        }
        stage('Deploy to kubernets'){
            steps{
                script{
                    withKubeConfig(caCertificate: '', clusterName: '', contextName: '', credentialsId: 'k8s', namespace: '', restrictKubeConfigAccess: false, serverUrl: '') {
                       sh 'kubectl apply -f K8S/deployment.yaml'
                  }
                }
            }
        }
    }
}
