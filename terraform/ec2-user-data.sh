#!/bin/bash
# Update packages
dnf update -y

# Install Docker
dnf install -y docker

# Start and enable Docker service
systemctl start docker
systemctl enable docker

# Add ec2-user to the docker group
usermod -aG docker ec2-user