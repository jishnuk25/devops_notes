def dockerRun = 'docker run -p 8080:8080 -d --name my-app3 25795/my-app:1.0'
pipeline {
    agent any
        environment{
            PATH = "/usr/share/maven/bin:$PATH"
        }

        stages {

            stage('SCM') {
                steps {
                    git credentialsId: '00548fae-61e6-47bf-a7f0-5add59ab5ded', url: 'https://github.com/jishnuk25/simple-java-maven-app.git'
                }
            }    
            stage('Build APP') {
                steps {
                    sh "mvn clean install"
                }
            }
            stage('Build Docker image') {
                steps {
                    sh 'sudo docker build -t 25795/my-app:1.0 .' // "." for docker files in the current directory(the git clone dir), else the docker file path has to be provided.
                }    
            }
            stage('Push Docker image') {
                steps {
                    withCredentials([string(credentialsId: 'dockerhub-pwd', variable: 'dockerHubPwd')]) {
                        sh "sudo docker login -u 25795 -p ${dockerHubPwd}"   // 25795 is the docker hub Id.
                    }
                    sh 'sudo docker push 25795/my-app:1.0'
                }    
            }

            stage('Deploy') {
                steps {
                    sshagent([dev-server]) {
                        sh "ssh -o StrictHostKeyChecking=no ec2-user@3.140.217.244 ${dockerRun}"
                    }
                }
            } 
        }
}
