#!/bin/bash

image_name="match_back"
image_tag="v1.0"
container_name="match_back"

# DockerFile所在目录
dockerFile_dir="DockerFile"

#构建镜像
echo "Building image..."
docker build -t $image_name:$image_tag -f $dockerFile_dir .
echo "Building image end..."


# 停掉容器
docker stop $container_name
docker rm $container_name
# 启动容器
echo "Starting containter"
docker run --name $container_name -d -p 8081:8081 $image_name:$image_tag

echo "container stared successfully"
