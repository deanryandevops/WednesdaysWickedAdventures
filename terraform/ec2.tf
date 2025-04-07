# Public EC2 Instance
resource "aws_instance" "public_instance" {
  ami             = var.ami_id
  instance_type   = var.instance_type
  subnet_id       = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.public_sg.id]
  tags            = { Name = "Public-Instance" }
  key_name        = "WickedWednesdayKP"
  user_data       = file("${path.module}/ec2-user-data.sh")
}

# Private EC2 Instance
resource "aws_instance" "private_instance" {
  ami              = var.ami_id
  instance_type    = var.instance_type
  subnet_id        = aws_subnet.private.id
  vpc_security_group_ids  = [aws_security_group.private_sg.id]
  tags             = { Name = "Private-Instance" }
  key_name        = "WickedWednesdayKP"
  user_data        = file("${path.module}/ec2-user-data.sh")
}

# Jump Box in Public Subnet
resource "aws_instance" "jump_box" {
  ami             = var.ami_id
  instance_type   = var.instance_type
  subnet_id       = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.public_sg.id]
  key_name        = "WickedWednesdayKP"
  tags            = { Name = "Jump-Box" }
}

resource "aws_instance" "monitoring_ec2" {
  ami                    = "ami-09de149defa704528" # Ubuntu 22.04 in eu-west-1
  instance_type          = "t2.micro"
  key_name               = aws_key_pair.ec2_grafana_key.key_name
  vpc_security_group_ids = [aws_security_group.monitoring_sg.id]

  user_data = <<-EOF
              #!/bin/bash
              apt update -y && apt upgrade -y
              apt install -y wget curl tar software-properties-common

              # Install Prometheus
              useradd --no-create-home --shell /bin/false prometheus
              cd /opt && wget https://github.com/prometheus/prometheus/releases/download/v2.52.0/prometheus-2.52.0.linux-amd64.tar.gz
              tar -xvf prometheus-2.52.0.linux-amd64.tar.gz
              mv prometheus-2.52.0.linux-amd64 prometheus
              cp prometheus/prometheus /usr/local/bin/
              cp prometheus/promtool /usr/local/bin/
              mkdir -p /etc/prometheus /var/lib/prometheus
              cp -r prometheus/consoles /etc/prometheus
              cp -r prometheus/console_libraries /etc/prometheus
              cat > /etc/prometheus/prometheus.yml <<PROM
              global:
                scrape_interval: 15s
              scrape_configs:
                - job_name: 'prometheus'
                  static_configs:
                    - targets: ['localhost:9090']
                - job_name: 'node_exporter'
                  static_configs:
                    - targets: ['localhost:9100']
              PROM
              cat > /etc/systemd/system/prometheus.service <<P
              [Unit]
              Description=Prometheus
              After=network.target

              [Service]
              User=prometheus
              ExecStart=/usr/local/bin/prometheus \
                --config.file=/etc/prometheus/prometheus.yml \
                --storage.tsdb.path=/var/lib/prometheus/
              [Install]
              WantedBy=multi-user.target
              P
              systemctl daemon-reexec
              systemctl enable --now prometheus

              # Install Node Exporter
              useradd --no-create-home --shell /bin/false node_exporter
              cd /opt && wget https://github.com/prometheus/node_exporter/releases/download/v1.8.0/node_exporter-1.8.0.linux-amd64.tar.gz
              tar -xvf node_exporter-1.8.0.linux-amd64.tar.gz
              cp node_exporter-1.8.0.linux-amd64/node_exporter /usr/local/bin/
              cat > /etc/systemd/system/node_exporter.service <<NE
              [Unit]
              Description=Node Exporter
              After=network.target

              [Service]
              User=node_exporter
              ExecStart=/usr/local/bin/node_exporter

              [Install]
              WantedBy=default.target
              NE
              systemctl daemon-reexec
              systemctl enable --now node_exporter

              # Install Grafana
              add-apt-repository "deb https://packages.grafana.com/oss/deb stable main" -y
              wget -q -O - https://packages.grafana.com/gpg.key | apt-key add -
              apt update
              apt install -y grafana
              systemctl enable --now grafana-server
              EOF

  tags = {
    Name = "Grafana-Prometheus-Monitoring"
  }
}

resource "aws_key_pair" "ec2_grafana_key" {
  key_name   = "monitoring-key"
  public_key = file("~/.ssh/id_rsa.pub")
}