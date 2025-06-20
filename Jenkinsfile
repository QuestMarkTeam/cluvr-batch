pipeline {
    agent any

    environment {
        MODULE = 'cluvr-batch'
        AWS_REGION = 'us-west-2'
        ECR_REGISTRY = '617373894870.dkr.ecr.us-west-2.amazonaws.com'
        ECR_REPO = 'cluvr-batch'
        IMAGE_TAG = 'latest'
        BATCH_EC2_IP = '54.212.54.184' // 배포 EC2 퍼블릭 IP
    }

    stages {
        stage('Build Docker Image') {
            steps {
                echo '✅ Building Docker image...'
                sh "docker build -t ${MODULE}:latest ."
            }
        }

        stage('Push to ECR') {
            steps {
                echo '✅ Logging in to ECR...'
                sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"

                echo '✅ Tag and push to ECR...'
                sh """
                    docker tag ${MODULE}:latest ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}
                    docker push ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}
                """
            }
        }

        stage('Deploy to EC2') {
            steps {
                echo '✅ Deploying on remote EC2...'
                sh """
ssh -o StrictHostKeyChecking=no -i /var/lib/jenkins/.ssh/id_rsa ubuntu@${BATCH_EC2_IP} << 'EOF'
echo "✅ ECR 로그인"
aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}

echo "✅ 최신 Docker 이미지 pull"
docker pull ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}

echo "✅ 기존 컨테이너 중지 및 제거"
docker stop ${ECR_REPO} || true
docker rm ${ECR_REPO} || true

echo "✅ 새 컨테이너 실행"
docker run -d --name ${ECR_REPO} -p 8080:8080 ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}
EOF
"""
            }
        }
    }
}
