pipeline {
    agent any
    stages {
        stage('install puppet agent') {
	         steps {
                // step1 
                echo 'running installation'
		            git url: 'https://github.com/eshwal/projCert.git'
		            sh script: 'ansible-playbook --inventory /tmp/inv $WORKSPACE/puppet.yml'
           }
        }
        stage('push ansible configuration') {
	         steps {
                // step2
                echo 'installing docker..'
		            sh script: 'ansible-playbook --inventory /tmp/inv $WORKSPACE/docker.yml'
           }
        }
        stage('containerization') {
	          steps {
                // step3
                echo 'container creation..'
	               sh script: 'docker build --file dockerfile --tag customimage:$BUILD_NUMBER .'
                 sh script: 'docker run -d -P customimage:$BUILD_NUMBER'
            }
	          post {
               failure {
                   sh script: 'docker rmi -f customimage:$BUILD_NUMBER'
               }
            }			
        }
  
    }
}
