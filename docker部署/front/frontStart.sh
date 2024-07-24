#!/bin/bash

#镜像名称
image_name="match_front"
#镜像标签
image_tag="v1.0"
#容器名称
container_name="match_front"

# 构建镜像
echo "Builting image..."
docker build -t $image_name:$image_tag -f DockerFile .

# 容器处理
docker stop match_front
docker rm match_front

# 启动容器
docker run --name $container_name -d -p 80:80 $image_name:$image_tag
