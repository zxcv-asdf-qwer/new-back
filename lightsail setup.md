![Pasted image 20240622220633.png](md_image%2FPasted%20image%2020240622220633.png)
![스크린샷 2024-06-22 오후 10.07.29.png](md_image%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-06-22%20%EC%98%A4%ED%9B%84%2010.07.29.png)
![스크린샷 2024-06-22 오후 10.08.34.png](md_image%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-06-22%20%EC%98%A4%ED%9B%84%2010.08.34.png)

ssh 연결

- codedeploy-agent install
    - [https://docs.aws.amazon.com/ko_kr/codedeploy/latest/userguide/codedeploy-agent-operations-install-ubuntu.html](https://docs.aws.amazon.com/ko_kr/codedeploy/latest/userguide/codedeploy-agent-operations-install-ubuntu.html)  
      ```wget[https://aws-codedeploy-ap-northeast-2.s3.ap-northeast-2.amazonaws.com/latest/install](https://aws-codedeploy-ap-northeast-2.s3.ap-northeast-2.amazonaws.com/latest/install)```
      ```systemctl status codedeploy-agent```

- docker install
    - [https://velog.io/@osk3856/Docker-Ubuntu-22.04-Docker-Installation](https://velog.io/@osk3856/Docker-Ubuntu-22.04-Docker-Installation)
      ```sudo systemctl status docker```

```
sudo usermod -aG docker ubuntu
sudo chmod 666 /var/run/docker.sock
```

- awscli install
  ```sudo apt install awscli```

- ec2 배포 일 경우

```
aws configure
```

---

- lightsail 배포 일 경우

```
vi /etc/codedeploy-agent/conf/codedeploy.onpremises.yml
```

```
aws_access_key_id: secret
aws_secret_access_key: secret+p
iam_user_arn: arn:aws:iam::secret:user/secret
region: ap-northeast-2
```

onPremiss tagging

```
aws deploy register-on-premises-instance --instance-name secret --iam-user-arn arn:aws:iam::secret:user/secret --region ap-northeast-2
```

```
aws deploy add-tags-to-on-premises-instances --instance-names secret --tags Key=Name,Value=secret --region ap-northeast-2
```

---
권한 재적용

```
sudo service codedeploy-agent restart
```

---

- codedeploy-agent log 확인

```
  tail -n 100 -f /var/log/aws/codedeploy-agent/codedeploy-agent.log
```