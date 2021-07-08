pipeline {
    agent any
        stages {
            stage('push container') {
                steps {
                    echo "workspace is $WORKSPACE"
                    dir("$WORKSPACE") {
                        script {
                            docker.withRegistry('https://index.docker.io/v1/', 'DockerHubID') {
                                def image = docker.build('dockerhub_id/image:tag')
                                image.push()
                            } //DockerHubID is created from global security of jenkins

                        } //groovy scripts
                    }
                    
                }
            }
        }
}