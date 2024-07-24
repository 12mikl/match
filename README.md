# match
项目名称：
	伙伴推荐系统

项目介绍：
	目的是帮助大家找到志同道合的伙伴的移动端 H5 网站（APP 风格），基于 Spring Boot 后端 + Vue3 前端的全栈项目，包括用户登录、根据标签匹配相似用户、更新个人信息、按标签搜索用户、组队等功能。

后端技术：
	SpringBoot框架、MySql数据库、MyBatis-Plus，Lambda表达式编程、
	Java8特性、分布式登录、并发编程、Redis、缓存及预热、定时任务、分布式锁、幂等性、编辑距离算法
	
前端技术：
	Vue3、Vant UI组件库、Vite脚手架、Axios请求库
	
部署方式：
	Docker部署
 	后端：
	  	DockerFile（见文件DockerFile）:
	   		# 依赖环境
			FROM openjdk:8-jre
			# 工作目录
			WORKDIR /febs
			COPY ./partner-back-0.0.1-SNAPSHOT.jar /febs/partner-back-0.0.1-SNAPSHOT.jar
			CMD ["java","-Duser.timezone=GMT+8","-jar","/febs/partner-back-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
	  	shell脚本执行发布（见文件backStart.sh）：
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
	前端：
 		DockerFile:
   			FROM nginx
			WORKDIR /usr/share/nginx/html/
			USER root
			COPY nginx.conf /etc/nginx/conf.d/default.conf
			COPY ./dist/ /usr/share/nginx/html/
			EXPOSE 80
			CMD ["nginx", "-g", "daemon off;"]
		nginx.conf（见文件）:
  			server {
			    listen 80;
			    server_name 你的host;
			    # gzip config
			    gzip on;
			    gzip_min_length 1k;
			    gzip_comp_level 9;
			    gzip_types text/plain text/css text/javascript application/json application/javascript application/x-javascript application/xml;
			    gzip_vary on;
			    gzip_disable "MSIE [1-6]\.";
			
			    root /usr/share/nginx/html;
			    include /etc/nginx/mime.types;
			
			    location / {
			        try_files $uri /index.html;
			
			    }
			    location /api {
			       # find from dist cache
			       add_header 'Access-Control-Max-Age' 0;
			       # find 502 gateway
			       proxy_pass http://你的host:8081/api;
			       proxy_set_header Host $host;
			       proxy_set_header X-Real-IP $remote_addr;
			  }
			
			}

		shell脚本执行文件：
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


