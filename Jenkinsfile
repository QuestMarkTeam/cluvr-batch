pipeline {
    agent any

    environment {
        AWS_REGION = 'us-west-2'
        AWS_ACCOUNT_ID = '617373894870'
        ECR_REPO = 'cluvr-batch'
        ECR_REGISTRY = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        IMAGE_TAG = 'latest'
        BATCH_EC2_IP = '54.212.54.184'
    }

    stages {
        stage('Checkout SCM') {
            steps {
                echo "✅ Checking out source code from GitHub..."
                checkout scm
            }
        }

        stage('Build & Deploy only if on develop branch') {
            when {
                branch 'develop'
            }

            steps {
                echo "✅ Deploying develop branch build..."

                script {
                    // 1. Docker Build & Tag
                    sh """
                    docker build -t ${ECR_REPO}:${IMAGE_TAG} .
                    docker tag ${ECR_REPO}:${IMAGE_TAG} ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}
                    """
                }

                script {
                    // 2. Docker Push to ECR
                    sh """
                    aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}
                    docker push ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}
                    """
                }
                script {
                    // 3. Remote Deploy on EC2
                    sh """
                    ssh -i /var/lib/jenkins/.ssh/id_rsa ubuntu@${BATCH_EC2_IP} << 'EOF'
                    echo "✅ Pulling latest Docker image..."
                    docker pull ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}

                    echo "✅ Stopping and removing old container (if exists)..."
                    docker stop ${ECR_REPO} || true
                    docker rm ${ECR_REPO} || true

                    echo "✅ Running new container..."
                    docker run -d --name ${ECR_REPO} -p 8082:8080 ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}
                    EOF
                    """
                }
            }
        }
    }
}
