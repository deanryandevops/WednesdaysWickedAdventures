# Use official NGINX lightweight image
FROM nginx:alpine

# Set working directory
WORKDIR /usr/share/nginx/html

# Remove default nginx index.html
RUN rm -rf ./*

# Add files to NGINX root directory
ADD ./Frontend/ .

# Expose port 80
EXPOSE 80

# Start nginx when container starts
CMD ["nginx", "-g", "daemon off;"]
