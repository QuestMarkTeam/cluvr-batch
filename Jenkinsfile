 pipeline {
    agent any

    environment {
        MODULE = 'cluvr-batch'
        AWS_REGION = 'us-west-2'
        ECR_REGISTRY = '617373894870.dkr.ecr.us-west-2.amazonaws.com'
        ECR_REPO = 'cluvr-batch'
        IMAGE_TAG = 'latest'
        EC2_IP = '54.212.54.184'
        ENV_PATH = '/home/ubuntu/.env'
    }

    stages {

        stage('Create .env & Send to EC2') {
            steps {
                echo '✅ Generating .env and sending to EC2...'
                withCredentials([
                    string(credentialsId: 'DB_HOST', variable: 'DB_HOST'),
                    string(credentialsId: 'DB_PORT', variable: 'DB_PORT'),
                    string(credentialsId: 'DB_NAME', variable: 'DB_NAME'),
                    string(credentialsId: 'DB_USERNAME', variable: 'DB_USERNAME'),
                    string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD'),
                    string(credentialsId: 'REDIS_HOST', variable: 'REDIS_HOST'),
                    string(credentialsId: 'REDIS_PORT', variable: 'REDIS_PORT'),
                    string(credentialsId: 'OPENAI_API_KEY', variable: 'OPENAI_API_KEY')
                ]) {
                    sh """
                        echo "SPRING_DATASOURCE_URL=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8" > .env
                        echo "SPRING_DATASOURCE_USERNAME=${DB_USERNAME}" >> .env
                        echo "SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}" >> .env
                        echo "REDIS_HOST=${REDIS_HOST}" >> .env
                        echo "REDIS_PORT=${REDIS_PORT}" >> .env
                        echo "OPENAI_API_KEY=${OPENAI_API_KEY}" >> .env

                        scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/.ssh/id_rsa .env ubuntu@${EC2_IP}:${ENV_PATH}
                    """
                }
            }
        }

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
ssh -o StrictHostKeyChecking=no -i /var/lib/jenkins/.ssh/id_rsa ubuntu@${EC2_IP} << EOF
AWS_REGION="${AWS_REGION}"
ECR_REGISTRY="${ECR_REGISTRY}"
ECR_REPO="${ECR_REPO}"
IMAGE_TAG="${IMAGE_TAG}"
ENV_PATH="${ENV_PATH}"

echo "✅ ECR 로그인"
aws ecr get-login-password --region \$AWS_REGION | docker login --username AWS --password-stdin \$ECR_REGISTRY

echo "✅ 최신 Docker 이미지 pull"
docker pull \$ECR_REGISTRY/\$ECR_REPO:\$IMAGE_TAG

echo "✅ 기존 컨테이너 중지 및 제거"
docker stop \$ECR_REPO || true
docker rm \$ECR_REPO || true

echo "✅ MongoDB 컨테이너 실행 (필요시)"
docker stop cluvr-mongo || true
docker rm cluvr-mongo || true
docker run -d --name cluvr-mongo -p 27017:27017 mongo:6.0

echo "✅ 새 컨테이너 실행"
docker run -d --name \$ECR_REPO \\
  --network host \\
  --env-file \$ENV_PATH \\
  --log-driver json-file \\
  --log-opt max-size=10m \\
  --log-opt max-file=3 \\
  \$ECR_REGISTRY/\$ECR_REPO:\$IMAGE_TAG
EOF
"""
            }
        }
    }
}
