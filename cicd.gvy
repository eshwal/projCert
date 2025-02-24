pipeline {
    agent any
    stages {
        stage('install puppet agent') {
	         steps {
                // step1 
                echo 'running installation'
		            git url: 'https://github.com/eshwal/projCert.git'
		            sh script: 'ansible-playbook $WORKSPACE/puppet.yml --inventory /etc/ansible/hosts '
           }
        }
        stage('push ansible configuration') {
	         steps {
                // step2
                echo 'installing docker..'
		            sh script: 'ansible-playbook $WORKSPACE/docker.yml --inventory /etc/ansible/hosts '
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
