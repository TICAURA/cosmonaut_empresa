properties([pipelineTriggers([githubPush()])])
def tag=''
environment {
    REGISTRY_SA = credentials('cosmonaut-registry-sa')
    K8S_SA=credentials('cosmonaut-k8s-sa')
}
pipeline {
	agent {
		label 'docker-agent'
	}

	stages {
		stage("Compilacion") {
		    agent{
				docker {
					label 'docker-agent'
					image 'gradle:6.7.0-jdk11-hotspot'
				}
			}
		   steps {
		       script{
    		       git url: 'https://github.com/ASG-BPM/cosmonaut_empresa',branch:'main',credentialsId: 'winter_user'
    		       tag = sh(script:'git describe --tags --always `git rev-list --tags` | grep DEV | head -1',returnStdout: true ).trim()
    		       sh "git checkout $tag"
    		       sh "gradle assemble dockerBuild -Ptag=$tag"
		       }
		   }
		   post {
				success {
					archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
				}
		   }
		}
		stage('Sonarqube') {
			agent{
				docker {
					label 'docker-agent'
					image 'gradle:6.7.0-jdk11-hotspot'
					args '--dns 192.168.0.154'
				}
			}
			steps{
				withSonarQubeEnv(installationName:'Asg Wintermute Server') {
				   git url: 'https://github.com/ASG-BPM/cosmonaut_empresa',branch:'main',credentialsId: 'winter_user'    
				   sh "git checkout $tag"
    		       sh './gradlew sonarqube --stacktrace'
				}
			}
		}
	    stage("Push de imagen") {
		    agent{
				docker {
					label 'docker-agent'
					image 'wintermutetec/dockerclient:2021-02-12'
					args "-u root --dns 192.168.0.154 -v $env.REGISTRY_SA:/service_account.json -v /var/run/docker.sock:/var/run/docker.sock"
				}
			}
		    steps{
		        script{
    		        docker.withRegistry('https://gcr.io', 'gcr:cosmonaut-registry-sa') {
                        docker.image("gcr.io/cosmonaut-299500/cosmonaut/empresa:$tag").push(tag)
                    }   
		        }
		    }
	    }
		stage('Deploy to Kubernetes') {
			agent{
				docker {
					label 'docker-agent'
					image 'gcr.io/google.com/cloudsdktool/cloud-sdk:327.0.0'
					args "--dns 192.168.0.154 -e HOME=/tmp -e TAG=$tag"
				}
			}
			steps{
			     script{
    			     withCredentials([file(credentialsId: 'cosmonaut-k8s-sa', variable: 'serviceaccount')]) {
        		       git url: 'https://github.com/ASG-BPM/cosmonaut_empresa',branch:'main',credentialsId: 'winter_user'
        		       sh "git checkout $tag"
        		       sh 'gcloud auth activate-service-account cosmonaut-gke-deployer@cosmonaut-299500.iam.gserviceaccount.com --key-file=$serviceaccount'
        		       sh "gcloud container clusters get-credentials cosmonaut-backend-dev --region=us-east1-b --project=cosmonaut-299500"
        		       sh "kubectl config view"
        		       sh "kubectl get pods"
        		       sh "sed -i 's/\$TAG/$tag/' src/jenkins/cosmonaut_empresa_deployment.yml"
        		       sh "cat src/jenkins/cosmonaut_empresa_deployment.yml | kubectl apply -f -"
    		         }
			     }
			}
		}
	}
}