pipeline {
    agent any

    environment {
        EC2_HOST = 'ubuntu@54.212.54.184'        // 너의 batch 서버 IP
        EC2_DIR = '/home/ubuntu/cluvr-batch'       // EC2 내부에서 git clone 받은 위치
    }

    stages {
        stage('Deploy to EC2') {
            steps {
                // batch 서비스만 재빌드 및 재시작 (다른 컨테이너는 유지)
                sh """
                ssh -o StrictHostKeyChecking=no -i /var/lib/jenkins/.ssh/id_rsa ${EC2_HOST} '
                cd ${EC2_DIR} &&
                git pull origin main &&
                sudo docker-compose rm -f spring || true &&
                sudo docker container prune -f &&
                sudo docker-compose up -d --build spring
                '
                """
            }
        }
    }
}